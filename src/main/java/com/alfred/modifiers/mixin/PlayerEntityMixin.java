package com.alfred.modifiers.mixin;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.ModifiersConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(PlayerEntity.class)
@SuppressWarnings("unused")
public abstract class PlayerEntityMixin {
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean modifyDamage(Entity instance, DamageSource source, float damage) {
        if (source.getAttacker() != null && source.getAttacker() instanceof PlayerEntity player) {
            double totalCritChance = ModifiersConfig.getInstance().baseCritChance;
            for (ItemStack equippedItem : source.getAttacker().getItemsEquipped())
                if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.CRIT))
                    totalCritChance += equippedItem.getNbt().getDouble(Constants.CRIT);
            if (Math.random() < totalCritChance) {
                instance.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, instance.getSoundCategory(), 1.0F, 1.0F);
                player.addCritParticles(instance);
                damage *= 1.5f;
            }
        }
        return instance.damage(source, damage);
    }

    @ModifyReturnValue(method = "getAttackCooldownProgressPerTick", at = @At("RETURN"))
    private float modifyAttackSpeed(float original) {
        ItemStack stack = ((PlayerEntity) (Object) this).getMainHandStack();
        if (stack.hasNbt() && stack.getNbt().contains(Constants.SPEED_MULT))
            original /= stack.getNbt().getFloat(Constants.SPEED_MULT);
        if (stack.hasNbt() && stack.getNbt().contains(Constants.SPEED))
            original -= stack.getNbt().getFloat(Constants.SPEED) * 20;
        return Math.max(original, 0.001f);
    }

    @Unique
    public double updateKnockback(double strength) {
        for (ItemStack equippedItem : ((PlayerEntity) (Object) this).getItemsEquipped())
            if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.KNOCKBACK))
                strength *= equippedItem.getNbt().getDouble(Constants.KNOCKBACK) + 1.0;
        return strength;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"))
    private void modifyKnockback(LivingEntity instance, double strength, double x, double z) {
        instance.takeKnockback(updateKnockback(strength), x, z);
    }
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void modifyKnockback(Entity instance, double deltaX, double deltaY, double deltaZ) {
        instance.addVelocity(updateKnockback(deltaX), updateKnockback(deltaY), updateKnockback(deltaZ));
    }
}
