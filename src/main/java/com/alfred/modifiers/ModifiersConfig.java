package com.alfred.modifiers;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = "modifiers")
@SuppressWarnings("unused")
public class ModifiersConfig implements ConfigData {
    @Comment("When crafting an item, this is the chance that it will have a modifier\nRange from 0 to 1")
    public double craftedItemModifierChance = 0.75;
    @Comment("This is the chance that any given item a mob spawns with will have a modifier")
    public double mobModifierChance = 0.5;
    @Comment("This is the chance that any given item a mob spawns with will have a modifier in Hard mode only")
    public double hardModeMobModifierChance = 0.75;
    @Comment("For any other method of item creation, this is the chance that a modifier will be applied to it")
    public double generalModifierChance = 0.75;
    @Comment("Enabling this will show the exact percentages that an item has been modified in its tooltip")
    public boolean showModifierPercentages = true;
    @Comment("4% is Terraria's base critical hit chance, however you can set this value to whatever you want")
    public double baseCritChance = 0.04;
    @Comment("Modifiers specified in the format of modifier.modifiername in this list will be disabled and no longer be attainable\nA few of the mod's extreme testing modifiers are included by default")
    public String[] disabledModifiers = new String[] {"modifier.zoomy", "modifier.gigantic", "modifier.ungodly"};
    public static ModifiersConfig getInstance() {
        return AutoConfig.getConfigHolder(ModifiersConfig.class).getConfig();
    }
    public static void save() {
        AutoConfig.getConfigHolder(ModifiersConfig.class).save();
    }
    public static void load() {
        AutoConfig.getConfigHolder(ModifiersConfig.class).load();
    }
}
