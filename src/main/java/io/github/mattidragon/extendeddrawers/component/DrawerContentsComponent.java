package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.List;
import java.util.function.Consumer;

public record DrawerContentsComponent(List<DrawerSlotComponent> slots) implements TooltipProvider {
    public static final Codec<DrawerContentsComponent> CODEC = DrawerSlotComponent.CODEC.listOf(1, 4).xmap(DrawerContentsComponent::new, DrawerContentsComponent::slots);
    public static final StreamCodec<RegistryFriendlyByteBuf, DrawerContentsComponent> PACKET_CODEC = DrawerSlotComponent.PACKET_CODEC.apply(ByteBufCodecs.list(4)).map(DrawerContentsComponent::new, DrawerContentsComponent::slots);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag type, DataComponentGetter components) {
        var list = slots()
                .stream()
                .filter(slot -> !slot.item().isBlank() || slot.upgrade() != null || slot.hidden() || slot.locked() || slot.voiding() || slot.duping())
                .toList();
        if (list.isEmpty()) return;
        boolean shift = ExtendedDrawers.SHIFT_ACCESS.isShiftPressed();

        if (!shift) {
            consumer.accept(Component.translatable("tooltip.extended_drawers.shift_for_modifiers").withStyle(ChatFormatting.GRAY));
            consumer.accept(Component.empty());
        }

        if (!list.stream().allMatch(slot -> slot.item().isBlank()) || shift)
            consumer.accept(Component.translatable("tooltip.extended_drawers.drawer_contents").withStyle(ChatFormatting.GRAY));
        for (var slot : list) {
            MutableComponent text;
            if (!slot.item().isBlank()) {
                text = Component.literal(" - ");
                text.append(Component.literal(String.valueOf(slot.amount())))
                        .append(" ")
                        .append(slot.item().toStack().getHoverName());
            } else if (shift) {
                text = Component.literal(" - ");
                text.append(Component.translatable("tooltip.extended_drawers.empty").withStyle(ChatFormatting.ITALIC));
            } else continue;

            // Seems like client code is safe here. If this breaks then other mods are broken too.
            if (shift) {
                text.append("  ")
                        .append(Component.literal("V").withStyle(slot.voiding() ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY))
                        .append(Component.literal("L").withStyle(slot.locked() ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY))
                        .append(Component.literal("H").withStyle(slot.hidden() ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY));
                if (slot.duping())
                    text.append(Component.literal("D").withStyle(ChatFormatting.WHITE));

                if (!slot.upgrade().isBlank()) {
                    text.append(" ").append(slot.upgrade().getItem().getName().copy().withStyle(ChatFormatting.AQUA));
                }
            }
            consumer.accept(text.withStyle(ChatFormatting.GRAY));
        }
    }
}
