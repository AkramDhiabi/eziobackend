package com.gemalto.eziomobile.demo;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationServiceUtil {
	
	public static String getLoggedInUser() {
		if(SecurityContextHolder.getContext().getAuthentication() != null) {
			return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		return null; 
	}

	public static String getLoggedInUserName() {

		ApplicationAuthToken appAuth = (ApplicationAuthToken) SecurityContextHolder.getContext().getAuthentication();
		return appAuth.getLoggedInUserName();
	}
	
	
	public static Integer getLoggedInUserId() {

		ApplicationAuthToken appAuth = (ApplicationAuthToken) SecurityContextHolder.getContext().getAuthentication();
		return appAuth.loggedInUserUid;
	}
	

	public static String getLoggedInUserRoleName() {

		ApplicationAuthToken appAuth = (ApplicationAuthToken) SecurityContextHolder.getContext().getAuthentication();
		return appAuth.getLoggedInRoleNames();

	}

}
