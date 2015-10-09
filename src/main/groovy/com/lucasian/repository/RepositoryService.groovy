package com.lucasian.repository

/**
 * Created by blzb on 10/8/15.
 */
interface RepositoryService{
    String storeItemAndGetId(RepositoryItem item)
    List<RepositoryItem> listItemsInPath(String path)
    RepositoryItemContents getContentByPath(String path, String version)
    RepositoryService getContentById(String id, String version)
    List<RepositoryItem> query(Map<String, ?> filters)
}
