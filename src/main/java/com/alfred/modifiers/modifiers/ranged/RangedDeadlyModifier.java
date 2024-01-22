package com.alfred.modifiers.modifiers.ranged;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class RangedDeadlyModifier extends ItemModifier {
    public RangedDeadlyModifier() {
        super("Deadly", ModifierType.RANGED);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 1f, null, null, 1.05f);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 1.05);
        itemStack.getOrCreateNbt().putDouble(Constants.VELOCITY, 1.05);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.02);
    }
}
