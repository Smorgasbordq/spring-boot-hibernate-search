package com.mozen.springboothibernatesearch.hb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

@Component
public class TenantRoutingDatasource extends AbstractRoutingDataSource {
	private static TenantRoutingDatasource inst;
	private static final String DEFAULT_TENANT = Tenants.TEN1;
	private Map<String, DataSource> sources;

	public static final TenantRoutingDatasource get() {
		return inst;
	}

	public TenantRoutingDatasource() {
		init();
	}

	public DataSource getPrimary() {
		return sources.get(DEFAULT_TENANT);
	}

	public Connection getAnyConnection() throws SQLException {
		return openCon(DEFAULT_TENANT);
	}

	public Connection getConnection(String tenantId) throws SQLException {
		return openCon(tenantId);
	}

	public void releaseAnyConnection(Connection connection) throws SQLException {
		closeCon(DEFAULT_TENANT, connection);
	}

	public void releaseConnection(String tenantId, Connection connection) throws SQLException {
		closeCon(tenantId, connection);
	}

	private Connection openCon(String tenantId) throws SQLException {
		return this.sources.get(tenantId).getConnection();
	}

	private void closeCon(String tenantId, Connection connection) throws SQLException {
		connection.close();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() {
		inst = this;
		sources = new HashMap<>(Tenants.TENANTS.size());
		for (String tenantId : Tenants.TENANTS) {
			Properties properties = new Properties();
			try {
				properties.load(getClass().getResourceAsStream(String.format("/hibernate-%s.properties", tenantId)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			sources.put(tenantId, dataSource);
            dataSource.setDriverClassName(properties.getProperty("datasource.driver-class-name"));
            dataSource.setUrl(properties.getProperty("datasource.url"));
            dataSource.setUsername(properties.getProperty("datasource.username"));
            dataSource.setPassword(properties.getProperty("datasource.password"));
            // HACK to trigger auto DDL generation.
			LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
			Map<String, Object> jpap = new HashMap<>();
			jpap.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
			jpap.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
			jpap.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
			jpap.put("hibernate.hbm2ddl.auto", "create");

			HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
			vendor.setGenerateDdl(true);
			vendor.setShowSql(false);
			emfBean.setJpaVendorAdapter(vendor);
			emfBean.setJpaPropertyMap(jpap);
			emfBean.setDataSource(dataSource);
			emfBean.setPackagesToScan("com");
			emfBean.setJpaVendorAdapter(vendor);
			emfBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
			emfBean.setPersistenceUnitName(tenantId);
			emfBean.afterPropertiesSet();
			emfBean.destroy();
		}
		setDefaultTargetDataSource(sources.get(Tenants.TEN1));
		setTargetDataSources((Map) sources);
	}

	@Override
	protected String determineCurrentLookupKey() {
		return TenantContext.getCurrentTenant();
	}

}