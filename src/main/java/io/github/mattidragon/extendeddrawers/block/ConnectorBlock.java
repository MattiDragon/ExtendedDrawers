package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.NetworkBlock;
import io.github.mattidragon.extendeddrawers.network.node.ConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;

public class ConnectorBlock extends NetworkBlock {
    public ConnectorBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public DrawerNetworkBlockNode getNode() {
        return ConnectorBlockNode.INSTANCE;
    }
}
