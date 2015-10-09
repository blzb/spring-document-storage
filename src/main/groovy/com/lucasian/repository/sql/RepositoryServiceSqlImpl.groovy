package com.lucasian.repository.sql

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
import groovy.sql.Sql
import jdk.internal.org.objectweb.asm.tree.analysis.Value

import javax.sql.DataSource

import static java.util.UUID.randomUUID


/**
 * Created by blzb on 10/8/15.
 */
class RepositoryServiceSqlImpl implements RepositoryService {
    DataSource dataSource

    @Override
    String storeItemAndGetId(RepositoryItem item) {
        assert dataSource != null
        Sql sql = new Sql(dataSource)
        randomUUID() as String
    }

    @Override
    List<RepositoryItem> listItemsInPath(String path) {
        assert dataSource != null
        Sql sql = new Sql(dataSource)
        []
    }

    @Override
    RepositoryItemContents getContentByPath(String path, String version) {
        assert dataSource != null
        Sql sql = new Sql(dataSource)
        new RepositoryItemContents()
    }

    @Override
    RepositoryService getContentById(String id, String version) {
        assert dataSource != null
        Sql sql = new Sql(dataSource)
        new RepositoryItemContents()
    }

    @Override
    List<RepositoryItem> query(Map<String, ?> filters) {
        assert dataSource != null
        []
    }
}
