package com.alfred.modifiers.mixin;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.ItemModifierRegistry;
import com.alfred.modifiers.ModifiersConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Unique
    private ServerWorld getServerWorld() {
        if (!((MobEntity) (Object) this).getWorld().isClient)
            return (ServerWorld) ((MobEntity) (Object) this).getWorld();
        return null;
    }

    @Unique
    private void addCritParticles(Entity target) {
        if (getServerWorld() != null)
            getServerWorld().getChunkManager().sendToNearbyPlayers((MobEntity) (Object) this, new EntityAnimationS2CPacket(target, 4));
    }

    @Redirect(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean modifyDamage(Entity instance, DamageSource source, float damage) {
        if (source.getAttacker() != null) {
            double totalCritChance = ModifiersConfig.baseCritChance;
            for (ItemStack equippedItem : source.getAttacker().getItemsEquipped())
                if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.CRIT))
                    totalCritChance += equippedItem.getNbt().getDouble(Constants.CRIT);
            if (Math.random() < totalCritChance) {
                instance.getWorld().playSound(null, instance.getX(), instance.getY(), instance.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, instance.getSoundCategory(), 1.0F, 1.0F);
                addCritParticles(instance);
                damage *= 1.5f;
            }
        }
        return instance.damage(source, damage);
    }

    @Inject(method = "updateEnchantments", at = @At("RETURN"))
    private void applyEquipmentModifiers(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        for (ItemStack equippedItem : ((MobEntity) (Object) this).getItemsEquipped()) {
            if (Math.random() < (((MobEntity) (Object) this).getWorld().getDifficulty() == Difficulty.HARD ? ModifiersConfig.getInstance().hardModeMobModifierChance : ModifiersConfig.getInstance().mobModifierChance)) {
                ItemModifier randomModifier = ItemModifierRegistry.getRandomModifier(equippedItem);
                if (randomModifier != null)
                    randomModifier.applyModifier(equippedItem);
            }
        }
    }

    @Unique
    public double updateKnockback(double strength) {
        for (ItemStack equippedItem : ((MobEntity) (Object) this).getItemsEquipped())
            if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.KNOCKBACK))
                strength *= equippedItem.getNbt().getDouble(Constants.KNOCKBACK) + 1.0;
        return strength;
    }

    @Redirect(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"))
    private void modifyKnockback(LivingEntity instance, double strength, double x, double z) {
        instance.takeKnockback(updateKnockback(strength), x, z);
    }
}
