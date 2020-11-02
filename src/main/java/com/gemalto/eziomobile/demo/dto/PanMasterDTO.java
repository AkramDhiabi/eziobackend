package com.gemalto.eziomobile.demo.dto;

public class PanMasterDTO {

	private int userId;

	private String accountNo;

	private String panNo;

	private String panType;

	private String expDate;

	private int cardCVV;

	private String tokenId;

	private int isDCV_Active;

	private int status;

	private String registrationDate;

	private String linkedToAccount;
	
	private boolean isCardFreezed;
	
	private int panTypeFlag;

	
	
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

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public int getCardCVV() {
		return cardCVV;
	}

	public void setCardCVV(int cardCVV) {
		this.cardCVV = cardCVV;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public int getIsDCV_Active() {
		return isDCV_Active;
	}

	public void setIsDCV_Active(int isDCV_Active) {
		this.isDCV_Active = isDCV_Active;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getPanType() {
		return panType;
	}

	public void setPanType(String panType) {
		this.panType = panType;
	}

	public String getLinkedToAccount() {
		return linkedToAccount;
	}

	public void setLinkedToAccount(String linkedToAccount) {
		this.linkedToAccount = linkedToAccount;
	}

	public boolean isCardFreezed() {
		return isCardFreezed;
	}

	public void setCardFreezed(boolean isCardFreezed) {
		this.isCardFreezed = isCardFreezed;
	}

	public int getPanTypeFlag() {
		return panTypeFlag;
	}

	public void setPanTypeFlag(int panTypeFlag) {
		this.panTypeFlag = panTypeFlag;
	}

	@Override
	public String toString() {
		return "PanMasterDTO [userId=" + userId + ", accountNo=" + accountNo + ", panNo=" + panNo + ", panType="
				+ panType + ", expDate=" + expDate + ", cardCVV=" + cardCVV + ", tokenId=" + tokenId + ", isDCV_Active="
				+ isDCV_Active + ", status=" + status + ", registrationDate=" + registrationDate + ", linkedToAccount="
				+ linkedToAccount + ", isCardFreezed=" + isCardFreezed + ", panTypeFlag=" + panTypeFlag + "]";
	}
	
}
