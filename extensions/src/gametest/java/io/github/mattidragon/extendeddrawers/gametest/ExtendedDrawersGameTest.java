package io.github.mattidragon.extendeddrawers.gametest;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.ModBlocks;
import io.github.mattidragon.extendeddrawers.item.ModItems;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerSlot;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.stream.StreamSupport;

public class ExtendedDrawersGameTest implements CustomTestMethodInvoker {
    @GameTest
    public void shadowDrawerTest(GameTestHelper helper) {
        helper.setBlock(0, 0, 0, ModBlocks.SINGLE_DRAWER);
        BlockPos.betweenClosed(1, 0, 0, 3, 0, 0).forEach(pos -> {
            helper.setBlock(pos, ModBlocks.CONNECTOR);
        });
        helper.setBlock(4, 0, 0, ModBlocks.SHADOW_DRAWER);

        helper.runAtTickTime(1, () -> {
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
            helper.useBlock(new BlockPos(4, 0, 0), player, new BlockHitResult(
                    Vec3.ZERO,
                    Direction.NORTH,
                    helper.absolutePos(new BlockPos(4, 0, 0)),
                    false
            ));
        });

        helper.runAtTickTime(2, () -> {
            var drawerStorage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(new BlockPos(0, 0, 0)), Direction.UP);
            helper.assertTrue(drawerStorage != null, "Storage must not be null");
            try (var t = Transaction.openOuter()) {
                drawerStorage.insert(ItemVariant.of(Items.STONE), 10, t);
                t.commit();
            }

            var shadowDrawerStorage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(new BlockPos(4, 0, 0)), Direction.UP);
            helper.assertTrue(shadowDrawerStorage != null, "Shadow drawer storage must not be null");
            try (var t = Transaction.openOuter()) {
                var extracted = shadowDrawerStorage.extract(ItemVariant.of(Items.STONE), 20, t);
                helper.assertTrue(extracted == 10, "Should be able to extract all inserted items");
                t.commit();
            }

            helper.assertTrue(StreamSupport.stream(shadowDrawerStorage.nonEmptyViews().spliterator(), false).findAny().isEmpty(), "Shadow drawer storage should be empty after extraction");
            helper.assertTrue(StreamSupport.stream(drawerStorage.nonEmptyViews().spliterator(), false).findAny().isEmpty(), "Drawer storage should be empty after extraction");
            helper.succeed();
        });
    }

    @GameTest
    public void compactingDrawerTest(GameTestHelper helper) {
        var pos = new BlockPos(3, 0, 3);
        helper.setBlock(pos, ModBlocks.COMPACTING_DRAWER);

        helper.runAtTickTime(1, () -> {
            var storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(pos), Direction.UP);
            helper.assertTrue(storage != null, "Storage must not be null");
            try (var t = Transaction.openOuter()) {
                storage.insert(ItemVariant.of(Items.IRON_INGOT), 10, t);
                t.commit();
            }
        });
        helper.runAtTickTime(2, () -> {
            var storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(pos), Direction.UP);
            helper.assertTrue(storage != null, "Storage must not be null");
            try (var t = Transaction.openOuter()) {
                var extracted1 = storage.extract(ItemVariant.of(Items.IRON_BLOCK), 20, t);
                helper.assertTrue(extracted1 == 1, "Should be able to extract 1 block from 10 ingots");
                var extracted2 = storage.extract(ItemVariant.of(Items.IRON_NUGGET), 10, t);
                helper.assertTrue(extracted2 == 9, "Should be able to extract 9 nuggets from the remaining ingots");
                t.commit();
            }
            helper.succeed();
        });
    }

    @GameTest
    public void upgradeTest(GameTestHelper helper) {
        var pos = new BlockPos(3, 0, 3);
        helper.setBlock(pos, ModBlocks.SINGLE_DRAWER);

        {
            var storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(pos), Direction.UP);
            helper.assertTrue(storage != null, "Storage must not be null");
            try (var t = Transaction.openOuter()) {
                var expected = ExtendedDrawers.CONFIG.get().storage().drawerCapacity();
                var actual = storage.insert(ItemVariant.of(Items.IRON_INGOT), Integer.MAX_VALUE, t);
                helper.assertTrue(actual == expected, "Should only be able to insert up to the drawer capacity");
                t.abort();
            }
        }

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var stack = ModItems.T1_UPGRADE.getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(
                Vec3.ZERO,
                Direction.NORTH,
                helper.absolutePos(pos),
                false
        )));

        {
            var storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(pos), Direction.UP);
            helper.assertTrue(storage != null, "Storage must not be null");
            try (var t = Transaction.openOuter()) {
                var expected = ExtendedDrawers.CONFIG.get().storage().drawerCapacity() * ExtendedDrawers.CONFIG.get().storage().t1UpgradeMultiplier();
                var actual = storage.insert(ItemVariant.of(Items.IRON_INGOT), Integer.MAX_VALUE, t);
                helper.assertTrue(actual == expected, "Should be able to insert up to the upgraded capacity");
                t.abort();
            }
        }

        helper.succeed();
    }

    private static void insertSingleItem(GameTestHelper helper, BlockPos pos, ItemVariant resource) {
        var storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(pos), Direction.UP);
        helper.assertTrue(storage != null, "Storage must not be null");

        try (var transaction = Transaction.openOuter()) {
            var inserted = storage.insert(resource, 1, transaction);
            helper.assertTrue(inserted == 1, "Drawer should accept a single item to initialize compacting storage");
            transaction.commit();
        }
    }

    private static CompactingDrawerStorage.Slot findSlotForResource(CompactingDrawerStorage storage, ItemVariant resource) {
        for (int i = 0; i < storage.getSlotCount(); i++) {
            var slot = storage.getSlot(i);
            if (!slot.isBlocked() && slot.getResource().equals(resource)) {
                return slot;
            }
        }

        throw new AssertionError("No slot for resource " + resource + " in compacting drawer");
    }

    @GameTest
    public void voidingTest(GameTestHelper helper) {
        var normalSlotPos = new BlockPos(0, 0, 0);
        var normalStoragePos = new BlockPos(0, 0, 2);
        var compactingSlotPos = new BlockPos(3, 0, 0);
        var compactingStoragePos = new BlockPos(3, 0, 2);

        helper.setBlock(normalSlotPos, ModBlocks.SINGLE_DRAWER);
        helper.setBlock(normalStoragePos, ModBlocks.SINGLE_DRAWER);
        helper.setBlock(compactingSlotPos, ModBlocks.COMPACTING_DRAWER);
        helper.setBlock(compactingStoragePos, ModBlocks.COMPACTING_DRAWER);

        helper.runAtTickTime(1, () -> {
            var normalSlotStorage = (SlottedStorage<ItemVariant>) ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(normalSlotPos), Direction.UP);
            helper.assertTrue(normalSlotStorage != null, "Normal drawer slot storage must not be null");
            DrawerSlot normalSlot = (DrawerSlot) normalSlotStorage.getSlot(0);
            normalSlot.setVoiding(true);

            try (var transaction = Transaction.openOuter()) {
                var inserted = normalSlot.insert(ItemVariant.of(Items.STONE), Long.MAX_VALUE, transaction);
                helper.assertTrue(inserted == Long.MAX_VALUE, "Single-slot voiding should consume all inserted items");
                helper.assertTrue(normalSlot.getTrueAmount() == normalSlot.getCapacity(), "Single-slot voiding should fill the drawer to capacity");
                transaction.abort();
            }

            var normalStorage = (SlottedStorage<ItemVariant>) ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(normalStoragePos), Direction.UP);
            helper.assertTrue(normalStorage != null, "Normal drawer storage must not be null");
            DrawerSlot normalStorageSlot = (DrawerSlot) normalStorage.getSlot(0);
            normalStorageSlot.setVoiding(true);

            try (var transaction = Transaction.openOuter()) {
                var inserted = normalStorage.insert(ItemVariant.of(Items.STONE), Long.MAX_VALUE, transaction);
                helper.assertTrue(inserted == Long.MAX_VALUE, "Whole-storage voiding should consume all inserted items");
                helper.assertTrue(normalStorageSlot.getTrueAmount() == normalStorageSlot.getCapacity(), "Whole-storage voiding should fill the drawer to capacity");
                transaction.abort();
            }

            var compactingSlotStorage = (CompactingDrawerStorage) ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(compactingSlotPos), Direction.UP);
            helper.assertTrue(compactingSlotStorage != null, "Compacting drawer slot storage must not be null");
            insertSingleItem(helper, compactingSlotPos, ItemVariant.of(Items.IRON_INGOT));
            compactingSlotStorage.setVoiding(true);
            var compactingSlot = findSlotForResource(compactingSlotStorage, ItemVariant.of(Items.IRON_INGOT));

            try (var transaction = Transaction.openOuter()) {
                var inserted = compactingSlot.insert(ItemVariant.of(Items.IRON_INGOT), Long.MAX_VALUE, transaction);
                helper.assertTrue(inserted == Long.MAX_VALUE, "Compacting single-slot voiding should consume all inserted items");
                helper.assertTrue(compactingSlotStorage.getTrueAmount() == compactingSlotStorage.getCapacity(), "Compacting single-slot voiding should fill to capacity");
                transaction.abort();
            }

            var compactingStorage = (CompactingDrawerStorage) ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(compactingStoragePos), Direction.UP);
            helper.assertTrue(compactingStorage != null, "Compacting drawer storage must not be null");
            insertSingleItem(helper, compactingStoragePos, ItemVariant.of(Items.IRON_INGOT));
            compactingStorage.setVoiding(true);

            try (var transaction = Transaction.openOuter()) {
                var inserted = compactingStorage.insert(ItemVariant.of(Items.IRON_INGOT), Long.MAX_VALUE, transaction);
                helper.assertTrue(inserted == Long.MAX_VALUE, "Compacting whole-storage voiding should consume all inserted items");
                helper.assertTrue(compactingStorage.getTrueAmount() == compactingStorage.getCapacity(), "Compacting whole-storage voiding should fill to capacity");
                transaction.abort();
            }

            helper.succeed();
        });
    }

    @Override
    public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
        // Fix graphlib issues
        NetworkRegistry.UNIVERSE.getGraphWorld(helper.getLevel())
                .updateNodes(BlockPos.betweenClosedStream(helper.getBounds()));

        method.invoke(this, helper);
    }
}
