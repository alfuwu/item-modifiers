package com.alfred.modifiers.modifiers.common;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class MurderousModifier extends ItemModifier {
    public MurderousModifier() {
        super("Murderous", ModifierType.COMMON);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 0.7f, null, null, 1.06f);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.03);
    }
}
