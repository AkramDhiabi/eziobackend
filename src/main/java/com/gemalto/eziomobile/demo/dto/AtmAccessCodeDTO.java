package com.gemalto.eziomobile.demo.dto;

public class AtmAccessCodeDTO {

	private String userId;

	private String accesscode;

	private int amount;

	private String fromAccountNo;
	
	private String otpValue;

	private int uId;

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

	public String getAccesscode() {
		return accesscode;
	}

	public void setAccesscode(String accesscode) {
		this.accesscode = accesscode;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getFromAccountNo() {
		return fromAccountNo;
	}

	public void setFromAccountNo(String fromAccountNo) {
		this.fromAccountNo = fromAccountNo;
	}
	
	public String getOtpValue() {
		return otpValue;
	}

	public void setOtpValue(String otpValue) {
		this.otpValue = otpValue;
	}

	@Override
	public String toString() {
		return "AtmAccessCodeDTO [userId=" + userId + ", accesscode=" + accesscode + ", amount=" + amount
				+ ", fromAccountNo=" + fromAccountNo + ", otpValue=" + otpValue + ", uId=" + uId + "]";
	}
}
