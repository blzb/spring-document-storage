package com.lucasian.repository.sql

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import org.apache.tika.Tika
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.IgnoreRest
import spock.lang.Specification

import static java.util.UUID.randomUUID

/**
 * Created by blzb on 10/8/15.
 */
class RepositoryServiceH2SqlImplSpec extends Specification {
  RepositoryServiceH2SqlImpl repositoryService

  def setup() {
    repositoryService = new RepositoryServiceH2SqlImpl()
    repositoryService.dataSource = new EmbeddedDatabaseBuilder()
      .setType(EmbeddedDatabaseType.H2)
      .addScript('classpath:schema.sql')
      .build();
    repositoryService.tika = new Tika()
  }

  def shutdown() {
    repositoryService.dataSource.shutdown()
  }

  def 'Should store file'() {
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

  def 'Should list items in folder'() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.txt', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.doc', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.jpg', 'folder/one'))
    when:
    List results = repositoryService.listItemsInPath('/folder/one')
    then:
    results != null
    results.size() == 4
  }

  def 'Should list folder in path'() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.txt', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.jpg', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.txt', 'folder/two'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.jpg', 'folder/two'))
    when:
    List<RepositoryItem> results1 = repositoryService.listFoldersInPath('')
    println("RES1:"+results1.collect(){it.path})
    List<RepositoryItem> results2 = repositoryService.listFoldersInPath('/folder')
    println("RES2:"+results2.collect(){it.path})
    then:
    results1 != null
    results2 != null
    results1.size() == 1
    results2.size() == 2
  }

  def 'Should get latest item by path '() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItem> result = repositoryService.getItemByPath('/folder/one/testFile.pdf')
    then:
    result.isPresent()
    result.get().version == '4'
  }

  def 'Should get item by path and version'() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItem> result = repositoryService.getItemByPath('folder/one/testFile.pdf', '2')
    then:
    result.isPresent()
    result.get().version == '2'

  }

  def 'Should get latest item by id'() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItem> result = repositoryService.getItemById(id)
    then:
    result.isPresent()
    result.get().version == '4'
  }

  def 'Should get item by id and version'() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItem> result = repositoryService.getItemById(id, '2')
    then:
    result.isPresent()
    result.get().version == '2'

  }

  def 'Should get latest file contents by path'() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItemContents> result = repositoryService.getContentByPath('/folder/one/testFile.pdf')
    then:
    result.isPresent()
    result.get().binary.size() > 0
  }

  def 'Should get file contents by path and version'() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItemContents> result = repositoryService.getContentByPath('folder/one/testFile.pdf', '1')
    then:
    result.isPresent()
    result.get().binary.size() > 0
  }

  def 'Should get latest file contents by id'() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItemContents> result = repositoryService.getContentById(id)
    then:
    result.isPresent()
    result.get().binary.size() > 0

  }

  def 'Should get file contents by id and version'() {
    setup:
    String id = repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    when:
    Optional<RepositoryItemContents> result = repositoryService.getContentById(id, '1')
    then:
    result.isPresent()
    result.get().binary.size() > 0
  }

  def 'Should store versioned content'() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', '/folder/one/'))
    repositoryService.storeItemAndGetId(getTestNodeWithName('testFile1.pdf', '/folder/one/', 'testFile.pdf'))
    repositoryService.storeItemAndGetId(getTestNodeWithName('testFile2.pdf', '/folder/one/', 'testFile.pdf'))
    when:
    Optional<RepositoryItemContents> first = repositoryService.getContentByPath('/folder/one/testFile.pdf', '1')
    Optional<RepositoryItemContents> second = repositoryService.getContentByPath('/folder/one/testFile.pdf', '2')
    Optional<RepositoryItemContents> third = repositoryService.getContentByPath('/folder/one/testFile.pdf', '3')
    then:
    first.isPresent()
    second.isPresent()
    third.isPresent()
    first.get().binary.size() != second.get().binary.size()
    first.get().binary.size() != third.get().binary.size()
    second.get().binary.size() != third.get().binary.size()
  }

  def 'Should return empty optional item content if not found by path'() {
    when:
    Optional<RepositoryItemContents> result = repositoryService.getContentByPath('/folder/one/testFile.pdf')
    then:
    result.isPresent() == false
  }

  def 'Should return empty optional item content if not found by id'() {
    when:
    Optional<RepositoryItemContents> result = repositoryService.getContentById(randomUUID() as String)
    then:
    result.isPresent() == false
  }

  def 'Should return empty optional item if not found by id'() {
    when:
    Optional<RepositoryItem> result = repositoryService.getItemById('/folder/one/testFile.pdf')
    then:
    result.isPresent() == false
  }

  def 'Should return empty optional item if not found by path'() {
    when:
    Optional<RepositoryItem> result = repositoryService.getItemByPath('/folder/one/testFile.pdf')
    then:
    result.isPresent() == false
  }

  def 'Should throw exception when RepositoryItemContents is null'() {
    setup:
    RepositoryItem item = getTestNode('testFile.txt', 'folder/one')
    item.contents = null
    when:
    String id = repositoryService.storeItemAndGetId(item)
    then:
    thrown(IllegalStateException)
  }

  def 'Should throw exception when binary is null'() {
    setup:
    RepositoryItem item = getTestNode('testFile.txt', 'folder/one')
    item.contents.binary = null
    when:
    String id = repositoryService.storeItemAndGetId(item)
    then:
    thrown(IllegalStateException)
  }

  def 'Should throw exception when binary is empty'() {
    setup:
    RepositoryItem item = getTestNode('testFile.txt', 'folder/one')
    item.contents.binary = new byte[0]
    when:
    String id = repositoryService.storeItemAndGetId(item)
    then:
    thrown(IllegalStateException)
  }

  def 'Should perform full text search '() {
    setup:
    repositoryService.storeItemAndGetId(getTestNode('testFile.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile1.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile2.pdf', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.txt', 'folder/one'))
    repositoryService.storeItemAndGetId(getTestNode('testFile.doc', 'folder/one'))
    when:
    List<RepositoryItem> result = repositoryService.query([fullText: 'ENTCS'])
    then:
    result.size() == 2
    result.find() { it.name == 'testFile.txt' }
    result.find() { it.name == 'testFile2.pdf' }


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
    if (item.path.endsWith('/')) {
      item.path = item.path + item.name
    } else {
      item.path = item.path + '/' + item.name
    }
    item
  }
}
