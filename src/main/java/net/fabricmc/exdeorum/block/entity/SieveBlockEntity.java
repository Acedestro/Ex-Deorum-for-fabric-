package net.fabricmc.exdeorum.block.entity;

import net.fabricmc.exdeorum.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SieveBlockEntity extends BlockEntity implements Inventory {
    private static final String MESH_KEY = "Mesh";
    private static final String INPUT_KEY = "Input";
    private static final String PROGRESS_KEY = "Progress";
    
    private static final int MAX_PROGRESS = 7; // Number of clicks to complete sieving
    
    private ItemStack meshStack = ItemStack.EMPTY;
    private ItemStack inputStack = ItemStack.EMPTY;
    private int progress = 0;
    
    // Store items that have not been collected yet
    private DefaultedList<ItemStack> outputItems = DefaultedList.ofSize(9, ItemStack.EMPTY);
    
    public SieveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIEVE, pos, state);
    }
    
    public ItemStack getMeshStack() {
        return meshStack;
    }
    
    public void setMeshStack(ItemStack stack) {
        this.meshStack = stack;
        markDirty();
    }
    
    public ItemStack getInputStack() {
        return inputStack;
    }
    
    public void setInputStack(ItemStack stack) {
        this.inputStack = stack;
        this.progress = 0;
        markDirty();
    }
    
    public int getProgress() {
        return progress;
    }
    
    public boolean hasMesh() {
        return !meshStack.isEmpty();
    }
    
    public boolean hasInput() {
        return !inputStack.isEmpty();
    }
    
    /**
     * Perform one sieve action (like a player right-click)
     * @return true if sieving is complete
     */
    public boolean sieveOnce() {
        if (!hasInput() || !hasMesh()) {
            return false;
        }
        
        progress++;
        markDirty();
        
        if (progress >= MAX_PROGRESS) {
            if (!world.isClient) {
                generateResults();
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Generate the results of sieving based on the mesh and input
     */
    private void generateResults() {
        // In a real implementation, this would check for recipes
        // For now, we'll implement a simple example
        
        Random random = new Random();
        Item mesh = meshStack.getItem();
        Item input = inputStack.getItem();
        
        // Clear the input
        inputStack = ItemStack.EMPTY;
        progress = 0;
        
        // TODO: Replace with actual recipe lookups
        // For testing, we'll generate a random number of items (1-3)
        int resultCount = random.nextInt(3) + 1;
        
        // Temporary: just return the mesh item as result for testing
        ItemStack result = new ItemStack(mesh, 1);
        addResult(result);
        
        markDirty();
    }
    
    /**
     * Add an item to the output inventory
     */
    private void addResult(ItemStack stack) {
        for (int i = 0; i < outputItems.size(); i++) {
            ItemStack current = outputItems.get(i);
            if (current.isEmpty()) {
                outputItems.set(i, stack);
                return;
            }
        }
        
        // If we get here, there's no space - drop the item
        if (world != null) {
            dropStack(world, pos, Direction.UP, stack);
        }
    }
    
    /**
     * Drop a stack in the world
     */
    private static void dropStack(World world, BlockPos pos, Direction direction, ItemStack stack) {
        // In a full implementation, this would use the proper item dropping method
        // For now, this is just a placeholder
    }
    
    /**
     * Get all the items that should drop when the block is broken
     */
    public List<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<>();
        
        if (!meshStack.isEmpty()) {
            drops.add(meshStack);
        }
        
        if (!inputStack.isEmpty()) {
            drops.add(inputStack);
        }
        
        for (ItemStack stack : outputItems) {
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
        
        return drops;
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        
        if (nbt.contains(MESH_KEY)) {
            meshStack = ItemStack.fromNbt(nbt.getCompound(MESH_KEY));
        }
        
        if (nbt.contains(INPUT_KEY)) {
            inputStack = ItemStack.fromNbt(nbt.getCompound(INPUT_KEY));
        }
        
        progress = nbt.getInt(PROGRESS_KEY);
        
        // Read output items
        outputItems = DefaultedList.ofSize(9, ItemStack.EMPTY);
        Inventories.readNbt(nbt, outputItems);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        
        if (!meshStack.isEmpty()) {
            nbt.put(MESH_KEY, meshStack.writeNbt(new NbtCompound()));
        }
        
        if (!inputStack.isEmpty()) {
            nbt.put(INPUT_KEY, inputStack.writeNbt(new NbtCompound()));
        }
        
        nbt.putInt(PROGRESS_KEY, progress);
        
        // Write output items
        Inventories.writeNbt(nbt, outputItems);
    }
    
    // Inventory implementation
    @Override
    public int size() {
        return outputItems.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : outputItems) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getStack(int slot) {
        return outputItems.get(slot);
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(outputItems, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(outputItems, slot);
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        outputItems.set(slot, stack);
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
        outputItems.clear();
        markDirty();
    }
}