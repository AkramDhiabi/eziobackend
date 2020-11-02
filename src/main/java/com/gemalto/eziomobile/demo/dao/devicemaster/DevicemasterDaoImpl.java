package com.gemalto.eziomobile.demo.dao.devicemaster;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

public class DevicemasterDaoImpl implements DevicemasterOperations {

	private static final LoggerUtil logger = new LoggerUtil(DevicemasterDaoImpl.class.getClass());


	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public int setDeviceOTPStatusZeroByUserId(int uId) {
		
		Query query = entityManager.createQuery("UPDATE DeviceMasterInfo deviceinfo SET deviceinfo.otpStatus = 0" + "WHERE deviceinfo.userId= :uId AND deviceinfo.status = 1");
		query.setParameter(U_ID_PARAM, uId);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info("OTP-Status has been updated!");
		}
		return updateCount;
	}
	
	
	@Override
	@Transactional
	public int updateDeviceOTPStatusByUserIdAndStatus(int uId, int otpStatus, int status) {
		
		Query query = entityManager.createQuery("UPDATE DeviceMasterInfo deviceinfo SET deviceinfo.otpStatus = :otpStatus WHERE deviceinfo.userId= :uId AND deviceinfo.status = :status");
		query.setParameter(U_ID_PARAM, uId);
		query.setParameter(OTP_STATUS_PARAM, String.valueOf(otpStatus));
		query.setParameter(STATUS_PARAM, status);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			logger.info("OTP-Status has been updated with : "+otpStatus);
		}
		return updateCount;
	}

	
	@Override
	@Transactional
	public int setDeviceStatusZeroByUserId(int uId) {
		Query query = entityManager.createQuery("UPDATE DeviceMasterInfo deviceinfo SET deviceinfo.status= 0 WHERE deviceinfo.userId= :uId AND deviceinfo.status = 1");
		query.setParameter(U_ID_PARAM, uId);
		
		int updateCount = query.executeUpdate();
		if(updateCount>0)
			logger.info("Device status has been updated!");
		
		return updateCount;
	}


	@Override
	public String findDevicePinByUserIdAndStatusAndRegCode(int uId, int status, String regCode) {
		Query query = entityManager
				.createQuery("SELECT pin from DeviceMasterInfo deviceinfo WHERE deviceinfo.userId= :userId AND deviceinfo.status= :status AND deviceinfo.regCode= :regCode");
		query.setParameter("userId", uId);
		query.setParameter(STATUS_PARAM, status);
		query.setParameter("regCode", regCode);

		String pin = (String) query.getSingleResult();
		logger.info("[DevicemasterDaoImpl] pin : " + pin);
		if (pin != null && !pin.equals("")) {
			return pin;
		}
		return null;
	}


	/* 
	 * 
	 */
	@Override
	@Transactional
	public int updateDeviceInfoStatusByUserIdAndRegCodeAndPinCode(int userId, int status, String regCode, String pin) {
		Query query = entityManager.createQuery("UPDATE DeviceMasterInfo deviceinfo SET deviceinfo.status= :status "
				+ "WHERE deviceinfo.userId= :userId AND deviceinfo.regCode = :regCode AND deviceinfo.pin = :pin");
		
		query.setParameter(STATUS_PARAM, status);
		query.setParameter("userId", userId);
		query.setParameter("regCode", regCode);
		query.setParameter("pin", pin);
		
		int updateCount = query.executeUpdate();
		if(updateCount>0)
			logger.info("Device status with status value : "+status+" for UserId : "+userId+""
					+ "AND RegCode : "+regCode+" AND PIN : "+pin+" has been updated!");
		
		return updateCount;
	}

}
