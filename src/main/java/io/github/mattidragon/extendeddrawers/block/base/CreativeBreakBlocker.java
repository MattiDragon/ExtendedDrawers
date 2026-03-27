package io.github.mattidragon.extendeddrawers.block.base;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface CreativeBreakBlocker {
    boolean isFront(BlockState state, Direction direction);
}
