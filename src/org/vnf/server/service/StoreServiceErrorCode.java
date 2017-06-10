package org.vnf.server.service;

/**
* Created by qik on 6/10/2017.
*/
public enum StoreServiceErrorCode {
    OK,
    CREATE_FAILED_ENTRY_ALREADY_EXISTS,
    UPDATE_FAILED_DUE_TO_OWNERSHIP_CHECK,
    GET_FAILED_ENTRY_NOT_FOUND,
    DELETE_FAILED_ENTRY_NOT_FOUND,
    DELETE_FAILED_DUE_TO_OWNERSHIP_CHECK
}
