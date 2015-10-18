package com.lucasian.repository

import groovy.transform.CompileStatic

/**
 * Created by blzb on 10/8/15.
 */
@CompileStatic
class RepositoryItem {
    String name
    String path
    String id
    Date lastModifiedDate
    Date createdAtDate
    String version
    String mimeType
    RepositoryItemContents contents
    List<String> tags = ['un tags']
}
