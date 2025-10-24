package io.github.mattidragon.extendeddrawers.extensions.item;

import com.kneelawk.graphlib.api.util.LinkPos;
import com.kneelawk.graphlib.api.util.NodePos;
import io.github.mattidragon.extendeddrawers.extensions.component.LinkingEnderConnectorComponent;
import io.github.mattidragon.extendeddrawers.extensions.network.link.EnderConnectorLinkKey;
import io.github.mattidragon.extendeddrawers.extensions.network.node.EnderConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionDataComponents;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderConnectorLinkerItem extends Item {
    public EnderConnectorLinkerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var state = context.getWorld().getBlockState(context.getBlockPos());
        if (!state.isOf(ExtensionBlocks.ENDER_CONNECTOR)) return ActionResult.FAIL;

        var component = context.getStack().get(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
        if (component == null) {
            context.getStack().set(ExtensionDataComponents.LINKING_ENDER_CONNECTOR, new LinkingEnderConnectorComponent(context.getBlockPos()));
            sendMessage(context.getPlayer(), Text.translatable("item.extended_drawers_extensions.ender_connector_linker.linking", context.getBlockPos().toShortString()));
            return ActionResult.SUCCESS;
        }
        var prevPos = component.pos();
        if (prevPos == context.getBlockPos()) {
            context.getStack().remove(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
            sendMessage(context.getPlayer(), Text.translatable("item.extended_drawers_extensions.ender_connector_linker.linking_clear"));
            return ActionResult.SUCCESS;
        }

        context.getStack().remove(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
        if (context.getWorld() instanceof ServerWorld serverWorld) {
            var graphWorld = NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld);
            var node1 = graphWorld.getNodeAt(new NodePos(prevPos, EnderConnectorBlockNode.INSTANCE));
            var node2 = graphWorld.getNodeAt(new NodePos(context.getBlockPos(), EnderConnectorBlockNode.INSTANCE));

            if (node1 == null || node2 == null) {
                return ActionResult.FAIL;
            }

            var linkPos = new LinkPos(node1.getPos(), node2.getPos(), EnderConnectorLinkKey.INSTANCE);
            if (graphWorld.linkExistsAt(linkPos)) {
                graphWorld.disconnectNodes(linkPos);
                sendMessage(context.getPlayer(), Text.translatable("item.extended_drawers_extensions.ender_connector_linker.unlink_success"));
            } else {
                graphWorld.connectNodes(linkPos);
                sendMessage(context.getPlayer(), Text.translatable("item.extended_drawers_extensions.ender_connector_linker.linking_success"));
            }
        }

        return ActionResult.SUCCESS;
    }

    private static void sendMessage(@Nullable PlayerEntity player, Text text) {
        if (player != null) {
            player.sendMessage(text, true);
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            var stack = user.getStackInHand(hand);
            stack.remove(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
            sendMessage(user, Text.translatable("item.extended_drawers_extensions.ender_connector_linker.linking_clear"));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return super.hasGlint(stack) || stack.contains(ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
    }
}
