package com.lucasian.repository.sql

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
import groovy.sql.GroovyResultSet
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

    private static final String DB = 'H2'
    DataSource dataSource

    Tika tika

    @Override
    String storeItemAndGetId(RepositoryItem item) {
        assert dataSource != null
        assert item.contents != null
        assert item.contents.binary != null
        assert item.contents.binary.size() > 0
        if (!item.path.contains(item.name)) {
            if (!item.path.endsWith('/')) {
                item.path += '/'
            }
            item.path += item.name
        }
        addMimeType(item)
        log.info('The mime type: {}', item.mimeType)
        Sql sql = new Sql(dataSource)
        String uuid = randomUUID()
        item.id = uuid
        Map properties = item.properties
        properties.binary = item.contents.binary
        properties.textContent = extractPlainText(item)
        sql.execute('''insert into repository_document(
                id,
                path,
                name,
                mime_type,
                tags,
                binary,
                text_content
                )values(
                :id,
                :path,
                :name,
                :mimeType,
                :tags,
                :binary,
                :textContent
                )
                ''', properties)
        item.id
    }

    @Override
    List<RepositoryItem> listItemsInPath(String path) {
        assert dataSource != null
        assert path != null
        Sql sql = new Sql(dataSource)
        String query = "Select id, path, name, last_modified_date, created_at_date, version, mime_type, tags from repository_document where path like '${path}%'"
        sql.rows(query).collect() {
            getItem(it)
        }
    }

    @Override
    RepositoryItem getItemInPath(String path, String version = '1') {
        assert dataSource != null
        assert path != null
        Sql sql = new Sql(dataSource)
        String query = "Select id, path, name, last_modified_date, created_at_date, version, mime_type, tags from repository_document where path = '${path}' and version = ${version}"
        getItem(sql.firstRow(query))
    }

    @Override
    RepositoryItemContents getContentByPath(String path, String version = '1') {
        assert dataSource != null
        String query = "Select binary from repository_document where path = '${path}' and version = ${version}"
        getContentsByQuery(query)
    }

    @Override
    RepositoryItemContents getContentById(String id, String version = '1') {
        assert dataSource != null
        String query = "Select binary from repository_document where id = '${id}' and version = ${version}"
        getContentsByQuery(query)
    }

    private RepositoryItemContents getContentsByQuery(String query) {
        Sql sql = new Sql(dataSource)
        RepositoryItemContents contents
        sql.eachRow(query) { GroovyResultSet row ->
            contents = new RepositoryItemContents(
                    binary: row.getBytes('BINARY')
            )
        }
        contents
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
        log.info('Plain text:[{}]', tika.parseToString(inputStream, metadata))
    }

    String extractPlainText(RepositoryItem repositoryItem) {
        log.info('Contents size:{}', repositoryItem.contents.binary.size())
        InputStream inputStream = new ByteArrayInputStream(repositoryItem.contents.binary)
        Metadata metadata = new Metadata()
        metadata.add(Metadata.RESOURCE_NAME_KEY, repositoryItem.getName())
        String plainText
        plainText = tika.parseToString(inputStream, metadata)
        log.info('Plain text:[{}]', plainText)
        plainText
    }

    private RepositoryItem getItem(Map columnValues) {
        columnValues.with {
            String tagList = tags
            new RepositoryItem(
                    path: path,
                    name: name,
                    version: version,
                    mimeType: mime_type,
                    id: id,
                    tags: tagList.split(','),
                    lastModifiedDate: last_modified_date,
                    createdAtDate: created_at_date
            )
        }
    }
}
