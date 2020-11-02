package com.gemalto.eziomobile.demo.dto;

public class PageRequestDTO {
	
	private int pageNo;
	
	private int noOfRecords;
	
	private String userId;
	
	private String accountNo;
	
	private String cardNo;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Override
	public String toString() {
		return "PageRequestDTO [pageNo=" + pageNo + ", noOfRecords=" + noOfRecords + ", userId=" + userId
				+ ", accountNo=" + accountNo + ", cardNo=" + cardNo + "]";
	}

}
