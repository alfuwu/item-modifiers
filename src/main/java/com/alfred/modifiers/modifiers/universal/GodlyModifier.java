package com.alfred.modifiers.modifiers.universal;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class GodlyModifier extends ItemModifier {
    public GodlyModifier() {
        super("Godly", ModifierType.UNIVERSAL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 1.5f, null, null, null);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 1.15);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.05);
    }
}
