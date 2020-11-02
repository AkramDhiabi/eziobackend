package com.gemalto.eziomobile.demo.dto;

import java.util.Date;

public class SigndataMasterDTO {
	
	private int userId;

	private String transactionId;

	private String singdata;

	private String description;

	private String msgid;

	private int status;

	private String transactionDate;

	private int type;
	
	private String hasheddata;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getSingdata() {
		return singdata;
	}

	public void setSingdata(String singdata) {
		this.singdata = singdata;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getHasheddata() {
		return hasheddata;
	}

	public void setHasheddata(String hasheddata) {
		this.hasheddata = hasheddata;
	}

	@Override
	public String toString() {
		return "SigndataMasterDTO [userId=" + userId + ", transactionId=" + transactionId + ", singdata=" + singdata
				+ ", description=" + description + ", msgid=" + msgid + ", status=" + status + ", transactionDate="
				+ transactionDate + ", type=" + type + ", hasheddata=" + hasheddata + "]";
	}
}
