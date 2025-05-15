package net.fabricmc.exdeorum.registry;

import net.fabricmc.exdeorum.ExDeorum;
import net.fabricmc.exdeorum.item.CrookItem;
import net.fabricmc.exdeorum.item.HammerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registry for all items in Ex Deorum
 */
public class ModItems {
    
    // Tools
    public static final Item WOODEN_HAMMER = new HammerItem(ToolMaterials.WOOD, 5, -3.3f, new Item.Settings());
    public static final Item STONE_HAMMER = new HammerItem(ToolMaterials.STONE, 5, -3.3f, new Item.Settings());
    public static final Item IRON_HAMMER = new HammerItem(ToolMaterials.IRON, 5, -3.2f, new Item.Settings());
    public static final Item GOLDEN_HAMMER = new HammerItem(ToolMaterials.GOLD, 5, -3.0f, new Item.Settings());
    public static final Item DIAMOND_HAMMER = new HammerItem(ToolMaterials.DIAMOND, 5, -3.1f, new Item.Settings());
    public static final Item NETHERITE_HAMMER = new HammerItem(ToolMaterials.NETHERITE, 5, -3.0f, new Item.Settings());
    
    public static final Item COMPRESSED_WOODEN_HAMMER = new HammerItem(ToolMaterials.WOOD, 7, -3.5f, new Item.Settings());
    public static final Item COMPRESSED_STONE_HAMMER = new HammerItem(ToolMaterials.STONE, 7, -3.5f, new Item.Settings());
    
    public static final Item WOODEN_CROOK = new CrookItem(ToolMaterials.WOOD, 1, -2.0f, new Item.Settings());
    public static final Item BONE_CROOK = new CrookItem(ToolMaterials.STONE, 1, -1.8f, new Item.Settings());
    
    // Meshes for sieves
    public static final Item STRING_MESH = new Item(new FabricItemSettings());
    public static final Item FLINT_MESH = new Item(new FabricItemSettings());
    public static final Item IRON_MESH = new Item(new FabricItemSettings());
    public static final Item GOLDEN_MESH = new Item(new FabricItemSettings());
    public static final Item DIAMOND_MESH = new Item(new FabricItemSettings());
    public static final Item NETHERITE_MESH = new Item(new FabricItemSettings());
    
    // Miscellaneous
    public static final Item SILKWORM = new Item(new FabricItemSettings());
    public static final Item PORCELAIN_CLAY = new Item(new FabricItemSettings());
    
    // Pebbles
    public static final Item STONE_PEBBLE = new Item(new FabricItemSettings());
    public static final Item GRANITE_PEBBLE = new Item(new FabricItemSettings());
    public static final Item DIORITE_PEBBLE = new Item(new FabricItemSettings());
    public static final Item ANDESITE_PEBBLE = new Item(new FabricItemSettings());
    
    /**
     * Register all items
     */
    public static void register() {
        // Register tools
        registerItem("wooden_hammer", WOODEN_HAMMER);
        registerItem("stone_hammer", STONE_HAMMER);
        registerItem("iron_hammer", IRON_HAMMER);
        registerItem("golden_hammer", GOLDEN_HAMMER);
        registerItem("diamond_hammer", DIAMOND_HAMMER);
        registerItem("netherite_hammer", NETHERITE_HAMMER);
        
        registerItem("compressed_wooden_hammer", COMPRESSED_WOODEN_HAMMER);
        registerItem("compressed_stone_hammer", COMPRESSED_STONE_HAMMER);
        
        registerItem("wooden_crook", WOODEN_CROOK);
        registerItem("bone_crook", BONE_CROOK);
        
        // Register meshes
        registerItem("string_mesh", STRING_MESH);
        registerItem("flint_mesh", FLINT_MESH);
        registerItem("iron_mesh", IRON_MESH);
        registerItem("golden_mesh", GOLDEN_MESH);
        registerItem("diamond_mesh", DIAMOND_MESH);
        registerItem("netherite_mesh", NETHERITE_MESH);
        
        // Register miscellaneous items
        registerItem("silkworm", SILKWORM);
        registerItem("porcelain_clay", PORCELAIN_CLAY);
        
        // Register pebbles
        registerItem("stone_pebble", STONE_PEBBLE);
        registerItem("granite_pebble", GRANITE_PEBBLE);
        registerItem("diorite_pebble", DIORITE_PEBBLE);
        registerItem("andesite_pebble", ANDESITE_PEBBLE);
        
        ExDeorum.LOGGER.info("Registered items");
    }
    
    /**
     * Register an item
     */
    private static void registerItem(String name, Item item) {
        Registry.register(Registries.ITEM, new Identifier(ExDeorum.MOD_ID, name), item);
    }
}