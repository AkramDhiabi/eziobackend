package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "atmqrcodemaster")
public class AtmQRCodeInfo extends BaseModel{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;
	
	@Column(name = "atm_id")
	private String atmId;
	
	@Column(name = "challenge")
	private String challenge;
	
	@Column(name = "amount")
	private int amount;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "generation_date")
	private Date generationDate;

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

	public String getAtmId() {
		return atmId;
	}

	public void setAtmId(String atmId) {
		this.atmId = atmId;
	}

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getGenerationDate() {
		return generationDate;
	}

	public void setGenerationDate(Date generationDate) {
		this.generationDate = generationDate;
	}

	
	@Override
	public String toString() {
		return "AtmQRCodeInfo [id=" + id + ", userId=" + userId + ", atmId=" + atmId + ", challenge=" + challenge
				+ ", amount=" + amount + ", status=" + status + ", generationDate=" + generationDate + "]";
	}

}
