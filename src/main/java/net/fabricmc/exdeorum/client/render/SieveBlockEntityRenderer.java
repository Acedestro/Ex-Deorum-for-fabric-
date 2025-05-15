package net.fabricmc.exdeorum.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.exdeorum.block.entity.SieveBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Renderer for the sieve block entity
 * Shows the mesh and input material in different stages of sieving
 */
@Environment(EnvType.CLIENT)
public class SieveBlockEntityRenderer implements BlockEntityRenderer<SieveBlockEntity> {
    
    public SieveBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        // Constructor needed for renderer factory
    }
    
    @Override
    public void render(SieveBlockEntity entity, float tickDelta, MatrixStack matrices, 
                      VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Save matrix state
        matrices.push();
        
        // In the actual implementation, we would render:
        // 1. The mesh in the sieve
        // 2. The input material at different heights based on progress
        
        // Restore matrix state
        matrices.pop();
    }
}