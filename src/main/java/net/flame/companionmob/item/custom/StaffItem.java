package net.flame.companionmob.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


import java.util.List;

public class StaffItem extends Item {
    private static final int PRIMARY_COOLDOWN = 40;
    private static final int SPECIAL_COOLDOWN = 300;

    public StaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.companionmob.necromancer_staff.shift_down1"));
            tooltip.add(Text.translatable("tooltip.companionmob.necromancer_staff.shift_down2"));
            tooltip.add(Text.translatable("tooltip.companionmob.necromancer_staff.shift_down3"));
        } else {
            tooltip.add(Text.translatable("tooltip.companionmob.necromancer_staff"));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            if (user.getItemCooldownManager().isCoolingDown(this)) {
               return TypedActionResult.fail(stack);
            }

            if (user.isSneaking()) {
                if (!world.isClient && world instanceof ServerWorld sw) {
                    Vec3d start = user.getEyePos();
                    Vec3d look = user.getRotationVec(1.0F);
                    Vec3d end = start.add(look.multiply(30.0));

                    spawnBeam(sw, start, end);

                    EntityHitResult ehr = ProjectileUtil.raycast(
                            user,
                            start,
                            end,
                            user.getBoundingBox().expand(30.0),
                            e -> e instanceof LivingEntity && e != user,
                            30 * 30
                    );

                    if (ehr != null && ehr.getEntity() instanceof LivingEntity living) {
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
                    }
                }
                user.getItemCooldownManager().set(this, SPECIAL_COOLDOWN);
                user.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                user.setSneaking(false);
                return TypedActionResult.success(stack, world.isClient);
            }
            else {
                if (!world.isClient && world instanceof ServerWorld sw) {
                    Vec3d start = user.getEyePos();
                    Vec3d look = user.getRotationVec(1.0F);
                    DragonFireballEntity fireball = new DragonFireballEntity(EntityType.DRAGON_FIREBALL, world);
                    fireball.refreshPositionAndAngles(
                            start.x + look.x * 2, start.y + look.y * 2, start.z + look.z * 2,
                            user.getYaw(),
                            user.getPitch()
                    );
                    fireball.setOwner(user);
                    fireball.setVelocity(look.x, look.y, look.z, 1.5f, 0.0f);

                    world.spawnEntity(fireball);
                    world.playSound(
                            null,
                            user.getX(), user.getY(), user.getZ(),
                            SoundEvents.ENTITY_ENDER_DRAGON_SHOOT,
                            user.getSoundCategory(),
                            1.0f,
                            1.0f
                    );
                }
                user.getItemCooldownManager().set(this, PRIMARY_COOLDOWN);
                return TypedActionResult.success(stack, world.isClient);
            }
        }
        return TypedActionResult.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && selected) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 4, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 4, true, false, false));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 40;
    }

    private void spawnBeam(ServerWorld world, Vec3d start, Vec3d end) {
        double distance = start.distanceTo(end);
        if (distance <= 0.0001) return;
        Vec3d dir = end.subtract(start).normalize();
        int steps = (int) Math.max(4, distance * 8.0);

        for (int i = 0; i <= steps; i++) {
            Vec3d pos = start.add(dir.multiply(distance * ((double)i / (double)steps)));
            world.spawnParticles(ParticleTypes.END_ROD,
                    pos.x, pos.y, pos.z,
                    2, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
