package com.lucasian.repository.taglib

import org.thymeleaf.Arguments
import org.thymeleaf.dom.Element
import org.thymeleaf.processor.IAttributeNameProcessorMatcher
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor

/**
 * Created by blzb on 10/26/15.
 */
class RepositoryProcessor extends AbstractTextChildModifierAttrProcessor {

  public RepositoryProcessor() {
    // Only execute this processor for 'sayto' attributes.
    super("sayto");
  }


  public int getPrecedence() {
    // A value of 10000 is higher than any attribute in the
    // SpringStandard dialect. So this attribute will execute
    // after all other attributes from that dialect, if in the
    // same tag.
    return 10000;
  }


  //
  // Our processor is a subclass of the convenience abstract implementation
  // 'AbstractTextChildModifierAttrProcessor', which takes care of the
  // DOM modifying stuff and allows us just to implement this 'getText(...)'
  // method to compute the text to be set as tag body.
  //
  @Override
  protected String getText(final Arguments arguments, final Element element,
                           final String attributeName) {
    return "Hello, "  + element.getAttributeValue(attributeName) + "!";
  }
}
