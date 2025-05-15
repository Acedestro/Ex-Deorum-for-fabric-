package net.fabricmc.exdeorum.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.exdeorum.block.entity.BarrelBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Renderer for the barrel block entity
 * Shows water level, compost level, and dirt when appropriate
 */
@Environment(EnvType.CLIENT)
public class BarrelBlockEntityRenderer implements BlockEntityRenderer<BarrelBlockEntity> {
    
    public BarrelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        // Constructor needed for renderer factory
    }
    
    @Override
    public void render(BarrelBlockEntity entity, float tickDelta, MatrixStack matrices, 
                      VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Save matrix state
        matrices.push();
        
        // In the actual implementation, we would render:
        // 1. Water level/fluid when in FLUID mode
        // 2. Compost level when in COMPOST mode
        // 3. Dirt block when in DIRT mode
        
        // Restore matrix state
        matrices.pop();
    }
}