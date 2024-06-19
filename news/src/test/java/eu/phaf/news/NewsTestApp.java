package eu.phaf.news;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan("eu.phaf.news")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class NewsTestApp {
}
