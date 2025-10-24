package io.github.mattidragon.extendeddrawers.extensions.client.renderer.state;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import org.joml.Vector3f;

import java.util.List;

public class EnderConnectorRenderState extends BlockEntityRenderState {
    public List<Vector3f> rays = List.of();
}
