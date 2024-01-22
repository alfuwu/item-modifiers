package com.alfred.modifiers.modifiers.universal;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;

public class UnpleasantModifier extends ItemModifier {
    public UnpleasantModifier() {
        super("Unpleasant", ModifierType.UNIVERSAL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 0.5f, null, null, null);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 1.15);
    }
}
