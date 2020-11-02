package com.gemalto.eziomobile.demo.dto;

public class CardManagementDTO {

	private int userId;

	private String panNo;

	private String cardStatus;

	private String internationalTravel;

	private String onlineTransaction;

	private String spendLimitTransactionStatus;

	private int amountLimitPerTransaction;

	private String spendLimitMonthStatus;

	private int amountLimitPerMonth;

	private int status;

	
	
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

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getInternationalTravel() {
		return internationalTravel;
	}

	public void setInternationalTravel(String internationalTravel) {
		this.internationalTravel = internationalTravel;
	}

	public String getOnlineTransaction() {
		return onlineTransaction;
	}

	public void setOnlineTransaction(String onlineTransaction) {
		this.onlineTransaction = onlineTransaction;
	}

	public String getSpendLimitTransactionStatus() {
		return spendLimitTransactionStatus;
	}

	public void setSpendLimitTransactionStatus(String spendLimitTransactionStatus) {
		this.spendLimitTransactionStatus = spendLimitTransactionStatus;
	}

	public int getAmountLimitPerTransaction() {
		return amountLimitPerTransaction;
	}

	public void setAmountLimitPerTransaction(int amountLimitPerTransaction) {
		this.amountLimitPerTransaction = amountLimitPerTransaction;
	}

	public String getSpendLimitMonthStatus() {
		return spendLimitMonthStatus;
	}

	public void setSpendLimitMonthStatus(String spendLimitMonthStatus) {
		this.spendLimitMonthStatus = spendLimitMonthStatus;
	}

	public int getAmountLimitPerMonth() {
		return amountLimitPerMonth;
	}

	public void setAmountLimitPerMonth(int amountLimitPerMonth) {
		this.amountLimitPerMonth = amountLimitPerMonth;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CardManagementDTO [userId=" + userId + ", panNo=" + panNo + ", cardStatus=" + cardStatus
				+ ", internationalTravel=" + internationalTravel + ", onlineTransaction=" + onlineTransaction
				+ ", spendLimitTransactionStatus=" + spendLimitTransactionStatus + ", amountLimitPerTransaction="
				+ amountLimitPerTransaction + ", spendLimitMonthStatus=" + spendLimitMonthStatus
				+ ", amountLimitPerMonth=" + amountLimitPerMonth + ", status=" + status + "]";
	}
}
