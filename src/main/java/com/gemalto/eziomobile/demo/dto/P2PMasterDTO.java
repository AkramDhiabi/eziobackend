package com.gemalto.eziomobile.demo.dto;

public class P2PMasterDTO {

	private int userId;

	private String benificiaryUserId;

	private String fromAccountNo;

	private String amount;

	private int challenge;

	private String msg;
	

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getBenificiaryUserId() {
		return benificiaryUserId;
	}

	public void setBenificiaryUserId(String benificiaryUserId) {
		this.benificiaryUserId = benificiaryUserId;
	}

	public String getFromAccountNo() {
		return fromAccountNo;
	}

	public void setFromAccountNo(String fromAccountNo) {
		this.fromAccountNo = fromAccountNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public int getChallenge() {
		return challenge;
	}

	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "P2PMasterDTO [userId=" + userId + ", benificiaryUserId=" + benificiaryUserId + ", fromAccountNo="
				+ fromAccountNo + ", amount=" + amount + ", challenge=" + challenge + ", msg=" + msg + "]";
	}

}
