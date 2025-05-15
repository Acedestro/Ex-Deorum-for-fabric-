package net.fabricmc.exdeorum.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * The hammer is used to crush blocks like cobblestone into gravel,
 * gravel into sand, etc.
 */
public class HammerItem extends MiningToolItem {
    
    public HammerItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(attackDamage, attackSpeed, material, BlockTags.PICKAXE_MINEABLE, settings);
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        // Don't process on client side
        if (world.isClient) {
            return super.postMine(stack, world, state, pos, miner);
        }
        
        // Process hammer crushing logic
        if (isSuitableFor(state)) {
            // Check if we can crush this block
            BlockState crushedState = getCrushedState(state);
            if (crushedState != null) {
                // Set the new block state
                world.setBlockState(pos, crushedState);
                
                // Damage the tool
                stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                
                return true;
            }
        }
        
        return super.postMine(stack, world, state, pos, miner);
    }
    
    /**
     * Check if this hammer can be used on a specific block state
     */
    @Override
    public boolean isSuitableFor(BlockState state) {
        // The hammer can crush specific blocks
        Block block = state.getBlock();
        return block == Blocks.COBBLESTONE ||
               block == Blocks.GRAVEL ||
               block == Blocks.SAND ||
               block == Blocks.NETHERRACK ||
               super.isSuitableFor(state);
    }
    
    /**
     * Get the block state that results from crushing a given block state
     * @return The resulting block state, or null if the block can't be crushed
     */
    private BlockState getCrushedState(BlockState state) {
        Block block = state.getBlock();
        
        if (block == Blocks.COBBLESTONE) {
            return Blocks.GRAVEL.getDefaultState();
        } else if (block == Blocks.GRAVEL) {
            return Blocks.SAND.getDefaultState();
        } else if (block == Blocks.SAND) {
            return Blocks.DIRT.getDefaultState();
        } else if (block == Blocks.NETHERRACK) {
            return Blocks.SOUL_SAND.getDefaultState();
        }
        
        return null;
    }
    
    /**
     * Calculate mining speed based on block being mined
     */
    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        // If this is a block we can crush, use a higher mining speed
        if (getCrushedState(state) != null) {
            return miningSpeed * 1.5f;
        }
        
        return super.getMiningSpeedMultiplier(stack, state);
    }
}