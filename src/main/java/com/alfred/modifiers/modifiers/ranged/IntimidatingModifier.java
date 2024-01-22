package com.alfred.modifiers.modifiers.ranged;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import net.minecraft.item.ItemStack;

public class IntimidatingModifier extends ItemModifier {
    public IntimidatingModifier() {
        super("Intimidating", ModifierType.RANGED);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 1.15);
        itemStack.getOrCreateNbt().putDouble(Constants.VELOCITY, 1.05);
    }
}
