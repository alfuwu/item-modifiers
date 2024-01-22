package com.alfred.modifiers.access;

import net.minecraft.item.ItemStack;

public interface ProjectileMixinAccessor {
    Iterable<ItemStack> getOwnerItems();
}
