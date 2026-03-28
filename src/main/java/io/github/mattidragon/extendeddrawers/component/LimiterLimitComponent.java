package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record LimiterLimitComponent(long limit) implements TooltipProvider {
    public static final Codec<LimiterLimitComponent> CODEC = Codec.LONG.xmap(LimiterLimitComponent::new, LimiterLimitComponent::limit);
    public static final StreamCodec<ByteBuf, LimiterLimitComponent> PACKET_CODEC
            = ByteBufCodecs.VAR_LONG.map(LimiterLimitComponent::new, LimiterLimitComponent::limit);

    public static final LimiterLimitComponent NO_LIMIT = new LimiterLimitComponent(Long.MAX_VALUE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("item.extended_drawers.limiter.limit", String.valueOf(limit)).withStyle(ChatFormatting.GRAY));
    }
}
