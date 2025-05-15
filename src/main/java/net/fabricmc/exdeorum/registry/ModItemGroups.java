package net.fabricmc.exdeorum.registry;

import net.fabricmc.exdeorum.ExDeorum;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Registry for item groups (creative tabs) in the mod.
 * Designed with VR accessibility in mind - simple navigation and clear organization.
 */
public class ModItemGroups {
    
    // Main item group for all Ex Deorum items
    public static final RegistryKey<ItemGroup> MAIN = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            new Identifier(ExDeorum.MOD_ID, "main")
    );
    
    /**
     * Registers all item groups in the mod.
     */
    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, MAIN, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.WOODEN_HAMMER))
                .displayName(Text.translatable("itemGroup.exdeorum.main"))
                .entries((context, entries) -> {
                    // Add barrel blocks
                    entries.add(ModBlocks.OAK_BARREL);
                    entries.add(ModBlocks.SPRUCE_BARREL);
                    entries.add(ModBlocks.BIRCH_BARREL);
                    entries.add(ModBlocks.JUNGLE_BARREL);
                    entries.add(ModBlocks.ACACIA_BARREL);
                    entries.add(ModBlocks.DARK_OAK_BARREL);
                    entries.add(ModBlocks.MANGROVE_BARREL);
                    entries.add(ModBlocks.CHERRY_BARREL);
                    entries.add(ModBlocks.CRIMSON_BARREL);
                    entries.add(ModBlocks.WARPED_BARREL);
                    
                    // Add sieve blocks
                    entries.add(ModBlocks.OAK_SIEVE);
                    entries.add(ModBlocks.SPRUCE_SIEVE);
                    entries.add(ModBlocks.BIRCH_SIEVE);
                    entries.add(ModBlocks.JUNGLE_SIEVE);
                    
                    // Add crucible blocks
                    entries.add(ModBlocks.OAK_CRUCIBLE);
                    entries.add(ModBlocks.SPRUCE_CRUCIBLE);
                    entries.add(ModBlocks.UNFIRED_PORCELAIN_CRUCIBLE);
                    entries.add(ModBlocks.PORCELAIN_CRUCIBLE);
                    
                    // Add hammers
                    entries.add(ModItems.WOODEN_HAMMER);
                    entries.add(ModItems.STONE_HAMMER);
                    entries.add(ModItems.IRON_HAMMER);
                    entries.add(ModItems.GOLDEN_HAMMER);
                    entries.add(ModItems.DIAMOND_HAMMER);
                    entries.add(ModItems.NETHERITE_HAMMER);
                    
                    // Add compressed hammers
                    entries.add(ModItems.COMPRESSED_WOODEN_HAMMER);
                    entries.add(ModItems.COMPRESSED_STONE_HAMMER);
                    
                    // Add crooks
                    entries.add(ModItems.WOODEN_CROOK);
                    entries.add(ModItems.BONE_CROOK);
                    
                    // Add meshes
                    entries.add(ModItems.STRING_MESH);
                    entries.add(ModItems.FLINT_MESH);
                    entries.add(ModItems.IRON_MESH);
                    entries.add(ModItems.GOLDEN_MESH);
                    entries.add(ModItems.DIAMOND_MESH);
                    entries.add(ModItems.NETHERITE_MESH);
                    
                    // Add resources
                    entries.add(ModItems.SILKWORM);
                    entries.add(ModItems.PORCELAIN_CLAY);
                    
                    // Add pebbles
                    entries.add(ModItems.STONE_PEBBLE);
                    entries.add(ModItems.GRANITE_PEBBLE);
                    entries.add(ModItems.DIORITE_PEBBLE);
                    entries.add(ModItems.ANDESITE_PEBBLE);
                })
                .build()
        );
        
        ExDeorum.LOGGER.info("Registered item groups for " + ExDeorum.MOD_ID);
    }
}