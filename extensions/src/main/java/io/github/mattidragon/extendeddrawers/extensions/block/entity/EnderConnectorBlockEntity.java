package io.github.mattidragon.extendeddrawers.extensions.block.entity;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.util.NodePos;
import io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions;
import io.github.mattidragon.extendeddrawers.extensions.network.link.EnderConnectorLinkKey;
import io.github.mattidragon.extendeddrawers.extensions.network.node.EnderConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class EnderConnectorBlockEntity extends BlockEntity {
    private List<Vector3f> rayDirectionCache = List.of();

    public EnderConnectorBlockEntity(BlockPos pos, BlockState state) {
        super(ExtensionBlocks.ENDER_CONNECTOR_ENTITY, pos, state);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtWriteView writeView;
        try (var errorReporter = new ErrorReporter.Logging(getReporterContext(), ExtendedDrawersExtensions.LOGGER)) {
            writeView = NbtWriteView.create(errorReporter);
            writeView.put("ray_directions", Codecs.VECTOR_3F.listOf(), rayDirectionCache);
        }
        return writeView.getNbt();
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        view.read("ray_directions", Codecs.VECTOR_3F.listOf()).ifPresentOrElse(
            rays -> rayDirectionCache = rays,
            () -> rayDirectionCache = List.of()
        );
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world instanceof ServerWorld serverWorld) {
            var nodeHolder = NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                    .getNodeAt(new NodePos(pos, EnderConnectorBlockNode.INSTANCE));
            if (nodeHolder != null) {
                updateRayCache(nodeHolder);
            }
        }
    }

    public void updateRayCache(NodeHolder<BlockNode> self) {
        var centerPos = self.getBlockPos().toCenterPos();
        rayDirectionCache = self.getConnectionsOfType(EnderConnectorLinkKey.class)
                .map(holder -> holder.other(self).getBlockPos().toCenterPos())
                .map(pos1 -> pos1.subtract(centerPos).toVector3f())
                .map(pos1 -> pos1.lengthSquared() > (6 * 6) ? pos1.normalize(3) : pos1.mul(0.5f))
                .toList();
    }

    public List<Vector3f> rayDirectionCache() {
        return rayDirectionCache;
    }
}
