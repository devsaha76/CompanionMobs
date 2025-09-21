package net.flame.companionmob.mixin;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void companionmob$damageEntities(CallbackInfo ci) {
        AreaEffectCloudEntity cloud = (AreaEffectCloudEntity) (Object) this;
        World world = cloud.getWorld();
        if (world.isClient) return;
        if (!ParticleTypes.DRAGON_BREATH.equals(cloud.getParticleType())) return;
        List<LivingEntity> list = world.getNonSpectatingEntities(
                LivingEntity.class,
                cloud.getBoundingBox()
        );
        for (LivingEntity living : list) {
            if (living == cloud.getOwner()) continue;
            living.damage(world.getDamageSources().magic(), 4.0F);
        }
    }
}