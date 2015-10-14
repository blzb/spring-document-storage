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

    def "Should retrieve file by path"() {
        setup:
        repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
        when:
        RepositoryItemContents result = repositoryService.getContentByPath('folder/one/testFile.pdf', '1')
        then:
        result
        result.binary.size()>0
    }

    def "Should retrieve file by id"() {
        setup:
        String id = repositoryService.storeItemAndGetId(getTestNode("testFile.pdf", "folder/one"))
        when:
        RepositoryItemContents result = repositoryService.getContentById(id, '1')
        then:
        result
        result.binary.size()>0
    }

    @Ignore
    def "Should store versioned content"() {
        setup:
        repositoryService.storeItemAndGetId(getTestNode("testFile", "/folder/one/"))
        repositoryService.storeItemAndGetId(getTestNode("testFile", "/folder/one/"))
        repositoryService.storeItemAndGetId(getTestNode("testFile", "/folder/one/"))
        when:
        RepositoryItemContents first = repositoryService.getContentByPath("/folder/one/testFile", "1.0")
        RepositoryItemContents second = repositoryService.getContentByPath("/folder/one/testFile", "1.1")
        RepositoryItemContents third = repositoryService.getContentByPath("/folder/one/testFile", "1.2")
        then:
        first
        second
        third
    }

    def getTestNode(String name, String path) {
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
}
