package tech.nilanjan.spring.backend.main.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private Contact contact() {
        return new Contact(
                "Nilanjan Deb",
                "https://www.nilanjan.tech",
                "nilanjan172nsvian@gmail.com"
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Spring API documentation",
                "Spring Boot API documentation for CRUD operation and Spring Security",
                "v1",
                null,
                contact(),
                "MIT",
                "https://github.com/nil1729/spring-boot-backend-1/blob/master/LICENSE",
                new ArrayList<>()
        );
    }

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("tech.nilanjan.spring.backend"))
                .paths(PathSelectors.any())
                .build();
    }
}
