package com.lucasian.repository

/**
 * Created by blzb on 10/8/15.
 */
interface RepositoryService{
    String storeItemAndGetId(RepositoryItem item)
    String createFolder(String path, String name)
    void deleteAllVersionOfItem(String id)
    void deleteItemByIdAndVersion(String id, String version)
    void addTags(String id, List<String> tags)
    void deleteLatestVersion(String id)
    List<RepositoryItem> listItemsInPath(String path)
    List<RepositoryItem> listFoldersInPath(String path)
    Optional<RepositoryItem> getItemByPath(String path)
    Optional<RepositoryItem> getItemByPath(String path, String version)
    Optional<RepositoryItem> getItemById(String id)
    Optional<RepositoryItem> getItemById(String id, String version)
    Optional<RepositoryItem> getItemAndContentsById(String id)
    Optional<RepositoryItem> getItemAndContentsById(String id, String version)
    Optional<RepositoryItemContents> getContentByPath(String path)
    Optional<RepositoryItemContents> getContentByPath(String path, String version)
    Optional<RepositoryItemContents> getContentById(String id)
    Optional<RepositoryItemContents> getContentById(String id, String version)
    List<RepositoryItem> query(Map<String, ?> filters)
}
