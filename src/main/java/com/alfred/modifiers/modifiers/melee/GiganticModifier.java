package com.alfred.modifiers.modifiers.melee;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class GiganticModifier extends ItemModifier {
    public GiganticModifier() {
        super("Gigantic", ModifierType.MELEE_TOOL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 9f, -0.25f, null, 0.75f);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 3.0);
        itemStack.getOrCreateNbt().putDouble(Constants.SIZE, 3.0);
    }
}
