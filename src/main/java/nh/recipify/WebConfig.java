package nh.recipify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger( WebConfig.class );

    private final WebConfigProps props;

    WebConfig(WebConfigProps props) {
        this.props = props;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Register images folder: '{}'", props.imagesDir());
        registry.addResourceHandler("/images/**")
            .addResourceLocations(props.imagesDir());
    }
}
