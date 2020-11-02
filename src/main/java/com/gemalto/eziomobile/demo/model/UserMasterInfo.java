package com.gemalto.eziomobile.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;


@Entity
@Table(name = "usermaster")
public class UserMasterInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uid")
	private int uId;

	@Column(name = "userid")
	private String userId;

	@Column(name = "pwd")
	private String password;

	@Column(name = "group_id")
	private int groupId;

	@Column(name = "last_login")
	private String lastLoginTime;

	@Column(name = "status")
	private int status;

	@Column(name = "user_role")
	private String userRole;
	
	@Column(name = "email_address")
	private String emailAddress;

	@Column(name = "recover_token")
	private String recoverToken;

	@Column(name = "token_expiration_date")
	private Date tokenExpirationDate;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getRecoverToken() {
		return recoverToken;
	}

	public void setRecoverToken(String recoverToken) {
		this.recoverToken = recoverToken;
	}

	public Date getTokenExpirationDate() {
		return tokenExpirationDate;
	}

	public void setTokenExpirationDate(Date tokenExpirationDate) {
		this.tokenExpirationDate = tokenExpirationDate;
	}

	@Override
	public String toString() {
		return "UserMasterInfo [uId=" + uId + ", userId=" + userId + ", password=" + password + ", groupId=" + groupId
				+ ", lastLoginTime=" + lastLoginTime + ", status=" + status + ", userRole=" + userRole
				+ ", emailAddress=" + emailAddress + "]";
	}

}
