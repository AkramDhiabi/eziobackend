package com.gemalto.eziomobile.demo.dto;

public class MobileRegistrationDTO {

	String regCode;

	String pin;

	String tokenId;

	String qrCodeData;

	public String getRegCode() {
		return regCode;
	}

	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getQrCodeData() {
		return qrCodeData;
	}

	public void setQrCodeData(String qrCodeData) {
		this.qrCodeData = qrCodeData;
	}

	@Override
	public String toString() {
		return "MobileRegistrationDTO [regCode=" + regCode + ", pin=" + pin + ", tokenId=" + tokenId + ", qrCodeData="
				+ qrCodeData + "]";
	}

}
