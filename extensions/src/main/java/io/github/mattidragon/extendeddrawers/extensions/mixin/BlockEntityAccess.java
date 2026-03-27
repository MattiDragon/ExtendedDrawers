package io.github.mattidragon.extendeddrawers.extensions.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockEntity.class)
public interface BlockEntityAccess {
    @Mutable
    @Accessor("type")
    void extended_drawers_extensions$setType(BlockEntityType<?> type);
}
