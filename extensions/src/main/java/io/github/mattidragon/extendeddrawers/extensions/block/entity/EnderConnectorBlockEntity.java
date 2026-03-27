package io.github.mattidragon.extendeddrawers.extensions.block.entity;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.util.NodePos;
import io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions;
import io.github.mattidragon.extendeddrawers.extensions.network.link.EnderConnectorLinkKey;
import io.github.mattidragon.extendeddrawers.extensions.network.node.EnderConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.List;

public class EnderConnectorBlockEntity extends BlockEntity {
    private List<Vector3fc> rayDirectionCache = List.of();

    public EnderConnectorBlockEntity(BlockPos pos, BlockState state) {
        super(ExtensionBlocks.ENDER_CONNECTOR_ENTITY, pos, state);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        TagValueOutput writeView;
        try (var errorReporter = new ProblemReporter.ScopedCollector(problemPath(), ExtendedDrawersExtensions.LOGGER)) {
            writeView = TagValueOutput.createWithoutContext(errorReporter);
            writeView.store("ray_directions", ExtraCodecs.VECTOR3F.listOf(), rayDirectionCache);
        }
        return writeView.buildResult();
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        view.read("ray_directions", ExtraCodecs.VECTOR3F.listOf()).ifPresentOrElse(
            rays -> rayDirectionCache = rays,
            () -> rayDirectionCache = List.of()
        );
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        if (world instanceof ServerLevel serverWorld) {
            var nodeHolder = NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                    .getNodeAt(new NodePos(worldPosition, EnderConnectorBlockNode.INSTANCE));
            if (nodeHolder != null) {
                updateRayCache(nodeHolder);
            }
        }
    }

    public void updateRayCache(NodeHolder<BlockNode> self) {
        var centerPos = self.getBlockPos().getCenter();
        rayDirectionCache = self.getConnectionsOfType(EnderConnectorLinkKey.class)
                .map(holder -> holder.other(self).getBlockPos().getCenter())
                .map(pos1 -> pos1.subtract(centerPos).toVector3f())
                .<Vector3fc>map(pos1 -> pos1.lengthSquared() > (6 * 6) ? pos1.normalize(3) : pos1.mul(0.5f))
                .toList();
    }

    public List<Vector3fc> rayDirectionCache() {
        return rayDirectionCache;
    }
}
