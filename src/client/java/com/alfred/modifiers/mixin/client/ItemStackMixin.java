package com.alfred.modifiers.mixin.client;

import com.alfred.modifiers.Constants;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeBaseValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    private double modifyTooltipSpeed(PlayerEntity instance, EntityAttribute attribute) {
        double val = instance.getAttributeBaseValue(attribute);
        ItemStack stack = ((ItemStack) (Object) this);
        if (stack.hasNbt() && stack.getNbt().contains(Constants.SPEED_MULT) && attribute.equals(EntityAttributes.GENERIC_ATTACK_SPEED)) {
            double originalSpeed = stack.getAttributeModifiers(EquipmentSlot.MAINHAND)
                    .get(EntityAttributes.GENERIC_ATTACK_SPEED).stream()
                    .filter(modifier -> modifier.getId().equals(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3")))
                    .findFirst().orElse(new EntityAttributeModifier("", 0.0, null)).getValue();
            val += ((val + originalSpeed) * stack.getNbt().getDouble(Constants.SPEED_MULT)) - (val + originalSpeed);
        }
        if (stack.hasNbt() && stack.getNbt().contains(Constants.SPEED) && attribute.equals(EntityAttributes.GENERIC_ATTACK_SPEED))
            val += stack.getNbt().getDouble(Constants.SPEED);
        return val;
    }
    /*@ModifyVariable(method = "getTooltip", at = @At("STORE"), ordinal = 0)
    private double modifyAttackSpeed(double originalSpeed, @Local PlayerEntity player, @Local EntityAttributeModifier attribute) {
        if (player != null) {
            if (attribute.getId().equals(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"))) {

            }
        }

        return modifiedSpeed;
    }*/
}
