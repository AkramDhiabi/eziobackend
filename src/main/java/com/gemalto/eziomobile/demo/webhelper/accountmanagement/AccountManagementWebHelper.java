package com.gemalto.eziomobile.demo.webhelper.accountmanagement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.AccountMasterDTO;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;
import com.gemalto.eziomobile.demo.util.CurrencyUtil;

@Component
public class AccountManagementWebHelper {

	private static final LoggerUtil logger = new LoggerUtil(AccountManagementWebHelper.class.getClass());

	/**
	 * Prepare list of accounts based on the name of the account
	 * 
	 * @param accountsList
	 * @return
	 */
	public List<AccountMasterDTO> prepareAccountList(List<AccountMasterInfo> accountsList) {

		List<AccountMasterDTO> accounts = new ArrayList<>();

		accountsList.forEach(accountItem -> {
			AccountMasterDTO accountDTO = new AccountMasterDTO();
			
			switch (accountItem.getAccountName()) {
			case EzioMobileDemoConstant.ACCOUNT_TYPE_CARD:
				accountDTO.setAccountType(EzioMobileDemoConstant.ACCOUNT_TYPE_CREDIT);
				break;
			case EzioMobileDemoConstant.ACCOUNT_TYPE_SAVINGS:
				accountDTO.setAccountType(EzioMobileDemoConstant.ACCOUNT_TYPE_DEBIT);
				break;
			default:
				break;
			}
			accountDTO.setUserId(accountItem.getUserId());
			accountDTO.setAccountName(accountItem.getAccountName());
			accountDTO.setAccountNo(accountItem.getAccountNo());
			accountDTO.setAccountBalance(accountItem.getAccountBalance());
			accountDTO.setFormatAccBalance(CurrencyUtil.formatCurrency(accountItem.getAccountBalance()));
			accountDTO.setAccountRegistrationDate(ConvertDateToStringDate.convertDateToString(accountItem.getAccountRegistrationDate()));

			accounts.add(accountDTO);
		});

		logger.info("[AccountManagementWebHelper] accounts : " + accounts.toString());
		return accounts;
	}

}
