package com.gemalto.eziomobile.demo.dao.atm;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;

public class AtmAccessCodeDaoImpl implements AtmAccessCodeOperations{

	private static final LoggerUtil logger = new LoggerUtil(AtmAccessCodeDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;
	
	//private static final TEN_MINUTES = 10 * 60 * 1000;
	
	@Override
	public String findAccesscodeByUserId(int userId) {
		String status = "1";
		Date d = new Date();
		logger.info("print date d :"+ d);
		LocalDateTime today = LocalDateTime.now();
	    LocalDateTime tenMinAgo = today.minusMinutes(10);

	    Date codeGenerationTime = java.sql.Timestamp.valueOf(tenMinAgo);
	
		String accessCode = "";
		
		Query query = entityManager.createQuery("SELECT accesscode from AtmAccessCodeInfo atmAccessCodeInfo WHERE  atmAccessCodeInfo.status= :status and atmAccessCodeInfo.userId= :userId and atmAccessCodeInfo.codeGenerationTime >= :codeGenerationTime");
		query.setParameter("userId", userId);
		query.setParameter("status", status);
		query.setParameter("codeGenerationTime", codeGenerationTime);

		logger.info("print query : "+ query);
	
		accessCode = (String) query.getSingleResult();
		logger.info("print accessCode : "+ accessCode);
		if(accessCode != null && !accessCode.isEmpty()) {
			logger.info("[AtmAccessCodeDaoImpl] accessCode : " + accessCode);
		}
				
		return accessCode;
	}
}
