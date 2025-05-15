package net.fabricmc.exdeorum.block;

import net.fabricmc.exdeorum.block.entity.SieveBlockEntity;
import net.fabricmc.exdeorum.registry.ModBlockEntities;
import net.fabricmc.exdeorum.registry.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The sieve block allows players to:
 * 1. Place a mesh in the sieve
 * 2. Add materials to sift through the mesh
 * 3. Click repeatedly to sift and get resources
 */
public class SieveBlock extends BlockWithEntity {
    public static final IntProperty PROGRESS = IntProperty.of("progress", 0, 7);
    
    protected static final VoxelShape LEG_SHAPE = Block.createCuboidShape(0, 0, 0, 2, 12, 2);
    protected static final VoxelShape SIEVE_SHAPE = VoxelShapes.union(
        // Legs
        LEG_SHAPE,
        Block.createCuboidShape(14, 0, 0, 16, 12, 2),
        Block.createCuboidShape(0, 0, 14, 2, 12, 16),
        Block.createCuboidShape(14, 0, 14, 16, 12, 16),
        
        // Frame
        Block.createCuboidShape(0, 8, 0, 16, 12, 16),
        
        // Mesh area (slightly lowered)
        Block.createCuboidShape(1, 8, 1, 15, 9, 15)
    );
    
    public SieveBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(PROGRESS, 0));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PROGRESS);
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SIEVE_SHAPE;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof SieveBlockEntity sieveBE)) {
            return ActionResult.PASS;
        }
        
        ItemStack heldStack = player.getStackInHand(hand);
        
        // If the sieve doesn't have a mesh yet
        if (!sieveBE.hasMesh()) {
            if (isMesh(heldStack.getItem())) {
                // Insert the mesh
                ItemStack meshStack = heldStack.copy();
                meshStack.setCount(1);
                sieveBE.setMeshStack(meshStack);
                
                if (!player.isCreative()) {
                    heldStack.decrement(1);
                }
                
                world.playSound(null, pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.8f, 1.0f);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        
        // If the sieve has a mesh but no input material
        if (!sieveBE.hasInput()) {
            if (isSiftable(heldStack.getItem())) {
                // Insert the input material
                ItemStack inputStack = heldStack.copy();
                inputStack.setCount(1);
                sieveBE.setInputStack(inputStack);
                
                if (!player.isCreative()) {
                    heldStack.decrement(1);
                }
                
                world.setBlockState(pos, state.with(PROGRESS, 0));
                world.playSound(null, pos, SoundEvents.BLOCK_GRAVEL_PLACE, SoundCategory.BLOCKS, 0.5f, 1.0f);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        
        // If sieve has mesh and input, perform a sieving action (regardless of held item)
        boolean complete = sieveBE.sieveOnce();
        int progress = sieveBE.getProgress();
        
        world.setBlockState(pos, state.with(PROGRESS, progress));
        world.playSound(null, pos, SoundEvents.BLOCK_SAND_BREAK, SoundCategory.BLOCKS, 0.3f, 0.6f + (progress / 7.0f) * 0.4f);
        
        if (complete) {
            // Spawn any result items in the world
            List<ItemStack> drops = sieveBE.getDrops();
            for (ItemStack drop : drops) {
                if (!drop.isEmpty()) {
                    player.giveItemStack(drop);
                }
            }
            
            sieveBE.clear();
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.SUCCESS;
    }
    
    /**
     * Check if an item is a valid mesh for the sieve
     */
    private boolean isMesh(Item item) {
        return item == ModItems.STRING_MESH || 
               item == ModItems.FLINT_MESH ||
               item == ModItems.IRON_MESH ||
               item == ModItems.GOLDEN_MESH ||
               item == ModItems.DIAMOND_MESH ||
               item == ModItems.NETHERITE_MESH;
    }
    
    /**
     * Check if an item can be sifted in the sieve
     */
    private boolean isSiftable(Item item) {
        // For now, we'll use some simple checks
        // In a full implementation, this would be recipe-based
        BlockState blockState = Block.getBlockFromItem(item).getDefaultState();
        return blockState.isIn(BlockTags.DIRT) || 
               blockState.isIn(BlockTags.SAND) ||
               blockState.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS) ||
               blockState.isOf(Blocks.GRAVEL) ||
               blockState.isOf(Blocks.CLAY);
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SieveBlockEntity sieveBE) {
                // Drop all contained items
                List<ItemStack> drops = sieveBE.getDrops();
                for (ItemStack drop : drops) {
                    Block.dropStack(world, pos, drop);
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SieveBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}