/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.config.datasource;

import cn.com.jingcloud.utils.repository.GenericRepository;
import cn.com.jingcloud.utils.repository.IdGenerator;
import cn.com.jingcloud.utils.repository.IdTableGenerator;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * 多数据源配置,随着业务量发展，我们通常会进行数据库拆分或是引入其他数据库，从而我们需要配置多个数据源
 *
 * @author liyong
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "primaryDataSource")
//    @Qualifier("primaryDataSource")
    @Primary//优先选择 默认情况下不指定名称时候使用这个数据源
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();

        //分布式事务时候使用
//        return new AtomikosDataSourceBean();
    }

    @Bean(name = "secondaryDataSource")
//    @Qualifier("secondaryDataSource")

    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
        //分布式事务时候使用
//        return new AtomikosDataSourceBean();
    }

    @Bean(name = "primaryJdbcTemplate")
    @Primary//优先选择 默认情况下 不指定名称的时候使用这个模板
    public JdbcTemplate primaryJdbcTemplate(
            @Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "secondaryJdbcTemplate")
    public JdbcTemplate secondaryJdbcTemplate(
            @Qualifier("secondaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "primaryNamedParameterJdbcTemplate")
    @Primary
    public NamedParameterJdbcTemplate primaryNamedParameterJdbcTemplate(
            @Qualifier("primaryJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean(name = "secondaryNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate secondaryNamedParameterJdbcTemplate(
            @Qualifier("secondaryJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean(name = "primaryIdGenerator")
    public IdGenerator primaryIdGenerator(
            @Qualifier("primaryDataSource") DataSource dataSource) {

        IdTableGenerator idGenerator = new IdTableGenerator();
        idGenerator.setIdDataSource(dataSource);
        return idGenerator;
    }

    @Bean(name = "primaryGenericRepository")
    @Primary
    public GenericRepository primaryGenericRepository(
            @Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbcTemplate,
            @Qualifier("primaryNamedParameterJdbcTemplate") NamedParameterJdbcTemplate primaryNamedParameterJdbcTemplate,
            @Qualifier("primaryIdGenerator") IdGenerator primaryIdGenerator
    ) {
        GenericRepository gen = new GenericRepository();
        gen.setJdbcTemplate(primaryJdbcTemplate);
        gen.setNamedParameterJdbcTemplate(primaryNamedParameterJdbcTemplate);
        gen.setIdGenerator(primaryIdGenerator);
        return gen;
    }

}
