package net.flame.companionmob.mixin;

import net.flame.companionmob.effect.ModEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class IgnoreMixin {
    @Inject(method = "canTarget", at = @At("HEAD"), cancellable = true)
    private void companionmob_ignore(EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        // check only if target is a player entity type
        if (type == EntityType.PLAYER) {
            MobEntity mob = (MobEntity) (Object) this;

            PlayerEntity nearest = mob.getWorld().getClosestPlayer(mob, 32.0);
            if (nearest != null && nearest.hasStatusEffect(ModEffects.MOB_IGNORE)) {
                cir.setReturnValue(false);
            }
        }
    }
}
