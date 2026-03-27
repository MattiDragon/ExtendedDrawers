package io.github.mattidragon.extendeddrawers.extensions.item;

import com.kneelawk.graphlib.api.util.LinkPos;
import com.kneelawk.graphlib.api.util.NodePos;
import io.github.mattidragon.extendeddrawers.extensions.component.LinkingEnderConnectorComponent;
import io.github.mattidragon.extendeddrawers.extensions.network.link.EnderConnectorLinkKey;
import io.github.mattidragon.extendeddrawers.extensions.network.node.EnderConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionDataComponents;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnderConnectorLinkerItem extends Item {
    public EnderConnectorLinkerItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var state = context.getLevel().getBlockState(context.getClickedPos());
        if (!state.is(ExtensionBlocks.ENDER_CONNECTOR)) return InteractionResult.FAIL;

        var component = context.getItemInHand().get(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
        if (component == null) {
            context.getItemInHand().set(ExtensionDataComponents.LINKING_ENDER_CONNECTOR, new LinkingEnderConnectorComponent(context.getClickedPos()));
            sendMessage(context.getPlayer(), Component.translatable("item.extended_drawers_extensions.ender_connector_linker.linking", context.getClickedPos().toShortString()));
            return InteractionResult.SUCCESS;
        }
        var prevPos = component.pos();
        if (prevPos == context.getClickedPos()) {
            context.getItemInHand().remove(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
            sendMessage(context.getPlayer(), Component.translatable("item.extended_drawers_extensions.ender_connector_linker.linking_clear"));
            return InteractionResult.SUCCESS;
        }

        context.getItemInHand().remove(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
        if (context.getLevel() instanceof ServerLevel serverWorld) {
            var graphWorld = NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld);
            var node1 = graphWorld.getNodeAt(new NodePos(prevPos, EnderConnectorBlockNode.INSTANCE));
            var node2 = graphWorld.getNodeAt(new NodePos(context.getClickedPos(), EnderConnectorBlockNode.INSTANCE));

            if (node1 == null || node2 == null) {
                return InteractionResult.FAIL;
            }

            var linkPos = new LinkPos(node1.getPos(), node2.getPos(), EnderConnectorLinkKey.INSTANCE);
            if (graphWorld.linkExistsAt(linkPos)) {
                graphWorld.disconnectNodes(linkPos);
                sendMessage(context.getPlayer(), Component.translatable("item.extended_drawers_extensions.ender_connector_linker.unlink_success"));
            } else {
                graphWorld.connectNodes(linkPos);
                sendMessage(context.getPlayer(), Component.translatable("item.extended_drawers_extensions.ender_connector_linker.linking_success"));
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static void sendMessage(@Nullable Player player, Component text) {
        if (player != null) {
            player.displayClientMessage(text, true);
        }
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (user.isShiftKeyDown()) {
            var stack = user.getItemInHand(hand);
            stack.remove(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
            sendMessage(user, Component.translatable("item.extended_drawers_extensions.ender_connector_linker.linking_clear"));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || stack.has(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
    }
}
