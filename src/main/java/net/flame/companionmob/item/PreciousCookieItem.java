package net.flame.companionmob.item;

import net.flame.companionmob.entity.Tameable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class PreciousCookieItem extends Item {
    public PreciousCookieItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, net.minecraft.entity.LivingEntity target, Hand hand) {
        if (target.getType() == EntityType.CREEPER || target.getType() == EntityType.ZOMBIE) {
            if (!player.getWorld().isClient) {
                if (target instanceof Tameable tameable) {
                    if (!tameable.isTamed()) {
                        if (player.getRandom().nextInt(3) == 0) {
                            tameable.setTamed(true);
                            tameable.setOwnerUuid(player.getUuid());
                            player.sendMessage(Text.literal("Successfully tamed a " + target.getName().getString() + "!"), true);
                            target.getWorld().sendEntityStatus(target, (byte)7);
                        } else {
                            target.getWorld().sendEntityStatus(target, (byte)6);
                        }
                    } else {
                        target.heal(4.0F);
                    }

                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                }

            }
            return ActionResult.SUCCESS;
        }
        return super.useOnEntity(stack, player, target, hand);
    }
}
