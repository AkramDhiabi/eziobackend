package com.gemalto.eziomobile.demo.dto;

public class TransactionInfoDTO {

	private int userId;

	private String fromAccountNo;

	private String toAccountNo;

	private int debit;

	private int credit;

	private String description;

	private String transactionDate;

	private String amount;
	
	private String cashIn;
	
	private String cashOut;
	
	private int status;

	private String panNo;

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
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

	public int getDebit() {
		return debit;
	}

	public void setDebit(int debit) {
		this.debit = debit;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getCashIn() {
		return cashIn;
	}

	public void setCashIn(String cashIn) {
		this.cashIn = cashIn;
	}

	public String getCashOut() {
		return cashOut;
	}

	public void setCashOut(String cashOut) {
		this.cashOut = cashOut;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "TransactionInfoDTO [userId=" + userId + ", fromAccountNo=" + fromAccountNo + ", toAccountNo="
				+ toAccountNo + ", debit=" + debit + ", credit=" + credit + ", description=" + description
				+ ", transactionDate=" + transactionDate + ", amount=" + amount + ", cashIn=" + cashIn + ", cashOut="
				+ cashOut + ", status=" + status + ", panNo=" + panNo + "]";
	}


	
}
