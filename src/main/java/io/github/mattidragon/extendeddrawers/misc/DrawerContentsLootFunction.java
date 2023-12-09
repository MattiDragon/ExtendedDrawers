package io.github.mattidragon.extendeddrawers.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.List;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class DrawerContentsLootFunction extends ConditionalLootFunction {
    private static final Codec<DrawerContentsLootFunction> CODEC = RecordCodecBuilder.create((instance) -> addConditionsField(instance).apply(instance, DrawerContentsLootFunction::new));
    private static final LootFunctionType TYPE = new LootFunctionType(CODEC);
    
    protected DrawerContentsLootFunction(List<LootCondition> conditions) {
        super(conditions);
    }
    
    public static void register() {
        Registry.register(Registries.LOOT_FUNCTION_TYPE, id("drawer_contents"), TYPE);
    }
    
    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(DrawerContentsLootFunction::new);
    }
    
    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isEmpty() || ExtendedDrawers.CONFIG.get().misc().drawersDropContentsOnBreak()) return stack;
        var blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof StorageDrawerBlockEntity drawer && !drawer.isEmpty()) {
            var nbt = new NbtCompound();
            drawer.writeNbt(nbt);
            var itemNbt = BlockItem.getBlockEntityNbt(stack);
            if (itemNbt == null) {
                itemNbt = nbt;
            } else {
                itemNbt.copyFrom(nbt);
            }
            BlockItem.setBlockEntityNbt(stack, ModBlocks.DRAWER_BLOCK_ENTITY, itemNbt);
        }
        return stack;
    }
    
    @Override
    public LootFunctionType getType() {
        return TYPE;
    }
}
