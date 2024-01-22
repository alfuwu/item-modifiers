package com.alfred.modifiers.mixin.client;

import com.alfred.modifiers.Constants;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyVariable(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"), argsOnly = true)
    private MatrixStack renderCustomScale(MatrixStack stack, @Local ItemStack itemStack, @Local ModelTransformationMode renderMode) {
        if (itemStack.hasNbt() && itemStack.getNbt().contains(Constants.SIZE)) {
            boolean bl = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED;
            float f = itemStack.getNbt().getFloat(Constants.SIZE);
            stack.scale(f, f, f);
            if (!bl) // don't translate model when its not in the player's hand
                stack.translate(0, 0, -f/35); // approximately puts the model in the player's hand, might not work well at extremes
        }
        return stack;
    }
}
