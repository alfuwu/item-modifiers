package com.alfred.modifiers.mixin;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.access.ProjectileMixinAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

public abstract class ProjectileMixin {
    @Mixin(ProjectileEntity.class)
    public abstract static class ProjectileEntityMixin implements ProjectileMixinAccessor {
        @Unique protected Iterable<ItemStack> ownerItems = null;
        @Override
        public Iterable<ItemStack> getOwnerItems() {
            return ownerItems;
        }
        @Shadow private Entity owner;

        @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
        private void writeOwnerItemsNbt(NbtCompound nbt, CallbackInfo ci) {
            if (ownerItems == null)
                return;
            List<ItemStack> array = new ArrayList<>();
            for (ItemStack stack : ownerItems)
                array.add(stack);
            NbtList items = new NbtList();
            for (ItemStack itemStack : array) {
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    itemStack.writeNbt(nbtCompound);
                    items.add(nbtCompound);
                }
            }
            nbt.put("OwnerItems", items);
        }

        @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
        private void readOwnerItemsNbt(NbtCompound nbt, CallbackInfo ci) {
            if (ownerItems != null)
                return;
            if (nbt.contains("OwnerItems")) {
                List<ItemStack> savedItems = new ArrayList<>();
                for (int i = 0; i < nbt.getList("OwnerItems", 10).size(); ++i)
                    savedItems.add(ItemStack.fromNbt(nbt.getList("OwnerItems", 10).getCompound(i)));
                ownerItems = savedItems;
            }
        }

        @Inject(method = "createSpawnPacket", at = @At("RETURN"))
        private void logOwnerEquipment(CallbackInfoReturnable<Packet<ClientPlayPacketListener>> cir) {
            if (owner != null && ownerItems == null)
                ownerItems = owner.getItemsEquipped();
        }
        @Inject(method = "onSpawnPacket", at = @At("RETURN")) // in case this was called without createSpawnPacket for some reason
        private void logOwnerEquipment(EntitySpawnS2CPacket packet, CallbackInfo ci) {
            if (owner != null && ownerItems == null)
                ownerItems = owner.getItemsEquipped();
        }
    }

    @Mixin(PersistentProjectileEntity.class)
    public abstract static class PersistentProjectileEntityMixin extends ProjectileEntityMixin {
        @Unique
        private ServerWorld getServerWorld() {
            if (!((PersistentProjectileEntity) (Object) this).getWorld().isClient)
                return (ServerWorld) ((PersistentProjectileEntity) (Object) this).getWorld();
            return null;
        }

        @Unique
        private void addCritParticles(Entity target) {
            if (getServerWorld() != null)
                getServerWorld().getChunkManager().sendToNearbyPlayers((PersistentProjectileEntity) (Object) this, new EntityAnimationS2CPacket(target, 4));
        }

        @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
        private boolean critDamage(Entity instance, DamageSource source, float damage) {
            if (source.getAttacker() != null && !(instance.isPlayer() && ((PlayerEntity) instance).getAbilities().creativeMode) && ownerItems != null) {
                double totalCritChance = 0.0;
                for (ItemStack equippedItem : source.getAttacker().getItemsEquipped()) {
                    if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.CRIT))
                        totalCritChance += equippedItem.getNbt().getDouble(Constants.CRIT);
                    if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.PROJECTILE_DAMAGE_MULT))
                        damage *= equippedItem.getNbt().getDouble(Constants.PROJECTILE_DAMAGE_MULT);
                    if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.PROJECTILE_DAMAGE))
                        damage += equippedItem.getNbt().getDouble(Constants.PROJECTILE_DAMAGE);
                }
                if (Math.random() < totalCritChance) {
                    instance.getWorld().playSound(null, instance.getX(), instance.getY(), instance.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, instance.getSoundCategory(), 1.0F, 1.0F);
                    addCritParticles(instance);
                    damage *= 1.5f;
                }
            }
            return instance.damage(source, damage);
        }

        @Unique
        public double updateKnockback(double strength) {
            for (ItemStack equippedItem : ownerItems)
                if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.KNOCKBACK))
                    strength *= equippedItem.getNbt().getDouble(Constants.KNOCKBACK) + 1.0;
            return strength;
        }

        @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addVelocity(DDD)V"))
        private void modifyKnockback(LivingEntity instance, double deltaX, double deltaY, double deltaZ) {
            instance.addVelocity(updateKnockback(deltaX), updateKnockback(deltaY), updateKnockback(deltaZ));
        }

        @ModifyVariable(method = "setVelocity", at = @At("HEAD"), ordinal = 0, argsOnly = true)
        private float setSpeed(float speed) {
            if (ownerItems == null && ((PersistentProjectileEntity) (Object) this).getOwner() != null) // no spawn packet is created when BowItem sets velocity of the arrow, however the owner variable is still accessible, so get items currently equipped
                ownerItems = ((PersistentProjectileEntity) (Object) this).getOwner().getItemsEquipped();
            if (ownerItems != null)
                for (ItemStack equippedItem : ownerItems)
                    if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.VELOCITY))
                        speed *= equippedItem.getNbt().getDouble(Constants.VELOCITY);
            return speed;
        }
        @ModifyVariable(method = "setVelocity", at = @At("HEAD"), ordinal = 1, argsOnly = true)
        private float setDivergence(float divergence) {
            if (ownerItems == null && ((PersistentProjectileEntity) (Object) this).getOwner() != null)
                ownerItems = ((PersistentProjectileEntity) (Object) this).getOwner().getItemsEquipped();
            if (ownerItems != null)
                for (ItemStack equippedItem : ownerItems)
                    if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.DIVERGENCE))
                        divergence /= equippedItem.getNbt().getDouble(Constants.DIVERGENCE);
            return divergence;
        }
    }
}
