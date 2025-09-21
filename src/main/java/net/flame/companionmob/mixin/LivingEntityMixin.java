package net.flame.companionmob.mixin;

import net.flame.companionmob.item.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void companionmob$tryUseCustomTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        ItemStack itemStack = null;
        for (Hand hand : Hand.values()) {
            ItemStack stack = self.getStackInHand(hand);
            if (stack.isOf(Items.TOTEM_OF_UNDYING) || stack.isOf(ModItems.CREEPER_TOTEM)) {
                itemStack = stack.copy();
                stack.decrement(1);
                break;
            }
        }

        if (itemStack != null) {
            if (self instanceof ServerPlayerEntity serverPlayerEntity) {
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
                self.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }

            self.setHealth(1.0F);
            self.clearStatusEffects();
            self.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            self.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            self.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));

            if (itemStack.isOf(ModItems.CREEPER_TOTEM)) {
                self.getWorld().sendEntityStatus(self, (byte)36);
            } else {
                self.getWorld().sendEntityStatus(self, (byte)35);
            }

            cir.setReturnValue(true);
        }
    }
}
