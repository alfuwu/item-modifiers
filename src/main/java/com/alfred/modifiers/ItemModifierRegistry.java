package com.alfred.modifiers;

import com.alfred.modifiers.modifiers.common.*;
import com.alfred.modifiers.modifiers.common.DeadlyModifier;
import com.alfred.modifiers.modifiers.melee.*;
import com.alfred.modifiers.modifiers.ranged.*;
import com.alfred.modifiers.modifiers.universal.*;
import com.alfred.modifiers.Constants.ModifierType;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemModifierRegistry {
    private static final List<ItemModifier> modifiers = new ArrayList<>();
    public static boolean isRegistered = false;

    public static ItemModifier getModifier(int index) {
        return modifiers.get(index);
    }
    public static List<ItemModifier> getModifiers() {
        return modifiers;
    }
    public static void registerModifier(ItemModifier modifier) {
        modifiers.add(modifier);
    }

    public static ItemModifier getRandomModifier() {
        if (!isRegistered)
            registerModifiers();
        return getRandomModifier(ModifierType.ITEM);
    }
    public static ItemModifier getRandomModifier(ItemStack itemStack) {
        if (!isRegistered)
            registerModifiers();
        if (modifiers.isEmpty())
            return null;
        ItemModifier[] array = modifiers.stream().filter(modifier -> modifier.canApplyModifier(itemStack)).toArray(ItemModifier[]::new);
        return array.length == 0 ? null : array[new Random().nextInt(array.length)];
    }
    public static ItemModifier getRandomModifier(ModifierType type) {
        if (!isRegistered)
            registerModifiers();
        if (modifiers.isEmpty())
            return null;
        return modifiers.get(new Random().nextInt(modifiers.size()));
    }

    public static void registerModifiers() {
        isRegistered = true;
        // Universal modifiers
        registerModifier(new KeenModifier());
        registerModifier(new SuperiorModifier());
        registerModifier(new ForcefulModifier());
        registerModifier(new BrokenModifier());
        registerModifier(new DamagedModifier());
        registerModifier(new ShoddyModifier());
        registerModifier(new HurtfulModifier());
        registerModifier(new StrongModifier());
        registerModifier(new UnpleasantModifier());
        registerModifier(new WeakModifier());
        registerModifier(new RuthlessModifier());
        registerModifier(new GodlyModifier());
        registerModifier(new DemonicModifier());
        registerModifier(new ZealousModifier());

        // Common modifiers (swords, ranged, magic, summoning)
        registerModifier(new QuickModifier());
        registerModifier(new DeadlyModifier());
        registerModifier(new AgileModifier());
        registerModifier(new NimbleModifier());
        registerModifier(new MurderousModifier());
        registerModifier(new SlowModifier());
        registerModifier(new SluggishModifier());
        registerModifier(new LazyModifier());
        registerModifier(new AnnoyingModifier());
        registerModifier(new NastyModifier());

        // Melee modifiers (swords, axes, pickaxes, shovels)
        registerModifier(new LargeModifier());
        registerModifier(new MassiveModifier());
        registerModifier(new DangerousModifier());
        registerModifier(new SavageModifier());
        registerModifier(new SharpModifier());
        registerModifier(new PointyModifier());
        registerModifier(new TinyModifier());
        registerModifier(new TerribleModifier());
        registerModifier(new SmallModifier());
        registerModifier(new DullModifier());
        registerModifier(new UnhappyModifier());
        registerModifier(new BulkyModifier());
        registerModifier(new ShamefulModifier());
        registerModifier(new HeavyModifier());
        registerModifier(new LightModifier());
        registerModifier(new LegendaryModifier());

        // Ranged modifiers (bows, crossbows)
        registerModifier(new SightedModifier());
        registerModifier(new RapidModifier());
        registerModifier(new HastyModifier());
        registerModifier(new IntimidatingModifier());
        registerModifier(new RangedDeadlyModifier());
        registerModifier(new StaunchModifier());
        registerModifier(new AwfulModifier());
        registerModifier(new LethargicModifier());
        registerModifier(new AwkwardModifier());
        registerModifier(new PowerfulModifier());
        registerModifier(new FrenzyingModifier());
        registerModifier(new UnrealModifier());

        // Testing modifiers
        registerModifier(new GiganticModifier());
        registerModifier(new UngodlyModifier());
        registerModifier(new ZoomyModifier());

        // Magic & summoning
        //registerModifier(new MysticModifier());
        //registerModifier(new AdeptModifier());
        //registerModifier(new MasterfulModifier());
        //registerModifier(new IneptModifier());
        //registerModifier(new IgnorantModifier());
        //registerModifier(new DerangedModifier());
        //registerModifier(new IntenseModifier());
        //registerModifier(new TabooModifier());
        //registerModifier(new CelestialModifier());
        //registerModifier(new FuriousModifier());
        //registerModifier(new ManicModifier());
        //registerModifier(new MythicalModifier());
    }
}