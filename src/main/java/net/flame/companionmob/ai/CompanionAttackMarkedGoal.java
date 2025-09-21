package net.flame.companionmob.ai;

import net.flame.companionmob.effect.ModEffects;
import net.flame.companionmob.entity.Tameable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.EnumSet;

public class CompanionAttackMarkedGoal extends Goal {
    private final PathAwareEntity mob;
    private LivingEntity target;

    public CompanionAttackMarkedGoal(PathAwareEntity mob) {
        super();
        this.mob = mob;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!(this.mob instanceof Tameable) || !((Tameable)this.mob).isTamed()) {
            return false;
        }

        this.target = this.mob.getWorld()
                .getEntitiesByClass(LivingEntity.class, this.mob.getBoundingBox().expand(16.0D),
                        e -> e.hasStatusEffect(ModEffects.MARKED) && e.isAlive() && e != mob)
                .stream()
                .findFirst()
                .orElse(null);
        return this.target != null;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }
}
