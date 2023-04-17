package io.github.mattidragon.extendeddrawers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.misc.CreativeExtractionBehaviour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow private float currentBreakingProgress;

    // Makes creative block breaking behave like survival if we are blocking breaking of a drawer. The other injection handles complete blocking
    @ModifyExpressionValue(method = {"attackBlock", "updateBlockBreakingProgress"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z"))
    private boolean extended_drawers$stopCreativeBreaking(boolean original, BlockPos pos, Direction direction) {
        if (CommonConfig.HANDLE.get().creativeExtractionMode() == CreativeExtractionBehaviour.NORMAL) return original;

        var world = MinecraftClient.getInstance().world;
        if (world == null) return original;
        if (world.getBlockState(pos).getBlock() instanceof CreativeBreakBlocker blocker) {
            if (!CommonConfig.HANDLE.get().creativeExtractionMode().isFrontOnly() || blocker.shouldBlock(world, pos, direction)) {
                return false;
            }
        }
        return original;
    }

    // Prevents breaking of blocks in creative mod if config is set to do that
    @Inject(method = "updateBlockBreakingProgress",
            at = @At(value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;currentBreakingProgress:F",
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F")))
    private void extended_drawers$stopCreativeBreaking(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.HANDLE.get().creativeExtractionMode().isAllowMine()) return;

        var client = MinecraftClient.getInstance();
        if (client.world == null) return;
        if (client.player == null) return;

        if (!client.player.isCreative()) return; // We only want to run in creative

        var block = client.world.getBlockState(pos).getBlock();
        if (!(block instanceof CreativeBreakBlocker blocker)) return;

        if (!CommonConfig.HANDLE.get().creativeExtractionMode().isFrontOnly() || blocker.shouldBlock(client.world, pos, direction)) {
            currentBreakingProgress = 0;
        }
    }
}
