package com.lucasian.repository.sql

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
import org.apache.tika.Tika
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

import javax.activation.DataSource

/**
 * Created by blzb on 10/8/15.
 */
class RepositoryServiceSqlImplSpec extends Specification {
  RepositoryServiceSqlImpl repositoryService

  def setup() {
    repositoryService = new RepositoryServiceSqlImpl()
    repositoryService.dataSource = new EmbeddedDatabaseBuilder()
      .setType(EmbeddedDatabaseType.H2)
      .addScript("classpath:schema.sql")
      .build();
    repositoryService.tika = new Tika()
  }

  def shutdown() {
    repositoryService.dataSource.shutdown()
  }

  def "Should store file"() {
    setup:
    RepositoryItem item = getTestNode(name, path)
    when:
    String id = repositoryService.storeItemAndGetId(item)
    then:
    id
    where:
    name           | path
    'testFile.txt' | 'folder/one'
    'testFile.pdf' | 'folder/one'
    'testFile.doc' | 'folder/one'
    'testFile.jpg' | 'folder/one'
  }

  def "Should list items in folder"() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.txt', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.doc', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.jpg', 'folder/one'))
    when:
    List results = repositoryService.listItemsInPath("folder/one")
    then:
    results != null
    results.size() == 4
  }

  def "Should get latest item by path "() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItem result = repositoryService.getItemByPath('folder/one/testFile.pdf')
    then:
    result
    result.version == '4'
  }

  def 'Should get item by path and version'() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItem result = repositoryService.getItemByPath('folder/one/testFile.pdf', '2')
    then:
    result
    result.version == '2'

  }

  def 'Should get latest item by id'() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItem result = repositoryService.getItemById(id)
    then:
    result
    result.version == '4'
  }

  def 'Should get item by id and version'() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItem result = repositoryService.getItemById(id, '2')
    then:
    result
    result.version == '2'

  }

  def "Should get latest file contents by path"() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItemContents result = repositoryService.getContentByPath('folder/one/testFile.pdf')
    then:
    result
    result.binary.size() > 0
  }

  def "Should get file contents by path and version"() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItemContents result = repositoryService.getContentByPath('folder/one/testFile.pdf', '1')
    then:
    result
    result.binary.size() > 0
  }

  def 'Should get latest file contents by id'() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItemContents result = repositoryService.getContentById(id)
    then:
    result
    result.binary.size() > 0

  }

  def "Should get file contents by id and version"() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
    when:
    RepositoryItemContents result = repositoryService.getContentById(id, '1')
    then:
    result
    result.binary.size() > 0
  }

  def "Should store versioned content"() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "/folder/one/"))
    repositoryService.storeItemAndGetId(getTestNodeWithName("testFile1.pdf", "/folder/one/", "testFile.pdf"))
    repositoryService.storeItemAndGetId(getTestNodeWithName("testFile2.pdf", "/folder/one/", "testFile.pdf"))
    when:
    RepositoryItemContents first = repositoryService.getContentByPath("/folder/one/testFile.pdf", "1")
    RepositoryItemContents second = repositoryService.getContentByPath("/folder/one/testFile.pdf", "2")
    RepositoryItemContents third = repositoryService.getContentByPath("/folder/one/testFile.pdf", "3")
    then:
    first
    second
    third
    first.binary.size() != second.binary.size()
    first.binary.size() != third.binary.size()
    second.binary.size() != third.binary.size()
  }

  RepositoryItem getTestNode(String name, String path) {
    File file = new File('src/test/resources/' + path + '/' + name)
    new RepositoryItem(
      name: name,
      path: path,
      tags: ['test', 'tag', 'mexico'],
      contents: new RepositoryItemContents(
        metadata: [:],
        binary: file.getBytes()
      )
    )
  }

  RepositoryItem getTestNodeWithName(String name, String path, String newName) {
    RepositoryItem item = getTestNode(name, path)
    item.name = item.name.replace(name, newName)
    item.path = item.path.replace(name, newName)
    item
  }
}
