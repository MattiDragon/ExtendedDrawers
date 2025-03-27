package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public record DrawerSlotComponent(
        ItemVariant upgrade,
        ItemVariant limiter,
        boolean locked,
        boolean hidden,
        boolean voiding,
        boolean duping,
        ItemVariant item,
        long amount
) implements TooltipAppender {
    public static final Codec<DrawerSlotComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemVariant.CODEC.fieldOf("upgrade").forGetter(DrawerSlotComponent::upgrade),
            ItemVariant.CODEC.fieldOf("limiter").forGetter(DrawerSlotComponent::limiter),
            Codec.BOOL.fieldOf("locked").forGetter(DrawerSlotComponent::locked),
            Codec.BOOL.fieldOf("hidden").forGetter(DrawerSlotComponent::hidden),
            Codec.BOOL.fieldOf("voiding").forGetter(DrawerSlotComponent::voiding),
            Codec.BOOL.fieldOf("duping").forGetter(DrawerSlotComponent::duping),
            ItemVariant.CODEC.fieldOf("item").forGetter(DrawerSlotComponent::item),
            Codec.LONG.fieldOf("amount").forGetter(DrawerSlotComponent::amount)
    ).apply(instance, DrawerSlotComponent::new));
    public static final PacketCodec<RegistryByteBuf, DrawerSlotComponent> PACKET_CODEC = PacketCodecs.registryCodec(CODEC);

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> consumer, TooltipType type, ComponentsAccess components) {
        if (ExtendedDrawers.SHIFT_ACCESS.isShiftPressed()) {
            if (upgrade().getItem() instanceof UpgradeItem upgradeItem) {
                consumer.accept(upgradeItem.getName().copy().formatted(Formatting.AQUA));
            }

            var modifierText = Text.empty()
                    .append(Text.literal("V").formatted(voiding() ? Formatting.WHITE : Formatting.DARK_GRAY))
                    .append(Text.literal("L").formatted(locked() ? Formatting.WHITE : Formatting.DARK_GRAY))
                    .append(Text.literal("H").formatted(hidden() ? Formatting.WHITE : Formatting.DARK_GRAY));
            if (duping()) {
                modifierText.append(Text.literal("D").formatted(Formatting.WHITE));
            }

            consumer.accept(Text.translatable("tooltip.extended_drawers.modifiers", modifierText).formatted(Formatting.GRAY));
        } else {
            consumer.accept(Text.translatable("tooltip.extended_drawers.shift_for_modifiers").formatted(Formatting.GRAY));
        }
        consumer.accept(Text.empty());

        consumer.accept(Text.translatable("tooltip.extended_drawers.drawer_contents").formatted(Formatting.GRAY));
        consumer.accept(Text.literal(" - ")
                .append(Text.literal(String.valueOf(amount())))
                .append(" ")
                .append(item().toStack().getName())
                .formatted(Formatting.GRAY));

    }
}