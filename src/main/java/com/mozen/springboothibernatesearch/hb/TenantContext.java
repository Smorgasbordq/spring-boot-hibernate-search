package com.mozen.springboothibernatesearch.hb;

public class TenantContext {

	private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

	public static String getCurrentTenant() {
		String tc = CURRENT_TENANT.get();
		if (tc == null) {
			// new Exception("No tenant set").printStackTrace();
			// throw new RuntimeException("No tenant set");
			return Tenants.TEN1;
		}
		return tc;
	}

	public static void setCurrentTenant(String tenant) {
		CURRENT_TENANT.set(tenant);
	}

	public static void clear() {
		CURRENT_TENANT.set(null);
	}
}