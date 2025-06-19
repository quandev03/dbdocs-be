package com.vissoft.vn.dbdocs.infrastructure.util;

import java.util.Collection;

public class DataUtils {
    private DataUtils() {
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }
    public static boolean notNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean notNullOrEmpty(Collection<?> collection) {
        return !isNullOrEmpty(collection);
    }
}
