package com.lucasian.repository.taglib

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryService
import org.thymeleaf.Arguments
import org.thymeleaf.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.dom.Element
import org.thymeleaf.processor.IAttributeNameProcessorMatcher
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor
import org.thymeleaf.standard.expression.IStandardExpression
import org.thymeleaf.standard.expression.IStandardExpressionParser
import org.thymeleaf.standard.expression.StandardExpressions

/**
 * Created by blzb on 10/26/15.
 */
class RepositoryProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {

  RepositoryService repositoryService

  TemplateEngine templateEngine

  String templateName


  public RepositoryProcessor() {
    super('path')
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
    final Configuration configuration = arguments.getConfiguration();

    final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

    final String attributeValue = element.getAttributeValue(attributeName);

    final IStandardExpression expression =
      parser.parseExpression(configuration, arguments, attributeValue);

    final String path = (String) expression.execute(configuration, arguments);

    List<RepositoryItem> items = repositoryService.listItemsInPath(path)
    Context context = new Context()
    context.setVariable('items', items)
    context.setVariable('path', path)
    return templateEngine.process(templateName, context)
  }
}
