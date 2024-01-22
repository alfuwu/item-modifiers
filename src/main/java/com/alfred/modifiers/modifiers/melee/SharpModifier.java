package com.alfred.modifiers.modifiers.melee;

import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class SharpModifier extends ItemModifier {
    public SharpModifier() {
        super("Sharp", ModifierType.MELEE_TOOL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 1.5f, null, null, null);
    }
}
