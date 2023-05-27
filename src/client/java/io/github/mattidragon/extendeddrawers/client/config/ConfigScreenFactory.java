package io.github.mattidragon.extendeddrawers.client.config;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import dev.isxander.yacl.gui.controllers.string.number.LongFieldController;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import io.github.mattidragon.extendeddrawers.config.category.MiscCategory;
import io.github.mattidragon.extendeddrawers.config.category.StorageCategory;
import io.github.mattidragon.extendeddrawers.misc.CreativeBreakingBehaviour;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

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

    private static ConfigCategory createStorageCategory(StorageCategory.Mutable instance) {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("config.extended_drawers.storage"))
                .option(Option.createBuilder(long.class)
                        .name(Text.translatable("config.extended_drawers.storage.drawerCapacity"))
                        .binding(DEFAULT.storage().drawerCapacity(), () -> instance.defaultCapacity, value -> instance.defaultCapacity = value)
                        .controller(option -> new LongFieldController(option, 1, Long.MAX_VALUE))
                        .description(description(Text.translatable("config.extended_drawers.storage.drawerCapacity"), Text.translatable("config.extended_drawers.storage.drawerCapacity.description")))
                        .build())
                .option(Option.createBuilder(long.class)
                        .name(Text.translatable("config.extended_drawers.storage.compactingCapacity"))
                        .binding(DEFAULT.storage().compactingCapacity(), () -> instance.compactingCapacity, value -> instance.compactingCapacity = value)
                        .controller(option -> new LongFieldController(option, 1, Long.MAX_VALUE))
                        .description(description(Text.translatable("config.extended_drawers.storage.compactingCapacity"), Text.translatable("config.extended_drawers.storage.compactingCapacity.description")))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.storage.stackSizeAffectsCapacity"))
                        .binding(DEFAULT.storage().stackSizeAffectsCapacity(), () -> instance.stackSizeAffectsCapacity, value -> instance.stackSizeAffectsCapacity = value)
                        .controller(TickBoxController::new)
                        .description(description(Text.translatable("config.extended_drawers.storage.stackSizeAffectsCapacity"), Text.translatable("config.extended_drawers.storage.stackSizeAffectsCapacity.description")))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.storage.slotCountAffectsCapacity"))
                        .binding(DEFAULT.storage().slotCountAffectsCapacity(), () -> instance.slotCountAffectsCapacity, value -> instance.slotCountAffectsCapacity = value)
                        .controller(TickBoxController::new)
                        .description(description(Text.translatable("config.extended_drawers.storage.slotCountAffectsCapacity"), Text.translatable("config.extended_drawers.storage.slotCountAffectsCapacity.description")))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers"))
                        .description(description(Text.translatable("config.extended_drawers.storage.upgradeMultipliers"), Text.translatable("config.extended_drawers.storage.upgradeMultipliers.description")))
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.1"))
                                .binding(DEFAULT.storage().t1UpgradeMultiplier(), () -> instance.t1UpgradeMultiplier, value -> instance.t1UpgradeMultiplier = value)
                                .controller(option -> new IntegerFieldController(option, 1, Integer.MAX_VALUE))
                                .description(description(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.1"), Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 1)))
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.2"))
                                .binding(DEFAULT.storage().t2UpgradeMultiplier(), () -> instance.t2UpgradeMultiplier, value -> instance.t2UpgradeMultiplier = value)
                                .controller(option -> new IntegerFieldController(option, 1, Integer.MAX_VALUE))
                                .description(description(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.2"), Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 2)))
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.3"))
                                .binding(DEFAULT.storage().t3UpgradeMultiplier(), () -> instance.t3UpgradeMultiplier, value -> instance.t3UpgradeMultiplier = value)
                                .description(description(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.3"), Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 3)))
                                .controller(option -> new IntegerFieldController(option, 1, Integer.MAX_VALUE))
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.4"))
                                .binding(DEFAULT.storage().t4UpgradeMultiplier(), () -> instance.t4UpgradeMultiplier, value -> instance.t4UpgradeMultiplier = value)
                                .description(description(Text.translatable("config.extended_drawers.storage.upgradeMultipliers.4"), Text.translatable("config.extended_drawers.storage.upgradeMultipliers.n.description", 4)))
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
                        .description(description(Text.translatable("config.extended_drawers.misc.insertAllTime"), Text.translatable("config.extended_drawers.misc.insertAllTime.description")))
                        .build())
                .option(Option.createBuilder(CreativeBreakingBehaviour.class)
                        .name(Text.translatable("config.extended_drawers.misc.frontBreakingBehaviour"))
                        .binding(DEFAULT.misc().frontBreakingBehaviour(), () -> instance.frontBreakingBehaviour, value -> instance.frontBreakingBehaviour = value)
                        .controller(option -> new EnumController<>(option, CreativeBreakingBehaviour::getDisplayName))
                        .description(value -> creativeBreakingBehaviourDescription(Text.translatable("config.extended_drawers.misc.frontBreakingBehaviour"), Text.translatable("config.extended_drawers.misc.frontBreakingBehaviour.description"), value))
                        .build())
                .option(Option.createBuilder(CreativeBreakingBehaviour.class)
                        .name(Text.translatable("config.extended_drawers.misc.sideBreakingBehaviour"))
                        .binding(DEFAULT.misc().sideBreakingBehaviour(), () -> instance.sideBreakingBehaviour, value -> instance.sideBreakingBehaviour = value)
                        .controller(option -> new EnumController<>(option, CreativeBreakingBehaviour::getDisplayName))
                        .description(value -> creativeBreakingBehaviourDescription(Text.translatable("config.extended_drawers.misc.sideBreakingBehaviour"), Text.translatable("config.extended_drawers.misc.sideBreakingBehaviour.description"), value))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.misc.blockUpgradeRemovalsWithOverflow"))
                        .binding(DEFAULT.misc().blockUpgradeRemovalsWithOverflow(), () -> instance.blockUpgradeRemovalsWithOverflow, value -> instance.blockUpgradeRemovalsWithOverflow = value)
                        .controller(TickBoxController::new)
                        .description(description(Text.translatable("config.extended_drawers.misc.blockUpgradeRemovalsWithOverflow"), Text.translatable("config.extended_drawers.misc.blockUpgradeRemovalsWithOverflow.description")))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.misc.allowRecursion"))
                        .binding(DEFAULT.misc().allowRecursion(), () -> instance.allowRecursion, value -> instance.allowRecursion = value)
                        .controller(TickBoxController::new)
                        .description(description(Text.translatable("config.extended_drawers.misc.allowRecursion"), Text.translatable("config.extended_drawers.misc.allowRecursion.description").append(Text.translatable("config.extended_drawers.misc.allowRecursion.warning").formatted(Formatting.YELLOW))))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak"))
                        .binding(DEFAULT.misc().drawersDropContentsOnBreak(), () -> instance.drawersDropContentsOnBreak, value -> instance.drawersDropContentsOnBreak = value)
                        .controller(TickBoxController::new)
                        .description(description(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak"), Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak.description").append(Text.translatable("config.extended_drawers.misc.drawersDropContentsOnBreak.warning").formatted(Formatting.YELLOW))))
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
                        .description(description(Text.translatable("config.extended_drawers.client.itemRenderDistance"), Text.translatable("config.extended_drawers.client.itemRenderDistance.description")))
                        .build())
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("config.extended_drawers.client.iconRenderDistance"))
                        .binding(DEFAULT.client().iconRenderDistance(), () -> instance.iconRenderDistance, value -> instance.iconRenderDistance = value)
                        .controller(option -> new IntegerFieldController(option, 16, 256))
                        .description(description(Text.translatable("config.extended_drawers.client.iconRenderDistance"), Text.translatable("config.extended_drawers.client.iconRenderDistance.description")))
                        .build())
                .option(Option.createBuilder(int.class)
                        .name(Text.translatable("config.extended_drawers.client.textRenderDistance"))
                        .binding(DEFAULT.client().textRenderDistance(), () -> instance.textRenderDistance, value -> instance.textRenderDistance = value)
                        .controller(option -> new IntegerFieldController(option, 16, 256))
                        .description(description(Text.translatable("config.extended_drawers.client.textRenderDistance"), Text.translatable("config.extended_drawers.client.textRenderDistance.description")))
                        .build())
                .option(Option.createBuilder(boolean.class)
                        .name(Text.translatable("config.extended_drawers.client.displayEmptyCount"))
                        .binding(DEFAULT.client().displayEmptyCount(), () -> instance.displayEmptyCount, value -> instance.displayEmptyCount = value)
                        .controller(TickBoxController::new)
                        .description(description(Text.translatable("config.extended_drawers.client.displayEmptyCount"), Text.translatable("config.extended_drawers.client.displayEmptyCount.description")))
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("config.extended_drawers.client.layout"))
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.smallItemScale"))
                                .binding(DEFAULT.client().smallItemScale(), () -> instance.smallItemScale, value -> instance.smallItemScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .description(description(Text.translatable("config.extended_drawers.client.smallItemScale"), Text.translatable("config.extended_drawers.client.smallItemScale.description")))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.largeItemScale"))
                                .binding(DEFAULT.client().largeItemScale(), () -> instance.largeItemScale, value -> instance.largeItemScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .description(description(Text.translatable("config.extended_drawers.client.largeItemScale"), Text.translatable("config.extended_drawers.client.largeItemScale.description")))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.smallTextScale"))
                                .binding(DEFAULT.client().smallTextScale(), () -> instance.smallTextScale, value -> instance.smallTextScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .description(description(Text.translatable("config.extended_drawers.client.smallTextScale"), Text.translatable("config.extended_drawers.client.smallTextScale.description")))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.largeTextScale"))
                                .binding(DEFAULT.client().largeTextScale(), () -> instance.largeTextScale, value -> instance.largeTextScale = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .description(description(Text.translatable("config.extended_drawers.client.largeTextScale"), Text.translatable("config.extended_drawers.client.largeTextScale.description")))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.translatable("config.extended_drawers.client.textOffset"))
                                .binding(DEFAULT.client().textOffset(), () -> instance.textOffset, value -> instance.textOffset = value)
                                .controller(option -> new FloatFieldController(option, 0, 2, FLOAT_FORMATTER))
                                .description(description(Text.translatable("config.extended_drawers.client.textOffset"), Text.translatable("config.extended_drawers.client.textOffset.description")))
                                .build())
                        .build())
                .build();
    }

    private static OptionDescription creativeBreakingBehaviourDescription(Text name, Text text, CreativeBreakingBehaviour value) {
        return OptionDescription.createBuilder()
                .name(name)
                .description(text, Text.translatable("config.extended_drawers.creativeBreakingBehaviour." + value.asString() + ".description"))
                .build();
    }

    private static OptionDescription description(Text name, Text... text) {
        return OptionDescription.createBuilder()
                .name(name)
                .description(text)
                .build();
    }
}
