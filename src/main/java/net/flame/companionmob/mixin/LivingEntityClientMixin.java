package net.flame.companionmob.mixin;

import net.flame.companionmob.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityClientMixin {
    @Inject(method = "handleStatus", at = @At("HEAD"), cancellable = true)
    private void companionmob$handleCustomTotem(byte status, CallbackInfo ci) {
        if (status == 36) {
            MinecraftClient.getInstance().gameRenderer.showFloatingItem(
                    new ItemStack(ModItems.CREEPER_TOTEM)
            );
            ci.cancel();
        }
    }

}
