package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "atmaccesscodemaster")
public class AtmAccessCodeInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "accesscode")
	private String accesscode;

	@Column(name = "amount")
	private int amount;

	@Column(name = "status")
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "codegentime")
	private Date codeGenerationTime;
	
	@Column(name = "fromaccountno")
	private String fromAccountNo;

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

	public String getAccesscode() {
		return accesscode;
	}

	public void setAccesscode(String accesscode) {
		this.accesscode = accesscode;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCodeGenerationTime() {
		return codeGenerationTime;
	}

	public void setCodeGenerationTime(Date codeGenerationTime) {
		this.codeGenerationTime = codeGenerationTime;
	}

	public String getFromAccountNo() {
		return fromAccountNo;
	}

	public void setFromAccountNo(String fromAccountNo) {
		this.fromAccountNo = fromAccountNo;
	}

	@Override
	public String toString() {
		return "AtmAccessCodeInfo [id=" + id + ", userId=" + userId + ", accesscode=" + accesscode + ", amount="
				+ amount + ", status=" + status + ", codeGenerationTime=" + codeGenerationTime + ", fromAccountNo="
				+ fromAccountNo + "]";
	}
	
}
