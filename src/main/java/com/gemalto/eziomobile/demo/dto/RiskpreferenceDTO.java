package com.gemalto.eziomobile.demo.dto;

import java.util.Date;

public class RiskpreferenceDTO {

	private String userId;
	private int securityMode;
	private int status;
	private Date updatedDate;
	private String securityRiskManagement;
	private String addBeneficiaryThreshold;
	private String ownAccThreshold;
	private String otherAccThreshold;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getSecurityMode() {
		return securityMode;
	}
	public void setSecurityMode(int securityMode) {
		this.securityMode = securityMode;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getSecurityRiskManagement() {
		return securityRiskManagement;
	}
	public void setSecurityRiskManagement(String securityRiskManagement) {
		this.securityRiskManagement = securityRiskManagement;
	}
	public String getAddBeneficiaryThreshold() {
		return addBeneficiaryThreshold;
	}
	public void setAddBeneficiaryThreshold(String addBeneficiaryThreshold) {
		this.addBeneficiaryThreshold = addBeneficiaryThreshold;
	}
	public String getOwnAccThreshold() {
		return ownAccThreshold;
	}
	public void setOwnAccThreshold(String ownAccThreshold) {
		this.ownAccThreshold = ownAccThreshold;
	}
	public String getOtherAccThreshold() {
		return otherAccThreshold;
	}
	public void setOtherAccThreshold(String otherAccThreshold) {
		this.otherAccThreshold = otherAccThreshold;
	}
	@Override
	public String toString() {
		return "RiskpreferenceDTO [userId=" + userId + ", securityMode=" + securityMode + ", status=" + status
				+ ", updatedDate=" + updatedDate + ", securityRiskManagement=" + securityRiskManagement
				+ ", addBeneficiaryThreshold=" + addBeneficiaryThreshold + ", ownAccThreshold=" + ownAccThreshold
				+ ", otherAccThreshold=" + otherAccThreshold + "]";
	}
	
	
}
