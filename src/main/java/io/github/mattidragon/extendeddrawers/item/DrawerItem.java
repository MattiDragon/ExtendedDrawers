package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class DrawerItem extends BlockItem {
    public DrawerItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public boolean canBeNested() {
        return ExtendedDrawers.CONFIG.get().misc().allowRecursion();
    }
}
