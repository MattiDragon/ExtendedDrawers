package io.github.mattidragon.extendeddrawers.block.base;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface CreativeBreakBlocker {
    boolean isFront(BlockState state, Direction direction);
}
