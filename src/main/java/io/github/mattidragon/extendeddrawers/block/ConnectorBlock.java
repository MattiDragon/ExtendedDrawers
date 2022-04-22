package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import net.minecraft.block.Block;

public class ConnectorBlock extends Block implements NetworkComponent {
    public ConnectorBlock(Settings settings) {
        super(settings);
    }
}
