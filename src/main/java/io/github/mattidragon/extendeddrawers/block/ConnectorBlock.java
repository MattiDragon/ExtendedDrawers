package io.github.mattidragon.extendeddrawers.block;

import com.kneelawk.graphlib.graph.BlockNode;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlock;
import io.github.mattidragon.extendeddrawers.network.node.ConnectorBlockNode;

import java.util.Collection;
import java.util.List;

public class ConnectorBlock extends NetworkBlock {
    public ConnectorBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public Collection<BlockNode> createNodes() {
        return List.of(ConnectorBlockNode.INSTANCE);
    }
}
