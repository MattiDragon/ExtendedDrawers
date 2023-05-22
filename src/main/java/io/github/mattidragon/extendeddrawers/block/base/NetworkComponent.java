package io.github.mattidragon.extendeddrawers.block.base;

import com.kneelawk.graphlib.api.graph.NodeContext;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;

public interface NetworkComponent {
    DrawerNetworkBlockNode getNode(NodeContext context);
}
