package org.royaldev.thehumanity.server.configurations.jtwig;

import com.lyncode.jtwig.mvc.JtwigViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class JTwigConfiguration {

    @Bean
    public JTwigFunctions userFunctions() {
        return new JTwigFunctions();
    }

    @Bean
    public ViewResolver viewResolver() {
        final JtwigViewResolver viewResolver = new JtwigViewResolver();
        viewResolver.setPrefix("/views/");
        viewResolver.setSuffix(".twig");
        viewResolver.configuration().render().functionRepository().include(this.userFunctions());
        // Disable caching
        viewResolver.setCacheSystem((key, instanceProvider) -> {
            try {
                return instanceProvider.call();
            } catch (final Exception ex) {
                return null;
            }
        });
        return viewResolver;
    }

}
