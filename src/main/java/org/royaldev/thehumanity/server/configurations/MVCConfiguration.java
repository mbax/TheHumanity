package org.royaldev.thehumanity.server.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class MVCConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/assets/**")
            .addResourceLocations("/assets/");
    }

}
