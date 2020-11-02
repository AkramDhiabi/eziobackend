package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "signdatamaster")
public class SignDataInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "transectionid")
	private String transactionId;

	@Column(name = "singdata")
	private String singdata;

	@Column(name = "description")
	private String description;

	@Column(name = "msgid")
	private String msgid;

	@Column(name = "status")
	private int status;

	@Column(name = "tr_date")
	private Date transactionDate;

	@Column(name = "type")
	private int type;
	
	@Column(name = "hasheddata")
	private String hasheddata;

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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getSingdata() {
		return singdata;
	}

	public void setSingdata(String singdata) {
		this.singdata = singdata;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getHasheddata() {
		return hasheddata;
	}

	public void setHasheddata(String hasheddata) {
		this.hasheddata = hasheddata;
	}

	@Override
	public String toString() {
		return "SignDataInfo [id=" + id + ", userId=" + userId + ", transactionId=" + transactionId + ", singdata="
				+ singdata + ", description=" + description + ", msgid=" + msgid + ", status=" + status
				+ ", transactionDate=" + transactionDate + ", type=" + type + ", hasheddata=" + hasheddata + "]";
	}

}
