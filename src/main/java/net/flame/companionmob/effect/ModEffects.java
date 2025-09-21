package net.flame.companionmob.effect;

import net.flame.companionmob.Companionmob;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> MOB_IGNORE = registerStatusEffect("ignore_effect",
            new IgnoreEffect(StatusEffectCategory.BENEFICIAL, 0xFFD700));

    public static final RegistryEntry<StatusEffect> MARKED = registerStatusEffect("marked",
            new MarkedEffect());

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Companionmob.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {
        Companionmob.LOGGER.info("Registering Mod Effects for " + Companionmob.MOD_ID);
    }
}
