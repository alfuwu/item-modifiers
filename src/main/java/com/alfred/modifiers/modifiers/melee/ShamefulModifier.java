package com.alfred.modifiers.modifiers.melee;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class ShamefulModifier extends ItemModifier {
    public ShamefulModifier() {
        super("Shameful", ModifierType.MELEE_TOOL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, null, null, 0.9f, null);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 0.9);
        itemStack.getOrCreateNbt().putDouble(Constants.SIZE, 0.9);
    }
}
