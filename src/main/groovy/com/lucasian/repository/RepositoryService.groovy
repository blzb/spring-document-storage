package com.lucasian.repository

/**
 * Created by blzb on 10/8/15.
 */
interface RepositoryService{
    String storeItemAndGetId(RepositoryItem item)
    List<RepositoryItem> listItemsInPath(String path)
    RepositoryItem getItemInPath(String path, String version)
    RepositoryItemContents getContentByPath(String path, String version)
    RepositoryItemContents getContentById(String id, String version)
    RepositoryItem getItemInPath(String path)
    RepositoryItemContents getContentByPath(String path)
    RepositoryItemContents getContentById(String id)
    List<RepositoryItem> query(Map<String, ?> filters)
}
