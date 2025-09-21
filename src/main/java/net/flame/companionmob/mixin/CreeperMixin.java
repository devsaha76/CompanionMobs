package net.flame.companionmob.mixin;

import net.flame.companionmob.ai.CompanionAttackMarkedGoal;
import net.flame.companionmob.ai.CompanionFollowOwnerGoal;
import net.flame.companionmob.entity.Tameable;
import net.flame.companionmob.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(CreeperEntity.class)
public abstract class CreeperMixin extends HostileEntity implements Tameable {

    @Unique private boolean companionmob$tamed;
    @Unique private UUID companionmob$owner;

    protected CreeperMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isTamed() {
        return companionmob$tamed;
    }

    @Override
    public void setTamed(boolean tamed) {
        this.companionmob$tamed = tamed;
    }

    @Override
    public UUID getOwnerUuid() {
        return companionmob$owner;
    }

    @Override
    public void setOwnerUuid(UUID uuid) {
        this.companionmob$owner = uuid;
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void companionmob$addGoals(CallbackInfo ci) {
        this.goalSelector.add(5, new CompanionFollowOwnerGoal(
                (CreeperEntity)(Object)this, 1.0, 5.0f, 2.0f
        ));

        this.targetSelector.add(1, new ActiveTargetGoal<>(
                (CreeperEntity)(Object)this,
                PlayerEntity.class,
                10,
                true,
                false,
                (LivingEntity target) -> {
                    if (this.isTamed()) {
                        return !target.getUuid().equals(this.getOwnerUuid());
                    }
                    return true;
                }
        ));
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void companionmob$addMarkedAttackGoal(CallbackInfo ci) {
        this.targetSelector.add(2, new CompanionAttackMarkedGoal((PathAwareEntity) (Object)this));
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void companionmob$preventOwnerTarget(LivingEntity target, CallbackInfo ci) {
        if (isTamed() && target != null && target.getUuid().equals(getOwnerUuid())) {
            ci.cancel();
        }
    }

    @Inject(method = "ignite", at = @At("HEAD"), cancellable = true)
    private void companionmob$preventOwnerExplosion(CallbackInfo ci) {
        LivingEntity target = this.getTarget();
        if (isTamed() && target != null && target.getUuid().equals(getOwnerUuid())) {
            ci.cancel();
        }
    }

    @Inject(method = "explode", at = @At("TAIL"))
    private void companionmob$dropTotemExplode(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.isTamed()) {
            this.dropStack(new ItemStack(ModItems.CREEPER_TOTEM));
        }
    }



    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void companionmob$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("CompanionTamed", companionmob$tamed);
        if (companionmob$owner != null) {
            nbt.putUuid("CompanionOwner", companionmob$owner);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void companionmob$readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.companionmob$tamed = nbt.getBoolean("CompanionTamed");
        if (nbt.containsUuid("CompanionOwner")) {
            this.companionmob$owner = nbt.getUuid("CompanionOwner");
        }

        if (this.isTamed()) {
            this.targetSelector.getGoals().clear();
            this.goalSelector.add(5, new CompanionFollowOwnerGoal(
                    (CreeperEntity)(Object)this, 1.0, 5.0f, 2.0f
            ));

        }
    }
}
