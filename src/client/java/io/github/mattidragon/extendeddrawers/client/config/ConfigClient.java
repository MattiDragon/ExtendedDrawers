package io.github.mattidragon.extendeddrawers.client.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import dev.isxander.yacl.gui.controllers.string.number.LongFieldController;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import io.github.mattidragon.extendeddrawers.config.category.MiscCategory;
import io.github.mattidragon.extendeddrawers.config.category.StorageCategory;
import io.github.mattidragon.extendeddrawers.misc.CreativeExtractionBehaviour;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.DEFAULT;

public class ConfigClient {
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

    private static ConfigCategory createStorageCategory(StorageCategory.Mutable instance) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("config.extended_drawers.storage"))
                .option(Option.createBuilder(long.class)
                        .name(Text.translatable("config.extended_drawers.storage.defaultCapacity"))
                        .binding(DEFAULT.storage().defaultCapacity(), () -> instance.defaultCapacity, value -> instance.defaultCapacity = value)
                        .controller(option -> new LongFieldController(option, 1, Long.MAX_VALUE))
                        .tooltip(Text.translatable("config.extended_drawers.storage.defaultCapacity.tooltip"))
                        .build())
                .option(Option.createBuilder(long.class)
                        .name(Text.translatable("config.extended_drawers.storage.compactingCapacity"))
                        .binding(DEFAULT.storage().compactingCapacity(), () -> instance.compactingCapacity, value -> instance.compactingCapacity = value)
                        .controller(option -> new LongFieldController(option, 1, Long.MAX_VALUE))
                        .tooltip(Text.translatable("config.extended_drawers.storage.compactingCapacity.tooltip"))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.storage.stackSizeAffectsCapacity"))
                        .binding(DEFAULT.storage().stackSizeAffectsCapacity(), () -> instance.stackSizeAffectsCapacity, value -> instance.stackSizeAffectsCapacity = value)
                        .controller(TickBoxController::new)
                        .tooltip(Text.translatable("config.extended_drawers.storage.stackSizeAffectsCapacity.tooltip"))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.storage.slotCountAffectsCapacity"))
                        .binding(DEFAULT.storage().slotCountAffectsCapacity(), () -> instance.slotCountAffectsCapacity, value -> instance.slotCountAffectsCapacity = value)
                        .controller(TickBoxController::new)
                        .tooltip(Text.translatable("config.extended_drawers.storage.slotCountAffectsCapacity.tooltip"))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers"))
                        .tooltip(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.tooltip"))
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.1"))
                                .binding(DEFAULT.storage().t1UpgradeMultiplier(), () -> instance.t1UpgradeMultiplier, value -> instance.t1UpgradeMultiplier = value)
                                .controller(option -> new IntegerFieldController(option, 1, Integer.MAX_VALUE))
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.2"))
                                .binding(DEFAULT.storage().t2UpgradeMultiplier(), () -> instance.t2UpgradeMultiplier, value -> instance.t2UpgradeMultiplier = value)
                                .controller(option -> new IntegerFieldController(option, 1, Integer.MAX_VALUE))
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.3"))
                                .binding(DEFAULT.storage().t3UpgradeMultiplier(), () -> instance.t3UpgradeMultiplier, value -> instance.t3UpgradeMultiplier = value)
                                .controller(option -> new IntegerFieldController(option, 1, Integer.MAX_VALUE))
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.4"))
                                .binding(DEFAULT.storage().t4UpgradeMultiplier(), () -> instance.t4UpgradeMultiplier, value -> instance.t4UpgradeMultiplier = value)
                                .controller(option -> new IntegerFieldController(option, 1, Integer.MAX_VALUE))
                                .build())
                        .build())
                .build();
    }

    private static ConfigCategory createMiscCategory(MiscCategory.Mutable instance) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("config.extended_drawers.misc"))
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("config.extended_drawers.misc.insertAllTime"))
                        .binding(DEFAULT.misc().insertAllTime(), () -> instance.insertAllTime, value -> instance.insertAllTime = value)
                        .controller(option -> new IntegerFieldController(option, 1, 20))
                        .tooltip(Text.translatable("config.extended_drawers.misc.insertAllTime.tooltip"))
                        .build())
                .option(Option.createBuilder(CreativeExtractionBehaviour.class)
                        .name(Text.translatable("config.extended_drawers.misc.creativeExtractionMode"))
                        .binding(DEFAULT.misc().creativeExtractionMode(), () -> instance.creativeExtractionMode, value -> instance.creativeExtractionMode = value)
                        .controller(option -> new EnumController<>(option, value -> Text.literal(value.asString())))
                        .tooltip(Text.translatable("config.extended_drawers.misc.creativeExtractionMode.tooltip"))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.misc.blockUpgradeRemovalsWithOverflow"))
                        .binding(DEFAULT.misc().blockUpgradeRemovalsWithOverflow(), () -> instance.blockUpgradeRemovalsWithOverflow, value -> instance.blockUpgradeRemovalsWithOverflow = value)
                        .controller(TickBoxController::new)
                        .tooltip(Text.translatable("config.extended_drawers.misc.blockUpgradeRemovalsWithOverflow.tooltip"))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.misc.allowRecursion"))
                        .binding(DEFAULT.misc().allowRecursion(), () -> instance.allowRecursion, value -> instance.allowRecursion = value)
                        .controller(TickBoxController::new)
                        .tooltip(Text.translatable("config.extended_drawers.misc.allowRecursion.tooltip"))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak"))
                        .binding(DEFAULT.misc().drawersDropContentsOnBreak(), () -> instance.drawersDropContentsOnBreak, value -> instance.drawersDropContentsOnBreak = value)
                        .controller(TickBoxController::new)
                        .tooltip(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak.tooltip"))
                        .build())
                .build();
    }

    private static ConfigCategory createClientCategory(ClientCategory.Mutable instance) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("config.extended_drawers.client"))
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("config.extended_drawers.client.itemRenderDistance"))
                        .binding(DEFAULT.client().itemRenderDistance(), () -> instance.itemRenderDistance, value -> instance.itemRenderDistance = value)
                        .controller(option -> new IntegerFieldController(option, 16, 256))
                        .tooltip(Text.translatable("config.extended_drawers.client.itemRenderDistance.tooltip"))
                        .build())
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("config.extended_drawers.client.iconRenderDistance"))
                        .binding(DEFAULT.client().iconRenderDistance(), () -> instance.iconRenderDistance, value -> instance.iconRenderDistance = value)
                        .controller(option -> new IntegerFieldController(option, 16, 256))
                        .tooltip(Text.translatable("config.extended_drawers.client.iconRenderDistance.tooltip"))
                        .build())
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("config.extended_drawers.client.textRenderDistance"))
                        .binding(DEFAULT.client().textRenderDistance(), () -> instance.textRenderDistance, value -> instance.textRenderDistance = value)
                        .controller(option -> new IntegerFieldController(option, 16, 256))
                        .tooltip(Text.translatable("config.extended_drawers.client.textRenderDistance.tooltip"))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.client.displayEmptyCount"))
                        .binding(DEFAULT.client().displayEmptyCount(), () -> instance.displayEmptyCount, value -> instance.displayEmptyCount = value)
                        .controller(TickBoxController::new)
                        .tooltip(Text.translatable("config.extended_drawers.client.displayEmptyCount.tooltip"))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.layout"))
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.smallItemScale"))
                                .binding(DEFAULT.client().smallItemScale(), () -> instance.smallItemScale, value -> instance.smallItemScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .tooltip(Text.translatable("config.extended_drawers.client.smallItemScale.tooltip"))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.largeItemScale"))
                                .binding(DEFAULT.client().largeItemScale(), () -> instance.largeItemScale, value -> instance.largeItemScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .tooltip(Text.translatable("config.extended_drawers.client.largeItemScale.tooltip"))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.smallTextScale"))
                                .binding(DEFAULT.client().smallTextScale(), () -> instance.smallTextScale, value -> instance.smallTextScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .tooltip(Text.translatable("config.extended_drawers.client.smallTextScale.tooltip"))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.largeTextScale"))
                                .binding(DEFAULT.client().largeTextScale(), () -> instance.largeTextScale, value -> instance.largeTextScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .tooltip(Text.translatable("config.extended_drawers.client.largeTextScale.tooltip"))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.textOffset"))
                                .binding(DEFAULT.client().textOffset(), () -> instance.textOffset, value -> instance.textOffset = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .tooltip(Text.translatable("config.extended_drawers.client.textOffset.tooltip"))
                                .build())
                        .build())
                .build();
    }
}
