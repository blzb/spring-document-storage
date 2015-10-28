package com.lucasian.repository.taglib

import org.thymeleaf.dialect.AbstractDialect
import org.thymeleaf.processor.IProcessor

/**
 * Created by blzb on 10/26/15.
 */
class RepositoryDialect extends AbstractDialect {

  private final Set<IProcessor> processors = new HashSet<IProcessor>()

  public RepositoryDialect() {
    super()
  }

  public RepositoryDialect(Set<IProcessor> processors){
    super()
    this.processors = processors
  }

  //
  // All of this dialect's attributes and/or tags
  // will start with 'hello:'
  //
  public String getPrefix() {
     'repo'
  }


  //
  // The processors.
  //
  @Override
  public Set<IProcessor> getProcessors() {
     processors
  }

}
