package net.flame.companionmob.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.flame.companionmob.Companionmob;
import net.flame.companionmob.effect.ModEffects;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item PRECIOUS_COOKIES = registerItem("precious_cookies", new Item(new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.8F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 20 * 60, 1), 1.0F).statusEffect(new StatusEffectInstance(ModEffects.MOB_IGNORE, 20 * 60, 0), 1.0F).build())
    ));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Companionmob.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Companionmob.LOGGER.info("Registering Mod Items for " + Companionmob.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(PRECIOUS_COOKIES);
        });
    }
}
