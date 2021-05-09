package org.amidukr.software.vnf.server.utils;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Dmytro Brazhnyk on 6/5/2017.
 */
public interface CollectionUtils {
    public static <T>Collection<T> emptyIfNull(Collection<T> collection) {
        return collection != null ? collection : Collections.emptyList();
    }
}
