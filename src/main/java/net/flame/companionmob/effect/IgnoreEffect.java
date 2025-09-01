package net.flame.companionmob.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;

public class IgnoreEffect extends StatusEffect {
    public IgnoreEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!(entity instanceof net.minecraft.entity.player.PlayerEntity player)) return true;

        for (var mob : player.getWorld().getEntitiesByClass(HostileEntity.class,
                player.getBoundingBox().expand(16), e -> true)) {

            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }

            mob.setAttacking(false);
            mob.setAttacker(null);
        }


        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        if (!(entity instanceof PlayerEntity player)) return;

        for (HostileEntity mob : player.getWorld().getEntitiesByClass(
                HostileEntity.class, player.getBoundingBox().expand(24.0), e -> true)) {

            mob.setTarget(null);
            mob.setAttacking(false);
            mob.setAttacker(null);
            mob.getNavigation().stop();

            if (mob.canTarget(player)) {
                mob.setTarget(player);
            }
        }
    }
}
