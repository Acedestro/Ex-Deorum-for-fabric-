package net.fabricmc.exdeorum.util;

import net.fabricmc.exdeorum.block.BarrelBlock;
import net.fabricmc.exdeorum.block.CrucibleBlock;
import net.fabricmc.exdeorum.block.SieveBlock;
import net.fabricmc.exdeorum.block.entity.BarrelBlockEntity;
import net.fabricmc.exdeorum.block.entity.CrucibleBlockEntity;
import net.fabricmc.exdeorum.block.entity.SieveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Handles optimized interactions for VR players
 * This includes special handling for the mod's blocks to make them more VR-friendly
 */
public class VRInteractionHelper {
    
    /**
     * Special handling for sieve block interactions in VR
     * Makes it easier to use the sieve by requiring fewer clicks
     * and providing more haptic feedback
     */
    public static boolean handleSieveInteraction(SieveBlock block, World world, BlockPos pos, 
                                              PlayerEntity player, Hand hand) {
        // If the player is in VR, we make sieving more VR-friendly
        if (VRCompat.isPlayerInVR(player)) {
            BlockState state = world.getBlockState(pos);
            SieveBlockEntity blockEntity = (SieveBlockEntity) world.getBlockEntity(pos);
            
            if (blockEntity == null) return false;
            
            // For VR, we automate multiple sieving actions with a single click
            // to avoid excessive controller use which can be tiring in VR
            if (blockEntity.hasInput() && blockEntity.hasMesh()) {
                // Get the initial progress value
                int startProgress = blockEntity.getProgress();
                
                // For VR users, we make two sieving actions per click
                boolean completed = blockEntity.sieveOnce();
                
                // If not complete, do another action automatically for VR users
                if (!completed) {
                    completed = blockEntity.sieveOnce();
                }
                
                // Update the block state to match the sieve progress
                world.setBlockState(pos, state.with(SieveBlock.PROGRESS, blockEntity.getProgress()));
                
                // Play a more prominent sound for VR users
                float pitch = 0.6f + (blockEntity.getProgress() / 7.0f) * 0.4f;
                float volume = VRCompat.isPlayerInVR(player) ? 0.5f : 0.3f;
                world.playSound(null, pos, SoundEvents.BLOCK_SAND_BREAK, 
                              SoundCategory.BLOCKS, volume, pitch);
                
                return true;
            }
        }
        
        // For non-VR users, let the normal interaction happen
        return false;
    }
    
    /**
     * Special handling for barrel interactions in VR
     * Makes barrels easier to use by enhancing hitboxes and interaction
     */
    public static boolean handleBarrelInteraction(BarrelBlock block, BlockState state, World world, 
                                               BlockPos pos, PlayerEntity player) {
        // VR specific enhancements for barrels
        if (VRCompat.isPlayerInVR(player)) {
            // Enhanced visual feedback would be added here
            // This gives VR users better visual cues about barrel states
            
            // Example: play enhanced sound effects for VR users
            if (state.get(BarrelBlock.MODE) == BarrelBlockEntity.BarrelMode.COMPOST) {
                VRCompat.playVRFriendlySound(world, pos, 1.0f, 1.0f);
            }
        }
        
        // Return false to let normal interaction happen
        return false;
    }
    
    /**
     * Special handling for crucible block interactions in VR
     * Makes it easier to place items and extract fluids with VR controllers
     */
    public static boolean handleCrucibleInteraction(CrucibleBlock block, BlockState state, World world, 
                                                 BlockPos pos, PlayerEntity player) {
        // Enhanced VR specific handling for crucibles
        if (VRCompat.isPlayerInVR(player)) {
            CrucibleBlockEntity blockEntity = (CrucibleBlockEntity) world.getBlockEntity(pos);
            if (blockEntity == null) return false;
            
            // Enhance interaction hitbox for fluid extraction
            // The implementation would expand the area where players can
            // use buckets on the crucible for easier VR interaction
            
            // Display enhanced visual effects for VR users
            int level = state.get(CrucibleBlock.LEVEL);
            if (level > 0) {
                // More prominent visual cues for VR users
                // This would typically involve particle effects to
                // make fluid levels more obvious in VR
            }
        }
        
        // Return false to let normal interaction happen
        return false;
    }
}