package net.fabricmc.exdeorum.block.entity;

import net.fabricmc.exdeorum.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrucibleBlockEntity extends BlockEntity {
    private static final String SOLID_AMOUNT_KEY = "SolidAmount";
    private static final String FLUID_AMOUNT_KEY = "FluidAmount";
    private static final String FLUID_KEY = "Fluid";
    private static final String PROCESSING_TIME_KEY = "ProcessingTime";

    private static final int MAX_SOLID_AMOUNT = 1000;
    private static final int MAX_FLUID_AMOUNT = 1000;
    private static final int BASE_PROCESSING_TIME = 200; // 10 seconds at 20 ticks/second

    private int solidAmount = 0; // Amount of solid material in the crucible
    private int fluidAmount = 0; // Amount of fluid produced
    private Fluid fluid = Fluids.EMPTY;
    private int processingTime = 0; // Time left to process current solid into fluid

    private boolean isWooden = true; // Determines if it's a wooden or porcelain crucible

    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUCIBLE, pos, state);
        
        // Determine crucible type from the block state
        // In a real implementation, this would check the block type directly
        String blockId = state.getBlock().getTranslationKey();
        isWooden = !blockId.contains("porcelain");
    }

    public boolean isWooden() {
        return isWooden;
    }

    public int getSolidAmount() {
        return solidAmount;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public Fluid getFluid() {
        return fluid;
    }

    /**
     * Add a solid material to the crucible
     * @param stack The item to add
     * @param meltValue How much fluid this item produces
     * @return True if the item was added
     */
    public boolean addSolid(ItemStack stack, int meltValue) {
        // Check if we have room for more solid
        if (solidAmount >= MAX_SOLID_AMOUNT) {
            return false;
        }

        // Add as much as we can (up to the stack size or remaining capacity)
        int amountToAdd = Math.min(stack.getCount(), (MAX_SOLID_AMOUNT - solidAmount) / meltValue);
        if (amountToAdd <= 0) return false;

        solidAmount += amountToAdd * meltValue;
        stack.decrement(amountToAdd);
        markDirty();
        return true;
    }

    /**
     * Try to extract fluid from the crucible
     * @param amount The amount to extract
     * @return The fluid that was extracted, or EMPTY if none
     */
    public Fluid extractFluid(int amount) {
        if (fluidAmount >= amount && fluid != Fluids.EMPTY) {
            fluidAmount -= amount;
            Fluid extracted = fluid;
            
            // If we've emptied the crucible, reset the fluid type
            if (fluidAmount <= 0) {
                fluid = Fluids.EMPTY;
            }
            
            markDirty();
            return extracted;
        }
        
        return Fluids.EMPTY;
    }

    /**
     * Check if there is a heat source below the crucible
     */
    private boolean hasHeatSource(World world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();

        // For wooden crucibles: only non-fire blocks since they would burn
        if (isWooden) {
            // Simple check for a few hot blocks that won't burn wood
            return belowBlock == Blocks.TORCH || 
                   belowBlock == Blocks.WALL_TORCH ||
                   belowBlock == Blocks.SOUL_TORCH ||
                   belowBlock == Blocks.SOUL_WALL_TORCH;
        } else {
            // For porcelain: check for any heat source
            return belowBlock == Blocks.FIRE || 
                   belowBlock == Blocks.SOUL_FIRE ||
                   belowBlock == Blocks.LAVA ||
                   belowBlock == Blocks.CAMPFIRE ||
                   belowBlock == Blocks.SOUL_CAMPFIRE ||
                   belowBlock == Blocks.MAGMA_BLOCK ||
                   belowBlock == Blocks.TORCH ||
                   belowBlock == Blocks.WALL_TORCH ||
                   belowBlock == Blocks.SOUL_TORCH ||
                   belowBlock == Blocks.SOUL_WALL_TORCH;
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, CrucibleBlockEntity blockEntity) {
        if (world.isClient) return;

        // Process solids into fluid if there's a heat source
        if (blockEntity.solidAmount > 0 && blockEntity.hasHeatSource(world, pos)) {
            // If we're starting to process a new batch
            if (blockEntity.processingTime <= 0) {
                blockEntity.processingTime = BASE_PROCESSING_TIME;
                
                // Wooden crucibles can only make water
                if (blockEntity.isWooden) {
                    blockEntity.fluid = Fluids.WATER;
                } else {
                    // In a real implementation, this would determine the fluid based on the processed item
                    blockEntity.fluid = Fluids.LAVA;
                }
            }

            // Continue processing
            blockEntity.processingTime--;

            // If processing is complete, convert solid to fluid
            if (blockEntity.processingTime <= 0) {
                // Convert solid to fluid at 1:1 ratio (can be customized)
                int solidToConvert = Math.min(blockEntity.solidAmount, MAX_FLUID_AMOUNT - blockEntity.fluidAmount);
                blockEntity.solidAmount -= solidToConvert;
                blockEntity.fluidAmount += solidToConvert;
                blockEntity.markDirty();
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        
        solidAmount = nbt.getInt(SOLID_AMOUNT_KEY);
        fluidAmount = nbt.getInt(FLUID_AMOUNT_KEY);
        processingTime = nbt.getInt(PROCESSING_TIME_KEY);
        
        // In a real implementation, this would properly deserialize the fluid
        String fluidStr = nbt.getString(FLUID_KEY);
        if (fluidStr.equals("minecraft:water")) {
            fluid = Fluids.WATER;
        } else if (fluidStr.equals("minecraft:lava")) {
            fluid = Fluids.LAVA;
        } else {
            fluid = Fluids.EMPTY;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        
        nbt.putInt(SOLID_AMOUNT_KEY, solidAmount);
        nbt.putInt(FLUID_AMOUNT_KEY, fluidAmount);
        nbt.putInt(PROCESSING_TIME_KEY, processingTime);
        
        // In a real implementation, this would properly serialize the fluid
        if (fluid == Fluids.WATER) {
            nbt.putString(FLUID_KEY, "minecraft:water");
        } else if (fluid == Fluids.LAVA) {
            nbt.putString(FLUID_KEY, "minecraft:lava");
        } else {
            nbt.putString(FLUID_KEY, "");
        }
    }
}