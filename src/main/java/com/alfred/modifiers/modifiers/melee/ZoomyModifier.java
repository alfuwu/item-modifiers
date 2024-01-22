package com.alfred.modifiers.modifiers.melee;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class ZoomyModifier extends ItemModifier {
    public ZoomyModifier() {
        super("Zoomy", ModifierType.MINING_TOOL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, null, null, null, 500f); // this makes the tool mine insanely fast
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 0.9);
        itemStack.getOrCreateNbt().putDouble(Constants.SIZE, 0.75);
    }
}