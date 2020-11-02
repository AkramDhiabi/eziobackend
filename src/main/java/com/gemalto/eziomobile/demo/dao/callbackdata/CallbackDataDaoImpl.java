package com.gemalto.eziomobile.demo.dao.callbackdata;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.USER_ID_PARAM;

public class CallbackDataDaoImpl implements CallbackDataOperations{
	
	private static final LoggerUtil logger = new LoggerUtil(CallbackDataDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String findCallbackTypeByUserIdAndMessageId(String userId, String messageId) {
		Query query = entityManager
				.createQuery("SELECT callBackType from CallbackDataInfo callbackinfo WHERE callbackinfo.userId= :userId and callbackinfo.messageId= :messageId");
		query.setParameter(USER_ID_PARAM, userId);
		query.setParameter("messageId", messageId);

		String callBackType = (String) query.getSingleResult();
		logger.info("[CallbackDataDaoImpl] callBackType : " + callBackType);
		if (callBackType != null && !callBackType.equals("")) {
			return callBackType;
		}
		return null;
	}

	@Override
	public String findCallbackTypeByUserId(String userId) {
		Query query = entityManager
				.createQuery("SELECT callBackType from CallbackDataInfo callbackinfo WHERE callbackinfo.userId= :userId");
		query.setParameter(USER_ID_PARAM, userId);

		String callBackType = (String) query.getSingleResult();
		logger.info("[CallbackDataDaoImpl] callBackType : " + callBackType);
		if (callBackType != null && !callBackType.equals("")) {
			return callBackType;
		}
		return null;
	}

	
	@Override
	@Transactional
	public void updateCallBackData(String userId, String messageId, String callBackResult, int responseCode) {
			
			Query query = entityManager.createQuery("UPDATE CallbackDataInfo callbackinfo SET callbackinfo.callBackResult= :callBackResult, callbackinfo.responseCode= :responseCode WHERE callbackinfo.userId= :userId AND callbackinfo.messageId= :messageId");
			query.setParameter(USER_ID_PARAM, userId);
			query.setParameter("messageId", messageId);
			query.setParameter("callBackResult", callBackResult);
			query.setParameter("responseCode", responseCode);
			
			int updateCount = query.executeUpdate();
			if (updateCount > 0) {
				logger.info("Callback data has been updated!");
			}
	}

}
