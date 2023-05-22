package io.github.mattidragon.extendeddrawers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import io.github.mattidragon.extendeddrawers.config.ExtendedDrawersConfig;
import io.github.mattidragon.extendeddrawers.misc.CreativeExtractionBehaviour;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow protected ServerWorld world;
    
    @ModifyExpressionValue(method = "processBlockBreakingAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;isCreative()Z"))
    private boolean extended_drawers$stopCreativeBreaking(boolean original, BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
        if (ExtendedDrawersConfig.get().misc().creativeExtractionMode() == CreativeExtractionBehaviour.NORMAL) return original;
        if (world == null) return original;
        if (world.getBlockState(pos).getBlock() instanceof CreativeBreakBlocker blocker) {
            if (!ExtendedDrawersConfig.get().misc().creativeExtractionMode().isFrontOnly() || blocker.shouldBlock(world, pos, direction)) {
                return false;
            }
        }
        return original;
    }
}
