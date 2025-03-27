package io.github.mattidragon.extendeddrawers.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow private float currentBreakingProgress;

    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Shadow @Final private MinecraftClient client;

    // Makes creative block breaking behave like survival if we are blocking breaking of a drawer. The other injection handles complete blocking
    @ModifyExpressionValue(method = {"attackBlock", "updateBlockBreakingProgress"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z"))
    private boolean extended_drawers$stopCreativeBreaking(boolean original, BlockPos pos, Direction direction) {
        var world = MinecraftClient.getInstance().world;
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
    @Inject(method = "updateBlockBreakingProgress",
            at = @At(value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;currentBreakingProgress:F",
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F")))
    private void extended_drawers$stopCreativeBreaking(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        var world = networkHandler.getWorld();
        var player = client.player;
        if (world == null || player == null || !player.isCreative()) return;
        var state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof CreativeBreakBlocker blocker)) return;

        var config = ExtendedDrawers.CONFIG.get().misc();
        var behaviour = blocker.isFront(state, direction) ? config.frontBreakingBehaviour() : config.sideBreakingBehaviour();

        switch (behaviour) {
            case BREAK, MINE -> {}
            case NO_BREAK -> currentBreakingProgress = 0;
        }
    }
}
