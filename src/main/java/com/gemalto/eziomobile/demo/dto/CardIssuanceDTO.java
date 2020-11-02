package com.gemalto.eziomobile.demo.dto;

public class CardIssuanceDTO {

	private int userId;

	private String panNo;

	private String expDate;

	private int cardCVV;

	private int isDCV_Active;

	private int status;

	private String cardRequestDate;
	
	private int panType;
	

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public String getCardRequestDate() {
		return cardRequestDate;
	}

	public void setCardRequestDate(String cardRequestDate) {
		this.cardRequestDate = cardRequestDate;
	}

	public int getPanType() {
		return panType;
	}

	public void setPanType(int panType) {
		this.panType = panType;
	}

	@Override
	public String toString() {
		return "CardIssuanceDTO [userId=" + userId + ", panNo=" + panNo + ", expDate=" + expDate + ", cardCVV="
				+ cardCVV + ", isDCV_Active=" + isDCV_Active + ", status=" + status + ", cardRequestDate="
				+ cardRequestDate + ", panType=" + panType + "]";
	}

}
