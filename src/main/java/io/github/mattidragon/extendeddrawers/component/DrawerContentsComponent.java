package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

public record DrawerContentsComponent(List<DrawerSlotComponent> slots) implements TooltipAppender {
    public static final Codec<DrawerContentsComponent> CODEC = DrawerSlotComponent.CODEC.listOf(1, 4).xmap(DrawerContentsComponent::new, DrawerContentsComponent::slots);
    public static final PacketCodec<RegistryByteBuf, DrawerContentsComponent> PACKET_CODEC = DrawerSlotComponent.PACKET_CODEC.collect(PacketCodecs.toList(4)).xmap(DrawerContentsComponent::new, DrawerContentsComponent::slots);

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> consumer, TooltipType type, ComponentsAccess components) {
        var list = slots()
                .stream()
                .filter(slot -> !slot.item().isBlank() || slot.upgrade() != null || slot.hidden() || slot.locked() || slot.voiding() || slot.duping())
                .toList();
        if (list.isEmpty()) return;
        boolean shift = ExtendedDrawers.SHIFT_ACCESS.isShiftPressed();

        if (!shift) {
            consumer.accept(Text.translatable("tooltip.extended_drawers.shift_for_modifiers").formatted(Formatting.GRAY));
            consumer.accept(Text.empty());
        }

        if (!list.stream().allMatch(slot -> slot.item().isBlank()) || shift)
            consumer.accept(Text.translatable("tooltip.extended_drawers.drawer_contents").formatted(Formatting.GRAY));
        for (var slot : list) {
            MutableText text;
            if (!slot.item().isBlank()) {
                text = Text.literal(" - ");
                text.append(Text.literal(String.valueOf(slot.amount())))
                        .append(" ")
                        .append(slot.item().toStack().getName());
            } else if (shift) {
                text = Text.literal(" - ");
                text.append(Text.translatable("tooltip.extended_drawers.empty").formatted(Formatting.ITALIC));
            } else continue;

            // Seems like client code is safe here. If this breaks then other mods are broken too.
            if (shift) {
                text.append("  ")
                        .append(Text.literal("V").formatted(slot.voiding() ? Formatting.WHITE : Formatting.DARK_GRAY))
                        .append(Text.literal("L").formatted(slot.locked() ? Formatting.WHITE : Formatting.DARK_GRAY))
                        .append(Text.literal("H").formatted(slot.hidden() ? Formatting.WHITE : Formatting.DARK_GRAY));
                if (slot.duping())
                    text.append(Text.literal("D").formatted(Formatting.WHITE));

                if (!slot.upgrade().isBlank()) {
                    text.append(" ").append(slot.upgrade().getItem().getName().copy().formatted(Formatting.AQUA));
                }
            }
            consumer.accept(text.formatted(Formatting.GRAY));
        }
    }
}
