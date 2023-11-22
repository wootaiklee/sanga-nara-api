package com.gsntalk.api.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.gsntalk.api.util.GsntalkEncryptor;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
public class GsntalkDatasourceConfig {
	
	private Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	@Bean( name = "dataSource" )
	@ConfigurationProperties( prefix = "spring.datasource.hikari" )
	public DataSource DataSource( ConfigurableEnvironment environment ) throws Exception {
		HikariDataSource hds = new HikariDataSource();
		String db_password = environment.getProperty( "db.pwd" );
		int max_pool_size = Integer.valueOf( environment.getProperty( "spring.datasource.hikari.maximum-pool-size" ) );
		String connection_init_sql = environment.getProperty( "spring.datasource.hikari.connection-init-sql" );
		if( db_password == null ) {
			return null;
		}
		// password using property for decryption
		hds.addDataSourceProperty( "password", GsntalkEncryptor.decrypt( db_password ) );
		hds.setMaximumPoolSize( max_pool_size );
		hds.setConnectionInitSql( connection_init_sql );
		
		logger.info( "@@@@@@@@@@@@@@@@@@ GsntalkDatasourceConfig - set DataSource -> done." );
		return hds;
	}
	
	@Bean( name = "sqlSessionFactory" )
	public SqlSessionFactory sqlSessionFactory( @Qualifier( "dataSource" ) DataSource dataSource )throws Exception{
		SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
		ssfb.setDataSource( dataSource );
		
		ssfb.setMapperLocations( new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*.xml") );
		ssfb.setConfigLocation( new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml") );
		ssfb.setTypeAliasesPackage( "com.gsntalk.api.common.vo" );
		
		// trigger hikari pool
		dataSource.getConnection();
		
		logger.info( "@@@@@@@@@@@@@@@@@@ GsntalkDatasourceConfig - set sqlSessionFactory -> done." );
		
		return ssfb.getObject();
	}
}