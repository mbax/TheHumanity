package org.royaldev.thehumanity.server.configurations;

import org.royaldev.thehumanity.TheHumanity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HumanityConfiguration {

    private static TheHumanity humanity;

    @Bean
    public TheHumanity getHumanity() {
        return HumanityConfiguration.humanity;
    }

    public static void setHumanity(final TheHumanity humanity) {
        HumanityConfiguration.humanity = humanity;
    }

}
