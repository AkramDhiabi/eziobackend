package com.gemalto.eziomobile.demo.dao.riskpreferencemaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;

public class RiskpreferenceMasterDaoImpl implements RiskpreferenceMasterOperations{

	private static final LoggerUtil logger = new LoggerUtil(RiskpreferenceMasterDaoImpl.class.getClass());
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	@Transactional
	public int updateRiskPreferenceBySecMode(int uId, int securityMode) {
		String userId = String.valueOf(uId);
		logger.info("userId :" + userId);
		Query query = entityManager.createQuery("UPDATE RiskPreferenceInfo riskPreferenceInfo SET riskPreferenceInfo.securityMode= :securityMode WHERE riskPreferenceInfo.userId= :userId");
		query.setParameter("securityMode", securityMode);
		query.setParameter("userId", userId);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info("RiskPreference has been updated with securityMode!");
		}
		return updateCount;
	}
}
