package com.gemalto.eziomobile.demo.dao.accountmaster;

import java.util.List;

public interface AccountmasterOperations {

	int updateSenderAccount(int amount, int status, String fromAccountNo, int userId);
	
	int updateReceiverAccount(int amount, int status, String toAccountNo, int userId);
	
	int updateSenderAccount(int amount, int status, String fromAccountNo);
	
	int updateReceiverAccount(int amount, int status, String toAccountNo);
	
	int updateAccountWithDefaultData(int userId);
	
	int findTypeByStatusAndAccountNoAndUserId(int status, String accountNo, int userId);
	
	List<String> findAccountByUserIdNotEqualsToSavings(int userId, int type, int status);
	
	String findAccountNoByUserIdAndAccountNoNotEqualToFromAccountNo(int userId, String accountNo);
}
