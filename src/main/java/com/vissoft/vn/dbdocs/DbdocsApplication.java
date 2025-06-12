package com.vissoft.vn.dbdocs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.vissoft.vn.dbdocs"})
public class DbdocsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbdocsApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            String protocol = "http";
            if (environment.getProperty("server.ssl.key-store") != null) {
                protocol = "https";
            }
            String serverPort = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "/");
            if (!contextPath.startsWith("/")) {
                contextPath = "/" + contextPath;
            }
            if (!contextPath.endsWith("/")) {
                contextPath += "/";
            }
            
            String hostAddress = "localhost";
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                log.warn("Could not determine host address", e);
            }
            
            // For Swagger and OpenAPI URLs, we don't want a trailing slash
            String apiContextPath = contextPath;
            if (apiContextPath.endsWith("/")) {
                apiContextPath = apiContextPath.substring(0, apiContextPath.length() - 1);
            }
            
            log.info("\n----------------------------------------------------------\n\t" +
                    "Application is running! Access URLs:\n\t" +
                    "Local: \t\t{}://localhost:{}{}\n\t" +
                    "External: \t{}://{}:{}{}\n\t" +
                    "Swagger UI: \t{}://{}:{}{}/swagger-ui.html\n\t" +
                    "OpenAPI: \t{}://{}:{}{}/v3/api-docs\n\t" +
                    "----------------------------------------------------------",
                    protocol, serverPort, contextPath,
                    protocol, hostAddress, serverPort, contextPath,
                    protocol, hostAddress, serverPort, apiContextPath,
                    protocol, hostAddress, serverPort, apiContextPath);
        };
    }
}
