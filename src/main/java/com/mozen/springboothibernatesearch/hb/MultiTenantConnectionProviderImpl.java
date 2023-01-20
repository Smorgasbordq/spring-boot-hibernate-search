package com.mozen.springboothibernatesearch.hb;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
	private static final long serialVersionUID = 1L;
	private final TenantRoutingDatasource config;

	public MultiTenantConnectionProviderImpl() {
		config = TenantRoutingDatasource.get();
	}

	@Override
	public boolean isUnwrappableAs(@SuppressWarnings("rawtypes") Class unwrapType) {
		try {
			return config.getPrimary().isWrapperFor(unwrapType);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		try {
			return config.getPrimary().unwrap(unwrapType);
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		return config.getAnyConnection();
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		config.releaseAnyConnection(connection);
	}

	@Override
	public Connection getConnection(String tenantId) throws SQLException {
		return config.getConnection(tenantId);
	}

	@Override
	public void releaseConnection(String tenantId, Connection connection) throws SQLException {
		config.releaseConnection(tenantId, connection);
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return true;
	}
}