package com.gemalto.eziomobile.demo.service.transactionmaster;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.TransactionInfoDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface TransactionmasterService {

	void saveTransaction(TransactionInfoDTO transactionInfo) throws ServiceException;

	List<TransactionInfoDTO> findTop20TransactionsByUserIdAndFromAccountNoAndStatus(int userId, String accountNo, int status) throws ServiceException;

	boolean deleteUserTransactions(int userId) throws ServiceException;

	List<TransactionInfoDTO> findTopNTransactionsForCard(int userId,
			String accountNo, int transactionsToDisplay, String panNo) throws ServiceException;
	
}
