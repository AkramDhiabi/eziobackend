package com.gemalto.eziomobile.demo.dao.userpreferencemaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.dto.UserpreferenceMasterDTO;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.STATUS_PARAM;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.USER_ID_PARAM;


public class UserpreferenceMasterDaoImpl implements UserpreferenceMasterOperation{

	private static final LoggerUtil logger = new LoggerUtil(UserpreferenceMasterDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	
	@Override
	public String findSecLoginByStatusAndUserId(int status, int userId) {
		
			String secLogin = "";
			Query query = entityManager.createQuery("SELECT sec_login from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
			query.setParameter(USER_ID_PARAM, userId);
			query.setParameter(STATUS_PARAM, status);
			
			secLogin = (String) query.getSingleResult();
			logger.info("[UserpreferenceMasterDaoImpl] secLogin : "+secLogin);
			
			return secLogin;
	}

	@Override
	public String findSecTxOtherByStatusAndUserId(int status, int userId) {
		
		String secTxOtherAcc = "";
		Query query = entityManager.createQuery("SELECT sec_txother from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter(STATUS_PARAM, status);
		
		secTxOtherAcc = (String) query.getSingleResult();
		logger.info("[UserpreferenceMasterDaoImpl] secTxOtherAcc : "+secTxOtherAcc);
		
		return secTxOtherAcc;
	}

	@Override
	public String findSecTxOwnAccByStatusAndUserId(int status, int userId) {
		
		String secTxOwnAcc = "";
		Query query = entityManager.createQuery("SELECT sec_txownacc from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter(STATUS_PARAM, status);
		
		secTxOwnAcc = (String) query.getSingleResult();
		logger.info("[UserpreferenceMasterDaoImpl] secTxOwnAcc : "+secTxOwnAcc);
		
		return secTxOwnAcc;
	}

	@Override
	public String findSecAddPayeeByStatusAndUserId(int status, int userId) {
		
		String secAddPayee = "";
		Query query = entityManager.createQuery("SELECT sec_addpayee from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter(STATUS_PARAM, status);
		
		secAddPayee = (String) query.getSingleResult();
		logger.info("[UserpreferenceMasterDaoImpl] secAddPayee : "+secAddPayee);
		
		return secAddPayee;
	}

	@Override
	public String findSecEcommerce3dsByStatusAndUserId(int status, int userId) {
		
		String secEcommerce3DS = "";
		Query query = entityManager.createQuery("SELECT sec_ecommerce3ds from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter(STATUS_PARAM, status);
		
		secEcommerce3DS = (String) query.getSingleResult();
		logger.info("[UserpreferenceMasterDaoImpl] secEcommerce3DS : "+secEcommerce3DS);
		
		return secEcommerce3DS;
	}

	@Override
	public String findSecP2pNotificationByStatusAndUserId(int status, int userId) {
		
		String secP2pNotification = "";
		Query query = entityManager.createQuery("SELECT p2p_notification from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter(STATUS_PARAM, status);
		
		secP2pNotification = (String) query.getSingleResult();
		logger.info("[UserpreferenceMasterDaoImpl] secP2pNotification : "+secP2pNotification);
		
		return secP2pNotification;
	}

	@Override
	public String findSecWebNotificationByStatusAndUserId(int status, int userId) {
		
		String secWebNotification = "";
		Query query = entityManager.createQuery("SELECT web_notification from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter(STATUS_PARAM, status);
		
		secWebNotification = (String) query.getSingleResult();
		logger.info("[UserpreferenceMasterDaoImpl] secWebNotification : "+secWebNotification);
		
		return secWebNotification;
	}

	@Override
	public String findSecMobileBankingByStatusAndUserId(int status, int userId) {
		
		String secMobileBanking = "";
		Query query = entityManager.createQuery("SELECT mobile_banking from UserPreferenceInfo userpreference WHERE userpreference.status= :status AND userpreference.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter(STATUS_PARAM, status);
		
		secMobileBanking = (String) query.getSingleResult();
		logger.info("[UserpreferenceMasterDaoImpl] secMobileBanking : "+secMobileBanking);
		
		return secMobileBanking;
	}

	@Override
	@Transactional
	public int updateUserPreferenceInfo(UserpreferenceMasterDTO userpreferenceMasterDTO) {
		
		int secMode = userpreferenceMasterDTO.getSec_mode();
		String secTxOtherAcc = "02"; //implemented as per previous demo -- userpreferenceMasterDTO.getSec_txother();
		String secTxOwnAcc = userpreferenceMasterDTO.getSec_txownacc();
		String secAddPayee = "02"; // implemented as per previous demo userpreferenceMasterDTO.getSec_addpayee();
		String secEcommerce3DS = userpreferenceMasterDTO.getSec_ecommerce3ds();
		String secP2pNotification = userpreferenceMasterDTO.getP2p_notification();
		String secWebNotification = "01";   // implemented as per previous demo userpreferenceMasterDTO.getWeb_notification();
		String secMobileBanking = userpreferenceMasterDTO.getMobile_banking();
		int userId = userpreferenceMasterDTO.getUserId();
		
		Query query = entityManager.createQuery("UPDATE UserPreferenceInfo userPreferenceInfo SET userPreferenceInfo.sec_mode= :secMode, userPreferenceInfo.sec_txother= :secTxOtherAcc,"
				+ "userPreferenceInfo.sec_txownacc= :secTxOwnAcc , userPreferenceInfo.sec_addpayee= :secAddPayee , userPreferenceInfo.sec_ecommerce3ds= :secEcommerce3DS , "
				+ "userPreferenceInfo.p2p_notification= :secP2pNotification , userPreferenceInfo.web_notification= :secWebNotification , userPreferenceInfo.mobile_banking= :secMobileBanking "
				+ "WHERE userPreferenceInfo.userId= :userId");
		query.setParameter("secMode", secMode);
		query.setParameter("secTxOtherAcc", secTxOtherAcc);
		query.setParameter("secTxOwnAcc", secTxOwnAcc);
		query.setParameter("secAddPayee", secAddPayee);
		query.setParameter("secEcommerce3DS", secEcommerce3DS);
		query.setParameter("secP2pNotification", secP2pNotification);
		query.setParameter("secWebNotification", secWebNotification);
		query.setParameter("secMobileBanking", secMobileBanking);
		query.setParameter(USER_ID_PARAM, userId);
		
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info("Sender's Account has been updated!");
		}
		return updateCount;
	}

/*	@Override
	@Transactional
	public int updateSenderAccount(int amount, int status, String fromAccountNo, int userId) {
		
		//entityManager.getTransaction().begin();
		Query query = entityManager
				.createQuery("from AccountMasterInfo where accountNo=:fromAccountNo and userId=:userId");
		query.setParameter("fromAccountNo", fromAccountNo);
		query.setParameter("userId", userId);
		
		AccountMasterInfo accountInfo = (AccountMasterInfo) query.getSingleResult();
		logger.info("Sender's account balance : "+accountInfo.getAccountBalance());
		
		amount = accountInfo.getAccountBalance()-amount;
		
		logger.info("Sender's updated account balance : "+amount);
		//entityManager.getTransaction().commit();
		
		query = entityManager.createQuery("UPDATE AccountMasterInfo accountinfo SET accountinfo.accountBalance= :amount WHERE accountinfo.userId= :userId AND accountinfo.status= :status AND accountinfo.accountNo= :fromAccountNo");
		query.setParameter("userId", userId);
		query.setParameter("fromAccountNo", fromAccountNo);
		query.setParameter("amount", amount);
		query.setParameter("status", status);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info("Sender's Account has been updated!");
		}
		return updateCount;
	}*/


}
