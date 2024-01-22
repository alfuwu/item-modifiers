package com.alfred.modifiers.modifiers;

import com.alfred.modifiers.Constants;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;

public abstract class Util {
    public static void modifyStack(ItemStack stack, Float damage, Float speed, Float damageMult, Float speedMult) {
        /*if (damage == null)
            damage = 0f;
        if (speed == null)
            speed = 0f;
        if (damageMult == null)
            damageMult = 1f;
        if (speedMult == null)
            speedMult = 1f;*/
        if (!(stack.getItem() instanceof RangedWeaponItem)) {
            /*Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
            double originalDamage = attributeModifiers.get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream()
                    .findFirst()
                    .orElse(new EntityAttributeModifier("", 0.0, null)).getValue();
            double originalSpeed = attributeModifiers.get(EntityAttributes.GENERIC_ATTACK_SPEED).stream()
                    .findFirst()
                    .orElse(new EntityAttributeModifier("", 0.0, null)).getValue();

            EntityAttributeModifier newModifier = new EntityAttributeModifier(
                    Constants.MODIFIED_ATTACK_DAMAGE_MODIFIER_ID,
                    "Weapon modifier",
                    (originalDamage * damageMult) + damage,
                    EntityAttributeModifier.Operation.ADDITION
            );
            EntityAttributeModifier newSpeedModifier = new EntityAttributeModifier(
                    Constants.MODIFIED_ATTACK_SPEED_MODIFIER_ID,
                    "Weapon modifier",
                    Math.min((originalSpeed / speedMult) - speed, Math.max(4f + originalSpeed, 4f)),
                    EntityAttributeModifier.Operation.ADDITION
            );

            stack.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, newModifier, EquipmentSlot.MAINHAND);
            stack.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, newSpeedModifier, EquipmentSlot.MAINHAND);*/
            
            // switch to NBT damage modification system, requires another mixin so that entities appropriately get this NBT tag if its available and applies it to its damage
            if (damageMult != null)
                stack.getOrCreateNbt().putDouble(Constants.DAMAGE_MULT, damageMult);
            if (damage != null)
                stack.getOrCreateNbt().putDouble(Constants.DAMAGE, damage);
            if (speedMult != null) {
                stack.getOrCreateNbt().putDouble(Constants.SPEED_MULT, speedMult);
                stack.getOrCreateNbt().putDouble(Constants.MINING_SPEED_MULT, speedMult);
            }
            if (speed != null) {
                stack.getOrCreateNbt().putDouble(Constants.SPEED, speed);
                stack.getOrCreateNbt().putDouble(Constants.MINING_SPEED, speed);
            }
        } else {
            if (damageMult != null)
                stack.getOrCreateNbt().putFloat(Constants.PROJECTILE_DAMAGE_MULT, damageMult);
            if (damage != null)
                stack.getOrCreateNbt().putFloat(Constants.PROJECTILE_DAMAGE, damage);
            if (speedMult != null || speed != null)
                stack.getOrCreateNbt().putFloat(Constants.RANGED_WEAPON_SPEED, (1 + speed != null ? speed : 0) * speedMult != null ? speedMult : 1);
        }
    }
}
