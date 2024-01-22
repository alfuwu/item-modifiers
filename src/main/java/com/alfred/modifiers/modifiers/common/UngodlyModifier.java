package com.alfred.modifiers.modifiers.common;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class UngodlyModifier extends ItemModifier {
    public UngodlyModifier() {
        super("Ungodly", ModifierType.COMMON);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 3f, 4f, 2f, 1.1f);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 4.0);
        itemStack.getOrCreateNbt().putDouble(Constants.VELOCITY, 3.0);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.2);
    }
}
