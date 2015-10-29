package com.lucasian.repository.configuration

import com.lucasian.repository.RepositoryService
import com.lucasian.repository.rest.RepositoryController
import com.lucasian.repository.sql.RepositoryServiceH2SqlImpl
import com.lucasian.repository.taglib.RepositoryDialect
import com.lucasian.repository.taglib.RepositoryProcessor
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.processor.IProcessor
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.TemplateResolver

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
    @Value('${repository.template:explorer.html}')
    String templateName
    @Autowired
    RepositoryService repositoryService

    @Bean
    RepositoryProcessor repositoryProcessor() {
      TemplateEngine engine = new TemplateEngine();
      TemplateResolver resolver = new ClassLoaderTemplateResolver()
      resolver.prefix = 'repository/'
      resolver.templateMode = 'HTML5'
      resolver.characterEncoding = 'UTF-8'
      resolver.order = 1
      engine.templateResolver = resolver
      new RepositoryProcessor(
        templateEngine: engine,
        repositoryService: repositoryService,
        templateName: templateName
      )
    }

    @Bean
    IDialect RepositoryDialect() {
      new RepositoryDialect([repositoryProcessor()].toSet())
    }
    @Bean
    RepositoryController repositoryController(){
      new RepositoryController(repositoryService: repositoryService)
    }
  }
}
