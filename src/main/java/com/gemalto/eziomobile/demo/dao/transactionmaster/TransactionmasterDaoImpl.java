package com.gemalto.eziomobile.demo.dao.transactionmaster;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.TransactionInfo;

public class TransactionmasterDaoImpl implements TransactionmasterOperations {

	@PersistenceContext
	private EntityManager entityManager;

	private static final LoggerUtil logger = new LoggerUtil(TransactionmasterDaoImpl.class.getClass());

	@Override
	public List<TransactionInfo> findTopNTransactionsByUserIdAndAccountNoAndDescriptionEqualsToEzioShop(int userId,
			String accountNo, int transactionsToDisplay, String panNo) {
		logger.info("fetching 20 transactions (of card).............");
		List<TransactionInfo> transactionsList = new ArrayList<>();

		Query query = entityManager.createQuery("FROM  TransactionInfo transactioninfo WHERE transactioninfo.userId= :userId "
				+ "AND transactioninfo.status = 1 AND transactioninfo.description = 'Ezio Shop' AND transactioninfo.panNo =:panNo AND "
				+ "(transactioninfo.fromAccountNo= :accountNo AND transactioninfo.toAccountNo != :accountNo) ORDER BY transactioninfo.transactionDate DESC");
		
		query.setParameter("userId", userId);
		query.setParameter("accountNo", accountNo);
		query.setParameter("panNo", panNo);
		query.setMaxResults(transactionsToDisplay);

		transactionsList = query.getResultList();

		logger.info("[TransactionmasterDaoImpl] transactionsList : " + transactionsList.toString());
		if (!transactionsList.isEmpty() && transactionsList.size() > 0) {
			return transactionsList;
		}
		return transactionsList;
	}

	@Override
	public List<TransactionInfo> findTop20TransactionsByUserIdAndFromAccountNoAndStatus(int userId,
			String accountNo, int status) {
		logger.info("fetching 20 transactions (of accountNo) : "+accountNo);
		List<TransactionInfo> transactionsList = new ArrayList<>();

		Query query = entityManager.createQuery("FROM  TransactionInfo transactioninfo WHERE transactioninfo.status= :status AND transactioninfo.userId= :userId "
				+ "AND (transactioninfo.fromAccountNo = :accountNo AND transactioninfo.toAccountNo != :accountNo)"
				+ " ORDER BY transactioninfo.transactionDate DESC");
		
		query.setParameter("status", status);
		query.setParameter("userId", userId);
		query.setParameter("accountNo", accountNo);
		query.setMaxResults(20);

		transactionsList = query.getResultList();

		logger.info("[TransactionmasterDaoImpl] transactionsList : " + transactionsList.toString());
		if (!transactionsList.isEmpty() && transactionsList.size() > 0) {
			return transactionsList;
		}
		return transactionsList;
	}

	
}
