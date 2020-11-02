package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "riskpreferencemaster")
public class RiskPreferenceInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private String userId;

	@Column(name = "sec_mode")
	private int securityMode;

	@Column(name = "status")
	private int status;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "sec_risk_management")
	private String securityRiskManagement;

	@Column(name = "add_beneficiary_threshold")
	private String addBeneficiaryThreshold;

	@Column(name = "own_acc_threshold")
	private String ownAccThreshold;

	@Column(name = "other_acc_threshold")
	private String otherAccThreshold;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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
		return "RiskPreferenceInfo [id=" + id + ", userId=" + userId + ", securityMode=" + securityMode
				+ ", status=" + status + ", updatedDate=" + updatedDate + ", securityRiskManagement="
				+ securityRiskManagement + ", addBeneficiaryThreshold=" + addBeneficiaryThreshold + ", ownAccThreshold="
				+ ownAccThreshold + ", otherAccThreshold=" + otherAccThreshold + "]";
	}

}
