package com.gemalto.eziomobile.demo.dao.transactionmaster;

import java.util.List;

import com.gemalto.eziomobile.demo.model.TransactionInfo;

public interface TransactionmasterOperations {
	
	List<TransactionInfo> findTopNTransactionsByUserIdAndAccountNoAndDescriptionEqualsToEzioShop(int userId, String accountNo, int transactionsToDisplay, String panNo);

	List<TransactionInfo> findTop20TransactionsByUserIdAndFromAccountNoAndStatus(int userId, String accountNo, int status);
}
