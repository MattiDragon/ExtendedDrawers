package io.github.mattidragon.extendeddrawers.client.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.ImageRenderer;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.client.renderer.AbstractDrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import io.github.mattidragon.extendeddrawers.config.category.MutableClientCategory;
import io.github.mattidragon.extendeddrawers.config.category.MutableMiscCategory;
import io.github.mattidragon.extendeddrawers.config.category.MutableStorageCategory;
import io.github.mattidragon.extendeddrawers.misc.CreativeBreakingBehaviour;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;
import static io.github.mattidragon.extendeddrawers.config.ConfigData.DEFAULT;

public class ConfigScreenFactory {
    public static final Function<Float, Text> FLOAT_FORMATTER;

    static {
        var format = NumberFormat.getNumberInstance(Locale.ROOT);
        format.setMaximumFractionDigits(3);
        FLOAT_FORMATTER = (value) -> Text.literal(format.format(value));
    }

    public static Screen createScreen(Screen parent, ConfigData config, Consumer<ConfigData> saveConsumer) {
        var client = config.client().toMutable();
        var storage = config.storage().toMutable();
        var misc = config.misc().toMutable();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("config.extended_drawers"))
                .category(createStorageCategory(storage))
                .category(createMiscCategory(misc))
                .category(createClientCategory(client))
                .save(() -> saveConsumer.accept(new ConfigData(client.toImmutable(), storage.toImmutable(), misc.toImmutable())))
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory createStorageCategory(MutableStorageCategory instance) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("config.extended_drawers.storage"))
                .option(Option.<Long>createBuilder()
                        .name(Text.translatable("config.extended_drawers.storage.drawerCapacity"))
                        .binding(DEFAULT.storage().drawerCapacity(), instance::drawerCapacity, instance::drawerCapacity)
                        .controller(option -> LongFieldControllerBuilder.create(option).min(1L))
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.drawerCapacity.description")))
                        .build())
                .option(Option.<Long>createBuilder()
                        .name(Text.translatable("config.extended_drawers.storage.compactingCapacity"))
                        .binding(DEFAULT.storage().compactingCapacity(), instance::compactingCapacity, instance::compactingCapacity)
                        .controller(option -> LongFieldControllerBuilder.create(option).min(1L))
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.compactingCapacity.description")))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.storage.stackSizeAffectsCapacity"))
                        .binding(DEFAULT.storage().stackSizeAffectsCapacity(), instance::stackSizeAffectsCapacity, instance::stackSizeAffectsCapacity)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.stackSizeAffectsCapacity.description")))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.storage.slotCountAffectsCapacity"))
                        .binding(DEFAULT.storage().slotCountAffectsCapacity(), instance::slotCountAffectsCapacity, instance::slotCountAffectsCapacity)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.slotCountAffectsCapacity.description")))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers"))
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.description")))
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.1"))
                                .binding(DEFAULT.storage().t1UpgradeMultiplier(), instance::t1UpgradeMultiplier, instance::t1UpgradeMultiplier)
                                .controller(option -> IntegerFieldControllerBuilder.create(option).min(1))
                                .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 1)))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.2"))
                                .binding(DEFAULT.storage().t2UpgradeMultiplier(), instance::t2UpgradeMultiplier, instance::t2UpgradeMultiplier)
                                .controller(option -> IntegerFieldControllerBuilder.create(option).min(1))
                                .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 2)))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.3"))
                                .binding(DEFAULT.storage().t3UpgradeMultiplier(), instance::t3UpgradeMultiplier, instance::t3UpgradeMultiplier)
                                .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 3)))
                                .controller(option -> IntegerFieldControllerBuilder.create(option).min(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.4"))
                                .binding(DEFAULT.storage().t4UpgradeMultiplier(), instance::t4UpgradeMultiplier, instance::t4UpgradeMultiplier)
                                .description(OptionDescription.of(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 4)))
                                .controller(option -> IntegerFieldControllerBuilder.create(option).min(1))
                                .build())
                        .build())
                .build();
    }

    private static ConfigCategory createMiscCategory(MutableMiscCategory instance) {
        var text = new Text[]{Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak.description").append(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak.warning").formatted(Formatting.YELLOW))};
        var text1 = new Text[]{Text.translatable("config.extended_drawers.misc.allowRecursion.description").append(Text.translatable("config.extended_drawers.misc.allowRecursion.warning").formatted(Formatting.YELLOW))};
        return ConfigCategory.createBuilder()
                .name(Text.translatable("config.extended_drawers.misc"))
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.insertAllTime"))
                        .binding(DEFAULT.misc().insertAllTime(), instance::insertAllTime, instance::insertAllTime)
                        .controller(option -> IntegerFieldControllerBuilder.create(option)
                                .min(1)
                                .max(20))
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.misc.insertAllTime.description")))
                        .build())
                .option(Option.<CreativeBreakingBehaviour>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.frontBreakingBehaviour"))
                        .binding(DEFAULT.misc().frontBreakingBehaviour(), instance::frontBreakingBehaviour, instance::frontBreakingBehaviour)
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(CreativeBreakingBehaviour.class).valueFormatter(CreativeBreakingBehaviour::getDisplayName))
                        .description(value -> creativeBreakingBehaviourDescription(Text.translatable("config.extended_drawers.misc.frontBreakingBehaviour.description"), value))
                        .build())
                .option(Option.<CreativeBreakingBehaviour>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.sideBreakingBehaviour"))
                        .binding(DEFAULT.misc().sideBreakingBehaviour(), instance::sideBreakingBehaviour, instance::sideBreakingBehaviour)
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(CreativeBreakingBehaviour.class).valueFormatter(CreativeBreakingBehaviour::getDisplayName))
                        .description(value -> creativeBreakingBehaviourDescription(Text.translatable("config.extended_drawers.misc.sideBreakingBehaviour.description"), value))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.blockUpgradeRemovalsWithOverflow"))
                        .binding(DEFAULT.misc().blockUpgradeRemovalsWithOverflow(), instance::blockUpgradeRemovalsWithOverflow, instance::blockUpgradeRemovalsWithOverflow)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.misc.blockUpgradeRemovalsWithOverflow.description")))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.allowRecursion"))
                        .binding(DEFAULT.misc().allowRecursion(), instance::allowRecursion, instance::allowRecursion)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(text1))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak"))
                        .binding(DEFAULT.misc().drawersDropContentsOnBreak(), instance::drawersDropContentsOnBreak, instance::drawersDropContentsOnBreak)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(text))
                        .build())
                .build();
    }

    private static ConfigCategory createClientCategory(MutableClientCategory instance) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("config.extended_drawers.client"))
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.itemRenderDistance"))
                        .binding(DEFAULT.client().itemRenderDistance(), instance::itemRenderDistance, instance::itemRenderDistance)
                        .controller(option -> IntegerFieldControllerBuilder.create(option).min(16).max(256))
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.client.itemRenderDistance.description")))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.iconRenderDistance"))
                        .binding(DEFAULT.client().iconRenderDistance(), instance::iconRenderDistance, instance::iconRenderDistance)
                        .controller(option -> IntegerFieldControllerBuilder.create(option).min(16).max(256))
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.client.iconRenderDistance.description")))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.textRenderDistance"))
                        .binding(DEFAULT.client().textRenderDistance(), instance::textRenderDistance, instance::textRenderDistance)
                        .controller(option -> IntegerFieldControllerBuilder.create(option).min(16).max(256))
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.client.textRenderDistance.description")))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.displayEmptyCount"))
                        .binding(DEFAULT.client().displayEmptyCount(), instance::displayEmptyCount, instance::displayEmptyCount)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.client.displayEmptyCount.description")))
                        .build())
                .group(createLayoutGroup(instance.layout()))
                .group(createIconGroup(instance.icons()))
                .build();
    }

    private static OptionGroup createIconGroup(MutableClientCategory.MutableIconGroup icons) {
        return OptionGroup.createBuilder()
                .name(Text.translatable("config.extended_drawers.client.icons"))
                .collapsed(true)
                .option(Option.<Identifier>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.lockedIcon"))
                        .binding(DEFAULT.client().icons().lockedIcon(), icons::lockedIcon, icons::lockedIcon)
                        .customController(IdentifierController::new)
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRenderer.getOrMakeSync(id, () -> Optional.of(new IconRenderer(id)))).text(Text.translatable("config.extended_drawers.client.lockedIcon.description")).build())
                        .build())
                .option(Option.<Identifier>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.voidingIcon"))
                        .binding(DEFAULT.client().icons().voidingIcon(), icons::voidingIcon, icons::voidingIcon)
                        .customController(IdentifierController::new)
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRenderer.getOrMakeSync(id, () -> Optional.of(new IconRenderer(id)))).text(Text.translatable("config.extended_drawers.client.voidingIcon.description")).build())
                        .build())
                .option(Option.<Identifier>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.hiddenIcon"))
                        .binding(DEFAULT.client().icons().hiddenIcon(), icons::hiddenIcon, icons::hiddenIcon)
                        .customController(IdentifierController::new)
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRenderer.getOrMakeSync(id, () -> Optional.of(new IconRenderer(id)))).text(Text.translatable("config.extended_drawers.client.hiddenIcon.description")).build())
                        .build())
                .option(Option.<Identifier>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.dupingIcon"))
                        .binding(DEFAULT.client().icons().dupingIcon(), icons::dupingIcon, icons::dupingIcon)
                        .customController(IdentifierController::new)
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRenderer.getOrMakeSync(id, () -> Optional.of(new IconRenderer(id)))).text(Text.translatable("config.extended_drawers.client.dupingIcon.description")).build())
                        .build())
                .build();
    }

    private static OptionGroup createLayoutGroup(MutableClientCategory.MutableLayoutGroup instance) {
        var layoutRenderer = new LayoutRenderer();

        var smallItemScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.smallItemScale"))
                .binding(DEFAULT.client().layout().smallItemScale(), instance::smallItemScale, instance::smallItemScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).valueFormatter(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.smallItemScale.description")).build())
                .build();
        var largeItemScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.largeItemScale"))
                .binding(DEFAULT.client().layout().largeItemScale(), instance::largeItemScale, instance::largeItemScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).valueFormatter(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.largeItemScale.description")).build())
                .build();
        var smallTextScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.smallTextScale"))
                .binding(DEFAULT.client().layout().smallTextScale(), instance::smallTextScale, instance::smallTextScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).valueFormatter(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.smallTextScale.description")).build())
                .build();
        var largeTextScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.largeTextScale"))
                .binding(DEFAULT.client().layout().largeTextScale(), instance::largeTextScale, instance::largeTextScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).valueFormatter(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.largeTextScale.description")).build())
                .build();
        var textOffset = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.textOffset"))
                .binding(DEFAULT.client().layout().textOffset(), instance::textOffset, instance::textOffset)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 1f).step(0.05f).valueFormatter(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.textOffset.description")).build())
                .build();

        layoutRenderer.init(smallItemScale, largeItemScale, smallTextScale, largeTextScale, textOffset);

        return OptionGroup.createBuilder()
                .name(Text.translatable("config.extended_drawers.client.layout"))
                .collapsed(true)
                .option(smallItemScale)
                .option(largeItemScale)
                .option(smallTextScale)
                .option(largeTextScale)
                .option(textOffset)
                .build();
    }

    private static OptionDescription creativeBreakingBehaviourDescription(Text text, CreativeBreakingBehaviour value) {
        return OptionDescription.of(text, Text.translatable("config.extended_drawers.creativeBreakingBehaviour." + value.asString() + ".description"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static class LayoutRenderer implements ImageRenderer {
        private Option<Float> smallItemScale = null;
        private Option<Float> largeItemScale = null;
        private Option<Float> smallTextScale = null;
        private Option<Float> largeTextScale = null;
        private Option<Float> textOffset = null;
        private boolean initialized = false;

        public void init(Option<Float> smallItemScale, Option<Float> largeItemScale, Option<Float> smallTextScale, Option<Float> largeTextScale, Option<Float> textOffset) {
            this.smallItemScale = smallItemScale;
            this.largeItemScale = largeItemScale;
            this.smallTextScale = smallTextScale;
            this.largeTextScale = largeTextScale;
            this.textOffset = textOffset;
            this.initialized = true;
        }

        @Override
        public int render(DrawContext context, int x, int y, int renderWidth) {
            if (!initialized) return 0;

            var atlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
            var size = renderWidth / 3;
            var config = ExtendedDrawers.CONFIG.get();
            var client = config.client();
            var newConfig = new ConfigData(
                    new ClientCategory(client.itemRenderDistance(),
                            client.iconRenderDistance(),
                            client.textRenderDistance(),
                            client.displayEmptyCount(),
                            new ClientCategory.LayoutGroup(smallItemScale.pendingValue(),
                                    largeItemScale.pendingValue(),
                                    smallTextScale.pendingValue(),
                                    largeTextScale.pendingValue(),
                                    textOffset.pendingValue()),
                            client.icons()),
                    config.storage(),
                    config.misc());

            var renderer = AbstractDrawerBlockEntityRenderer.createRendererTool();
            var matrices = context.getMatrices();

            var player = MinecraftClient.getInstance().player;
            var playerPos = player == null ? BlockPos.ORIGIN : player.getBlockPos();

            try (var ignored = ExtendedDrawers.CONFIG.override(newConfig)) {
                context.drawSprite(x, y, 0, size, size, atlas.apply(id("block/single_drawer")));
                context.drawSprite(x + size, y, 0, size, size, atlas.apply(id("block/quad_drawer")));
                context.drawSprite(x + 2 * size, y, 0, size, size, atlas.apply(id("block/compacting_drawer")));

                matrices.push();
                matrices.translate(x, y, 1);
                matrices.scale(size, size, -size);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                matrices.translate(0.5, -0.5, 0);

                var voidingSprite = atlas.apply(new Identifier("minecraft", "item/lava_bucket"));
                var lockSprite = atlas.apply(id("item/lock"));
                var upgrade2Sprite = atlas.apply(id("item/t2_upgrade"));
                var upgrade4Sprite = atlas.apply(id("item/t4_upgrade"));

                renderer.renderSlot(ItemVariant.of(Items.COBBLESTONE), String.valueOf((Long) 128L), false, false, List.of(lockSprite), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);

                matrices.translate(0.75, 0.25, 0);
                renderer.renderSlot(ItemVariant.of(Items.REDSTONE), String.valueOf((Long) 16L), true, false, List.of(lockSprite), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
                matrices.translate(0.5, 0, 0);
                renderer.renderSlot(ItemVariant.of(Items.GUNPOWDER), String.valueOf((Long) 32L), true, false, List.of(voidingSprite), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
                matrices.translate(-0.5, -0.5, 0);
                renderer.renderSlot(ItemVariant.of(Items.SUGAR), String.valueOf((Long) 64L), true, false, List.of(lockSprite, voidingSprite, upgrade2Sprite), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
                matrices.translate(0.5, 0, 0);
                renderer.renderSlot(ItemVariant.of(Items.GLOWSTONE_DUST), String.valueOf((Long) 128L), true, false, List.of(upgrade4Sprite), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);

                matrices.translate(0.75, 0.5, 0);
                renderer.renderIcons(List.of(lockSprite, voidingSprite, upgrade4Sprite), true, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, matrices, context.getVertexConsumers());
                renderer.renderSlot(ItemVariant.of(Items.IRON_INGOT), String.valueOf((Long) 9L), true, false, List.of(), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
                matrices.translate(0.25, -0.5, 0);
                renderer.renderSlot(ItemVariant.of(Items.IRON_NUGGET), String.valueOf((Long) 81L), true, false, List.of(), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
                matrices.translate(-0.5, 0, 0);
                renderer.renderSlot(ItemVariant.of(Items.IRON_BLOCK), String.valueOf((Long) 1L), true, false, List.of(), matrices, context.getVertexConsumers(), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);

                matrices.pop();
            }

            return size;
        }

        @Override
        public void close() {

        }
    }

    private record IconRenderer(Identifier id) implements ImageRenderer {
        @Override
        public int render(DrawContext graphics, int x, int y, int renderWidth) {
            var blockAtlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
            var sprite = blockAtlas.apply(id);

            graphics.drawSprite(x + renderWidth / 3, y, 0, renderWidth / 3, renderWidth / 3, sprite);

            return renderWidth / 3;
        }

        @Override
        public void close() {

        }
    }
}
