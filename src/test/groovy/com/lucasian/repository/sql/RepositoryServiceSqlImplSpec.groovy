package com.lucasian.repository.sql

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
import org.apache.tika.Tika
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import spock.lang.IgnoreRest
import spock.lang.Specification

import javax.activation.DataSource

/**
 * Created by blzb on 10/8/15.
 */
class RepositoryServiceSqlImplSpec extends Specification{
    RepositoryServiceSqlImpl repositoryService
    def setup(){
        repositoryService = new RepositoryServiceSqlImpl()
        repositoryService.dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .build();
        repositoryService.tika = new Tika()
    }
    def shutdown(){
        repositoryService.dataSource.shutdown()
    }
    @IgnoreRest
    def "Should store file"(){
        setup:
        println new File('src/test/resources/folder/one/testFile.txt')
        RepositoryItem item = getTestNode("testFile.txt","folder/one")
        when:
        String id = repositoryService.storeItemAndGetId(item)
        then:
        id
    }
    def "Should list items in folder"(){
        setup:
        repositoryService.storeItemAndGetId(getTestNode("testFile","/folder/one/two/tree"))
        repositoryService.storeItemAndGetId(getTestNode("testFile", "/folder/one/two",))
        when:
        List results = repositoryService.listItemsInPath("/folder/one/two")
        then:
        results != null
    }

    def "Should retrieve file"(){
        setup:
        repositoryService.storeItemAndGetId(getTestNode("testFile", "/folder/one/"))
        when:
        RepositoryItemContents result = repositoryService.getContentByPath("/folder/one/testFile", "")
        then:
        result
    }

    def "Should store versioned content"(){
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
    def getTestNode(String name, String path){
        File file = new File('src/test/resources/'+path+'/'+name)
        new RepositoryItem(
                name: name,
                path: path,
                contents: new RepositoryItemContents(
                        metadata: [:],
                        binary: file.getBytes()
                )
        )
    }
}
