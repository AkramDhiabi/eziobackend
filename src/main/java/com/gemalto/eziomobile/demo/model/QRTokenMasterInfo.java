package com.gemalto.eziomobile.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qrtokenmaster")
public class QRTokenMasterInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "user_uid")
	private String userId;

	@Column(name = "transaction_type")
	private String transactionType;

	@Column(name = "qrcode_version")
	private String qrcodeVersion;

	@Column(name = "transaction_hash")
	private String transactionHash;


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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getQrcodeVersion() {
		return qrcodeVersion;
	}

	public void setQrcodeVersion(String qrcodeVersion) {
		this.qrcodeVersion = qrcodeVersion;
	}

	public String getTransactionHash() {
		return transactionHash;
	}

	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
	}

}
