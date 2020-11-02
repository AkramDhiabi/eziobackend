package com.gemalto.eziomobile.demo.dto;

public class ResetUserAccountDTO {

	private String userId;

	private boolean isMobile;

	private boolean isDCVCards;

	private boolean isPhysicalTokens;

	private boolean isRiskManagement;

	private boolean isDemoData;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isMobile() {
		return isMobile;
	}

	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}

	public boolean isDCVCards() {
		return isDCVCards;
	}

	public void setDCVCards(boolean isDCVCards) {
		this.isDCVCards = isDCVCards;
	}

	public boolean isPhysicalTokens() {
		return isPhysicalTokens;
	}

	public void setPhysicalTokens(boolean isPhysicalTokens) {
		this.isPhysicalTokens = isPhysicalTokens;
	}

	public boolean isRiskManagement() {
		return isRiskManagement;
	}

	public void setRiskManagement(boolean isRiskManagement) {
		this.isRiskManagement = isRiskManagement;
	}

	public boolean isDemoData() {
		return isDemoData;
	}

	public void setDemoData(boolean isDemoData) {
		this.isDemoData = isDemoData;
	}

	@Override
	public String toString() {
		return "ResetUserAccountDTO [userId=" + userId + ", isMobile=" + isMobile + ", isDCVCards=" + isDCVCards
				+ ", isPhysicalTokens=" + isPhysicalTokens + ", isRiskManagement=" + isRiskManagement + ", isDemoData="
				+ isDemoData + "]";
	}

}
