package com.lucky.thymeleaf.template;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/1 12:51 下午
 */
public class ClasspathTemplateResolver extends AbstractConfigurableTemplateResolver {

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        return new ClasspathTemplateResource(resourceName, characterEncoding);
    }
}
