package com.gemalto.eziomobile.demo.dao.usermaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;

import java.util.List;

public class UsermasterDaoImpl implements UsermasterOperations{
	
	private static final LoggerUtil logger = new LoggerUtil(UsermasterDaoImpl.class.getClass());

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UsermasterDao usermasterDao;
	
	@Override
	public int findUidByUserId(String userId) {
		Query query = entityManager.createQuery("SELECT uId from UserMasterInfo userinfo WHERE userinfo.userId= :userId");
		query.setParameter("userId", userId);
		
		Integer uId = (Integer) query.getSingleResult();
		logger.info("[UserDaoImpl] uId : "+uId);
		if (uId > 0)
			return uId;
		return 0;
	}


	@Override
	public String findUserIdByUid(int uId) {
		Query query = entityManager.createQuery("SELECT userId from UserMasterInfo userinfo WHERE userinfo.uId= :uId");
		query.setParameter("uId", uId);
		
		String userId = (String) query.getSingleResult();
		logger.info("[UserDaoImpl] userId : "+userId);
		if (userId != null && !userId.equals(""))
			return userId;
		return null;
	}


	@Override
	@Transactional
	public int updateUserRoleByUserId(int uId, String userRole) {
		int update = 0;
		Query query = entityManager.createQuery("UPDATE UserMasterInfo userinfo SET userinfo.userRole= :userRole WHERE userinfo.uId= :uId");
		query.setParameter("uId", uId);
		query.setParameter("userRole", userRole);
		update = query.executeUpdate();
		
		UserMasterInfo userMasterInfo = null;
		userMasterInfo = usermasterDao.findUserRoleByUId(uId);
		
		entityManager.refresh(userMasterInfo);
		
		if (update != 0){
			logger.info("[UserDaoImpl] userMasterInfo is updated with userRole "+userRole);
		}else{
			logger.warn("UserMaster role hasn't been updated!");
		}
		return update;
	}

	@Override
	@Transactional
	public boolean updatePasswordByUsername(int uId, String newPassword) {
		boolean isStatusUpdated = false;
		Query query = entityManager.createQuery("UPDATE UserMasterInfo userinfo SET userinfo.password= :newPassword"
				+ " WHERE userinfo.uId= :uId");
		
		query.setParameter("uId", uId);
		query.setParameter("newPassword", newPassword);
		
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			isStatusUpdated = true;
			logger.info("UserMaster info has been updated with new password successfully !");
		}else{
			logger.warn("UserMaster password hasn't been updated!");
		}
		return isStatusUpdated;
	}

	@Override
	@Transactional
	public boolean updatePasswordByEmailAndRecoverToken(String emailAddress, String recoverToken, String newPassword) {
		boolean isStatusUpdated = false;
		Query query = entityManager.createQuery("UPDATE UserMasterInfo userinfo SET userinfo.password= :newPassword"
				+ " WHERE email_address=:email_address AND recover_token=:recover_token");

		query.setParameter("email_address", emailAddress);
		query.setParameter("recover_token", recoverToken);
		query.setParameter("newPassword", newPassword);

		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			isStatusUpdated = true;
			logger.info("UserMaster info has been updated with new password successfully !");
		}else{
			logger.warn("UserMaster password hasn't been updated!");
		}
		return isStatusUpdated;
	}

	@Override
	@Transactional
	public boolean updateRecoverTokenByEmailAndRecoverToken(String emailAddress, String recoverToken) {
		boolean isStatusUpdated = false;
		Query query = entityManager.createQuery("UPDATE UserMasterInfo userinfo SET recover_token=NULL"
				+ " WHERE email_address=:email_address AND recover_token=:recover_token");

		query.setParameter("email_address", emailAddress);
		query.setParameter("recover_token", recoverToken);

		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			isStatusUpdated = true;
			logger.info("UserMaster info has been updated with new password successfully !");
		}else{
			logger.warn("UserMaster recovery token hasn't been updated!");
		}
		return isStatusUpdated;
	}



	@Override
	@Transactional
	public boolean updateEmailByUsername(int uId, String emailAddr) {
		boolean isStatusUpdated = false;
		Query query = entityManager.createQuery("UPDATE UserMasterInfo userinfo SET userinfo.emailAddress= :emailAddr"
				+ " WHERE userinfo.uId= :uId");
		
		query.setParameter("uId", uId);
		query.setParameter("emailAddr", emailAddr);
		
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			isStatusUpdated = true;
			logger.info("UserMaster info has been updated with new password successfully !");
		}else{
			logger.warn("UserMaster email hasn't been updated!");
		}
		return isStatusUpdated;
	}


	@Override
	public String findPasswordByUId(int uId) {
		Query query = entityManager.createQuery("SELECT password from UserMasterInfo userinfo WHERE userinfo.uId= :uId");
		query.setParameter("uId", uId);

		String password = (String) query.getSingleResult();
		logger.info("[UserDaoImpl] password : "+password);
		if (password != null && !password.equals("")) {
			return password;
		}
		return null;
	}

	@Override
	@Transactional
	public boolean updateRecoverTokenByUsername(String recoverToken, int uId)
	{
		boolean isStatusUpdated = false;
		Query query = entityManager.createQuery("UPDATE UserMasterInfo userinfo SET userinfo.recoverToken= :recoverToken,"
				+ " userinfo.tokenExpirationDate=CURRENT_DATE"
				+ " WHERE userinfo.uId= :uId");

		query.setParameter("uId", uId);
		query.setParameter("recoverToken", recoverToken);

		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			isStatusUpdated = true;
			logger.info("UserMaster info has been updated with new password successfully !");
		}else{
			logger.warn("UserMaster recovery token hasn't been updated!");
		}
		return isStatusUpdated;
	}

    @Override
    public boolean isRecoverTokenValid(String emailAddress, String recoverToken) {
		boolean isValid = false;

		String queryString = "SELECT * from usermaster"
				+ " WHERE email_address=:email_address and recover_token=:recover_token"
				+ " and token_expiration_date >= NOW() - INTERVAL 3 DAY";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("email_address", emailAddress);
		query.setParameter("recover_token", recoverToken);

		List userMasterInfos =  query.getResultList();
		logger.info("updateCount : "+userMasterInfos.size());
		logger.info("updateCount : "+userMasterInfos.toString());

		if (userMasterInfos.size() >0) {
			isValid = true;
			logger.info("Valid Key!");
		}else{
			logger.warn("Invalid key!");
		}
		return isValid;
    }
}
