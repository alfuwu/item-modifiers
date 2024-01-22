package com.alfred.modifiers.mixin;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.access.ProjectileMixinAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private static double applyKnockback(double strength, @Nullable Iterable<ItemStack> iterable) {
        if (iterable != null)
            for (ItemStack equippedItem : iterable)
                if (equippedItem.hasNbt() && equippedItem.getNbt().contains(Constants.KNOCKBACK))
                    strength *= equippedItem.getNbt().getDouble(Constants.KNOCKBACK) + 1.0;
        return strength;
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"))
    public void modifyKnockback(LivingEntity instance, double strength, double x, double z, @Local DamageSource source) {
        strength = applyKnockback(strength,
                source.getAttacker() != null && !(source.getSource() instanceof ProjectileEntity) ?
                        source.getAttacker().getItemsEquipped() : source.getSource() != null ?
                        ((ProjectileMixinAccessor) source.getSource()).getOwnerItems() : null);
        instance.takeKnockback(strength, x, z);
    }
}
