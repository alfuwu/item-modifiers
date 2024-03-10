package com.alfred.modifiers.mixin;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.ItemModifierRegistry;
import com.alfred.modifiers.ModifiersConfig;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;
import java.text.DecimalFormat;

import static net.minecraft.item.BowItem.getPullProgress;

@SuppressWarnings("unused")
public abstract class ItemsMixin {
	@Mixin(ItemStack.class)
	public abstract static class ItemStackMixin {
		@Shadow @Nullable public abstract NbtCompound getNbt();
		@Shadow public abstract boolean hasNbt();
		@Shadow public abstract Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot);
		@Shadow public abstract boolean hasCustomName();

		@ModifyReturnValue(method = "getName", at = @At("RETURN"))
		private Text applyModifierToName(Text original) {
			if (!this.hasCustomName() && this.hasNbt() && this.getNbt().contains(Constants.MODIFIER_NAME))
				original = Text.translatable(this.getNbt().getString(Constants.MODIFIER_NAME)).append(ScreenTexts.SPACE).append(original);
			return original;
		}

		@Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeBaseValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
		private double modifyTooltipSpeed(PlayerEntity instance, EntityAttribute attribute) {
			double val = instance.getAttributeBaseValue(attribute);
			if (this.hasNbt() && this.getNbt().contains(Constants.SPEED_MULT) && attribute.equals(EntityAttributes.GENERIC_ATTACK_SPEED)) {
				double originalSpeed = this.getAttributeModifiers(EquipmentSlot.MAINHAND)
						.get(EntityAttributes.GENERIC_ATTACK_SPEED).stream()
						.filter(modifier -> modifier.getId().equals(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3")))
						.findFirst().orElse(new EntityAttributeModifier("", 0.0, null)).getValue();
				val += ((val + originalSpeed) * this.getNbt().getDouble(Constants.SPEED_MULT)) - (val + originalSpeed);
			}
			if (this.hasNbt() && this.getNbt().contains(Constants.SPEED) && attribute.equals(EntityAttributes.GENERIC_ATTACK_SPEED))
				val += this.getNbt().getDouble(Constants.SPEED);
			return val;
		}

		@Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendEnchantments(Ljava/util/List;Lnet/minecraft/nbt/NbtList;)V"))
		private void appendModifiers(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, @Local List<Text> tooltip) {
			if (this.hasNbt() && ModifiersConfig.getInstance().showModifierPercentages) {
				for (String nbtId : Constants.cleanedValues()) {
					if (this.getNbt().contains(nbtId, 5) || this.getNbt().contains(nbtId, 6)) {
						double value = (this.getNbt().getDouble(nbtId) * 100) - (Constants.modifierMapping(nbtId).equals("crit_chance") ? 0 : 100);
						tooltip.add(Text.translatable(
								"modifiers.values.%s".formatted(value < 0 ? "minus" : "plus"),
								new DecimalFormat("#,###.##").format(value < 0 ? value * -1 : value),
								Text.translatable("modifiers.constants.%s".formatted(Constants.modifierMapping(nbtId))).getString()
						).formatted(Formatting.DARK_PURPLE));
					}
				}
			}
		}

		// mixin to ItemStack's get rarity function to modify it based off of modifier

		@ModifyReturnValue(method = "getMiningSpeedMultiplier", at = @At("RETURN")) // maybe only apply mining speed boost if original is greater than 1f (1f = not efficient at mining), so that block breaking is fucked up
		private float modifyMiningSpeed(float original, BlockState state) {
			if (this.hasNbt() && this.getNbt().contains(Constants.MINING_SPEED_MULT))
				original *= this.getNbt().getFloat(Constants.MINING_SPEED_MULT);
			if (this.hasNbt() && this.getNbt().contains(Constants.MINING_SPEED))
				original += this.getNbt().getFloat(Constants.MINING_SPEED) * 4; // arbitrary multiplication of 4, seems about right
			return original;
		}
	}

	@Mixin(EnchantmentHelper.class)
	public abstract static class EnchantmentHelperMixin {
		@ModifyReturnValue(method = "getAttackDamage", at = @At("RETURN"))
		private static float modifyItemAttackDamage(float original, ItemStack stack, EntityGroup group) {
			if (stack.hasNbt() && stack.getNbt().contains(Constants.DAMAGE_MULT)) {
				double originalDamage = stack.getAttributeModifiers(EquipmentSlot.MAINHAND)
						.get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream()
						.filter(modifier -> modifier.getId().equals(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")))
						.findFirst().orElse(new EntityAttributeModifier("", 0.0, null)).getValue();
				original += originalDamage * (stack.getNbt().getDouble(Constants.DAMAGE_MULT) - 1);
			}
			if (stack.hasNbt() && stack.getNbt().contains(Constants.DAMAGE))
				original += stack.getNbt().getDouble(Constants.DAMAGE);
			return original;
		}
	}

	@Mixin(BowItem.class)
	public abstract static class BowItemMixin {
		@Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
		private float modifyUseTime(int useTicks, @Local(ordinal = 0) ItemStack stack) {
			if (stack.hasNbt() && stack.getNbt().contains(Constants.RANGED_WEAPON_SPEED))
				return Math.min(getPullProgress(useTicks) * stack.getNbt().getFloat(Constants.RANGED_WEAPON_SPEED), 1); // cap pull at 1
			return getPullProgress(useTicks);
		}
	}

	@Mixin(CrossbowItem.class)
	public abstract static class CrossbowItemMixin {
		@Inject(method = "getPullProgress", at = @At("RETURN"), cancellable = true)
		private static void modifyUseTime(int useTicks, ItemStack stack, CallbackInfoReturnable<Float> cir, @Local float f) {
			if (stack.hasNbt() && stack.getNbt().contains(Constants.RANGED_WEAPON_SPEED))
				cir.setReturnValue(Math.min(f * stack.getNbt().getFloat(Constants.RANGED_WEAPON_SPEED), 1));
		}
	}
}