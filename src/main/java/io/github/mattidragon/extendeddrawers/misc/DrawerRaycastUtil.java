package io.github.mattidragon.extendeddrawers.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DrawerRaycastUtil {
    @Nullable
    public static Vec2 calculateFaceLocation(BlockPos blockPos, Vec3 hitPos, Direction hitDirection, Direction blockDirection, AttachFace blockFace) {
        if (blockFace == AttachFace.CEILING && hitDirection != Direction.DOWN
            || blockFace == AttachFace.FLOOR && hitDirection != Direction.UP
            || blockFace == AttachFace.WALL && hitDirection != blockDirection)
            return null;
        var internalPos = hitPos.subtract(Vec3.atLowerCornerOf(blockPos));

        return switch (blockFace) {
            case WALL -> switch (blockDirection) {
                case NORTH -> new Vec2((float) (1 - internalPos.x), (float) (1 - internalPos.y));
                case SOUTH -> new Vec2((float) (internalPos.x), (float) (1 - internalPos.y));
                case EAST -> new Vec2((float) (1 - internalPos.z), (float) (1 - internalPos.y));
                case WEST -> new Vec2((float) (internalPos.z), (float) (1 - internalPos.y));
                default -> null;
            };
            case FLOOR -> switch (blockDirection) {
                case NORTH -> new Vec2((float) (1 - internalPos.x), (float) (1 - internalPos.z));
                case SOUTH -> new Vec2((float) (internalPos.x), (float) (internalPos.z));
                case EAST -> new Vec2((float) (1 - internalPos.z), (float) (internalPos.x));
                case WEST -> new Vec2((float) (internalPos.z), (float) (1 - internalPos.x));
                default -> null;
            };
            case CEILING -> switch (blockDirection) {
                case NORTH -> new Vec2((float) (1 - internalPos.x), (float) (internalPos.z));
                case SOUTH -> new Vec2((float) (internalPos.x), (float) (1 - internalPos.z));
                case EAST -> new Vec2((float) (1 - internalPos.z), (float) (1 - internalPos.x));
                case WEST -> new Vec2((float) (internalPos.z), (float) (internalPos.x));
                default -> null;
            };
        };
    }
    
    public static BlockHitResult getTarget(Player player, BlockPos target) {
        var from = player.getEyePosition();
        var length = Vec3.atCenterOf(target).subtract(from).length() + 1; //Add a bit of extra length for consistency
        var looking = player.getLookAngle();
        var to = from.add(looking.scale(length));
        return player.level().clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }
}
