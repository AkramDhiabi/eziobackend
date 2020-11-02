package com.gemalto.eziomobile.demo.dto;

public class BankingTransferDTO {

	private String userId;

	private String fromAccountNo;

	private String toAccountNo;

	private String description;

	private String amount;

	private String beneficiaryName;

	private String beneficiaryAccount;
	
	private String cardNumber;
	
	private String cvv;
	
	private String expDate;
	
	private String cardType;
	

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBeneficiaryAccount() {
		return beneficiaryAccount;
	}

	public void setBeneficiaryAccount(String beneficiaryAccount) {
		this.beneficiaryAccount = beneficiaryAccount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFromAccountNo() {
		return fromAccountNo;
	}

	public void setFromAccountNo(String fromAccountNo) {
		this.fromAccountNo = fromAccountNo;
	}

	public String getToAccountNo() {
		return toAccountNo;
	}

	public void setToAccountNo(String toAccountNo) {
		this.toAccountNo = toAccountNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@Override
	public String toString() {
		return "BankingTransferDTO [userId=" + userId + ", fromAccountNo=" + fromAccountNo + ", toAccountNo="
				+ toAccountNo + ", description=" + description + ", amount=" + amount + ", beneficiaryName="
				+ beneficiaryName + ", beneficiaryAccount=" + beneficiaryAccount + ", cardNumber=" + cardNumber
				+ ", cvv=" + cvv + ", expDate=" + expDate + ", cardType=" + cardType + "]";
	}

}
