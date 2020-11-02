package com.gemalto.eziomobile.demo.dto.mobiledata;

import java.util.List;

public class MobileDataDTO {

	private int status;

	private List<CardListDTO> card_list;

	private List<AccountListDTO> accounts_list;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<CardListDTO> getCard_list() {
		return card_list;
	}

	public void setCard_list(List<CardListDTO> card_list) {
		this.card_list = card_list;
	}

	public List<AccountListDTO> getAccounts_list() {
		return accounts_list;
	}

	public void setAccounts_list(List<AccountListDTO> accounts_list) {
		this.accounts_list = accounts_list;
	}

	@Override
	public String toString() {
		return "MobileDataDTO [status=" + status + ", card_list=" + card_list + ", accounts_list=" + accounts_list
				+ "]";
	}
}
