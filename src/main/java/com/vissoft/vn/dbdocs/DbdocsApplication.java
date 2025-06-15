package com.vissoft.vn.dbdocs;

import java.net.InetAddress;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

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
            
            log.info("\n----------------------------------------------------------\n" +
                    "Application is running! Access URLs:\n" +
                    "\tLocal: \t\thttp://localhost:{}{}\n" +
                    "\tExternal: \thttp://{}:{}{}\n" +
                    "\tSwagger UI: \thttp://{}:{}{}/swagger-ui.html\n" +
                    "\tOpenAPI: \thttp://{}:{}{}/v3/api-docs\n" +
                    "----------------------------------------------------------",
                    serverPort, contextPath,
                    hostAddress, serverPort, contextPath,
                    hostAddress, serverPort, apiContextPath,
                    hostAddress, serverPort, apiContextPath);
        };
    }
}
