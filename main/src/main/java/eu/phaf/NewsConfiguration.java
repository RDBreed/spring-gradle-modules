package eu.phaf;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@EnableConfigurationProperties
@ConfigurationPropertiesScan("eu.phaf.news.infrastructure")
@ComponentScan({
        "eu.phaf.news.infrastructure.config",
        "eu.phaf.news.interfaces.controller"})
@Profile("news")
public class NewsConfiguration {
}
