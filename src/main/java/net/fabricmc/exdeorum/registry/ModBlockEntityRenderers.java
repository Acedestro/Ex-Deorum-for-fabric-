package net.fabricmc.exdeorum.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.exdeorum.ExDeorum;
import net.fabricmc.exdeorum.client.render.BarrelBlockEntityRenderer;
import net.fabricmc.exdeorum.client.render.CrucibleBlockEntityRenderer;
import net.fabricmc.exdeorum.client.render.SieveBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

/**
 * Register all block entity renderers for the client-side
 */
@Environment(EnvType.CLIENT)
public class ModBlockEntityRenderers {
    
    public static void register() {
        // Register barrel renderer
        BlockEntityRendererFactories.register(ModBlockEntities.BARREL, BarrelBlockEntityRenderer::new);
        
        // Register sieve renderer
        BlockEntityRendererFactories.register(ModBlockEntities.SIEVE, SieveBlockEntityRenderer::new);
        
        // Register crucible renderer
        BlockEntityRendererFactories.register(ModBlockEntities.CRUCIBLE, CrucibleBlockEntityRenderer::new);
        
        ExDeorum.LOGGER.info("Registered block entity renderers");
    }
}