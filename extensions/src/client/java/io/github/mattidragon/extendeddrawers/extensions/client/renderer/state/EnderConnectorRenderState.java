package io.github.mattidragon.extendeddrawers.extensions.client.renderer.state;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import org.joml.Vector3fc;

import java.util.List;

public class EnderConnectorRenderState extends BlockEntityRenderState {
    public List<Vector3fc> rays = List.of();
}
