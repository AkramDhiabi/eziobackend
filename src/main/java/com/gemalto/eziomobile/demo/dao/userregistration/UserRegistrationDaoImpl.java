package com.gemalto.eziomobile.demo.dao.userregistration;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.UserRegistrationInfo;

public class UserRegistrationDaoImpl implements UserRegistrationOperation{
	
	private static final LoggerUtil logger = new LoggerUtil(UserRegistrationDaoImpl.class.getClass());
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public boolean updateStatusByUsernameAndActivationKey(String username, String activationKey) {
		boolean isStatusUpdated = false;
		Query query = entityManager.createQuery("UPDATE UserRegistrationInfo userRegistrationInfo SET userRegistrationInfo.status= 1"
				+ " WHERE userRegistrationInfo.username= :username AND userRegistrationInfo.activationkey= :activationKey");
		
		query.setParameter("username", username);
		query.setParameter("activationKey", activationKey);
		
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			isStatusUpdated = true;
			logger.info("UserRegistration info has been updated with status code : 1 !");
		}
		return isStatusUpdated;
	}

	
	
	@Override
	public boolean isActivationKeyValid(String username, String activationKey) {
		boolean isValid = false;
		
		String queryString = "SELECT * FROM userregistrationmaster "
				+ "WHERE username='"+username+"' and activationkey='"+activationKey+"' "
						+ "AND status='0' and lastupdate >= NOW() - INTERVAL 3 DAY";
		
		Query query = entityManager.createNativeQuery(queryString);
		
		List<UserRegistrationInfo> userRegistrationInfo =  query.getResultList();
		logger.info("updateCount : "+userRegistrationInfo.size());
		logger.info("updateCount : "+userRegistrationInfo.toString());
		
		if (userRegistrationInfo.size() >0) {
			isValid = true;
			logger.info("Valid Key!");
		}
		return isValid;
	}

}
