package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class DrawerItem extends BlockItem {
    public DrawerItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return ExtendedDrawers.CONFIG.get().misc().allowRecursion();
    }
}
