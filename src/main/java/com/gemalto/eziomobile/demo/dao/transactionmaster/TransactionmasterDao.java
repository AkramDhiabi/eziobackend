package com.gemalto.eziomobile.demo.dao.transactionmaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.TransactionInfo;

@Repository
public interface TransactionmasterDao extends CrudRepository<TransactionInfo, Integer>, TransactionmasterOperations{
	
	int deleteUserTransactionByUserIdAndStatus(int userId, int status);
	
	void deleteTransactionInfoByStatusAndUserId(int status, int userId);
}
