package net.fabricmc.exdeorum.registry;

import net.fabricmc.exdeorum.ExDeorum;
import net.fabricmc.exdeorum.block.BarrelBlock;
import net.fabricmc.exdeorum.block.CrucibleBlock;
import net.fabricmc.exdeorum.block.SieveBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Registry for all blocks in Ex Deorum
 */
public class ModBlocks {
    
    // Barrel blocks
    public static final Block OAK_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block SPRUCE_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block BIRCH_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block JUNGLE_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block ACACIA_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block DARK_OAK_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block MANGROVE_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block CHERRY_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block CRIMSON_BARREL = new BarrelBlock(woodenBarrelSettings());
    public static final Block WARPED_BARREL = new BarrelBlock(woodenBarrelSettings());
    
    // Sieve blocks
    public static final Block OAK_SIEVE = new SieveBlock(woodenSieveSettings());
    public static final Block SPRUCE_SIEVE = new SieveBlock(woodenSieveSettings());
    public static final Block BIRCH_SIEVE = new SieveBlock(woodenSieveSettings());
    public static final Block JUNGLE_SIEVE = new SieveBlock(woodenSieveSettings());
    
    // Crucible blocks
    public static final Block OAK_CRUCIBLE = new CrucibleBlock(woodenCrucibleSettings(), true);
    public static final Block SPRUCE_CRUCIBLE = new CrucibleBlock(woodenCrucibleSettings(), true);
    public static final Block PORCELAIN_CRUCIBLE = new CrucibleBlock(porcelainCrucibleSettings(), false);
    public static final Block UNFIRED_PORCELAIN_CRUCIBLE = new CrucibleBlock(unfiredPorcelainCrucibleSettings(), false);
    
    /**
     * Register all blocks and their corresponding items
     */
    public static void register() {
        // Register barrels
        registerBlockWithItem("oak_barrel", OAK_BARREL);
        registerBlockWithItem("spruce_barrel", SPRUCE_BARREL);
        registerBlockWithItem("birch_barrel", BIRCH_BARREL);
        registerBlockWithItem("jungle_barrel", JUNGLE_BARREL);
        registerBlockWithItem("acacia_barrel", ACACIA_BARREL);
        registerBlockWithItem("dark_oak_barrel", DARK_OAK_BARREL);
        registerBlockWithItem("mangrove_barrel", MANGROVE_BARREL);
        registerBlockWithItem("cherry_barrel", CHERRY_BARREL);
        registerBlockWithItem("crimson_barrel", CRIMSON_BARREL);
        registerBlockWithItem("warped_barrel", WARPED_BARREL);
        
        // Register sieves
        registerBlockWithItem("oak_sieve", OAK_SIEVE);
        registerBlockWithItem("spruce_sieve", SPRUCE_SIEVE);
        registerBlockWithItem("birch_sieve", BIRCH_SIEVE);
        registerBlockWithItem("jungle_sieve", JUNGLE_SIEVE);
        
        // Register crucibles
        registerBlockWithItem("oak_crucible", OAK_CRUCIBLE);
        registerBlockWithItem("spruce_crucible", SPRUCE_CRUCIBLE);
        registerBlockWithItem("porcelain_crucible", PORCELAIN_CRUCIBLE);
        registerBlockWithItem("unfired_porcelain_crucible", UNFIRED_PORCELAIN_CRUCIBLE);
        
        ExDeorum.LOGGER.info("Registered blocks");
    }
    
    /**
     * Register a block with its corresponding item
     */
    private static void registerBlockWithItem(String name, Block block) {
        // Register the block
        Registry.register(Registries.BLOCK, new Identifier(ExDeorum.MOD_ID, name), block);
        
        // Register the block item
        Registry.register(
            Registries.ITEM,
            new Identifier(ExDeorum.MOD_ID, name),
            new BlockItem(block, new FabricItemSettings())
        );
    }
    
    // Block settings helpers
    
    private static FabricBlockSettings woodenBarrelSettings() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
            .strength(2.0f, 3.0f)
            .nonOpaque();
    }
    
    private static FabricBlockSettings woodenSieveSettings() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
            .strength(2.0f, 3.0f)
            .nonOpaque();
    }
    
    private static FabricBlockSettings woodenCrucibleSettings() {
        return FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
            .strength(2.0f, 3.0f)
            .nonOpaque();
    }
    
    private static FabricBlockSettings porcelainCrucibleSettings() {
        return FabricBlockSettings.copyOf(Blocks.TERRACOTTA)
            .strength(2.0f, 3.0f)
            .nonOpaque()
            .sounds(BlockSoundGroup.STONE);
    }
    
    private static FabricBlockSettings unfiredPorcelainCrucibleSettings() {
        return FabricBlockSettings.copyOf(Blocks.CLAY)
            .strength(1.0f, 2.0f)
            .nonOpaque()
            .sounds(BlockSoundGroup.GRAVEL);
    }
}