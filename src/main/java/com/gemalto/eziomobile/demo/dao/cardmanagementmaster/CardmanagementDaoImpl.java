package com.gemalto.eziomobile.demo.dao.cardmanagementmaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;

public class CardmanagementDaoImpl implements CardmanagementOperation{

	private static final LoggerUtil logger = new LoggerUtil(CardmanagementDaoImpl.class.getClass());


	@PersistenceContext
	private EntityManager entityManager;

	
	@Override
	@Transactional
	public boolean updateCardStatusByUserIdAndPanNo(int userId, String panNo, String cardStatusValue) {
		boolean isCardUpdate = false;
		Query query = entityManager.createQuery("UPDATE CardManagementInfo cardinfo SET cardinfo.cardStatus = :cardStatusValue WHERE cardinfo.userId= :userId AND cardinfo.panNo = :panNo");
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		query.setParameter(CommonOperationsConstants.PAN_NO_PARAM, panNo);
		query.setParameter(CommonOperationsConstants.CARD_STATUS_VALUE_PARAM, cardStatusValue);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info("Card-Status has been updated with : "+cardStatusValue);
			isCardUpdate = true;
		}
		return isCardUpdate;
	}


	@Override
	public int findIdByUserIdAndPanNo(int userId, String panNo) {
		int cardId = 0;
		Query query = entityManager.createQuery("SELECT id from CardManagementInfo cardmanagementinfo WHERE cardmanagementinfo.userId= :userId AND cardmanagementinfo.panNo= :panNo");
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		query.setParameter(CommonOperationsConstants.PAN_NO_PARAM, panNo);
		
		cardId = (Integer) query.getSingleResult();
		if(cardId !=0)
			logger.info("[CardmanagementDaoImpl] panNo : "+panNo);
		
		return cardId;
	}


	@Override
	public String findCardStatusByUserIdAndPanNo(int userId, String panNo) {
		String cardStatus = "";
		Query query = entityManager.createQuery("SELECT cardStatus from CardManagementInfo cardmanagementinfo WHERE cardmanagementinfo.userId= :userId AND cardmanagementinfo.panNo= :panNo");
		query.setParameter(CommonOperationsConstants.USER_ID_PARAM, userId);
		query.setParameter(CommonOperationsConstants.PAN_NO_PARAM, panNo);
		
		cardStatus = (String) query.getSingleResult();
		if(cardStatus != null && !cardStatus.isEmpty())
			logger.info("[CardmanagementDaoImpl] panNo : "+panNo+" CardStatus : "+cardStatus);
		
		return cardStatus;
	}

}
