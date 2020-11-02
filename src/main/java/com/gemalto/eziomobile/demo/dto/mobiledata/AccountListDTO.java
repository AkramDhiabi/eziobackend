package com.gemalto.eziomobile.demo.dto.mobiledata;

public class AccountListDTO {

	private String cur;

	private String account_no;

	private String account_name;

	private int total;

	private int type;

	
	
	public String getCur() {
		return cur;
	}

	public void setCur(String cur) {
		this.cur = cur;
	}

	public String getAccount_no() {
		return account_no;
	}

	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "AccountListDTO [cur=" + cur + ", account_no=" + account_no + ", account_name=" + account_name
				+ ", total=" + total + ", type=" + type + "]";
	}
}
