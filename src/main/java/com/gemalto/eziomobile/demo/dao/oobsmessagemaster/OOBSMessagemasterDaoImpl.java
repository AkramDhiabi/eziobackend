package com.gemalto.eziomobile.demo.dao.oobsmessagemaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.MESSAGE_ID_PARAM;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.OOBSMESSAGEMASTER_DAO_IMPL_MESSAGE_ID;

public class OOBSMessagemasterDaoImpl implements OOBSMasterOperations {

	private static final LoggerUtil logger = new LoggerUtil(OOBSMessagemasterDaoImpl.class.getClass());


	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String findMsgIdByUserId(String userId) {
		Query query = entityManager
				.createQuery("SELECT messageId from OOBSMessageMasterInfo oobsinfo WHERE oobsinfo.userId= :userId");
		query.setParameter("userId", userId);

		String messageId = (String) query.getSingleResult();
		logger.info(OOBSMESSAGEMASTER_DAO_IMPL_MESSAGE_ID + messageId);
		if (messageId != null && !messageId.equals("")) {
			return messageId;
		}
		return null;
	}

	@Override
	public String findMsgTypeByMessageId(String messageId) {
		Query query = entityManager.createQuery(
				"SELECT messageType from OOBSMessageMasterInfo oobsinfo WHERE oobsinfo.messageId= :messageId");
		query.setParameter(MESSAGE_ID_PARAM, messageId);

		String messageType = (String) query.getSingleResult();
		logger.info("[OOBSMessagemasterDaoImpl] messageType : " + messageType);
		if (messageType != null && !messageType.equals("")) {
			return messageType;
		}
		return null;
	}

	
	@Override
	public String findUserIdByMessageId(String messageId) {
		String queryString = "SELECT userId from OOBSMessageMasterInfo oobsinfo WHERE oobsinfo.messageId= :messageId";
		Query query = entityManager
				.createQuery(queryString);
		query.setParameter(MESSAGE_ID_PARAM, messageId);

		String userId = (String) query.getSingleResult();
		logger.info(OOBSMESSAGEMASTER_DAO_IMPL_MESSAGE_ID + messageId);
		if (userId != null && !userId.equals("")) {
			return userId;
		}
		return null;
	}

	@Override
	public String checkNotificationStateFromOOBS(String messageId) {
		String userId = null;
		Query query = entityManager
				.createQuery("SELECT userId from OOBSMessageMasterInfo oobsinfo WHERE oobsinfo.messageId= :messageId");
		query.setParameter(MESSAGE_ID_PARAM, messageId);

		userId = (String) query.getSingleResult();
		logger.info(OOBSMESSAGEMASTER_DAO_IMPL_MESSAGE_ID + messageId);
		if (userId != null && !userId.equals("")) {
			return userId;
		}
		return null;
	}

}
