package com.gemalto.eziomobile.demo.dto;

public class TokenResynchronizationDTO {

	private String userId;

	private String tokenSerialNumber;

	private String otpValue1;

	private String otpValue2;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTokenSerialNumber() {
		return tokenSerialNumber;
	}

	public void setTokenSerialNumber(String tokenSerialNumber) {
		this.tokenSerialNumber = tokenSerialNumber;
	}

	public String getOtpValue1() {
		return otpValue1;
	}

	public void setOtpValue1(String otpValue1) {
		this.otpValue1 = otpValue1;
	}

	public String getOtpValue2() {
		return otpValue2;
	}

	public void setOtpValue2(String otpValue2) {
		this.otpValue2 = otpValue2;
	}

	@Override
	public String toString() {
		return "TokenResynchronizationDTO [userId=" + userId + ", tokenSerialNumber=" + tokenSerialNumber
				+ ", otpValue1=" + otpValue1 + ", otpValue2=" + otpValue2 + "]";
	}

}
