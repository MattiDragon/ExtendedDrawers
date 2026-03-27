package io.github.mattidragon.extendeddrawers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow protected ServerLevel level;
    
    @ModifyExpressionValue(method = "handleBlockBreakAction", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z"))
    private boolean extended_drawers$stopCreativeBreaking(boolean original, BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int worldHeight) {
        var state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CreativeBreakBlocker blocker)) return original;

        var config = ExtendedDrawers.CONFIG.get().misc();
        var behaviour = blocker.isFront(state, direction) ? config.frontBreakingBehaviour() : config.sideBreakingBehaviour();

        return switch (behaviour) {
            case BREAK -> original;
            case NO_BREAK, MINE -> false;
        };
    }
}
