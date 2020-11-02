package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accountmaster")
public class AccountMasterInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "ac_no")
	private String accountNo;

	@Column(name = "ac_name")
	private String accountName;

	@Column(name = "balance")
	private int accountBalance;

	@Column(name = "type")
	private int type;

	@Column(name = "status")
	private int status;

	@Column(name = "acc_regdate")
	private Date accountRegistrationDate;

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

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public int getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(int accountBalance) {
		this.accountBalance = accountBalance;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAccountRegistrationDate() {
		return accountRegistrationDate;
	}

	public void setAccountRegistrationDate(Date accountRegistrationDate) {
		this.accountRegistrationDate = accountRegistrationDate;
	}

	@Override
	public String toString() {
		return "AccountMasterInfo [id=" + id + ", userId=" + userId + ", accountNo=" + accountNo + ", accountName="
				+ accountName + ", accountBalance=" + accountBalance + ", type=" + type + ", status=" + status
				+ ", accountRegistrationDate=" + accountRegistrationDate + "]";
	}

}
