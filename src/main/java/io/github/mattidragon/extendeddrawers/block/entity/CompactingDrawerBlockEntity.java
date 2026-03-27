package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try (var logging = new ProblemReporter.ScopedCollector(this.problemPath(), ExtendedDrawers.LOGGER)) {
            var view = TagValueOutput.createWithContext(logging, registries);
            saveAdditional(view);
            return view.buildResult();
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        var component = components.get(ModDataComponents.COMPACTING_DRAWER_CONTENTS);
        if (component != null) {
            storage.readComponent(component);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder componentMapBuilder) {
        componentMapBuilder.set(ModDataComponents.COMPACTING_DRAWER_CONTENTS, storage.toComponent());
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        if (!ExtendedDrawers.CONFIG.get().misc().drawersDropContentsOnBreak()) return;

        var slots = storage.getSlotArray();
        var amount = storage.getTrueAmount();
        // Iterate slots in reverse order
        for (int i = slots.length - 1; i >= 0; i--) {
            var slot = slots[i];
            if (slot.isBlocked()) continue;

            var toDrop = amount / slot.getCompression();
            ItemUtils.offerOrDropStacks(level, pos, null, null, slot.getResource(), toDrop);
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
    public void setLevel(Level world) {
        super.setLevel(world);
        storage.updateSlots(); // Force compression ladders to load
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        view.child("storage").ifPresent(storage::readData);
    }

    @Override
    public void saveAdditional(ValueOutput view) {
        storage.writeData(view.child("storage"));
    }
}
