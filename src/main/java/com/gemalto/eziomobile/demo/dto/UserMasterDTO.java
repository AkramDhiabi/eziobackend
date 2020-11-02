package com.gemalto.eziomobile.demo.dto;

import java.util.Map;

public class UserMasterDTO {

	private int uId;

	private String userId;

	private int groupId;

	private String permission;
	
	private Map<String, Boolean> userRole;

	private String lastLoginTime;
	
	private String emailAddress;
	
	private String password;

	private String recoverToken;

	private String tokenExpirationDate;

	public int getuId() {
		return uId;
	}

	public void setuId(int uId) {
		this.uId = uId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public Map<String, Boolean> getUserRole() {
		return userRole;
	}

	public void setUserRole(Map<String, Boolean> userRole) {
		this.userRole = userRole;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permisssion) {
		this.permission = permisssion;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRecoverToken() {
		return recoverToken;
	}

	public void setRecoverToken(String recoverToken) {
		this.recoverToken = recoverToken;
	}

	public String getTokenExpirationDate() {
		return tokenExpirationDate;
	}

	public void setTokenExpirationDate(String tokenExpirationDate) {
		this.tokenExpirationDate = tokenExpirationDate;
	}

	@Override
	public String toString() {
		return "UserMasterDTO [uId=" + uId + ", userId=" + userId + ", groupId=" + groupId + ", permission="
				+ permission + ", userRole=" + userRole + ", lastLoginTime=" + lastLoginTime + ", emailAddress="
				+ emailAddress + ", password=" + password + "]";
	}

}
