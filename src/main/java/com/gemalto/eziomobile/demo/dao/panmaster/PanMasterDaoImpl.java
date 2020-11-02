package com.gemalto.eziomobile.demo.dao.panmaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gemalto.eziomobile.demo.dao.oobsmessagemaster.OOBSMessagemasterDaoImpl;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;

public class PanMasterDaoImpl implements PanMasterOperation{
	
	private static final LoggerUtil logger = new LoggerUtil(OOBSMessagemasterDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public String findAccountNoByUserIdAndPanNo(int userId, String panNo) {
		
		Query query = entityManager
				.createQuery("SELECT accountNo from PanMasterInfo paninfo WHERE paninfo.userId= :userId AND paninfo.panNo = :panNo");
		query.setParameter("userId", userId);
		query.setParameter("panNo", panNo);

		String accountNO = (String) query.getSingleResult();
		logger.info("[PanMasterDaoImpl] accountNO : " + accountNO);
		if (accountNO != null && !accountNO.equals("")) {
			return accountNO;
		}
		return null;
	}

}
