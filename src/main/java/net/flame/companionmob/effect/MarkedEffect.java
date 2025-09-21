package net.flame.companionmob.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class MarkedEffect extends StatusEffect {
    public MarkedEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF0000);
    }
}

