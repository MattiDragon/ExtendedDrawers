package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;

import java.util.stream.Stream;

public interface StorageProvidingDrawerBlockEntity {
    Stream<? extends DrawerStorage> streamStorages();

    boolean isEmpty();
}
