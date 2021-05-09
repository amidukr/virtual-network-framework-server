package org.amidukr.software.vnf.server.repository;

import org.amidukr.software.vnf.server.repository.entities.StoreCollection;
import org.amidukr.software.vnf.server.repository.entities.StoreEntry;
import org.amidukr.software.vnf.server.repository.entities.StoreOwnerEntries;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableCollection;

/**
 * Created by Dmytro Brazhnyk on 6/10/2017.
 */
public class StoreRepository {

    private final ConcurrentHashMap<String, StoreCollection> collectionsMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, StoreOwnerEntries> storeOwnerEntriesMap = new ConcurrentHashMap<>();


    private StoreCollection getOrCreateEntryCollection(String collection) {
        StoreCollection entryCollection = collectionsMap.get(collection);

        if(entryCollection != null) {
            return entryCollection;
        }

        collectionsMap.putIfAbsent(collection, new StoreCollection());

        return collectionsMap.get(collection);
    }

    public StoreCollection getEntryCollection(String collection) {
        return collectionsMap.get(collection);
    }

    public StoreEntry getEntry(String collectionName, String entryName) {
        StoreCollection entryCollection = getEntryCollection(collectionName);

        return entryCollection != null ? entryCollection.getEntryMap().get(entryName) : null;
    }

    public boolean createEntryIfNotExists(StoreEntry storeEntry) {
        StoreCollection collection = getOrCreateEntryCollection(storeEntry.getCollectionName());

        boolean newEntryCreated = collection.getEntryMap().putIfAbsent(storeEntry.getEntryName(), storeEntry) == null;

        if(newEntryCreated) {
            storeOwnerEntriesMap.putIfAbsent(storeEntry.getOwner(), new StoreOwnerEntries());
            StoreOwnerEntries storeOwnerEntries = storeOwnerEntriesMap.get(storeEntry.getOwner());
            storeOwnerEntries.getStoreEntries().put(storeEntry, new Object());
        }

        return newEntryCreated;
    }

    public boolean compareAndRemove(StoreEntry entry) {
        StoreCollection entryCollection = getEntryCollection(entry.getCollectionName());

        if(entryCollection == null) return false;

        boolean removed = entryCollection.getEntryMap().remove(entry.getEntryName(), entry);
        if(removed) {
            StoreOwnerEntries storeOwnerEntries = storeOwnerEntriesMap.get(entry.getOwner());
            storeOwnerEntries.getStoreEntries().remove(entry);
        }

        return removed;
    }

    public Collection<StoreEntry> getEntriesByOwner(String owner) {
        StoreOwnerEntries storeOwnerEntries = storeOwnerEntriesMap.get(owner);

        if(storeOwnerEntries == null) return Collections.emptyList();

        return unmodifiableCollection(storeOwnerEntries.getStoreEntries().keySet());
    }
}
