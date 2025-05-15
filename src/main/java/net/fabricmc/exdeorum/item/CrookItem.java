package net.fabricmc.exdeorum.item;

import net.fabricmc.exdeorum.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * The crook is used to harvest leaves more efficiently and
 * has a chance to drop silkworms.
 */
public class CrookItem extends SwordItem {
    
    private static final Random RANDOM = new Random();
    
    public CrookItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        // Don't process on client side
        if (world.isClient) {
            return super.postMine(stack, world, state, pos, miner);
        }
        
        // Process special crook actions for leaves
        if (state.isIn(BlockTags.LEAVES)) {
            // Damage the tool
            stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            
            // Have a chance to drop silkworms from leaves
            if (RANDOM.nextFloat() < 0.05f) { // 5% chance
                // Spawn silkworm item
                world.spawnEntity(new ItemEntity(
                    world,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    new ItemStack(ModItems.SILKWORM)
                ));
            }
        }
        
        return super.postMine(stack, world, state, pos, miner);
    }
    
    /**
     * Check if this crook is effective for a specific block state
     */
    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        // Crooks are especially good at breaking leaves
        if (state.isIn(BlockTags.LEAVES)) {
            return 3.0f;
        }
        
        return super.getMiningSpeedMultiplier(stack, state);
    }
    
    /**
     * Increase drop chances when using a crook
     */
    public static float getMultipliedDropChance(BlockState state) {
        if (state.isIn(BlockTags.LEAVES)) {
            // Triple the drop chance for leaves
            return 3.0f;
        }
        
        return 1.0f;
    }
}