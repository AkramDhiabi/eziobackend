package com.gemalto.eziomobile.demo.dto;

public class AccountMasterDTO {

	private int userId;

	private String accountNo;

	private String accountName;

	private int accountBalance;

	private String accountRegistrationDate;
	
	private String accountType;
	
	private String formatAccBalance;

	private int type;
	
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public int getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(int accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getAccountRegistrationDate() {
		return accountRegistrationDate;
	}

	public void setAccountRegistrationDate(String accountRegistrationDate) {
		this.accountRegistrationDate = accountRegistrationDate;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getFormatAccBalance() {
		return formatAccBalance;
	}

	public void setFormatAccBalance(String formatAccBalance) {
		this.formatAccBalance = formatAccBalance;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "AccountMasterDTO [userId=" + userId + ", accountNo=" + accountNo + ", accountName=" + accountName
				+ ", accountBalance=" + accountBalance + ", accountRegistrationDate=" + accountRegistrationDate
				+ ", accountType=" + accountType + ", formatAccBalance=" + formatAccBalance + ", type=" + type + "]";
	}
	
}
