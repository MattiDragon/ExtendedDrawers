package io.github.mattidragon.extendeddrawers.gametest;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.ModBlocks;
import io.github.mattidragon.extendeddrawers.item.ModItems;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
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

    @Override
    public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
        // Fix graphlib issues
        NetworkRegistry.UNIVERSE.getGraphWorld(helper.getLevel())
                .updateNodes(BlockPos.betweenClosedStream(helper.getBounds()));

        method.invoke(this, helper);
    }
}
