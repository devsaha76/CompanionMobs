package net.flame.companionmob.mixin;

import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireballEntity.class)
public abstract class DragonFireballMixin {
    @Inject(
            method = "onCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void companionmob$replaceEffect(HitResult hitResult, CallbackInfo ci) {
        DragonFireballEntity self = (DragonFireballEntity) (Object) this;

        self.getWorld().getEntitiesByClass(
                AreaEffectCloudEntity.class,
                self.getBoundingBox().expand(2.0D),
                cloud -> cloud.getOwner() == self.getOwner()
        ).forEach(cloud -> {
            cloud.setPotionContents(PotionContentsComponent.DEFAULT);
            cloud.setPotionContents(
                    PotionContentsComponent.DEFAULT.with(
                            new StatusEffectInstance(StatusEffects.WITHER, 200, 1, true, false, false)
                    )
            );
            cloud.setDuration(300);
            cloud.setRadiusGrowth(-cloud.getRadius() / (float) cloud.getDuration());
        });
    }
}
