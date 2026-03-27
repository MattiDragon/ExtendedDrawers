package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.component.DrawerContentsComponent;
import io.github.mattidragon.extendeddrawers.component.DrawerSlotComponent;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.storage.CombinedDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerSlot;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class DrawerBlockEntity extends StorageDrawerBlockEntity {
    public final int slots = ((DrawerBlock)this.getBlockState().getBlock()).slots;
    public final DrawerSlot[] storages = new DrawerSlot[((DrawerBlock)this.getBlockState().getBlock()).slots];
    public final CombinedDrawerStorage combinedStorage;
    
    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.combinedStorage, ModBlocks.DRAWER_BLOCK_ENTITY);
    }
    
    public DrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DRAWER_BLOCK_ENTITY, pos, state);
        var capacityMultiplier = ExtendedDrawers.CONFIG.get().storage().slotCountAffectsCapacity() ? 1.0 / slots : 1;
        for (int i = 0; i < storages.length; i++) {
            storages[i] = new DrawerSlot(this, capacityMultiplier);
        }
        combinedStorage = new CombinedDrawerStorage(storages);
        sortSlots();
    }

    private void sortSlots() {
        combinedStorage.sort();
    }

    public void onSlotChanged(boolean sortingChanged) {
        if (sortingChanged) sortSlots();
        super.onSlotChanged(sortingChanged);
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
        var component = components.get(ModDataComponents.DRAWER_CONTENTS);
        if (component == null) return;
        for (int i = 0; i < component.slots().size(); i++) {
            storages[i].readComponent(component.slots().get(i));
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        if (!ExtendedDrawers.CONFIG.get().misc().drawersDropContentsOnBreak()) return;

        for (var slot : storages) {
            ItemUtils.offerOrDropStacks(level, pos, null, null, slot.getResource(), slot.getAmount());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder componentMapBuilder) {
        if (isEmpty()) return;
        var slotComponents = new ArrayList<DrawerSlotComponent>();
        for (var storage : storages) {
            slotComponents.add(storage.toComponent());
        }
        componentMapBuilder.set(ModDataComponents.DRAWER_CONTENTS, new DrawerContentsComponent(slotComponents));
    }

    @Override
    public Stream<? extends DrawerStorage> streamStorages() {
        return Arrays.stream(storages);
    }

    @Override
    public boolean isEmpty() {
        for (var storage : storages) {
            if (storage.getUpgrade() != null || storage.getLimiter() != Long.MAX_VALUE || !storage.isResourceBlank() || storage.isHidden() || storage.isLocked() || storage.isVoiding()|| storage.isDuping())
                return false;
        }
        return true;
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        var items = view.childrenListOrEmpty("items").stream().toList();
        for (int i = 0; i < items.size(); i++) {
            storages[i].readData(items.get(i));
        }
    }

    @Override
    public void saveAdditional(ValueOutput view) {
        var items = view.childrenList("items");
        for (var storage : storages) {
            storage.writeData(items.addChild());
        }
    }
}
