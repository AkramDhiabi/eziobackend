package com.gemalto.eziomobile.demo.dto;

import java.util.List;

public class AccountNumberListDTO {
	
	private String userId;
	private List<String> accountList;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public List<String> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<String> accountList) {
		this.accountList = accountList;
	}

}
