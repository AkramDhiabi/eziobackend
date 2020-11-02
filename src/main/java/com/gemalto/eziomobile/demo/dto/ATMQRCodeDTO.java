package com.gemalto.eziomobile.demo.dto;

public class ATMQRCodeDTO {

	private String userId;

	private String atmId;

	private String challenge;

	private String amount;

	private String qrCodeForAtm;

	private String fromAccNo;

	private String otpValue;
	
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAtmId() {
		return atmId;
	}

	public void setAtmId(String atmId) {
		this.atmId = atmId;
	}

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getQrCodeForAtm() {
		return qrCodeForAtm;
	}

	public void setQrCodeForAtm(String qrCodeForAtm) {
		this.qrCodeForAtm = qrCodeForAtm;
	}

	public String getFromAccNo() {
		return fromAccNo;
	}

	public void setFromAccNo(String fromAccNo) {
		this.fromAccNo = fromAccNo;
	}

	public String getOtpValue() {
		return otpValue;
	}

	public void setOtpValue(String otpValue) {
		this.otpValue = otpValue;
	}

	@Override
	public String toString() {
		return "ATMQRCodeDTO [userId=" + userId + ", atmId=" + atmId + ", challenge=" + challenge + ", amount=" + amount
				+ ", qrCodeForAtm=" + qrCodeForAtm + ", fromAccNo=" + fromAccNo + ", otpValue=" + otpValue + "]";
	}
}
