package io.github.mattidragon.extendeddrawers.registry;

import com.mojang.serialization.Codec;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.component.DrawerContentsComponent;
import io.github.mattidragon.extendeddrawers.component.DrawerSlotComponent;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModDataComponents {
    public static final DataComponentType<Long> LIMITER_LIMIT = DataComponentType.<Long>builder()
            .codec(Codec.LONG)
            .packetCodec(PacketCodecs.VAR_LONG)
            .build();
    public static final DataComponentType<DrawerContentsComponent> DRAWER_CONTENTS = DataComponentType.<DrawerContentsComponent>builder()
            .codec(DrawerContentsComponent.CODEC)
            .packetCodec(DrawerContentsComponent.PACKET_CODEC)
            .cache()
            .build();
    public static final DataComponentType<DrawerSlotComponent> COMPACTING_DRAWER_CONTENTS = DataComponentType.<DrawerSlotComponent>builder()
            .codec(DrawerSlotComponent.CODEC)
            .packetCodec(DrawerSlotComponent.PACKET_CODEC)
            .cache()
            .build();
    
    public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, ExtendedDrawers.id("limiter_limit"), LIMITER_LIMIT);
        Registry.register(Registries.DATA_COMPONENT_TYPE, ExtendedDrawers.id("drawer_contents"), DRAWER_CONTENTS);
        Registry.register(Registries.DATA_COMPONENT_TYPE, ExtendedDrawers.id("compacting_drawer_contents"), COMPACTING_DRAWER_CONTENTS);
    }
}
