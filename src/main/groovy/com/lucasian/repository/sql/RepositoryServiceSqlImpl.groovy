package com.lucasian.repository.sql

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
import groovy.sql.GroovyResultSet
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata

import javax.sql.DataSource
import javax.swing.text.html.Option

import static java.util.UUID.randomUUID


/**
 * Created by blzb on 10/8/15.
 */
@Slf4j
@CompileStatic
class RepositoryServiceSqlImpl implements RepositoryService {

  private static final String DB = 'H2'
  DataSource dataSource

  Tika tika

  @Override
  String storeItemAndGetId(RepositoryItem item) {
    if (!item.contents || !item.contents.binary) {
      throw new IllegalStateException('empty file')
    }else{
      println('1')
    }
    if (!item.path.contains(item.name)) {
      if (!item.path.endsWith('/')) {
        item.path += '/'
      }
      item.path += item.name
    }
    Map maxVersionResult = getMaxVersionByPath(item.path)
    Integer maxVersion = maxVersionResult['max_version'] as Integer
    if (maxVersion) {
      item.version = maxVersion + 1
      item.id = maxVersionResult['id']
    } else {
      String uuid = randomUUID()
      item.id = uuid
      item.version = 1
    }
    addMimeType(item)
    Sql sql = new Sql(dataSource)
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
                text_content,
                version
                )values(
                :id,
                :path,
                :name,
                :mimeType,
                :tags,
                :binary,
                :textContent,
                :version
                )
                ''', properties)
    item.id
  }

  @Override
  List<RepositoryItem> listItemsInPath(String path) {
    Sql sql = new Sql(dataSource)
    String query = "Select id, path, name, last_modified_date, created_at_date, version, mime_type, tags from repository_document where path like '${path}%'"
    sql.rows(query).collect() {
      getItem(it).get()
    }
  }

  @Override
  Optional<RepositoryItem> getItemByPath(String path) {
    String version = getMaxVersionByPath(path)['max_version']
    getItemByPath(path, version)
  }

  @Override
  Optional<RepositoryItem> getItemByPath(String path, String version) {
    Sql sql = new Sql(dataSource)
    String query = "Select id, path, name, last_modified_date, created_at_date, version, mime_type, tags from repository_document where path = :path and version = :version"
    getItem(sql.firstRow(query, [path: path, version: version]))
  }

  @Override
  Optional<RepositoryItem> getItemById(String id) {
    String version = getMaxVersionById(id)['max_version']
    getItemById(id, version)
  }

  @Override
  Optional<RepositoryItem> getItemById(String id, String version) {
    Sql sql = new Sql(dataSource)
    String query = "Select id, path, name, last_modified_date, created_at_date, version, mime_type, tags from repository_document where id = :id and version = :version"
    getItem(sql.firstRow(query, [id: id, version: version]))
  }

  @Override
  Optional<RepositoryItemContents> getContentByPath(String path, String version) {
    String query = "Select binary from repository_document where path = '${path}' and version = ${version}"
    getContentsByQuery(query)
  }

  @Override
  Optional<RepositoryItemContents> getContentByPath(String path) {
    String version = getMaxVersionByPath(path)['max_version']
    getContentByPath(path, version)
  }

  @Override
  Optional<RepositoryItemContents> getContentById(String id, String version) {
    String query = "Select binary from repository_document where id = '${id}' and version = ${version}"
    getContentsByQuery(query)
  }

  @Override
  Optional<RepositoryItemContents> getContentById(String id) {
    String version = getMaxVersionById(id)['max_version']
    String query = "Select binary from repository_document where id = '${id}' and version = ${version}"
    getContentsByQuery(query)
  }


  @Override
  List<RepositoryItem> query(Map<String, ?> filters) {
    []
  }

  private Map getMaxVersionByPath(String path) {
    String query = "Select max(version) as max_version, id from repository_document where path = '${path}' GROUP BY ID"

    getMaxVersionWithQuery(query)
  }

  private Map getMaxVersionById(String id) {
    String query = "Select max(version) as max_version, id from repository_document where id = '${id}' GROUP BY ID"
    getMaxVersionWithQuery(query)
  }

  private Map getMaxVersionWithQuery(String query) {
    Sql sql = new Sql(dataSource)
    Map results = sql.firstRow(query)
    results ?: [:]
  }

  private Optional<RepositoryItemContents> getContentsByQuery(String query) {
    Sql sql = new Sql(dataSource)
    RepositoryItemContents contents
    sql.eachRow(query, 1, 0,
      { GroovyResultSet row ->
        contents = new RepositoryItemContents()
        contents.binary = row.getBytes('BINARY')
      })
    Optional.ofNullable(contents)
  }

  void addMimeType(RepositoryItem repositoryItem) {
    InputStream inputStream = new ByteArrayInputStream(repositoryItem.contents.binary)
    Metadata metadata = new Metadata()
    metadata.add(Metadata.RESOURCE_NAME_KEY, repositoryItem.getName())

    repositoryItem.mimeType = tika.detect(inputStream, metadata)
  }

  String extractPlainText(RepositoryItem repositoryItem) {
    InputStream inputStream = new ByteArrayInputStream(repositoryItem.contents.binary)
    Metadata metadata = new Metadata()
    metadata.add(Metadata.RESOURCE_NAME_KEY, repositoryItem.getName())
    String plainText
    plainText = tika.parseToString(inputStream, metadata)
    plainText
  }

  private Optional<RepositoryItem> getItem(Map columnValues) {
    if (columnValues?.size()) {
      RepositoryItem item
      columnValues.with {
        String tagList = tags
        item = new RepositoryItem(
          path: path as String,
          name: name as String ,
          version: version as String,
          mimeType: mime_type as String,
          id: id as String,
          tags: tagList.split(',').toList(),
          lastModifiedDate: last_modified_date as Date,
          createdAtDate: created_at_date as Date
        )
      }
      Optional.of(item)
    } else {
      Optional.empty()
    }
  }
}
