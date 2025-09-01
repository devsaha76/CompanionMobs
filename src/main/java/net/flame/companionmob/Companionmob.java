package net.flame.companionmob;

import net.fabricmc.api.ModInitializer;
import net.flame.companionmob.effect.ModEffects;
import net.flame.companionmob.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Companionmob implements ModInitializer {

    public static final String MOD_ID = "companionmob";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEffects.registerEffects();
        ModItems.registerModItems();
    }
}
