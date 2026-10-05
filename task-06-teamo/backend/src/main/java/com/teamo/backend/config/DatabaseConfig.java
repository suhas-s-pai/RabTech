package com.teamo.backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:#{null}}")
    private String rawUrl;

    @Value("${spring.datasource.username:#{null}}")
    private String rawUsername;

    @Value("${spring.datasource.password:#{null}}")
    private String rawPassword;

    @Bean
    @Primary
    @Profile("postgres")
    public DataSource postgresDataSource(DataSourceProperties properties) {
        String jdbcUrl = rawUrl;
        String username = rawUsername;
        String password = rawPassword;

        if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
            String trimmedUrl = jdbcUrl.trim();

            if (trimmedUrl.startsWith("postgres://") || trimmedUrl.startsWith("postgresql://")) {
                try {
                    String cleanUriStr = trimmedUrl.startsWith("jdbc:") ? trimmedUrl.substring(5) : trimmedUrl;
                    URI uri = new URI(cleanUriStr);

                    String host = uri.getHost();
                    int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                    String path = uri.getPath();

                    if (uri.getUserInfo() != null && (username == null || username.isEmpty())) {
                        String[] userInfo = uri.getUserInfo().split(":");
                        username = userInfo[0];
                        if (userInfo.length > 1 && (password == null || password.isEmpty())) {
                            password = userInfo[1];
                        }
                    }

                    jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                    if (uri.getQuery() != null) {
                        jdbcUrl += "?" + uri.getQuery();
                    }
                    log.info("Normalized PostgreSQL URL format to JDBC: {}", jdbcUrl);
                } catch (URISyntaxException e) {
                    if (!trimmedUrl.startsWith("jdbc:")) {
                        jdbcUrl = "jdbc:" + trimmedUrl;
                    }
                }
            } else if (!trimmedUrl.startsWith("jdbc:")) {
                jdbcUrl = "jdbc:" + trimmedUrl;
            }
        }

        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
            dataSource.setJdbcUrl(jdbcUrl);
        }
        if (username != null && !username.isEmpty()) {
            dataSource.setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            dataSource.setPassword(password);
        }
        dataSource.setDriverClassName("org.postgresql.Driver");

        return dataSource;
    }
}
