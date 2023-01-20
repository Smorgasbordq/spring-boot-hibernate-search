package com.mozen.springboothibernatesearch.controller;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mozen.springboothibernatesearch.hb.TenantContext;
import com.mozen.springboothibernatesearch.hb.Tenants;

@Component
@Order(1)
class TenantFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String tc = Tenants.TEN1;
		String query = req.getQueryString();
		int i = query.indexOf("tc=");
		if (i >= 0) {
			int j = query.indexOf("&", i + 3);
			tc = URLDecoder.decode(j >= 0 ? query.substring(i + 3, j) : query.substring(i + 3), "UTF-8");
		}
		TenantContext.setCurrentTenant(tc);
		try {
			chain.doFilter(request, response);
		} finally {
			TenantContext.clear();
		}

	}
}