package com.alfred.modifiers.mixin;

import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.ItemModifierRegistry;
import com.alfred.modifiers.ModifiersConfig;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public class LootMixin {
    @Inject(method = "supplyInventory", at = @At("RETURN"))
    private void applyLootModifiers(Inventory inventory, LootContextParameterSet parameters, long seed, CallbackInfo ci) {
        for (int i = 0; i < inventory.size(); ++i) {
            if (!inventory.getStack(i).isEmpty()) {
                ItemStack stack = ((LootableInventory) this).getStack(i);
                if (Math.random() < ModifiersConfig.getInstance().generalModifierChance * (1 + parameters.getLuck())) {
                    ItemModifier randomModifier = ItemModifierRegistry.getRandomModifier(stack);
                    if (randomModifier != null)
                        randomModifier.applyModifier(stack);
                }
            }
        }
    }
}
