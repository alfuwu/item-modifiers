package com.alfred.modifiers.modifiers.melee;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import net.minecraft.item.ItemStack;

public class LargeModifier extends ItemModifier {
    public LargeModifier() {
        super("Large", ModifierType.MELEE_TOOL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 1.12);
        itemStack.getOrCreateNbt().putDouble(Constants.SIZE, 1.12);
    }
}
