/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * Config class for Thymeleaf and Spring5 TemplateEngine, creates TemplateEngine and ITemplateResolver beans.
 * Sets prefix and suffix for the HTML templates used in the ReportBuilder, sets the template mode as HTML and the
 * encoding.
 */
@Configuration
public class SpringHtmlConfig
{
    private static final String CHARACTER_ENCODING = "UTF-8";

    /**
     * Configuration method to set up template engine bean using by Thymeleaf for generating reports.
     * @return TemplateEngine - Bean used to generate reports from templates.
     */
    @Bean
    public TemplateEngine htmlTemplateEngine()
    {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }

    /**
     * Configuration method used for resolving names and locations of HTML templates used for generating reports.
     * @return ITemplateResolver - Bean used to resolve location of HTML templates.
     */
    @Bean
    public ITemplateResolver htmlTemplateResolver()
    {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setPrefix("/reportTemplates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(CHARACTER_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
