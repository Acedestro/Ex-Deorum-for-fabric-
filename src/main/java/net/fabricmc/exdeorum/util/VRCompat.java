package net.fabricmc.exdeorum.util;

import net.fabricmc.exdeorum.ExDeorum;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Utility class to help with VR compatibility
 * This improves interaction ranges and hitboxes to make the mod more VR-friendly
 */
public class VRCompat {
    
    // Slightly increased interaction distances for VR users
    private static final double VR_REACH_EXTENSION = 0.5;
    
    // Enhanced hitbox sizes for easier VR interaction
    private static final double VR_HITBOX_EXTENSION = 0.15;
    
    /**
     * Detect if a player is using VR
     * This is a simple implementation that could be expanded for actual VR mods
     */
    public static boolean isPlayerInVR(PlayerEntity player) {
        // For now, we'll assume no VR present
        // In a real implementation, this would check for VR mod classes
        return false;
    }
    
    /**
     * Get the interaction reach distance for a player
     * VR players get a slightly extended reach to compensate for controller precision
     */
    public static double getPlayerReach(PlayerEntity player) {
        // Default reach in vanilla is 4.5 blocks
        double reach = 4.5;
        
        if (isPlayerInVR(player)) {
            reach += VR_REACH_EXTENSION;
        }
        
        return reach;
    }
    
    /**
     * Get an expanded hitbox for VR interaction
     * @param original The original hitbox
     * @param player The player interacting
     * @return The original box or a slightly expanded one for VR
     */
    public static Box getExpandedHitbox(Box original, PlayerEntity player) {
        if (isPlayerInVR(player)) {
            return original.expand(VR_HITBOX_EXTENSION);
        }
        return original;
    }
    
    /**
     * Provide more forgiving hitbox detection for VR players
     * This helps with precise block-placing actions that would be harder in VR
     */
    public static boolean isLookingAt(PlayerEntity player, BlockPos pos, double precision) {
        if (isPlayerInVR(player)) {
            // More forgiving precision for VR players
            precision *= 1.5;
        }
        
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0f);
        double reach = getPlayerReach(player);
        
        Vec3d endPos = eyePos.add(lookVec.multiply(reach));
        
        // Create a box around the target block
        Box blockBox = new Box(pos);
        
        // Create a larger box for collision testing if in VR
        if (isPlayerInVR(player)) {
            blockBox = blockBox.expand(VR_HITBOX_EXTENSION);
        }
        
        // Check if the look vector intersects with the block's box
        return blockBox.raycast(eyePos, endPos).isPresent();
    }
    
    /**
     * Add VR-friendly sound effects - louder and more directional
     * to compensate for VR spatial audio needs
     */
    public static void playVRFriendlySound(World world, BlockPos pos, 
                                          float volume, float pitch) {
        // In a real implementation, this would adjust sound for VR users
        // Example: make sounds slightly louder and more directional for VR
    }
}