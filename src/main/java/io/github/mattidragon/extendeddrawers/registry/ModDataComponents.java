package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.component.DrawerContentsComponent;
import io.github.mattidragon.extendeddrawers.component.DrawerSlotComponent;
import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModDataComponents {
    public static final DataComponentType<LimiterLimitComponent> LIMITER_LIMIT = DataComponentType.<LimiterLimitComponent>builder()
            .persistent(LimiterLimitComponent.CODEC)
            .networkSynchronized(LimiterLimitComponent.PACKET_CODEC)
            .build();
    public static final DataComponentType<DrawerContentsComponent> DRAWER_CONTENTS = DataComponentType.<DrawerContentsComponent>builder()
            .persistent(DrawerContentsComponent.CODEC)
            .networkSynchronized(DrawerContentsComponent.PACKET_CODEC)
            .cacheEncoding()
            .build();
    public static final DataComponentType<DrawerSlotComponent> COMPACTING_DRAWER_CONTENTS = DataComponentType.<DrawerSlotComponent>builder()
            .persistent(DrawerSlotComponent.CODEC)
            .networkSynchronized(DrawerSlotComponent.PACKET_CODEC)
            .cacheEncoding()
            .build();
    
    public static void register() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ExtendedDrawers.id("limiter_limit"), LIMITER_LIMIT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ExtendedDrawers.id("drawer_contents"), DRAWER_CONTENTS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ExtendedDrawers.id("compacting_drawer_contents"), COMPACTING_DRAWER_CONTENTS);
    }
}
