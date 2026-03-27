package io.github.mattidragon.extendeddrawers.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow private float destroyProgress;

    @Shadow @Final private ClientPacketListener connection;

    @Shadow @Final private Minecraft minecraft;

    // Makes creative block breaking behave like survival if we are blocking breaking of a drawer. The other injection handles complete blocking
    @ModifyExpressionValue(method = {"startDestroyBlock", "continueDestroyBlock"}, at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z"))
    private boolean extended_drawers$stopCreativeBreaking(boolean original, BlockPos pos, Direction direction) {
        var world = Minecraft.getInstance().level;
        if (world == null) return original;
        var state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof CreativeBreakBlocker blocker)) return original;

        var config = ExtendedDrawers.CONFIG.get().misc();
        var behaviour = blocker.isFront(state, direction) ? config.frontBreakingBehaviour() : config.sideBreakingBehaviour();

        return switch (behaviour) {
            case BREAK, NO_BREAK -> original;
            case MINE -> false;
        };
    }

    // Prevents breaking of blocks in creative mod if config is set to do that
    @Inject(method = "continueDestroyBlock",
            at = @At(value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyProgress:F",
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;calcBlockBreakingDelta(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F")))
    private void extended_drawers$stopCreativeBreaking(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        var world = connection.getLevel();
        var player = minecraft.player;
        if (world == null || player == null || !player.isCreative()) return;
        var state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof CreativeBreakBlocker blocker)) return;

        var config = ExtendedDrawers.CONFIG.get().misc();
        var behaviour = blocker.isFront(state, direction) ? config.frontBreakingBehaviour() : config.sideBreakingBehaviour();

        switch (behaviour) {
            case BREAK, MINE -> {}
            case NO_BREAK -> destroyProgress = 0;
        }
    }
}
