package com.lucasian.repository.sql

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.apache.tika.Tika
import org.apache.tika.detect.DefaultDetector
import org.apache.tika.detect.Detector
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType

import javax.sql.DataSource

import static java.util.UUID.randomUUID


/**
 * Created by blzb on 10/8/15.
 */
@Slf4j
class RepositoryServiceSqlImpl implements RepositoryService {
    DataSource dataSource

    Tika tika

    @Override
    String storeItemAndGetId(RepositoryItem item) {
        assert dataSource != null
        assert item.contents != null
        assert item.contents.binary != null
        assert item.contents.binary.size() > 0
        addMimeType(item)
        log.warn('The mime type: {}', item.mimeType)
        Sql sql = new Sql(dataSource)
        String uuid = randomUUID()
        item.id = uuid
        Map properties = item.properties
        properties.binary = item.contents.binary
        properties.textContent = extractPlainText(item)
        log.debug 'count, {}', sql.rows('Select count(*) from repository_document')
        sql.execute('''insert into repository_document(
                id,
                path,
                name,
                last_modified_date,
                created_at_date,
                version,
                mime_type,
                tags,
                binary,
                text_content
                )values(
                :id,
                :path,
                :name,
                :lastModifiedDate,
                :createdAtDate,
                :version,
                :mimeType,
                :tags,
                :binary,
                :textContent
                )
                ''', properties)
        log.debug 'count, {}', sql.rows('Select * from repository_document')
        item.id
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

    void addMimeType(RepositoryItem repositoryItem) {
        InputStream inputStream = new ByteArrayInputStream(repositoryItem.contents.binary)
        Metadata metadata = new Metadata()
        metadata.add(Metadata.RESOURCE_NAME_KEY, repositoryItem.getName())

        repositoryItem.mimeType = tika.detect(inputStream, metadata)
        log.debug('Plain text:[{}]', tika.parseToString(inputStream, metadata))
    }

    String extractPlainText(RepositoryItem repositoryItem) {
        log.debug('Contents size:{}', repositoryItem.contents.binary.size())
        InputStream inputStream = new ByteArrayInputStream(repositoryItem.contents.binary)
        Metadata metadata = new Metadata()
        metadata.add(Metadata.RESOURCE_NAME_KEY, repositoryItem.getName())
        String plainText
        plainText = tika.parseToString(inputStream, metadata)
        log.debug('Plain text:[{}]', plainText)
        plainText
    }
}
