package net.fabricmc.exdeorum;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.exdeorum.registry.ModBlockEntityRenderers;
import net.fabricmc.exdeorum.registry.ModScreenHandlers;

/**
 * Client-side initialization for Ex Deorum
 */
@Environment(EnvType.CLIENT)
public class ExDeorumClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        ExDeorum.LOGGER.info("Initializing Ex Deorum client");
        
        // Register client-side components
        ModBlockEntityRenderers.register();
        ModScreenHandlers.register();
        
        ExDeorum.LOGGER.info("Ex Deorum client initialized");
    }
}