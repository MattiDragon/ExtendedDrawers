package io.github.mattidragon.extendeddrawers.compacting;

public interface ExtendedDrawersDataPackContents {
    default CompressionOverrideLoader extended_drawers$getOverrideLoader() {
        throw new AssertionError();
    }
}
