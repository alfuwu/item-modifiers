package com.alfred.modifiers.mixin;

import com.alfred.modifiers.modifiers.Util;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {
    @Inject(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private void applyModifiers(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir, @Local(ordinal = 1) ItemStack stack) {
        Util.addModifier(stack);
    }
}
