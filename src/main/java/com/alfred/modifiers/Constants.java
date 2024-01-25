package com.alfred.modifiers;

public class Constants {
    public static final String DAMAGE = "AdditionalWeaponDamageModifier";
    public static final String SPEED = "AdditionalWeaponSpeedModifier";
    public static final String DAMAGE_MULT = "AdditionalWeaponDamageMultiplicativeModifier";
    public static final String SPEED_MULT = "AdditionalWeaponSpeedMultiplicativeModifier";
    public static final String CRIT = "CritChanceModifier";
    public static final String KNOCKBACK = "KnockbackModifier";
    public static final String MINING_SPEED = "MiningSpeedModifier";
    public static final String MINING_SPEED_MULT = "MiningSpeedMultiplicativeModifier";
    public static final String SIZE = "SizeModifier";
    public static final String VELOCITY = "VelocityModifier";
    public static final String DIVERGENCE = "SteadyModifier";
    public static final String PROJECTILE_DAMAGE = "ProjectileDamageModifier";
    public static final String PROJECTILE_DAMAGE_MULT = "ProjectileDamageMultiplicativeModifier";
    public static final String RANGED_WEAPON_SPEED = "RangedWeaponSpeedModifier"; // only supports items extending from BowItem or CrossbowItem, not custom-made ranged weapons unless they implicitly add support for this NBT tag
    public static final String HAS_MODIFIER = "HasModifier";
    public static final String MODIFIER_NAME = "ModifierName";

    public static String[] values() {
        return new String[] {
            DAMAGE, SPEED, DAMAGE_MULT, SPEED_MULT, CRIT, KNOCKBACK, MINING_SPEED, MINING_SPEED_MULT,
            SIZE, VELOCITY, DIVERGENCE,PROJECTILE_DAMAGE, PROJECTILE_DAMAGE_MULT, RANGED_WEAPON_SPEED,
            HAS_MODIFIER, MODIFIER_NAME
        };
    }

    public enum ModifierType {
        SWORD, // SwordItem.class
        PICKAXE, // PickaxeItem.class
        AXE, // AxeItem.class
        HOE, // HoeItem.class
        SHOVEL, // ShovelItem.class
        BOW, // BowItem.class
        CROSSBOW, // CrossbowItem.class
        FISHING_ROD, // FishingRodItem.class
        SHIELD, // ShieldItem.class
        TOOL, // ToolItem.class (swords, pickaxes, axes, hoes, shovels)
        UNIVERSAL, // All weapons (melee, ranged, magic, summoning)
        COMMON, // Same as universal but does not include items like drills, chainsaws, flails, spears, or yoyos
        MELEE, // Melee weapons, (swords, axes)
        MELEE_TOOL, // Melee weapons, (swords, axes, pickaxes, shovels)
        MINING_TOOL, // (pickaxes, axes, shovels, hoes)
        RANGED, // RangedWeaponItem.class (bows, crossbows)
        TRIDENT, // TridentItem.class
        ARMOR, // ArmorItem.class (???)
        ITEM // any (not recommended)
    }
}
