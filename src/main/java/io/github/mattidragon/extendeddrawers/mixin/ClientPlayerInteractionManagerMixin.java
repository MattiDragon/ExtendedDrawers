package io.github.mattidragon.extendeddrawers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow private float currentBreakingProgress;
    
    @ModifyExpressionValue(method = {"attackBlock", "updateBlockBreakingProgress"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z"))
    private boolean extended_drawers$stopCreativeBreaking(boolean original, BlockPos pos, Direction direction) {
        var world = MinecraftClient.getInstance().world;
        if (world == null) return original;
        if (world.getBlockState(pos).getBlock() instanceof CreativeBreakBlocker blocker
                && blocker.shouldBlock(world, pos, direction)) return false;
        return original;
    }
    
    
    /*
    @Inject(method = "updateBlockBreakingProgress",
            at = @At(value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;currentBreakingProgress:F",
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F")))
    private void extended_drawers$stopCreativeBreaking(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        var world = MinecraftClient.getInstance().world;
        if (world == null) return;
        if (world.getBlockState(pos).getBlock() instanceof CreativeBreakBlocker blocker && blocker.shouldBlock(world, pos, direction))
            currentBreakingProgress = 0;
    }*/
}
