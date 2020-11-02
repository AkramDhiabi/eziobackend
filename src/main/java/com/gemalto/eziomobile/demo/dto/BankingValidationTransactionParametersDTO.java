package com.gemalto.eziomobile.demo.dto;

public class BankingValidationTransactionParametersDTO extends BankingTransferDTO {

	private String tokenType;

	private String transactionData;

	private String otpValue;

	private String transactionType;

	private String sHardTokenChallenge;

	public String getsHardTokenChallenge() {
		return sHardTokenChallenge;
	}

	public void setsHardTokenChallenge(String sHardTokenChallenge) {
		this.sHardTokenChallenge = sHardTokenChallenge;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getTransactionData() {
		return transactionData;
	}

	public void setTransactionData(String transactionData) {
		this.transactionData = transactionData;
	}

	public String getOtpValue() {
		return otpValue;
	}

	public void setOtpValue(String otpValue) {
		this.otpValue = otpValue;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	public String toString() {
		return "BankingValidationTransactionParametersDTO [tokenType=" + tokenType + ", transactionData="
				+ transactionData + ", otpValue=" + otpValue + ", transactionType=" + transactionType
				+ ", sHardTokenChallenge=" + sHardTokenChallenge + "]";
	}

}
