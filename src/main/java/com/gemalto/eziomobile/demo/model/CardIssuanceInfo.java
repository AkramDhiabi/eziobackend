package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cardissuancemaster")
public class CardIssuanceInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "pan_no")
	private String panNo;

	@Column(name = "exp_date")
	private String expDate;

	@Column(name = "cvv")
	private int cardCVV;

	@Column(name = "is_dcv_active")
	private int isDCV_Active;

	@Column(name = "status")
	private int status;

	@Column(name = "regdate")
	private Date registrationDate;

	@Column(name = "pan_type")
	private int panType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public int getPanType() {
		return panType;
	}

	public void setPanType(int panType) {
		this.panType = panType;
	}

	@Override
	public String toString() {
		return "CardIssuanceInfo [id=" + id + ", userId=" + userId + ", panNo=" + panNo + ", expDate=" + expDate
				+ ", cardCVV=" + cardCVV + ", isDCV_Active=" + isDCV_Active + ", status=" + status
				+ ", registrationDate=" + registrationDate + ", panType=" + panType + "]";
	}

}
