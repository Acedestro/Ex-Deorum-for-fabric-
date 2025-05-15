package net.fabricmc.exdeorum.block;

import net.fabricmc.exdeorum.block.entity.CrucibleBlockEntity;
import net.fabricmc.exdeorum.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

/**
 * The crucible block allows players to:
 * 1. Put materials in to melt into liquids (water for wooden, lava for porcelain)
 * 2. Extract fluids with buckets
 */
public class CrucibleBlock extends BlockWithEntity {
    public static final IntProperty LEVEL = IntProperty.of("level", 0, 4);
    
    protected static final VoxelShape INSIDE = Block.createCuboidShape(2, 4, 2, 14, 16, 14);
    protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(
            // Outside shell
            VoxelShapes.fullCube(),
            // Inside hollow area (carved out)
            INSIDE,
            // Combine by removing the inner area from the outer area
            VoxelShapes.BooleanBiFunction.ONLY_FIRST);
    
    private final boolean isPorcelain;
    
    public CrucibleBlock(Settings settings, boolean isPorcelain) {
        super(settings);
        this.isPorcelain = isPorcelain;
        setDefaultState(getStateManager().getDefaultState().with(LEVEL, 0));
    }
    
    public boolean isPorcelain() {
        return isPorcelain;
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CrucibleBlockEntity crucibleBE)) {
            return ActionResult.PASS;
        }
        
        ItemStack heldStack = player.getStackInHand(hand);
        
        // Handle fluids extraction with empty bucket
        if (heldStack.isOf(Items.BUCKET) && crucibleBE.getFluidAmount() >= 1000) {
            // Get the fluid
            if (crucibleBE.getFluid() == Fluids.WATER) {
                // Extract water from crucible
                crucibleBE.extractFluid(1000);
                
                // Give water bucket to player
                if (!player.isCreative()) {
                    heldStack.decrement(1);
                    player.giveItemStack(new ItemStack(Items.WATER_BUCKET));
                }
                
                updateLevel(world, pos, state, crucibleBE);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            } else if (crucibleBE.getFluid() == Fluids.LAVA) {
                // Extract lava from crucible (only for porcelain crucibles)
                if (isPorcelain) {
                    crucibleBE.extractFluid(1000);
                    
                    // Give lava bucket to player
                    if (!player.isCreative()) {
                        heldStack.decrement(1);
                        player.giveItemStack(new ItemStack(Items.LAVA_BUCKET));
                    }
                    
                    updateLevel(world, pos, state, crucibleBE);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            }
        }
        
        // Handle adding materials to melt
        if (!heldStack.isEmpty()) {
            // Get the melt value of the item (how much fluid it produces)
            int meltValue = getMeltValue(heldStack.getItem(), crucibleBE.isWooden());
            
            if (meltValue > 0) {
                // Try to add the solid material
                if (crucibleBE.addSolid(heldStack, meltValue)) {
                    updateLevel(world, pos, state, crucibleBE);
                    
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            }
        }
        
        return ActionResult.PASS;
    }
    
    /**
     * Update the block state's level property based on the amount of fluid/solid in the crucible
     */
    private void updateLevel(World world, BlockPos pos, BlockState state, CrucibleBlockEntity crucibleBE) {
        int total = crucibleBE.getSolidAmount() + crucibleBE.getFluidAmount();
        int level = 0;
        
        if (total > 0) {
            // Calculate level value (0-4) based on the amount of fluid/solid
            // With maximum being 1000, so level 1 = 1-250, level 2 = 251-500, etc.
            level = Math.min(4, (total / 250) + 1);
        }
        
        if (state.get(LEVEL) != level) {
            world.setBlockState(pos, state.with(LEVEL, level));
        }
    }
    
    /**
     * Get the melt value for an item (how much fluid it produces)
     * @param item The item to check
     * @param isWooden Whether this is a wooden crucible (only accepts certain items)
     * @return The melt value (0 if not meltable)
     */
    private int getMeltValue(net.minecraft.item.Item item, boolean isWooden) {
        // Wooden crucibles accept less items and produce water
        if (isWooden) {
            // For example, leaves (125), saplings (250), etc.
            if (item == Items.OAK_LEAVES || item == Items.SPRUCE_LEAVES || 
                item == Items.BIRCH_LEAVES || item == Items.JUNGLE_LEAVES || 
                item == Items.ACACIA_LEAVES || item == Items.DARK_OAK_LEAVES) {
                return 125;
            }
            
            if (item == Items.OAK_SAPLING || item == Items.SPRUCE_SAPLING || 
                item == Items.BIRCH_SAPLING || item == Items.JUNGLE_SAPLING || 
                item == Items.ACACIA_SAPLING || item == Items.DARK_OAK_SAPLING) {
                return 250;
            }
        } else {
            // Porcelain crucibles accept more items and produce lava
            // For example, cobblestone, netherrack, etc.
            if (item == Items.COBBLESTONE || item == Items.STONE) {
                return 250;
            }
            
            if (item == Items.NETHERRACK) {
                return 500;
            }
        }
        
        // Return 0 for items that can't be melted in this crucible
        return 0;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CRUCIBLE, CrucibleBlockEntity::tick);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}