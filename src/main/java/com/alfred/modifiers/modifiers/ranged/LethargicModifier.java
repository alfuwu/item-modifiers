package com.alfred.modifiers.modifiers.ranged;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class LethargicModifier extends ItemModifier {
    public LethargicModifier() {
        super("Lethargic", ModifierType.RANGED);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, null, null, null, 0.85f);
        itemStack.getOrCreateNbt().putDouble(Constants.VELOCITY, 0.9);
    }
}
