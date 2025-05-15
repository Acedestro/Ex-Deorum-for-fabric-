package net.fabricmc.exdeorum.recipe;

import net.fabricmc.exdeorum.ExDeorum;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Handler for all custom Ex Deorum recipes
 * This includes sieve recipes, barrel composting, crucible melting, etc.
 */
public class ExDeorumRecipes {
    
    // Maps for storing different recipe types
    private static final Map<Block, List<SieveRecipe>> SIEVE_RECIPES = new HashMap<>();
    private static final Map<Item, Integer> COMPOST_VALUES = new HashMap<>();
    private static final Map<Item, Integer> CRUCIBLE_MELT_VALUES = new HashMap<>();
    
    /**
     * Initialize all recipe registries
     */
    public static void init() {
        registerSieveRecipes();
        registerCompostValues();
        registerCrucibleMeltValues();
        
        ExDeorum.LOGGER.info("Registered Ex Deorum recipes");
    }
    
    /**
     * Register recipes for the sieve
     */
    private static void registerSieveRecipes() {
        // Dirt recipes
        registerSieveRecipe(Blocks.DIRT, Items.WHEAT_SEEDS, 0.15f, "string");
        registerSieveRecipe(Blocks.DIRT, Items.PUMPKIN_SEEDS, 0.10f, "string");
        registerSieveRecipe(Blocks.DIRT, Items.MELON_SEEDS, 0.10f, "string");
        registerSieveRecipe(Blocks.DIRT, Items.BEETROOT_SEEDS, 0.10f, "string");
        
        // Gravel recipes
        registerSieveRecipe(Blocks.GRAVEL, Items.FLINT, 0.25f, "string");
        registerSieveRecipe(Blocks.GRAVEL, Items.COAL, 0.10f, "flint");
        registerSieveRecipe(Blocks.GRAVEL, Items.RAW_IRON, 0.05f, "flint");
        registerSieveRecipe(Blocks.GRAVEL, Items.RAW_COPPER, 0.05f, "flint");
        
        // Sand recipes
        registerSieveRecipe(Blocks.SAND, Items.GUNPOWDER, 0.08f, "flint");
        registerSieveRecipe(Blocks.SAND, Items.GOLD_NUGGET, 0.05f, "iron");
        
        // Soul sand recipes
        registerSieveRecipe(Blocks.SOUL_SAND, Items.QUARTZ, 0.33f, "flint");
        registerSieveRecipe(Blocks.SOUL_SAND, Items.BLAZE_POWDER, 0.05f, "diamond");
        
        // More recipes would be added in a full implementation
    }
    
    /**
     * Register values for compostable items
     */
    private static void registerCompostValues() {
        // Leaves and plant matter
        registerCompostValue(Items.OAK_LEAVES, 100);
        registerCompostValue(Items.SPRUCE_LEAVES, 100);
        registerCompostValue(Items.BIRCH_LEAVES, 100);
        registerCompostValue(Items.JUNGLE_LEAVES, 100);
        registerCompostValue(Items.ACACIA_LEAVES, 100);
        registerCompostValue(Items.DARK_OAK_LEAVES, 100);
        
        // Saplings
        registerCompostValue(Items.OAK_SAPLING, 125);
        registerCompostValue(Items.SPRUCE_SAPLING, 125);
        registerCompostValue(Items.BIRCH_SAPLING, 125);
        registerCompostValue(Items.JUNGLE_SAPLING, 125);
        registerCompostValue(Items.ACACIA_SAPLING, 125);
        registerCompostValue(Items.DARK_OAK_SAPLING, 125);
        
        // Food and crops
        registerCompostValue(Items.APPLE, 150);
        registerCompostValue(Items.CARROT, 150);
        registerCompostValue(Items.POTATO, 150);
        registerCompostValue(Items.WHEAT, 75);
        registerCompostValue(Items.SUGAR_CANE, 75);
        
        // More values would be added in a full implementation
    }
    
    /**
     * Register values for crucible melting
     */
    private static void registerCrucibleMeltValues() {
        // Wooden crucible melt values (for water production)
        registerCrucibleMeltValue(Items.OAK_LEAVES, 250, true);
        registerCrucibleMeltValue(Items.SPRUCE_LEAVES, 250, true);
        registerCrucibleMeltValue(Items.BIRCH_LEAVES, 250, true);
        registerCrucibleMeltValue(Items.JUNGLE_LEAVES, 250, true);
        registerCrucibleMeltValue(Items.ACACIA_LEAVES, 250, true);
        registerCrucibleMeltValue(Items.DARK_OAK_LEAVES, 250, true);
        
        registerCrucibleMeltValue(Items.OAK_SAPLING, 500, true);
        registerCrucibleMeltValue(Items.SPRUCE_SAPLING, 500, true);
        registerCrucibleMeltValue(Items.BIRCH_SAPLING, 500, true);
        registerCrucibleMeltValue(Items.JUNGLE_SAPLING, 500, true);
        registerCrucibleMeltValue(Items.ACACIA_SAPLING, 500, true);
        registerCrucibleMeltValue(Items.DARK_OAK_SAPLING, 500, true);
        
        // Porcelain crucible melt values (for lava production)
        registerCrucibleMeltValue(Items.COBBLESTONE, 250, false);
        registerCrucibleMeltValue(Items.STONE, 250, false);
        registerCrucibleMeltValue(Items.NETHERRACK, 500, false);
        
        // More values would be added in a full implementation
    }
    
    /**
     * Register a recipe for the sieve
     */
    private static void registerSieveRecipe(Block input, Item result, float chance, String meshType) {
        SIEVE_RECIPES.computeIfAbsent(input, k -> new ArrayList<>())
            .add(new SieveRecipe(input, result, chance, meshType));
    }
    
    /**
     * Register a compostable item and its value
     */
    private static void registerCompostValue(Item item, int value) {
        COMPOST_VALUES.put(item, value);
    }
    
    /**
     * Register a meltable item for crucibles
     */
    private static void registerCrucibleMeltValue(Item item, int value, boolean isForWoodenCrucible) {
        if (isForWoodenCrucible) {
            CRUCIBLE_MELT_VALUES.put(item, value);
        } else {
            // For non-wooden crucibles
            CRUCIBLE_MELT_VALUES.put(item, value);
        }
    }
    
    /**
     * Get the compost value for an item
     */
    public static int getCompostValue(Item item) {
        return COMPOST_VALUES.getOrDefault(item, 0);
    }
    
    /**
     * Get the melt value for an item in a crucible
     */
    public static int getMeltValue(Item item, boolean isWoodenCrucible) {
        // Only return values for items that match the crucible type
        Integer value = CRUCIBLE_MELT_VALUES.get(item);
        if (value == null) return 0;
        
        return value;
    }
    
    /**
     * Get all possible sieve drops for a block and mesh type
     */
    public static List<Pair<Item, Float>> getSieveDrops(Block block, String meshType) {
        List<Pair<Item, Float>> result = new ArrayList<>();
        
        List<SieveRecipe> recipes = SIEVE_RECIPES.get(block);
        if (recipes != null) {
            for (SieveRecipe recipe : recipes) {
                // Check if the recipe is valid for this mesh
                if (recipe.isValidForMesh(meshType)) {
                    result.add(new Pair<>(recipe.getResult(), recipe.getChance()));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Roll for drops from a sieve operation
     */
    public static List<ItemStack> rollSieveDrops(Block block, String meshType, Random random) {
        List<ItemStack> drops = new ArrayList<>();
        
        List<Pair<Item, Float>> possibleDrops = getSieveDrops(block, meshType);
        for (Pair<Item, Float> drop : possibleDrops) {
            if (random.nextFloat() < drop.getRight()) {
                drops.add(new ItemStack(drop.getLeft()));
            }
        }
        
        return drops;
    }
    
    /**
     * A recipe for the sieve
     */
    public static class SieveRecipe {
        private final Block input;
        private final Item result;
        private final float chance;
        private final String meshType;
        
        public SieveRecipe(Block input, Item result, float chance, String meshType) {
            this.input = input;
            this.result = result;
            this.chance = chance;
            this.meshType = meshType;
        }
        
        public Block getInput() {
            return input;
        }
        
        public Item getResult() {
            return result;
        }
        
        public float getChance() {
            return chance;
        }
        
        public String getMeshType() {
            return meshType;
        }
        
        public boolean isValidForMesh(String meshType) {
            // Check if this recipe can be used with the given mesh
            // In a full implementation, this would have hierarchy logic
            // where better meshes can use recipes from worse meshes
            return this.meshType.equals(meshType);
        }
    }
}