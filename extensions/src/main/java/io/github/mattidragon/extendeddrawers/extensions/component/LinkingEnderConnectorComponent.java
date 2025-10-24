package io.github.mattidragon.extendeddrawers.extensions.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public record LinkingEnderConnectorComponent(BlockPos pos) implements TooltipAppender {
    public static final Codec<LinkingEnderConnectorComponent> CODEC
            = BlockPos.CODEC.xmap(LinkingEnderConnectorComponent::new, LinkingEnderConnectorComponent::pos);
    public static final PacketCodec<ByteBuf, LinkingEnderConnectorComponent> PACKET_CODEC
            = BlockPos.PACKET_CODEC.xmap(LinkingEnderConnectorComponent::new, LinkingEnderConnectorComponent::pos);

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        textConsumer.accept(Text.translatable("item.extended_drawers_extensions.ender_connector_linker.linking", pos.toShortString())
                .formatted(Formatting.GRAY));
    }
}
