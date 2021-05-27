package io.onedev.server.rest.jersey;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.UnauthenticatedException;

import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.security.SecurityUtils;

@Provider
public class AnonymousCheckFilter implements ContainerRequestFilter {

	private final SettingManager settingManager;
	
	@Context
	private HttpServletRequest request;
	
	@Inject
	public AnonymousCheckFilter(SettingManager settingManager) {
		this.settingManager = settingManager;
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (SecurityUtils.getUser() == null) { 
			String method = request.getMethod();
			if (method.equals("POST") || method.equals("DELETE") || method.equals("PUT") 
					|| !settingManager.getSecuritySetting().isEnableAnonymousAccess()) {
				throw new UnauthenticatedException();
			}
		}
	}

}