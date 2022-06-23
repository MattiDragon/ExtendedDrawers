package io.github.mattidragon.extendeddrawers.block.base;

import com.kneelawk.graphlib.graph.BlockNode;

import java.util.Collection;

public interface NetworkComponent {
    Collection<BlockNode> createNodes();
}
