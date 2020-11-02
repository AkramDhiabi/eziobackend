package com.gemalto.eziomobile.demo.dao.qrtokenmaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;

public class QRTokenmasterDaoImpl implements QRTokenmasterOperations{
	
	private static final LoggerUtil logger = new LoggerUtil(QRTokenmasterDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String findtransactionHashByUserId(String userId) {
			
		String transaction_hash = "";
		
		Query query = entityManager.createQuery("SELECT transactionHash from QRTokenMasterInfo qrTokenMasterInfo WHERE qrTokenMasterInfo.userId= :userId");
		query.setParameter("userId", userId);
	
		transaction_hash = (String) query.getSingleResult();
		logger.info("print transaction_hash : "+ transaction_hash);
		if(transaction_hash != null && !transaction_hash.isEmpty()) {
			logger.info("[QRTokenmasterDaoImpl] transaction_hash : " + transaction_hash);
		}
				
		return transaction_hash;
	}

	@Override
	public String findtransactionHashByUserIdAndTranscationType(String userId) {
		String transaction_hash = "";
		
		Query query = entityManager.createQuery("SELECT transactionHash from QRTokenMasterInfo qrTokenMasterInfo WHERE qrTokenMasterInfo.userId= :userId AND qrTokenMasterInfo.transactionType = 01");
		query.setParameter("userId", userId);
	
		transaction_hash = (String) query.getSingleResult();
		logger.info("print transaction_hash : "+ transaction_hash);
		if(transaction_hash != null && !transaction_hash.isEmpty()) {
			logger.info("[QRTokenmasterDaoImpl] transaction_hash : " + transaction_hash);
		}
				
		return transaction_hash;
	}
	

}
