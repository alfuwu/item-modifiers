package com.alfred.modifiers.modifiers.melee;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class SavageModifier extends ItemModifier {
    public SavageModifier() {
        super("Savage", ModifierType.MELEE_TOOL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 1f, null, null, null);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 1.2);
        itemStack.getOrCreateNbt().putDouble(Constants.SIZE, 1.1);
    }
}
