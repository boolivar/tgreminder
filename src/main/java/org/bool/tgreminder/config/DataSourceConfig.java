package org.bool.tgreminder.config;

import com.zaxxer.hikari.HikariDataSource;

import org.bool.tgreminder.core.BucketKeyAdvice;
import org.bool.tgreminder.core.LocalKeyTargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextClosedEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig implements BeanPostProcessor {

    @Autowired
    private DataSourceProperties dataSourceProperties;
    
    @Autowired
    private BucketKeyAdvice bucketKeyAdvice;
    
    @Autowired
    private ConfigurableApplicationContext context;
    
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("defaultDataSource".equals(beanName)) {
            HikariDataSource dataSource = (HikariDataSource) bean;
            int connections = dataSource.getMaximumPoolSize();
            dataSource.setMaximumPoolSize((connections + 1) / 2);
            
            Stream<HikariDataSource> bucketDataSources = Stream.generate(this::dataSource)
                    .limit(connections - dataSource.getMaximumPoolSize())
                    .peek(ds -> ds.setMaximumPoolSize(1))
                    .peek(ds -> context.addApplicationListener((ContextClosedEvent e) -> context.getBeanFactory().destroyBean("dataSource", ds)));
            
            List<DataSource> targets = Stream.concat(Stream.of(dataSource), bucketDataSources).collect(Collectors.toList());
            
            return ProxyFactory.getProxy(DataSource.class, new LocalKeyTargetSource<>(DataSource.class, targets, bucketKeyAdvice::getKey));
        }
        return bean;
    }
    
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource defaultDataSource() {
        return createDataSource();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource() {
        return createDataSource();
    }
    
    private HikariDataSource createDataSource() {
        return dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
