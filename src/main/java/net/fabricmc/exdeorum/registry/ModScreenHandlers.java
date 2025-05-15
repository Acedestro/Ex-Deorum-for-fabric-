package net.fabricmc.exdeorum.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.exdeorum.ExDeorum;

/**
 * Register all screen handlers for the client-side
 */
@Environment(EnvType.CLIENT)
public class ModScreenHandlers {
    
    public static void register() {
        // This will be used in the future for interfaces like the sieve's output inventory
        ExDeorum.LOGGER.info("Registered screen handlers");
    }
}