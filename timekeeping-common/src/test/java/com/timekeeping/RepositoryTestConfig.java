package com.timekeeping;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackageClasses = RepositoryTestConfig.class)
@EntityScan(basePackageClasses = RepositoryTestConfig.class)
@Import({ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan(basePackageClasses = RepositoryTestConfig.class)
public class RepositoryTestConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .addScript("/test-schema.sql")
                .build();
    }
    
}
