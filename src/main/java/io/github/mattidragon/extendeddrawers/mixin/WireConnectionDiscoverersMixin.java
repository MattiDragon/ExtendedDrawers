package io.github.mattidragon.extendeddrawers.mixin;

import com.kneelawk.graphlib.api.wire.WireConnectionDiscoverers;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WireConnectionDiscoverers.class)
public class WireConnectionDiscoverersMixin {
    @Redirect(
            method = "fullBlockCanConnect(Lcom/kneelawk/graphlib/api/wire/FullWireBlockNode;Lcom/kneelawk/graphlib/api/graph/NodeHolder;Lcom/kneelawk/graphlib/api/util/HalfLink;Lcom/kneelawk/graphlib/api/wire/FullWireConnectionFilter;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;method_50026(III)Lnet/minecraft/util/math/Direction;"),
            require = 0
    )
    private static Direction tempGraphLibFixHack(int x, int y, int z) {
        return Direction.fromVector(x, y, z, null);
    }
}
