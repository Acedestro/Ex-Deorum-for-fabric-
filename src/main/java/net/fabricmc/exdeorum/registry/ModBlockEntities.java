package net.fabricmc.exdeorum.registry;

import net.fabricmc.exdeorum.ExDeorum;
import net.fabricmc.exdeorum.block.entity.BarrelBlockEntity;
import net.fabricmc.exdeorum.block.entity.CrucibleBlockEntity;
import net.fabricmc.exdeorum.block.entity.SieveBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registry for all block entities in Ex Deorum
 */
public class ModBlockEntities {
    
    // Block entity types
    public static BlockEntityType<BarrelBlockEntity> BARREL;
    public static BlockEntityType<SieveBlockEntity> SIEVE;
    public static BlockEntityType<CrucibleBlockEntity> CRUCIBLE;
    
    /**
     * Register all block entities
     */
    public static void register() {
        BARREL = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ExDeorum.MOD_ID, "barrel"),
            FabricBlockEntityTypeBuilder.create(BarrelBlockEntity::new, ModBlocks.OAK_BARREL, 
                ModBlocks.SPRUCE_BARREL, ModBlocks.BIRCH_BARREL, ModBlocks.JUNGLE_BARREL,
                ModBlocks.ACACIA_BARREL, ModBlocks.DARK_OAK_BARREL, ModBlocks.MANGROVE_BARREL,
                ModBlocks.CHERRY_BARREL, ModBlocks.CRIMSON_BARREL, ModBlocks.WARPED_BARREL)
                .build()
        );
        
        SIEVE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ExDeorum.MOD_ID, "sieve"),
            FabricBlockEntityTypeBuilder.create(SieveBlockEntity::new, ModBlocks.OAK_SIEVE,
                ModBlocks.SPRUCE_SIEVE, ModBlocks.BIRCH_SIEVE, ModBlocks.JUNGLE_SIEVE)
                .build()
        );
        
        CRUCIBLE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ExDeorum.MOD_ID, "crucible"),
            FabricBlockEntityTypeBuilder.create(CrucibleBlockEntity::new, ModBlocks.OAK_CRUCIBLE,
                ModBlocks.SPRUCE_CRUCIBLE, ModBlocks.PORCELAIN_CRUCIBLE, ModBlocks.UNFIRED_PORCELAIN_CRUCIBLE)
                .build()
        );
        
        ExDeorum.LOGGER.info("Registered block entities");
    }
}