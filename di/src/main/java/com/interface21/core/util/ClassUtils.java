package com.interface21.core.util;

import java.util.Collection;

public abstract class ClassUtils {

    private static final Class<?>[] EMPTY_CLASS_ARRAY = {};

    /**
     * Copy the given {@code Collection} into a {@code Class} array.
     * <p>The {@code Collection} must contain {@code Class} elements only.
     *
     * @param collection the {@code Collection} to copy
     * @return the {@code Class} array
     * @see StringUtils#toStringArray
     * @since 3.1
     */
    public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
        return (!CollectionUtils.isEmpty(collection) ? collection.toArray(EMPTY_CLASS_ARRAY) : EMPTY_CLASS_ARRAY);
    }
}
