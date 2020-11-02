package com.gemalto.eziomobile.demo.dto.mobiledata;

import java.util.List;
import java.util.Map;

public class CardListDTO {

	private List<Map<String, String>> Spend_limit_per_month;

	private List<Map<String, String>> Spend_limit_per_transaction;

	private String International_travel;

	private String Card_ON_OFF;

	private int cvv;

	private String pan;

	private String Online_transaction;

	private int dcven;

	private String expdate;
	
	private int panType;
	

	public List<Map<String, String>> getSpend_limit_per_month() {
		return Spend_limit_per_month;
	}

	public void setSpend_limit_per_month(List<Map<String, String>> spend_limit_per_month) {
		Spend_limit_per_month = spend_limit_per_month;
	}

	public List<Map<String, String>> getSpend_limit_per_transaction() {
		return Spend_limit_per_transaction;
	}

	public void setSpend_limit_per_transaction(List<Map<String, String>> spend_limit_per_transaction) {
		Spend_limit_per_transaction = spend_limit_per_transaction;
	}

	public String getInternational_travel() {
		return International_travel;
	}

	public void setInternational_travel(String international_travel) {
		International_travel = international_travel;
	}

	public String getCard_ON_OFF() {
		return Card_ON_OFF;
	}

	public void setCard_ON_OFF(String card_ON_OFF) {
		Card_ON_OFF = card_ON_OFF;
	}

	public int getCvv() {
		return cvv;
	}

	public void setCvv(int cvv) {
		this.cvv = cvv;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getOnline_transaction() {
		return Online_transaction;
	}

	public void setOnline_transaction(String online_transaction) {
		Online_transaction = online_transaction;
	}

	public int getDcven() {
		return dcven;
	}

	public void setDcven(int dcven) {
		this.dcven = dcven;
	}

	public String getExpdate() {
		return expdate;
	}

	public void setExpdate(String expdate) {
		this.expdate = expdate;
	}

	public int getPanType() {
		return panType;
	}

	public void setPanType(int panType) {
		this.panType = panType;
	}

	@Override
	public String toString() {
		return "CardListDTO [Spend_limit_per_month=" + Spend_limit_per_month + ", Spend_limit_per_transaction="
				+ Spend_limit_per_transaction + ", International_travel=" + International_travel + ", Card_ON_OFF="
				+ Card_ON_OFF + ", cvv=" + cvv + ", pan=" + pan + ", Online_transaction=" + Online_transaction
				+ ", dcven=" + dcven + ", expdate=" + expdate + ", panType=" + panType + "]";
	}
}
