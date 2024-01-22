package com.alfred.modifiers.modifiers.universal;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import net.minecraft.item.ItemStack;

public class ZealousModifier extends ItemModifier {
    public ZealousModifier() {
        super("Zealous", ModifierType.UNIVERSAL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.05);
    }
}
