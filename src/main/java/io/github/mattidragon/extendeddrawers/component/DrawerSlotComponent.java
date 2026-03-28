package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

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
) implements TooltipProvider {
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
    public static final StreamCodec<RegistryFriendlyByteBuf, DrawerSlotComponent> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        if (ExtendedDrawers.SHIFT_ACCESS.isShiftPressed()) {
            if (upgrade().getItem() instanceof UpgradeItem) {
                consumer.accept(upgrade().toStack().getDisplayName().copy().withStyle(ChatFormatting.AQUA));
            }

            var modifierText = Component.empty()
                    .append(Component.literal("V").withStyle(voiding() ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY))
                    .append(Component.literal("L").withStyle(locked() ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY))
                    .append(Component.literal("H").withStyle(hidden() ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY));
            if (duping()) {
                modifierText.append(Component.literal("D").withStyle(ChatFormatting.WHITE));
            }

            consumer.accept(Component.translatable("tooltip.extended_drawers.modifiers", modifierText).withStyle(ChatFormatting.GRAY));
        } else {
            consumer.accept(Component.translatable("tooltip.extended_drawers.shift_for_modifiers").withStyle(ChatFormatting.GRAY));
        }
        consumer.accept(Component.empty());

        consumer.accept(Component.translatable("tooltip.extended_drawers.drawer_contents").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.literal(" - ")
                .append(Component.literal(String.valueOf(amount())))
                .append(" ")
                .append(item().toStack().getHoverName())
                .withStyle(ChatFormatting.GRAY));

    }
}