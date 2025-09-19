package net.flame.companionmob.mixin;

import net.flame.companionmob.entity.Tameable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityTameMixin implements Tameable {
    private static final String TAMED_KEY = "tamed";
    private boolean companionmob$tamed = false;

    @Override
    public boolean isTamed(){
        return companionmob$tamed;
    }

    @Override
    public void setTamed(boolean tamed) {
        this.companionmob$tamed = tamed;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void companionmob$saveData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(TAMED_KEY, companionmob$tamed);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void companionmob$loadData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(TAMED_KEY)) {
            this.companionmob$tamed = nbt.getBoolean(TAMED_KEY);
        }
    }

    @Inject(method = "handleStatus", at = @At("HEAD"))
    private void companionmob$handleStatus(byte status, CallbackInfo ci) {
        if (status == 7) {
            LivingEntity self = (LivingEntity)(Object)this;
            World world = self.getWorld();
            int heartCount = 12;
            double spread = 0.05D;
            if (world.isClient) {
                double dx = self.getRandom().nextGaussian() * 0.01D;
                double dy = self.getRandom().nextGaussian() * 0.01D;
                double dz = self.getRandom().nextGaussian() * 0.01D;
                world.addParticle(
                        ParticleTypes.HEART,
                        self.getParticleX(1.0D),
                        self.getRandomBodyY() + 0.5D,
                        self.getParticleZ(1.0D),
                        dx, dy, dz
                );
            }
        }
    }
}
