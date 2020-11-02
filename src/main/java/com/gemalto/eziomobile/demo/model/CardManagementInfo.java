package com.gemalto.eziomobile.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cardmanagementmaster")
public class CardManagementInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "pan_no")
	private String panNo;

	@Column(name = "card_status")
	private String cardStatus;

	@Column(name = "international_travel")
	private String internationalTravel;

	@Column(name = "online_transaction")
	private String onlineTransaction;

	@Column(name = "spend_limit_transaction_status")
	private String spendLimitTransactionStatus;

	@Column(name = "amount_limit_per_transaction")
	private int amountLimitPerTransaction;

	@Column(name = "spend_limit_month_status")
	private String spendLimitMonthStatus;

	@Column(name = "amount_limit_per_month")
	private int amountLimitPerMonth;

	@Column(name = "status")
	private int status;

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

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getInternationalTravel() {
		return internationalTravel;
	}

	public void setInternationalTravel(String internationalTravel) {
		this.internationalTravel = internationalTravel;
	}

	public String getOnlineTransaction() {
		return onlineTransaction;
	}

	public void setOnlineTransaction(String onlineTransaction) {
		this.onlineTransaction = onlineTransaction;
	}

	public String getSpendLimitTransactionStatus() {
		return spendLimitTransactionStatus;
	}

	public void setSpendLimitTransactionStatus(String spendLimitTransactionStatus) {
		this.spendLimitTransactionStatus = spendLimitTransactionStatus;
	}

	public int getAmountLimitPerTransaction() {
		return amountLimitPerTransaction;
	}

	public void setAmountLimitPerTransaction(int amountLimitPerTransaction) {
		this.amountLimitPerTransaction = amountLimitPerTransaction;
	}

	public String getSpendLimitMonthStatus() {
		return spendLimitMonthStatus;
	}

	public void setSpendLimitMonthStatus(String spendLimitMonthStatus) {
		this.spendLimitMonthStatus = spendLimitMonthStatus;
	}

	public int getAmountLimitPerMonth() {
		return amountLimitPerMonth;
	}

	public void setAmountLimitPerMonth(int amountLimitPerMonth) {
		this.amountLimitPerMonth = amountLimitPerMonth;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CardManagementInfo [id=" + id + ", userId=" + userId + ", panNo=" + panNo + ", cardStatus="
				+ cardStatus + ", internationalTravel=" + internationalTravel + ", onlineTransaction="
				+ onlineTransaction + ", spendLimitTransactionStatus=" + spendLimitTransactionStatus
				+ ", amountLimitPerTransaction=" + amountLimitPerTransaction + ", spendLimitMonthStatus="
				+ spendLimitMonthStatus + ", amountLimitPerMonth=" + amountLimitPerMonth + ", status=" + status + "]";
	}

}
