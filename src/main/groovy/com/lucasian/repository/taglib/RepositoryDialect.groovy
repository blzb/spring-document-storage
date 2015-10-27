package com.lucasian.repository.taglib

import org.thymeleaf.dialect.AbstractDialect
import org.thymeleaf.processor.IProcessor

/**
 * Created by blzb on 10/26/15.
 */
class RepositoryDialect extends AbstractDialect {
  public RepositoryDialect() {
    super();
  }

  //
  // All of this dialect's attributes and/or tags
  // will start with 'hello:'
  //
  public String getPrefix() {
    return "repo";
  }


  //
  // The processors.
  //
  @Override
  public Set<IProcessor> getProcessors() {
    final Set<IProcessor> processors = new HashSet<IProcessor>();
    processors.add(new RepositoryProcessor());
    return processors;
  }

}
