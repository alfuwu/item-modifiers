package com.alfred.modifiers.mixin;

import com.alfred.modifiers.Constants;
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
public abstract class PlayerEntityMixin {
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean modifyDamage(Entity instance, DamageSource source, float damage) {
        if (source.getAttacker() != null && source.getAttacker() instanceof PlayerEntity player) {
            double totalCritChance = 0.0;
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
