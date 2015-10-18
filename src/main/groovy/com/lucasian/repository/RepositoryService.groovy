package com.lucasian.repository

/**
 * Created by blzb on 10/8/15.
 */
interface RepositoryService{
    String storeItemAndGetId(RepositoryItem item)
    List<RepositoryItem> listItemsInPath(String path)
    RepositoryItem getItemByPath(String path)
    RepositoryItem getItemByPath(String path, String version)
    RepositoryItem getItemById(String id)
    RepositoryItem getItemById(String id, String version)
    RepositoryItemContents getContentByPath(String path)
    RepositoryItemContents getContentByPath(String path, String version)
    RepositoryItemContents getContentById(String id)
    RepositoryItemContents getContentById(String id, String version)
    List<RepositoryItem> query(Map<String, ?> filters)
}
