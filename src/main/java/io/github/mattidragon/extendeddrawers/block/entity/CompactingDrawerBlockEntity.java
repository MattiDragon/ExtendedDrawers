package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class CompactingDrawerBlockEntity extends StorageDrawerBlockEntity {
    public final CompactingDrawerStorage storage;

    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.storage, ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY);
    }

    public CompactingDrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY, pos, state);

        storage = new CompactingDrawerStorage(this);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        try (var logging = new ErrorReporter.Logging(this.getReporterContext(), ExtendedDrawers.LOGGER)) {
            var view = NbtWriteView.create(logging, registries);
            writeData(view);
            return view.getNbt();
        }
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        var component = components.get(ModDataComponents.COMPACTING_DRAWER_CONTENTS);
        if (component != null) {
            storage.readComponent(component);
        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        componentMapBuilder.add(ModDataComponents.COMPACTING_DRAWER_CONTENTS, storage.toComponent());
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        if (!ExtendedDrawers.CONFIG.get().misc().drawersDropContentsOnBreak()) return;

        var slots = storage.getSlotArray();
        var amount = storage.getTrueAmount();
        // Iterate slots in reverse order
        for (int i = slots.length - 1; i >= 0; i--) {
            var slot = slots[i];
            if (slot.isBlocked()) continue;

            var toDrop = amount / slot.getCompression();
            ItemUtils.offerOrDropStacks(world, pos, null, null, slot.getResource(), toDrop);
            amount -= toDrop * slot.getCompression();
        }
    }

    @Override
    public Stream<? extends DrawerStorage> streamStorages() {
        return Stream.of(storage);
    }

    @Override
    public boolean isEmpty() {
        return storage.getUpgrade() == null && storage.isBlank() && !storage.isHidden() && !storage.isLocked() && !storage.isVoiding();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        storage.updateSlots(); // Force compression ladders to load
    }

    @Override
    protected void readData(ReadView view) {
        view.getOptionalReadView("storage").ifPresent(storage::readData);
    }

    @Override
    public void writeData(WriteView view) {
        storage.writeData(view.get("storage"));
    }
}
