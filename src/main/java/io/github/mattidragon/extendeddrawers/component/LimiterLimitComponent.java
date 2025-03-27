package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public record LimiterLimitComponent(long limit) implements TooltipAppender {
    public static final Codec<LimiterLimitComponent> CODEC = Codec.LONG.xmap(LimiterLimitComponent::new, LimiterLimitComponent::limit);
    public static final PacketCodec<ByteBuf, LimiterLimitComponent> PACKET_CODEC
            = PacketCodecs.VAR_LONG.xmap(LimiterLimitComponent::new, LimiterLimitComponent::limit);

    public static final LimiterLimitComponent NO_LIMIT = new LimiterLimitComponent(Long.MAX_VALUE);

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        textConsumer.accept(Text.translatable("item.extended_drawers.limiter.limit", String.valueOf(limit)).formatted(Formatting.GRAY));
    }
}
