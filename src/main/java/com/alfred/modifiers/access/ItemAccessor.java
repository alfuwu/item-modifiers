package com.alfred.modifiers.access;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor("ATTACK_DAMAGE_MODIFIER_ID")
    static UUID getAttackDamageModifierID() {
        return null;
    }

    @Accessor("ATTACK_SPEED_MODIFIER_ID")
    static UUID getAttackSpeedModifierID() {
        return null;
    }
}
