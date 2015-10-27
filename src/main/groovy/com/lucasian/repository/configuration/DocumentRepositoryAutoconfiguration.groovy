package com.lucasian.repository.configuration

import com.lucasian.repository.RepositoryService
import com.lucasian.repository.sql.RepositoryServiceH2SqlImpl
import com.lucasian.repository.taglib.RepositoryDialect
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.thymeleaf.dialect.IDialect

import javax.sql.DataSource


/**
 * Created by blzb on 10/18/15.
 */
@Configuration
class DocumentRepositoryAutoConfiguration {
  @Configuration
  @ConditionalOnClass(name = 'org.h2.Driver')
  protected static class H2RepositoryConfiguration {

    @Autowired
    private DataSource dataSource

    @Bean
    Tika tika() {
      new Tika()
    }

    @Bean
    RepositoryService H2RespositoryService() {
      new RepositoryServiceH2SqlImpl(
        tika: tika(),
        dataSource: dataSource
      )
    }
  }

  @Configuration
  protected static class RepositoryTagLibConfiguration {
    @Bean
    IDialect RepositoryDialect() {
      new RepositoryDialect()
    }
  }
}
