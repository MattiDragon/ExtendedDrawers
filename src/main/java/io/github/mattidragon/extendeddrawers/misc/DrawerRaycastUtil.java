package io.github.mattidragon.extendeddrawers.misc;

import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

public class DrawerRaycastUtil {
    @Nullable
    public static Vec2f calculateFaceLocation(BlockPos blockPos, Vec3d hitPos, Direction hitDirection, Direction blockDirection, WallMountLocation mountLocation) {
        if (mountLocation == WallMountLocation.CEILING && hitDirection != Direction.DOWN
            || mountLocation == WallMountLocation.FLOOR && hitDirection != Direction.UP
            || mountLocation == WallMountLocation.WALL && hitDirection != blockDirection)
            return null;
        var internalPos = hitPos.subtract(Vec3d.of(blockPos));

        return switch (mountLocation) {
            case WALL -> switch (blockDirection) {
                case NORTH -> new Vec2f((float) (1 - internalPos.x), (float) (1 - internalPos.y));
                case SOUTH -> new Vec2f((float) (internalPos.x), (float) (1 - internalPos.y));
                case EAST -> new Vec2f((float) (1 - internalPos.z), (float) (1 - internalPos.y));
                case WEST -> new Vec2f((float) (internalPos.z), (float) (1 - internalPos.y));
                default -> null;
            };
            case FLOOR -> switch (blockDirection) {
                case NORTH -> new Vec2f((float) (1 - internalPos.x), (float) (1 - internalPos.z));
                case SOUTH -> new Vec2f((float) (internalPos.x), (float) (internalPos.z));
                case EAST -> new Vec2f((float) (1 - internalPos.z), (float) (internalPos.x));
                case WEST -> new Vec2f((float) (internalPos.z), (float) (1 - internalPos.x));
                default -> null;
            };
            case CEILING -> switch (blockDirection) {
                case NORTH -> new Vec2f((float) (1 - internalPos.x), (float) (internalPos.z));
                case SOUTH -> new Vec2f((float) (internalPos.x), (float) (1 - internalPos.z));
                case EAST -> new Vec2f((float) (1 - internalPos.z), (float) (1 - internalPos.x));
                case WEST -> new Vec2f((float) (internalPos.z), (float) (internalPos.x));
                default -> null;
            };
        };
    }
    
    public static BlockHitResult getTarget(PlayerEntity player, BlockPos target) {
        var from = player.getEyePos();
        var length = Vec3d.ofCenter(target).subtract(from).length() + 1; //Add a bit of extra length for consistency
        var looking = player.getRotationVector();
        var to = from.add(looking.multiply(length));
        return player.getWorld().raycast(new RaycastContext(from, to, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }
}
