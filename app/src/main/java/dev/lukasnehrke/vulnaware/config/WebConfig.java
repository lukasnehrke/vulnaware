package dev.lukasnehrke.vulnaware.config;

import dev.lukasnehrke.vulnaware.project.util.ProjectConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ProjectConverter projectConverter;

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(projectConverter);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowCredentials(true).allowedOrigins("https://vulnaware.web.app/");
    }
}
