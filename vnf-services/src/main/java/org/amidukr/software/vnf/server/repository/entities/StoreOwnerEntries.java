package org.amidukr.software.vnf.server.repository.entities;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dmytro Brazhnyk on 6/10/2017.
 */
public class StoreOwnerEntries {
    private final ConcurrentHashMap<StoreEntry, Object> storeEntries = new ConcurrentHashMap<>();

    public ConcurrentHashMap<StoreEntry, Object> getStoreEntries() {
        return storeEntries;
    }
}
