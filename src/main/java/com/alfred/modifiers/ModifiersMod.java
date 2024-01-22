package com.alfred.modifiers;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ModifiersMod implements ModInitializer {
	@Override
	public void onInitialize() {
		AutoConfig.register(ModifiersConfig.class, JanksonConfigSerializer::new);
	}
}