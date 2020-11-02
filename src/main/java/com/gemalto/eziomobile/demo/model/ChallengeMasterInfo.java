package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "challengemaster")
public class ChallengeMasterInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "challenge")
	private String challenge;

	@Column(name = "singdata")
	private String singData;

	@Column(name = "status")
	private int status;

	@Column(name = "tr_date")
	private Date tr_date;

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

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public String getSingData() {
		return singData;
	}

	public void setSingData(String singData) {
		this.singData = singData;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getTr_date() {
		return tr_date;
	}

	public void setTr_date(Date tr_date) {
		this.tr_date = tr_date;
	}

	@Override
	public String toString() {
		return "ChallengeMasterInfo [id=" + id + ", userId=" + userId + ", challenge=" + challenge + ", singData="
				+ singData + ", status=" + status + ", tr_date=" + tr_date + "]";
	}

}
