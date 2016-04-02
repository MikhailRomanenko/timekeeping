package com.timekeeping;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.timekeeping.schedule.support.ScheduleAdapter;

@Configuration
@EnableJpaRepositories(basePackageClasses = RepositoryTestConfig.class)
@EntityScan(basePackageClasses = RepositoryTestConfig.class)
@Import({ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan(basePackageClasses = ScheduleAdapter.class)
public class RepositoryTestConfig {

}
