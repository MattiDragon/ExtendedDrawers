package io.github.mattidragon.extendeddrawers.client.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererManager;
import io.github.mattidragon.extendeddrawers.client.config.render.LayoutPreviewImageRenderer;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.config.category.MutableClientCategory;
import io.github.mattidragon.extendeddrawers.config.category.MutableMiscCategory;
import io.github.mattidragon.extendeddrawers.config.category.MutableStorageCategory;
import io.github.mattidragon.extendeddrawers.misc.CreativeBreakingBehaviour;
import io.github.mattidragon.extendeddrawers.network.cache.CachingMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.DEFAULT;

public class ConfigScreenFactory {
    public static final ValueFormatter<Float> FLOAT_FORMATTER;

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
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(CreativeBreakingBehaviour.class).formatValue(CreativeBreakingBehaviour::getDisplayName))
                        .description(value -> creativeBreakingBehaviourDescription(Text.translatable("config.extended_drawers.misc.frontBreakingBehaviour.description"), value))
                        .build())
                .option(Option.<CreativeBreakingBehaviour>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.sideBreakingBehaviour"))
                        .binding(DEFAULT.misc().sideBreakingBehaviour(), instance::sideBreakingBehaviour, instance::sideBreakingBehaviour)
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(CreativeBreakingBehaviour.class).formatValue(CreativeBreakingBehaviour::getDisplayName))
                        .description(value -> creativeBreakingBehaviourDescription(Text.translatable("config.extended_drawers.misc.sideBreakingBehaviour.description"), value))
                        .build())
                .option(Option.<CachingMode>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.cachingMode"))
                        .binding(DEFAULT.misc().cachingMode(), instance::cachingMode, instance::cachingMode)
                        .controller(option -> EnumControllerBuilder.create(option).enumClass(CachingMode.class).formatValue(CachingMode::getDisplayName))
                        .description(option -> OptionDescription.of(Text.translatable("config.extended_drawers.misc.cachingMode.description").append(Text.translatable("config.extended_drawers.cachingMode.%s.description".formatted(option.asString())))))
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
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.misc.allowRecursion.description").append(Text.translatable("config.extended_drawers.misc.allowRecursion.warning").formatted(Formatting.YELLOW))))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak"))
                        .binding(DEFAULT.misc().drawersDropContentsOnBreak(), instance::drawersDropContentsOnBreak, instance::drawersDropContentsOnBreak)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak.description").append(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak.warning").formatted(Formatting.YELLOW))))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.extended_drawers.misc.dropDrawersInCreative"))
                        .binding(DEFAULT.misc().dropDrawersInCreative(), instance::dropDrawersInCreative, instance::dropDrawersInCreative)
                        .controller(TickBoxControllerBuilder::create)
                        .description(OptionDescription.of(Text.translatable("config.extended_drawers.misc.dropDrawersInCreative.description")))
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
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRendererManager.registerOrGetImage(id, () -> () -> () -> new IconRenderer(id)).thenApply(Optional::of)).text(Text.translatable("config.extended_drawers.client.lockedIcon.description")).build())
                        .build())
                .option(Option.<Identifier>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.voidingIcon"))
                        .binding(DEFAULT.client().icons().voidingIcon(), icons::voidingIcon, icons::voidingIcon)
                        .customController(IdentifierController::new)
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRendererManager.registerOrGetImage(id, () -> () -> () -> new IconRenderer(id)).thenApply(Optional::of)).text(Text.translatable("config.extended_drawers.client.voidingIcon.description")).build())
                        .build())
                .option(Option.<Identifier>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.hiddenIcon"))
                        .binding(DEFAULT.client().icons().hiddenIcon(), icons::hiddenIcon, icons::hiddenIcon)
                        .customController(IdentifierController::new)
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRendererManager.registerOrGetImage(id, () -> () -> () -> new IconRenderer(id)).thenApply(Optional::of)).text(Text.translatable("config.extended_drawers.client.hiddenIcon.description")).build())
                        .build())
                .option(Option.<Identifier>createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.dupingIcon"))
                        .binding(DEFAULT.client().icons().dupingIcon(), icons::dupingIcon, icons::dupingIcon)
                        .customController(IdentifierController::new)
                        .description(id -> OptionDescription.createBuilder().customImage(ImageRendererManager.registerOrGetImage(id, () -> () -> () -> new IconRenderer(id)).thenApply(Optional::of)).text(Text.translatable("config.extended_drawers.client.dupingIcon.description")).build())
                        .build())
                .build();
    }

    private static OptionGroup createLayoutGroup(MutableClientCategory.MutableLayoutGroup instance) {
        var layoutRenderer = new LayoutPreviewImageRenderer();

        var smallItemScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.smallItemScale"))
                .binding(DEFAULT.client().layout().smallItemScale(), instance::smallItemScale, instance::smallItemScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).formatValue(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.smallItemScale.description")).build())
                .build();
        var largeItemScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.largeItemScale"))
                .binding(DEFAULT.client().layout().largeItemScale(), instance::largeItemScale, instance::largeItemScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).formatValue(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.largeItemScale.description")).build())
                .build();
        var smallTextScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.smallTextScale"))
                .binding(DEFAULT.client().layout().smallTextScale(), instance::smallTextScale, instance::smallTextScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).formatValue(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.smallTextScale.description")).build())
                .build();
        var largeTextScale = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.largeTextScale"))
                .binding(DEFAULT.client().layout().largeTextScale(), instance::largeTextScale, instance::largeTextScale)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 2f).step(0.05f).formatValue(FLOAT_FORMATTER))
                .description(OptionDescription.createBuilder().customImage(CompletableFuture.completedFuture(Optional.of(layoutRenderer))).text(Text.translatable("config.extended_drawers.client.largeTextScale.description")).build())
                .build();
        var textOffset = Option.<Float>createBuilder()
                .name(Text.translatable("config.extended_drawers.client.textOffset"))
                .binding(DEFAULT.client().layout().textOffset(), instance::textOffset, instance::textOffset)
                .controller(option -> FloatSliderControllerBuilder.create(option).range(0f, 1f).step(0.05f).formatValue(FLOAT_FORMATTER))
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

    private record IconRenderer(Identifier id) implements ImageRenderer {
        @Override
        public int render(DrawContext graphics, int x, int y, int renderWidth, float tickDelta) {
            @SuppressWarnings("deprecation")
            var blockAtlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            var sprite = blockAtlas.apply(id);

            graphics.drawSpriteStretched(RenderPipelines.GUI_TEXTURED, sprite, x + renderWidth / 3, y, renderWidth / 3, renderWidth / 3);

            return renderWidth / 3;
        }

        @Override
        public void close() {

        }
    }
}
