package org.amidukr.software.vnf.server.repository.entities;

/**
* Created by Dmytro Brazhnyk on 6/10/2017.
*/
public class StoreEntry {
    private final String owner;
    private final String collectionName;
    private final String entryName;
    private String value;


    public StoreEntry(String owner, String collectionName, String entryName, String value) {
        this.owner = owner;
        this.collectionName = collectionName;
        this.entryName = entryName;
        this.value = value;
    }

    public String getOwner() {
        return owner;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getEntryName() {
        return entryName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
