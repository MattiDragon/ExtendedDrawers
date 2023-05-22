package io.github.mattidragon.extendeddrawers.block;

import com.kneelawk.graphlib.api.graph.NodeContext;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlock;
import io.github.mattidragon.extendeddrawers.network.node.ConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;

public class ConnectorBlock extends NetworkBlock {
    public ConnectorBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public DrawerNetworkBlockNode getNode(NodeContext context) {
        return new ConnectorBlockNode(context);
    }
}
