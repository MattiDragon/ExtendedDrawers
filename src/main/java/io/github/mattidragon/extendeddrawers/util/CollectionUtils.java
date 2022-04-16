package io.github.mattidragon.extendeddrawers.util;

import java.util.Collection;
import java.util.function.Predicate;

public final class CollectionUtils {
    private CollectionUtils() {}
    
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        for (T item : collection) {
            if (predicate.test(item)) return true;
        }
        return false;
    }
}
