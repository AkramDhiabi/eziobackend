package com.gemalto.eziomobile.demo.dao.atm;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import com.gemalto.eziomobile.demo.dao.accountmaster.AccountmasterDaoImpl;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AtmQRCodeInfo;

public class AtmQRCodeDaoImpl implements AtmQRCodeOperations{

	private static final LoggerUtil logger = new LoggerUtil(AccountmasterDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	
	@Override
	@Transactional
	public boolean updateAtmQRCodeStatusByUserIdAndAtmId(int status, String atmId, int uId) {
	
		boolean flag = false;
		Query query = entityManager
				.createQuery("UPDATE AtmQRCodeInfo amtqrcodeinfo SET amtqrcodeinfo.status= :status WHERE amtqrcodeinfo.atmId = :atmId AND amtqrcodeinfo.userId= :uId");
		query.setParameter("status", status);
		query.setParameter("atmId", atmId);
		query.setParameter("uId", uId);
		
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			flag = true;
			logger.info("Status has been updated!");
		}
		return flag;
	}
	
	
	@Override
	public String findAccesscodeByUserId(int userId) {
		String status = "1";
		LocalDateTime today = LocalDateTime.now();
	    LocalDateTime tenMinAgo = today.minusMinutes(10);

	    Date codeGenerationTime = java.sql.Timestamp.valueOf(tenMinAgo);
	
		String accessCode = "";
		
		Query query = entityManager.createQuery("SELECT accesscode from AtmAccessCodeInfo atmAccessCodeInfo WHERE  atmAccessCodeInfo.status= :status and atmAccessCodeInfo.userId= :userId and atmAccessCodeInfo.codeGenerationTime >= :codeGenerationTime");
		query.setParameter("userId", userId);
		query.setParameter("status", status);
		query.setParameter("codeGenerationTime", codeGenerationTime);
	
		accessCode = (String) query.getSingleResult();
		logger.info("print accessCode : "+ accessCode);
		if(accessCode != null && !accessCode.isEmpty()) {
			logger.info("[AtmAccessCodeDaoImpl] accessCode : " + accessCode);
		}
				
		return accessCode;
	}


}
