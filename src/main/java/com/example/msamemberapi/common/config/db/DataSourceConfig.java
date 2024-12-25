package com.example.msamemberapi.common.config.db;

import com.example.msamemberapi.application.service.impl.SecureKeyManagerService;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile({"instance1", "instance2"})
public class DataSourceConfig {

    @Autowired
    private SecureKeyManagerService secureKeyManagerService;
    @Value("${secret.keys.db.url}")
    private String urlKey;
    @Value("${secret.keys.db.username}")
    private String usernameKey;
    @Value("${secret.keys.db.password}")
    private String passwordKey;

    @Bean
    public DataSource dataSource() {


        String url = secureKeyManagerService.fetchSecretFromKeyManager(urlKey);
        String username = secureKeyManagerService.fetchSecretFromKeyManager(usernameKey);
        String password = secureKeyManagerService.fetchSecretFromKeyManager(passwordKey);

        DatabaseCredentials databaseCredentials = new DatabaseCredentials(url, username, password);

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(databaseCredentials.getUrl());
        dataSource.setUsername(databaseCredentials.getUsername());
        dataSource.setPassword(databaseCredentials.getPassword());

        // 톰캣 기본 설정과 일치시키는 Connection Pool 설정
        dataSource.setInitialSize(100);        // 초기 커넥션 개수
        dataSource.setMaxTotal(100);         // 최대 커넥션 개수
        dataSource.setMinIdle(100);            // 최소 유휴 커넥션 개수
        dataSource.setMaxIdle(100);          // 최대 유휴 커넥션 개수

        // Connection 유효성 검사를 위한 설정
        dataSource.setTestOnBorrow(true);    // 커넥션 획득 전 테스트 (톰캣 기본값: true)
        dataSource.setValidationQuery("SELECT 1"); // 커넥션 유효성 검사 쿼리

        return dataSource;
    }

}