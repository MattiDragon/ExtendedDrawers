package io.github.mattidragon.extendeddrawers.extensions.registry;

import io.github.mattidragon.extendeddrawers.extensions.block.DrawerBarrelBlock;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.DrawerBarrelBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class ExtensionBlocks {
    public static final Block DRAWER_BARREL = new DrawerBarrelBlock(AbstractBlock.Settings.create().registryKey(key("drawer_barrel")).mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD));

    public static final BlockEntityType<DrawerBarrelBlockEntity> DRAWER_BARREL_ENTITY = FabricBlockEntityTypeBuilder.create(DrawerBarrelBlockEntity::new, DRAWER_BARREL).build();

    public static void register() {
        Registry.register(Registries.BLOCK, id("drawer_barrel"), DRAWER_BARREL);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, id("drawer_barrel"), DRAWER_BARREL_ENTITY);
    }

    private static RegistryKey<Block> key(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, id(path));
    }
}
