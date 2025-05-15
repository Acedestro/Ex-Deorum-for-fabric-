package net.fabricmc.exdeorum.block;

import net.fabricmc.exdeorum.block.entity.BarrelBlockEntity;
import net.fabricmc.exdeorum.registry.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
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
 * The barrel block allows players to:
 * 1. Add water with buckets or bottles
 * 2. Create dirt through composting organic materials
 */
public class BarrelBlock extends BlockWithEntity {
    public static final EnumProperty<BarrelBlockEntity.BarrelMode> MODE = EnumProperty.of(
            "mode", BarrelBlockEntity.BarrelMode.class);
    
    protected static final VoxelShape SHAPE = VoxelShapes.union(
        // Bottom
        Block.createCuboidShape(0, 0, 0, 16, 1, 16),
        // Sides
        Block.createCuboidShape(0, 1, 0, 1, 16, 16),
        Block.createCuboidShape(15, 1, 0, 16, 16, 16),
        Block.createCuboidShape(1, 1, 0, 15, 16, 1),
        Block.createCuboidShape(1, 1, 15, 15, 16, 16)
    );
    
    public BarrelBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(MODE, BarrelBlockEntity.BarrelMode.EMPTY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE);
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

        ItemStack heldStack = player.getStackInHand(hand);
        BlockEntity be = world.getBlockEntity(pos);
        
        if (!(be instanceof BarrelBlockEntity barrelBE)) {
            return ActionResult.PASS;
        }
        
        // Handle empty hand - extract items if needed
        if (heldStack.isEmpty()) {
            if (barrelBE.getMode() == BarrelBlockEntity.BarrelMode.DIRT) {
                // Give the player dirt
                player.giveItemStack(new ItemStack(Items.DIRT));
                
                // Reset the barrel
                barrelBE.clear();
                world.setBlockState(pos, state.with(MODE, BarrelBlockEntity.BarrelMode.EMPTY));
                
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        
        // Handle buckets (for adding/removing water)
        if (heldStack.getItem() instanceof BucketItem) {
            Fluid bucketFluid = ((BucketItem) heldStack.getItem()).getFluid();
            
            // Adding water to empty barrel
            if (bucketFluid != Fluids.EMPTY && barrelBE.getMode() == BarrelBlockEntity.BarrelMode.EMPTY) {
                if (barrelBE.addFluid(bucketFluid, 1000)) {
                    if (!player.isCreative()) {
                        player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    world.setBlockState(pos, state.with(MODE, BarrelBlockEntity.BarrelMode.FLUID));
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            }
            
            // Getting water from barrel
            if (bucketFluid == Fluids.EMPTY && barrelBE.getMode() == BarrelBlockEntity.BarrelMode.FLUID &&
                    barrelBE.getFluidAmount() >= 1000) {
                Fluid fluid = barrelBE.getFluid();
                if (fluid == Fluids.WATER) {
                    barrelBE.extractFluid(1000);
                    if (barrelBE.getFluidAmount() <= 0) {
                        world.setBlockState(pos, state.with(MODE, BarrelBlockEntity.BarrelMode.EMPTY));
                    }
                    
                    if (!player.isCreative()) {
                        player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
                    }
                    
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            }
        }
        
        // Handle compostable items
        if (barrelBE.getMode() == BarrelBlockEntity.BarrelMode.EMPTY || 
                barrelBE.getMode() == BarrelBlockEntity.BarrelMode.COMPOST) {
            
            // Check if item is compostable
            int compostValue = getCompostValue(heldStack);
            if (compostValue > 0) {
                if (barrelBE.addCompostItem(heldStack.copy(), compostValue)) {
                    if (!player.isCreative()) {
                        heldStack.decrement(1);
                    }
                    world.setBlockState(pos, state.with(MODE, BarrelBlockEntity.BarrelMode.COMPOST));
                    return ActionResult.SUCCESS;
                }
            }
        }
        
        return ActionResult.PASS;
    }

    /**
     * Get the compost value for an item
     * @param stack The item to check
     * @return The compost value (0 if not compostable)
     */
    private int getCompostValue(ItemStack stack) {
        // For simplicity, we're using some common tags to determine compostable items
        // In a full implementation, this would be more comprehensive
        if (stack.isIn(ItemTags.LEAVES)) {
            return 100;
        } else if (stack.isIn(ItemTags.SAPLINGS)) {
            return 125;
        } else if (stack.isOf(Items.WHEAT) || stack.isOf(Items.SUGAR_CANE)) {
            return 75;
        } else if (stack.isOf(Items.APPLE) || stack.isOf(Items.CARROT) || stack.isOf(Items.POTATO)) {
            return 150;
        }
        
        // In a real implementation, this would have many more items
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BarrelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.BARREL, BarrelBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}