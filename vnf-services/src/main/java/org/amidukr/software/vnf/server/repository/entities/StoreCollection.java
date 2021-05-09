package org.amidukr.software.vnf.server.repository.entities;

import java.util.concurrent.ConcurrentHashMap;

/**
* Created by Dmytro Brazhnyk on 6/10/2017.
*/
public class StoreCollection {
    private final ConcurrentHashMap<String, StoreEntry> entryMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, StoreEntry> getEntryMap() {
        return entryMap;
    }
}
