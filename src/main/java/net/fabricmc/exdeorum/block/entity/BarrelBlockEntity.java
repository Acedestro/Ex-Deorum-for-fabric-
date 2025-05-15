package net.fabricmc.exdeorum.block.entity;

import net.fabricmc.exdeorum.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class BarrelBlockEntity extends BlockEntity implements Inventory {
    private static final String ITEM_KEY = "Item";
    private static final String FLUID_KEY = "Fluid";
    private static final String FLUID_AMOUNT_KEY = "FluidAmount";
    private static final String COMPOST_LEVEL_KEY = "CompostLevel";
    private static final String COMPOST_TIME_KEY = "CompostTime";
    private static final String MODE_KEY = "Mode";

    private static final int MAX_FLUID_AMOUNT = 1000;
    private static final int MAX_COMPOST_LEVEL = 1000;
    private static final int COMPOST_TIME_TOTAL = 600; // 30 seconds

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private Fluid fluid = Fluids.EMPTY;
    private int fluidAmount = 0;
    private int compostLevel = 0;
    private int compostTime = 0;
    private BarrelMode mode = BarrelMode.EMPTY;

    public enum BarrelMode {
        EMPTY,
        FLUID,
        COMPOST,
        DIRT
    }

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BARREL, pos, state);
    }

    public BarrelMode getMode() {
        return mode;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public int getFluidAmount() {
        return fluidAmount;
    }

    public int getCompostLevel() {
        return compostLevel;
    }

    public int getCompostTime() {
        return compostTime;
    }

    public boolean addFluid(Fluid fluid, int amount) {
        if (mode == BarrelMode.EMPTY || (mode == BarrelMode.FLUID && this.fluid == fluid)) {
            int newAmount = fluidAmount + amount;
            if (newAmount <= MAX_FLUID_AMOUNT) {
                this.fluid = fluid;
                this.fluidAmount = newAmount;
                this.mode = BarrelMode.FLUID;
                markDirty();
                return true;
            }
        }
        return false;
    }

    public boolean addCompostItem(ItemStack stack, int compostValue) {
        if (mode == BarrelMode.EMPTY || mode == BarrelMode.COMPOST) {
            int newLevel = compostLevel + compostValue;
            if (newLevel <= MAX_COMPOST_LEVEL) {
                compostLevel = newLevel;
                mode = BarrelMode.COMPOST;
                markDirty();
                
                // Play sound and spawn particles
                if (world != null && !world.isClient) {
                    world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    spawnCompostParticles();
                }
                return true;
            }
        }
        return false;
    }

    private void spawnCompostParticles() {
        if (world != null && world.isClient) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            
            for (int i = 0; i < 10; i++) {
                double offsetX = world.random.nextGaussian() * 0.02;
                double offsetY = world.random.nextGaussian() * 0.02;
                double offsetZ = world.random.nextGaussian() * 0.02;
                
                world.addParticle(ParticleTypes.COMPOSTER, 
                    x + world.random.nextFloat() - 0.5, 
                    y, 
                    z + world.random.nextFloat() - 0.5, 
                    offsetX, offsetY, offsetZ);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BarrelBlockEntity blockEntity) {
        if (world.isClient) return;
        
        if (blockEntity.mode == BarrelMode.COMPOST && blockEntity.compostLevel >= MAX_COMPOST_LEVEL) {
            blockEntity.compostTime++;
            if (blockEntity.compostTime >= COMPOST_TIME_TOTAL) {
                blockEntity.mode = BarrelMode.DIRT;
                blockEntity.compostTime = 0;
                blockEntity.markDirty();
                world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(ITEM_KEY)) {
            inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
            Inventories.readNbt(nbt, inventory);
        }
        
        if (nbt.contains(FLUID_KEY)) {
            // In a full implementation, we would deserialize the fluid from registry ID
            // For now, we'll assume water if any fluid is present
            if (!nbt.getString(FLUID_KEY).isEmpty()) {
                fluid = Fluids.WATER;
            }
        }
        
        fluidAmount = nbt.getInt(FLUID_AMOUNT_KEY);
        compostLevel = nbt.getInt(COMPOST_LEVEL_KEY);
        compostTime = nbt.getInt(COMPOST_TIME_KEY);
        mode = BarrelMode.values()[nbt.getInt(MODE_KEY)];
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        
        // In a full implementation, we would serialize the fluid to registry ID
        // For now, we'll just check if it's water
        nbt.putString(FLUID_KEY, fluid == Fluids.WATER ? "minecraft:water" : "");
        
        nbt.putInt(FLUID_AMOUNT_KEY, fluidAmount);
        nbt.putInt(COMPOST_LEVEL_KEY, compostLevel);
        nbt.putInt(COMPOST_TIME_KEY, compostTime);
        nbt.putInt(MODE_KEY, mode.ordinal());
    }

    // Inventory Implementation
    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }
}