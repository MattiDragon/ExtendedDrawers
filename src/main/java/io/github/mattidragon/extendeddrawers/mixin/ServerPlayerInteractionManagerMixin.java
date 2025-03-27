package io.github.mattidragon.extendeddrawers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
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
    
    @ModifyExpressionValue(method = "processBlockBreakingAction", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z"))
    private boolean extended_drawers$stopCreativeBreaking(boolean original, BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
        if (world == null) return original;
        var state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof CreativeBreakBlocker blocker)) return original;

        var config = ExtendedDrawers.CONFIG.get().misc();
        var behaviour = blocker.isFront(state, direction) ? config.frontBreakingBehaviour() : config.sideBreakingBehaviour();

        return switch (behaviour) {
            case BREAK -> original;
            case NO_BREAK, MINE -> false;
        };
    }
}
