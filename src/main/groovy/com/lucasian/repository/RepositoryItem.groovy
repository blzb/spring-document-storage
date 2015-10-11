package com.lucasian.repository

/**
 * Created by blzb on 10/8/15.
 */
class RepositoryItem {
    String name
    String path
    String id
    Long userId
    Date lastModifiedDate
    Date createdAtDate
    String version
    String mimeType
    RepositoryItemContents contents
    List<String> tags = ['un tags']
    String toString() {
        "name[${name}] path[${path}] mimeType[${mimeType}] id[${id}]"
    }

}
