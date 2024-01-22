package com.alfred.modifiers.modifiers.ranged;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import net.minecraft.item.ItemStack;

public class SightedModifier extends ItemModifier {
    public SightedModifier() {
        super("Sighted", ModifierType.RANGED);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        itemStack.getOrCreateNbt().putDouble(Constants.PROJECTILE_DAMAGE_MULT, 1.1);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.03);
    }
}
