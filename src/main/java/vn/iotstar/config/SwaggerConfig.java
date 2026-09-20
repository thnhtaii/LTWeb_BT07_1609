package vn.iotstar.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi SWAGGERApi() {
		return GroupedOpenApi.builder()
				.group("SWAGGERApi")
				.packagesToScan("vn.iotstar")
				.pathsToMatch("/**")
				.build();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("OpenAPI definition")
						.version("v0")
						.description("Spring Boot Swagger API Documentation")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}
