package com.gemalto.eziomobile.demo;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ApplicationAuthToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 1L;

	protected Map<String, String> tokens;

	protected Integer loggedInUserUid;
	
	protected String loggedInUserName;
	
	protected String loggedInRoleNames;

	protected Integer userId;
	
	protected Integer groupId;

	
	
	public ApplicationAuthToken(UsernamePasswordAuthenticationToken token) {
		super(token.getPrincipal(), token.getCredentials(), token.getAuthorities());
	}

	public Map<String, String> getTokens() {
		return tokens;
	}

	public void setTokens(Map<String, String> tokens) {
		this.tokens = tokens;
	}

	public String getLoggedInUserName() {
		return loggedInUserName;
	}

	public void setLoggedInUserName(String loggedInUserName) {
		this.loggedInUserName = loggedInUserName;
	}

	public String getLoggedInRoleNames() {
		return loggedInRoleNames;
	}

	public void setLoggedInRoleNames(String loggedInRoleNames) {
		this.loggedInRoleNames = loggedInRoleNames;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getLoggedInUserUid() {
		return loggedInUserUid;
	}

	public void setLoggedInUserUid(Integer loggedInUserUid) {
		this.loggedInUserUid = loggedInUserUid;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

}
