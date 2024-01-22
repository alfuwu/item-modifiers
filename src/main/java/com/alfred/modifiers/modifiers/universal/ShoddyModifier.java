package com.alfred.modifiers.modifiers.universal;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class ShoddyModifier extends ItemModifier {
    public ShoddyModifier() {
        super("Shoddy", ModifierType.UNIVERSAL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, null, null, 0.9f, null);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 0.85);
    }
}
