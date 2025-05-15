package net.fabricmc.exdeorum;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.exdeorum.registry.ModBlockEntities;
import net.fabricmc.exdeorum.registry.ModBlocks;
import net.fabricmc.exdeorum.registry.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod class for Ex Deorum
 */
public class ExDeorum implements ModInitializer {
    /**
     * This is the mod's ID that is used for identification in various places
     */
    public static final String MOD_ID = "exdeorum";
    
    /**
     * This logger is used to write text to the console and the log file.
     * It is considered best practice to use your mod id as the logger's name.
     * That way, it's clear which mod wrote info, warnings, and errors.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    /**
     * This code runs as soon as Minecraft is in a mod-load-ready state.
     * However, some things (like resources) may still be uninitialized.
     * This is the place to register things like block, items, etc.
     */
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Ex Deorum");
        
        // Register the mod's items and blocks
        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        
        LOGGER.info("Ex Deorum initialized");
    }
}