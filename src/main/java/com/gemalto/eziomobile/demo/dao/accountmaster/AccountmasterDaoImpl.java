package com.gemalto.eziomobile.demo.dao.accountmaster;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;


public class AccountmasterDaoImpl implements AccountmasterOperations{

	private static final LoggerUtil logger = new LoggerUtil(AccountmasterDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public int updateSenderAccount(int amount, int status, String fromAccountNo, int userId) {
		
		//entityManager.getTransaction().begin();
		Query query = entityManager
				.createQuery("from AccountMasterInfo where accountNo=:fromAccountNo and userId=:userId");
		query.setParameter(CommonOperationsConstants.FROM_ACCOUNT_NO_PARAM, fromAccountNo);
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		
		AccountMasterInfo accountInfo = (AccountMasterInfo) query.getSingleResult();
		logger.info("Sender's account balance : "+accountInfo.getAccountBalance());
		
		amount = accountInfo.getAccountBalance()-amount;
		
		logger.info("Sender's updated account balance : "+amount);
		//entityManager.getTransaction().commit();
		
		query = entityManager.createQuery("UPDATE AccountMasterInfo accountinfo SET accountinfo.accountBalance= :amount WHERE accountinfo.userId= :userId AND accountinfo.status= :status AND accountinfo.accountNo= :fromAccountNo");
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		query.setParameter(CommonOperationsConstants.FROM_ACCOUNT_NO_PARAM, fromAccountNo);
		query.setParameter(CommonOperationsConstants.AMOUNT_PARAM, amount);
		query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info(CommonOperationsConstants.SENDER_S_ACCOUNT_HAS_BEEN_UPDATED_MESSAGE);
		}
		return updateCount;
	}

	
	@Override
	@Transactional
	public int updateReceiverAccount(int amount, int status, String toAccountNo, int userId) {
		
		Query query = entityManager
				.createQuery("from AccountMasterInfo where accountNo=:toAccountNo and userId=:userId");
		query.setParameter(CommonOperationsConstants.TO_ACCOUNT_NO_PARAM, toAccountNo);
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		
		AccountMasterInfo accountInfo = (AccountMasterInfo) query.getSingleResult();
		logger.info("Receiver's account balance : "+accountInfo.getAccountBalance());
		
		amount = accountInfo.getAccountBalance()+amount;
		logger.info("Receiver's updated account balance : "+amount);
		
		query = entityManager.createQuery("UPDATE AccountMasterInfo accountinfo SET accountinfo.accountBalance= :amount WHERE accountinfo.userId= :userId AND accountinfo.status= :status AND accountinfo.accountNo= :toAccountNo");
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		query.setParameter(CommonOperationsConstants.TO_ACCOUNT_NO_PARAM, toAccountNo);
		query.setParameter(CommonOperationsConstants.AMOUNT_PARAM, amount);
		query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info(CommonOperationsConstants.SENDER_S_ACCOUNT_HAS_BEEN_UPDATED_MESSAGE);
		}
		return updateCount;
	}

	@Override
	@Transactional
	public int updateSenderAccount(int amount, int status, String fromAccountNo) {
		
		Query query = entityManager
				.createQuery("from AccountMasterInfo where accountNo=:fromAccountNo and status=:status");
		query.setParameter(CommonOperationsConstants.FROM_ACCOUNT_NO_PARAM, fromAccountNo);
		query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		
		AccountMasterInfo accountInfo = (AccountMasterInfo) query.getSingleResult();
		logger.info("Sender's account balance : "+accountInfo.getAccountBalance());
		
		amount = accountInfo.getAccountBalance()-amount;
		logger.info("Sender's updated account balance : "+amount);
		
		query = entityManager.createQuery("UPDATE AccountMasterInfo accountinfo SET accountinfo.accountBalance= :amount WHERE accountinfo.status = :status AND accountinfo.accountNo= :fromAccountNo");
		query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		query.setParameter(CommonOperationsConstants.FROM_ACCOUNT_NO_PARAM, fromAccountNo);
		query.setParameter(CommonOperationsConstants.AMOUNT_PARAM, amount);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info(CommonOperationsConstants.SENDER_S_ACCOUNT_HAS_BEEN_UPDATED_MESSAGE);
		}
		return updateCount;
	}

	@Override
	@Transactional
	public int updateReceiverAccount(int amount, int status, String toAccountNo) {
		
		Query query = entityManager
				.createQuery("from AccountMasterInfo where accountNo=:toAccountNo and status=:status");
		query.setParameter(CommonOperationsConstants.TO_ACCOUNT_NO_PARAM, toAccountNo);
		query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		
		AccountMasterInfo accountInfo = (AccountMasterInfo) query.getSingleResult();
		logger.info("Receiver's account balance : "+accountInfo.getAccountBalance());
		
		amount = accountInfo.getAccountBalance()+amount;
		logger.info("Receiver's updated account balance : "+amount);
		
		query = entityManager.createQuery("UPDATE AccountMasterInfo accountinfo SET accountinfo.accountBalance= :amount WHERE accountinfo.status = :status AND accountinfo.accountNo= :toAccountNo");
		query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		query.setParameter(CommonOperationsConstants.TO_ACCOUNT_NO_PARAM, toAccountNo);
		query.setParameter(CommonOperationsConstants.AMOUNT_PARAM, amount);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info("Receiver's Account has been updated!");
		}
		return updateCount;
	}

	//queryString = "update accountmaster set balance='5000' where status='1' and type='0' and user_uid=(select uid from usermaster where userid='"+userid+"')" ;
	@Override
	@Transactional
	public int updateAccountWithDefaultData(int userId) {
		Query query = entityManager.createQuery("UPDATE AccountMasterInfo accountinfo SET accountinfo.accountBalance= 5000 WHERE accountinfo.status = 1 AND  accountinfo.type= 0 AND accountinfo.userId= :userId");
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
	
			logger.info("User's Account has been updated to default data!");
		}
		return updateCount;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAccountByUserIdNotEqualsToSavings(int userId, int type, int status) {
		
		logger.info("Caling.............");
		List<String> accountList = new ArrayList<>();
		
		 String sql = "SELECT accountNo FROM AccountMasterInfo WHERE accountName <> 'Savings' AND userId = :userId AND status= :status AND type= :type";

		 Query query = entityManager.createQuery(sql);
		 query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		 query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		 query.setParameter("type", type);
		 
		 accountList = query.getResultList();
         
		logger.info("[AccountMasterInfoDaoIMPL] AccountList : " + accountList.toString());
		if (!accountList.isEmpty() && accountList.size()>0) {
			return accountList;
		}
		return accountList;
	}

	@Override
	public int findTypeByStatusAndAccountNoAndUserId(int status, String accountNo, int userId) {
		
		int accountType;
		Query query = entityManager.createQuery("SELECT type from AccountMasterInfo accountinfo WHERE accountinfo.status= :status AND accountinfo.accountNo= :accountNo AND accountinfo.userId= :userId");
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		query.setParameter(CommonOperationsConstants.STATUS_PARAM, status);
		query.setParameter("accountNo", accountNo);
		
		accountType = (Integer) query.getSingleResult();
		logger.info("[AccountmasterDaoImpl] accountType : "+accountType);
		
		return accountType;
	}


	@Override
	public String findAccountNoByUserIdAndAccountNoNotEqualToFromAccountNo(int userId, String fromAccountNo) {
		String accountNo = "";
		List<String> accountsList = new ArrayList<>();
		Query query = entityManager.createQuery("SELECT accountNo from AccountMasterInfo accountinfo WHERE accountinfo.accountNo != :accountNo AND accountinfo.userId= :userId AND accountinfo.type = 0");
		
		query.setParameter("accountNo", fromAccountNo);
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		
		accountsList = query.getResultList();
		
		if(!accountsList.isEmpty() && accountsList.size() > 0){
			logger.info("accountsList : "+accountsList.size());
			accountNo = accountsList.get(0);
		}
		logger.info("[findAccountNoByUserIdAndAccountNoNotEqualTo] accountNo : "+accountNo);
		
		return accountNo;
	}

}
