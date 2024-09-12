package com.test;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class AppDataSourceConfiguration {

//	@Bean
//	@Primary
//	public DataSource datasource() {
//		HikariDataSource hikariDataSource = (HikariDataSource) DataSourceBuilder
//				.create(HikariDataSource.class.getClassLoader()).username("sa").password("")
//				.url("jdbc:h2:mem:prime;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false").driverClassName("org.h2.Driver")
//				.build();
//		hikariDataSource.setMaximumPoolSize(1);
//		hikariDataSource.setConnectionTimeout(5000);
//		hikariDataSource.setPoolName("PrimayPool");
//		return hikariDataSource;
//	}

	@Bean
	@Primary
	public DataSource datasource() {
		HikariDataSource hikariDataSource = (HikariDataSource) DataSourceBuilder
				.create(HikariDataSource.class.getClassLoader()).username("sa").password("")
				.url("jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1;").driverClassName("org.hsqldb.jdbc.JDBCDriver").build();
		hikariDataSource.setMaximumPoolSize(1);
		hikariDataSource.setConnectionTimeout(5000);
		hikariDataSource.setPoolName("PrimayPool");
		return hikariDataSource;
	}

	@Bean("metaDs")
	public DataSource metaDatasource() throws SQLException {
		DataSource dataSource = DataSourceBuilder
				.create()
				.username("sa")
				.password("")
				.url("jdbc:hsqldb:mem:metadb;sql.enforce_strict_size=true;hsqldb.txt=mvcc;DB_CLOSE_DELAY=-1")
				.driverClassName("org.hsqldb.jdbc.JDBCDriver")
				.build();


		// initialize with metadata tables
		DefaultResourceLoader drl = new DefaultResourceLoader();
		ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
		rdp.addScript(drl.getResource("/org/springframework/batch/core/schema-hsqldb.sql"));
		DatabasePopulatorUtils.execute(rdp, dataSource);

		return dataSource;

	}
	
//	@Bean
//	@BatchDataSource
//	public DataSource embeddedDatasource() {
////		HikariDataSource hikariDataSource = (HikariDataSource) DataSourceBuilder
////				.create(HikariDataSource.class.getClassLoader()).username("sa").password("")
////				.url("jdbc:h2:mem:meta;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false").driverClassName("org.h2.Driver")
////				.build();
////		hikariDataSource.setMaximumPoolSize(5);
////		hikariDataSource.setConnectionTimeout(5000);
////
////		hikariDataSource.setPoolName("MetaPool");
////		return hikariDataSource;
//		return new EmbeddedDatabaseBuilder()
//				.generateUniqueName(true)
//				.addScript("/org/springframework/batch/core/schema-h2.sql")
//				.setType(EmbeddedDatabaseType.H2)
//				.build();
//	}
}
