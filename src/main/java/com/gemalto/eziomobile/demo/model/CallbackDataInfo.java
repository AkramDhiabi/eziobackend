package com.gemalto.eziomobile.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "callbackdatamaster")
public class CallbackDataInfo {

	@Id
	@Column(name = "messageId")
	private String messageId;

	@Column(name = "userid")
	private String userId;

	@Column(name = "callBackResult")
	private String callBackResult;
	
	@Column(name = "responseCode")
	private int responseCode;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getCallBackResult() {
		return callBackResult;
	}

	public void setCallBackResult(String callBackResult) {
		this.callBackResult = callBackResult;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public String toString() {
		return "CallbackDataInfo [messageId=" + messageId + ", userId=" + userId + ", callBackResult=" + callBackResult
				+ ", responseCode=" + responseCode + "]";
	}

}
