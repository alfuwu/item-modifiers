package com.alfred.modifiers.mixin.client;

import com.alfred.modifiers.ModifiersConfig;
import com.alfred.modifiers.access.ItemAccessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.alfred.modifiers.Constants.MODIFIED_ATTACK_DAMAGE_MODIFIER_ID;
import static com.alfred.modifiers.Constants.MODIFIED_ATTACK_SPEED_MODIFIER_ID;
import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;
import static net.minecraft.item.ItemStack.appendEnchantments;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    private static boolean isSectionVisible(int flags, ItemStack.TooltipSection tooltipSection) {
        return (flags & tooltipSection.getFlag()) == 0;
    }

    @Shadow public abstract Text getName();
    @Shadow public abstract boolean hasCustomName();
    @Shadow public abstract Rarity getRarity();
    @Shadow public abstract Item getItem();
    @Shadow protected abstract int getHideFlags();
    @Shadow public abstract NbtList getEnchantments();
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract int getDamage();
    @Shadow public abstract Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot);
    @Shadow public abstract boolean hasNbt();

    @Shadow private static Collection<Text> parseBlockTag(String tag) {
        try {
            return (Collection) BlockArgumentParser.blockOrTag(Registries.BLOCK.getReadOnlyWrapper(), tag, true).map((blockResult) -> {
                return Lists.newArrayList(new Text[]{blockResult.blockState().getBlock().getName().formatted(Formatting.DARK_GRAY)});
            }, (tagResult) -> {
                return (List)tagResult.tag().stream().map((registryEntry) -> {
                    return ((Block)registryEntry.value()).getName().formatted(Formatting.DARK_GRAY);
                }).collect(Collectors.toList());
            });
        } catch (CommandSyntaxException var2) {
            return Lists.newArrayList(new Text[]{Text.literal("missingno").formatted(Formatting.DARK_GRAY)});
        }
    }

    @Shadow public abstract boolean isOf(Item item);
    @Shadow public abstract boolean isDamaged();
    @Shadow private NbtCompound nbt;
    @Final @Shadow private static Style LORE_STYLE;
    @Final @Shadow private static Text DISABLED_TEXT;

    @Unique
    private void applyEntry(List<Text> list, @Nullable PlayerEntity player, Map.Entry<EntityAttribute, EntityAttributeModifier> entry) {
        EntityAttributeModifier entityAttributeModifier = entry.getValue();
        double d = entityAttributeModifier.getValue();
        boolean bl = false;
        if (player != null) {
            if (entityAttributeModifier.getId() == ItemAccessor.getAttackDamageModifierID() || entityAttributeModifier.getId().equals(MODIFIED_ATTACK_DAMAGE_MODIFIER_ID)) {
                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                d += EnchantmentHelper.getAttackDamage((ItemStack) (Object) this, EntityGroup.DEFAULT);
                bl = true;
            } else if (entityAttributeModifier.getId() == ItemAccessor.getAttackSpeedModifierID() || entityAttributeModifier.getId().equals(MODIFIED_ATTACK_SPEED_MODIFIER_ID)) {
                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                bl = true;
            }
        }

        double e;
        if (entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
            if (entry.getKey().equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE))
                e = d * 10.0;
            else
                e = d;
        else
            e = d * 100.0;

        if (bl) {
            list.add(ScreenTexts.space().append(Text.translatable("attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e), Text.translatable((entry.getKey()).getTranslationKey()))).formatted(Formatting.DARK_GREEN));
        } else if (d > 0.0) {
            list.add(Text.translatable("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e), Text.translatable((entry.getKey()).getTranslationKey())).formatted(Formatting.BLUE));
        } else if (d < 0.0) {
            e *= -1.0;
            list.add(Text.translatable("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(e), Text.translatable((entry.getKey()).getTranslationKey())).formatted(Formatting.RED));
        }
    }

    /**
     * @author alfred
     * @reason need to overwrite how tooltips are displayed so that damage is shown vanilla style
     */
    @Overwrite
    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        List<Text> list = Lists.newArrayList();
        MutableText mutableText = Text.empty().append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            mutableText.formatted(Formatting.ITALIC);
        }

        list.add(mutableText);
        if (!context.isAdvanced() && !this.hasCustomName() && this.isOf(Items.FILLED_MAP)) {
            Integer integer = FilledMapItem.getMapId((ItemStack) (Object)this);
            if (integer != null) {
                list.add(FilledMapItem.getIdText((ItemStack) (Object)this));
            }
        }

        int i = this.getHideFlags();
        if (isSectionVisible(i, ItemStack.TooltipSection.ADDITIONAL)) {
            this.getItem().appendTooltip((ItemStack) (Object)this, player == null ? null : player.getWorld(), list, context);
        }

        int j;
        if (this.hasNbt()) {
            if (isSectionVisible(i, ItemStack.TooltipSection.UPGRADES) && player != null) {
                ArmorTrim.appendTooltip((ItemStack) (Object)this, player.getWorld().getRegistryManager(), list);
            }

            if (isSectionVisible(i, ItemStack.TooltipSection.ENCHANTMENTS)) {
                appendEnchantments(list, this.getEnchantments());
            }

            if (this.nbt.contains("display", 10)) {
                NbtCompound nbtCompound = this.nbt.getCompound("display");
                if (isSectionVisible(i, ItemStack.TooltipSection.DYE) && nbtCompound.contains("color", 99)) {
                    if (context.isAdvanced()) {
                        list.add(Text.translatable("item.color", String.format(Locale.ROOT, "#%06X", nbtCompound.getInt("color"))).formatted(Formatting.GRAY));
                    } else {
                        list.add(Text.translatable("item.dyed").formatted(Formatting.GRAY, Formatting.ITALIC));
                    }
                }

                if (nbtCompound.getType("Lore") == 9) {
                    NbtList nbtList = nbtCompound.getList("Lore", 8);

                    for(j = 0; j < nbtList.size(); ++j) {
                        String string = nbtList.getString(j);

                        try {
                            MutableText mutableText2 = Text.Serialization.fromJson(string);
                            if (mutableText2 != null) {
                                list.add(Texts.setStyleIfAbsent(mutableText2, LORE_STYLE));
                            }
                        } catch (Exception var19) {
                            nbtCompound.remove("Lore");
                        }
                    }
                }
            }
        }

        int k;
        if (isSectionVisible(i, ItemStack.TooltipSection.MODIFIERS)) {
            EquipmentSlot[] var21 = EquipmentSlot.values();
            k = var21.length;

            for(j = 0; j < k; ++j) {
                EquipmentSlot equipmentSlot = var21[j];
                Multimap<EntityAttribute, EntityAttributeModifier> multimap = this.getAttributeModifiers(equipmentSlot);
                if (!multimap.isEmpty()) {
                    list.add(ScreenTexts.EMPTY);
                    list.add(Text.translatable("item.modifiers." + equipmentSlot.getName()).formatted(Formatting.GRAY));

                    List<Map.Entry<EntityAttribute, EntityAttributeModifier>> damageModifiers = new ArrayList<>();
                    List<Map.Entry<EntityAttribute, EntityAttributeModifier>> speedModifiers = new ArrayList<>();
                    List<Map.Entry<EntityAttribute, EntityAttributeModifier>> otherModifiers = new ArrayList<>();

                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        if (player != null) {
                            if (entityAttributeModifier.getId() == ItemAccessor.getAttackDamageModifierID() || entityAttributeModifier.getId().equals(MODIFIED_ATTACK_DAMAGE_MODIFIER_ID))
                                damageModifiers.add(entry);
                            else if (entityAttributeModifier.getId() == ItemAccessor.getAttackSpeedModifierID() || entityAttributeModifier.getId().equals(MODIFIED_ATTACK_SPEED_MODIFIER_ID))
                                speedModifiers.add(entry);
                            else
                                otherModifiers.add(entry);
                        } else {
                            otherModifiers.add(entry);
                        }
                    }

                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : damageModifiers)
                        applyEntry(list, player, entry);
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : speedModifiers)
                        applyEntry(list, player, entry);
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : otherModifiers)
                        applyEntry(list, player, entry);
                }
            }
        }

        if (this.hasNbt()) {
            if (ModifiersConfig.getInstance().showModifierPercentages) {
                // show modifier modifications here by getting item NBT data
            }

            if (isSectionVisible(i, ItemStack.TooltipSection.UNBREAKABLE) && this.nbt.getBoolean("Unbreakable"))
                list.add(Text.translatable("item.unbreakable").formatted(Formatting.BLUE));

            NbtList nbtList2;
            if (isSectionVisible(i, ItemStack.TooltipSection.CAN_DESTROY) && this.nbt.contains("CanDestroy", 9)) {
                nbtList2 = this.nbt.getList("CanDestroy", 8);
                if (!nbtList2.isEmpty()) {
                    list.add(ScreenTexts.EMPTY);
                    list.add(Text.translatable("item.canBreak").formatted(Formatting.GRAY));

                    for(k = 0; k < nbtList2.size(); ++k)
                        list.addAll(parseBlockTag(nbtList2.getString(k)));
                }
            }

            if (isSectionVisible(i, ItemStack.TooltipSection.CAN_PLACE) && this.nbt.contains("CanPlaceOn", 9)) {
                nbtList2 = this.nbt.getList("CanPlaceOn", 8);
                if (!nbtList2.isEmpty()) {
                    list.add(ScreenTexts.EMPTY);
                    list.add(Text.translatable("item.canPlace").formatted(Formatting.GRAY));

                    for(k = 0; k < nbtList2.size(); ++k)
                        list.addAll(parseBlockTag(nbtList2.getString(k)));
                }
            }
        }

        if (context.isAdvanced()) {
            if (this.isDamaged())
                list.add(Text.translatable("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));

            list.add(Text.literal(Registries.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
            if (this.hasNbt())
                list.add(Text.translatable("item.nbt_tags", this.nbt.getKeys().size()).formatted(Formatting.DARK_GRAY));
        }

        if (player != null && !this.getItem().isEnabled(player.getWorld().getEnabledFeatures()))
            list.add(DISABLED_TEXT);

        return list;
    }
}
