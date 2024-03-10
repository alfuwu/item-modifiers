package com.alfred.modifiers.modifiers;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.ItemModifierRegistry;
import com.alfred.modifiers.ModifiersConfig;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;

public abstract class Util {
    public static void modifyStack(ItemStack stack, Float damage, Float speed, Float damageMult, Float speedMult) {
        if (!(stack.getItem() instanceof RangedWeaponItem)) {
            if (damageMult != null)
                stack.getOrCreateNbt().putFloat(Constants.DAMAGE_MULT, damageMult);
            if (damage != null)
                stack.getOrCreateNbt().putFloat(Constants.DAMAGE, damage);
            if (speedMult != null) {
                stack.getOrCreateNbt().putFloat(Constants.SPEED_MULT, speedMult);
                stack.getOrCreateNbt().putFloat(Constants.MINING_SPEED_MULT, speedMult);
            }
            if (speed != null) {
                stack.getOrCreateNbt().putFloat(Constants.SPEED, speed);
                stack.getOrCreateNbt().putFloat(Constants.MINING_SPEED, speed);
            }
        } else {
            if (damageMult != null)
                stack.getOrCreateNbt().putFloat(Constants.PROJECTILE_DAMAGE_MULT, damageMult);
            if (damage != null)
                stack.getOrCreateNbt().putFloat(Constants.PROJECTILE_DAMAGE, damage);
            if (speedMult != null || speed != null)
                stack.getOrCreateNbt().putFloat(Constants.RANGED_WEAPON_SPEED, (1 + (speed != null ? speed : 0)) * (speedMult != null ? speedMult : 1));
        }
    }

    public static void addModifier(ItemStack stack) {
        if (Math.random() < ModifiersConfig.getInstance().craftedItemModifierChance && !(stack.hasNbt() && stack.getNbt().contains(Constants.HAS_MODIFIER) && stack.getNbt().getBoolean(Constants.HAS_MODIFIER))) {
            // get a random modifier that's applicable to this item
            ItemModifier randomModifier = ItemModifierRegistry.getRandomModifier(stack);

            if (randomModifier != null)
                randomModifier.applyModifier(stack);
        }
    }
}
