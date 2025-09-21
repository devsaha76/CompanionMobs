package net.flame.companionmob.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.flame.companionmob.Companionmob;
import net.flame.companionmob.effect.ModEffects;
import net.flame.companionmob.item.custom.CreeperTotemItem;
import net.flame.companionmob.item.custom.StaffItem;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item PRECIOUS_COOKIES = registerItem("precious_cookies", new PreciousCookieItem(new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.8F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * 60, 1), 1.0F).statusEffect(new StatusEffectInstance(ModEffects.MOB_IGNORE, 20 * 60, 0), 1.0F).build())
    ));

    public static final Item NECROMANCER_STAFF = registerItem("necromancer_staff",
            new StaffItem(new Item.Settings().maxCount(1)));

    public static final Item CREEPER_TOTEM = registerItem("creeper_totem",
            new CreeperTotemItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Companionmob.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Companionmob.LOGGER.info("Registering Mod Items for " + Companionmob.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(PRECIOUS_COOKIES);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(NECROMANCER_STAFF);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(CREEPER_TOTEM);
        });
    }
}
