package org.vnf.server.service;

import org.vnf.server.repository.StoreRepository;
import org.vnf.server.repository.entities.StoreEntry;
import org.vnf.server.repository.entities.StoreCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by qik on 6/10/2017.
 */
public class StoreService {
    private final StoreRepository storeRepository = new StoreRepository();

    public StoreServiceErrorCode createEntry(String owner, String collection, String entryName, String value) {
        if(!storeRepository.createEntryIfNotExists(new StoreEntry(owner, collection, entryName, value))) {
            return StoreServiceErrorCode.CREATE_FAILED_ENTRY_ALREADY_EXISTS;
        }

        return StoreServiceErrorCode.OK;
    }

    public StoreServiceErrorCode createOrUpdateEntry(String owner, String collection, String entryName, String value) {

        if(!storeRepository.createEntryIfNotExists(new StoreEntry(owner, collection, entryName, value))) {

            StoreEntry entry = getEntry(collection, entryName);

            if (!entry.getOwner().equals(owner)) {
                return StoreServiceErrorCode.UPDATE_FAILED_DUE_TO_OWNERSHIP_CHECK;
            }

            entry.setValue(value);
        }

        return StoreServiceErrorCode.OK;
    }

    public StoreEntry getEntry(String collection, String entryName) {
        return storeRepository.getEntry(collection, entryName);
    }

    public StoreServiceErrorCode deleteEntry(String owner, String collection, String entryName) {
        StoreEntry entry = null;

        do {
            entry = storeRepository.getEntry(collection, entryName);

            if (entry == null) {
                return StoreServiceErrorCode.DELETE_FAILED_ENTRY_NOT_FOUND;
            }

            if (!entry.getOwner().equals(owner)) {
                return StoreServiceErrorCode.DELETE_FAILED_DUE_TO_OWNERSHIP_CHECK;
            }

        } while (!storeRepository.compareAndRemove(entry));

        return StoreServiceErrorCode.OK;
    }

    public Map<String, StoreEntry> getEntries(String collection) {
        StoreCollection entryCollection = storeRepository.getEntryCollection(collection);

        return entryCollection == null ? Collections.emptyMap() : entryCollection.getEntryMap();
    }

    public void dropEntriesByOwner(String owner) {
        Collection<StoreEntry> entriesByOwner = new ArrayList<>(storeRepository.getEntriesByOwner(owner));

        for (StoreEntry storeEntry : entriesByOwner) {
            storeRepository.compareAndRemove(storeEntry);
        }
    }
}
