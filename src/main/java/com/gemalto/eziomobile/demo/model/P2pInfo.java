package com.gemalto.eziomobile.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "p2pmaster")
public class P2pInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "benificiaryuserid")
	private String benificiary_UserId;

	@Column(name = "fromaccount")
	private String fromAccountNo;

	@Column(name = "amount")
	private String amount;

	@Column(name = "challenge")
	private int challenge;

	@Column(name = "msg")
	private String msg;

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

	public String getBenificiary_UserId() {
		return benificiary_UserId;
	}

	public void setBenificiary_UserId(String benificiary_UserId) {
		this.benificiary_UserId = benificiary_UserId;
	}

	public String getFromAccountNo() {
		return fromAccountNo;
	}

	public void setFromAccountNo(String fromAccountNo) {
		this.fromAccountNo = fromAccountNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public int getChallenge() {
		return challenge;
	}

	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "P2pInfo [id=" + id + ", userId=" + userId + ", benificiary_UserId=" + benificiary_UserId
				+ ", fromAccountNo=" + fromAccountNo + ", amount=" + amount + ", challenge=" + challenge + ", msg="
				+ msg + "]";
	}

}
