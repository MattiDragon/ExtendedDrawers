package io.github.mattidragon.extendeddrawers.extensions.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record LinkingEnderConnectorComponent(BlockPos pos) implements TooltipProvider {
    public static final Codec<LinkingEnderConnectorComponent> CODEC
            = BlockPos.CODEC.xmap(LinkingEnderConnectorComponent::new, LinkingEnderConnectorComponent::pos);
    public static final StreamCodec<ByteBuf, LinkingEnderConnectorComponent> PACKET_CODEC
            = BlockPos.STREAM_CODEC.map(LinkingEnderConnectorComponent::new, LinkingEnderConnectorComponent::pos);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> textConsumer, TooltipFlag type, DataComponentGetter components) {
        textConsumer.accept(Component.translatable("item.extended_drawers_extensions.ender_connector_linker.linking", pos.toShortString())
                .withStyle(ChatFormatting.GRAY));
    }
}
