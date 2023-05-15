package io.github.mattidragon.extendeddrawers;

import com.kneelawk.graphlib.GraphLib;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.config.ClientConfig;
import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.misc.DrawerContentsLootFunction;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedDrawers implements ModInitializer {
    public static final String MOD_ID = "extended_drawers";
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
    
    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        DrawerContentsLootFunction.register();
        registerItemGroup();
        NetworkRegistry.register();
        ClientConfig.HANDLE.load();
        CommonConfig.HANDLE.load();
        ResourceManagerHelper.registerBuiltinResourcePack(id("alt"), MOD_CONTAINER, Text.translatable("resourcepack.extended_drawers.alt"), ResourcePackActivationType.NORMAL);
        ResourceManagerHelper.registerBuiltinResourcePack(id("dev"), MOD_CONTAINER, Text.translatable("resourcepack.extended_drawers.programmer_art"), ResourcePackActivationType.NORMAL);

        //TODO: move to better place
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (!CommonConfig.HANDLE.get().automaticNetworkHealing()) return;
            
            var profiler = world.getProfiler();
            profiler.push("extended_drawers:update_chunks");
            var chunkPos = chunk.getPos();
            var controller = GraphLib.getController(world);
            //LOGGER.info("Healing graphs for chunk at " + chunkPos.x + ", " + chunkPos.z);
            var area = BlockPos.iterate(chunkPos.getStartX(), chunk.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), chunk.getTopY(), chunkPos.getEndZ());
            for (var pos : area) {
                var state = chunk.getBlockState(pos);
                if (state.getBlock() instanceof NetworkComponent) {
                    if (controller.getGraphsAt(pos).findAny().isEmpty()) {
                        LOGGER.info("Scheduling graph refresh at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                        BlockPos pos1 = pos.toImmutable();
                        GraphLib.getController(world).updateNodes(pos1);
                    }
                }
            }
            profiler.pop();
        });
    }

    private void registerItemGroup() {
        FabricItemGroup.builder(id("main"))
                .icon(ModItems.SHADOW_DRAWER::getDefaultStack)
                .entries((context, entries) -> {
                    entries.add(ModBlocks.SINGLE_DRAWER);
                    entries.add(ModBlocks.DOUBLE_DRAWER);
                    entries.add(ModBlocks.QUAD_DRAWER);
                    entries.add(ModBlocks.CONNECTOR);
                    entries.add(ModBlocks.ACCESS_POINT);
                    entries.add(ModBlocks.SHADOW_DRAWER);
                    entries.add(ModBlocks.COMPACTING_DRAWER);

                    entries.add(ModItems.T1_UPGRADE);
                    entries.add(ModItems.T2_UPGRADE);
                    entries.add(ModItems.T3_UPGRADE);
                    entries.add(ModItems.T4_UPGRADE);
                    entries.add(ModItems.DOWNGRADE);
                    entries.add(ModItems.CREATIVE_UPGRADE);
                    entries.add(ModItems.UPGRADE_FRAME);
                    entries.add(ModItems.LOCK);
                })
                .build();
    }
}
