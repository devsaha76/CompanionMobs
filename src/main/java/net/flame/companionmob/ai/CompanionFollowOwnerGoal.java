package net.flame.companionmob.ai;

import net.flame.companionmob.entity.Tameable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.EnumSet;
import java.util.UUID;

public class CompanionFollowOwnerGoal extends Goal {
    private final MobEntity mob;
    private final double speed;
    private final float startDistance;
    private final float stopDistance;
    private LivingEntity owner;

    public CompanionFollowOwnerGoal(MobEntity mob, double speed, float startDistance, float stopDistance) {
        this.mob = mob;
        this.speed = speed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    private LivingEntity resolveOwner() {
        if (!(mob instanceof Tameable tame)) return null;
        UUID id = tame.getOwnerUuid();
        if (id == null) return null;
        if (mob.getWorld() instanceof ServerWorld sw) {
            var e = sw.getEntity(id);
            return e instanceof LivingEntity l ? l : null;
        }
        return null;
    }

    @Override
    public boolean canStart() {
        if (!(mob instanceof Tameable tame) || !tame.isTamed()) return false;
        owner = resolveOwner();
        return owner != null && mob.squaredDistanceTo(owner) > (double)(startDistance * startDistance);
    }

    @Override
    public boolean shouldContinue() {
        return owner != null && mob.squaredDistanceTo(owner) > (double)(stopDistance * stopDistance);
    }

    @Override
    public void stop() {
        owner = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (owner == null) return;
        mob.getLookControl().lookAt(owner, 10.0F, mob.getMaxLookPitchChange());
        mob.getNavigation().startMovingTo(owner, speed);
    }
}
