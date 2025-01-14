package com.mozen.springboothibernatesearch.hb;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

	@Override
	public String resolveCurrentTenantIdentifier() {
		return TenantContext.getCurrentTenant();
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}
