package com.gemalto.eziomobile.demo.service.devicemaster;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.devicemaster.DevicemasterDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.DeviceMasterInfo;

@Service
public class DevicemasterServiceImpl implements DevicemasterService{
	
	private static final LoggerUtil logger = new LoggerUtil(DevicemasterServiceImpl.class.getClass());
	
	@Autowired
	private DevicemasterDao deviceMasterDao;

	
	@Override
	public void saveDeviceInfo(DeviceMasterInfo deviceMasterInfo) throws ServiceException {
		try{
			deviceMasterDao.save(deviceMasterInfo);
		} catch (Exception e) {
			logger.error("Exception : Couldn't save device info! " +e);
			throw new ServiceException(e);
		}
		
	}
	
	@Override
	public List<DeviceMasterInfo> getDevicesByUserId(int uId) throws ServiceException {
		List<DeviceMasterInfo> devices = new ArrayList<>();
		try{
			devices = deviceMasterDao.getDevicesByUserId(uId);
		} catch (Exception e) {
			logger.error("Exception : unable to fetch list of devices for UID : "+uId, e);
			throw new ServiceException(e);
		}
		return devices;
	}

	
	@Override
	public String findPinByUserIdAndStatusAndRegCode(int uId, int status, String regCode) throws ServiceException {
		String pin = "";
		try {
			pin = deviceMasterDao.findDevicePinByUserIdAndStatusAndRegCode(uId, status, regCode);
			logger.info("[DeviceMasterServiceIMPL] PIN : "+pin);
			//pin = deviceMasterInfo.getPin();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception : Unable to find user PIN with userId : "+uId+" and RegCode : "+regCode, e);
		}
		return pin;
	}

	@Override
	public List<DeviceMasterInfo> getDeviceListByUserIdAndStatus(int uId, int status) throws ServiceException {
		List<DeviceMasterInfo> deviceOTPStatusList = new ArrayList<>();
		try{
			deviceOTPStatusList = deviceMasterDao.getDeviceListByUserIdAndStatus(uId, status);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception : Unable to find list of Device OTP status!", e);
		}
		return deviceOTPStatusList;
	}
	
	
	@Override
	public int setDeviceOTPStatusZeroByUserId(int uId) throws ServiceException {
		int updateCount = 0;
		try{
			updateCount =  deviceMasterDao.setDeviceOTPStatusZeroByUserId(uId);
		} catch (Exception e) {
			logger.error("Exception : Couldn't update devicce OTP status for UID : "+uId, e);
			throw new ServiceException(e);
		}
		return updateCount;
	}
	
	
	@Override
	public int updateDeviceOTPStatusByUserIdAndStatus(int uId, int otpStatus, int status) throws ServiceException {
		int updateCount = 0;
		try{
			updateCount = deviceMasterDao.updateDeviceOTPStatusByUserIdAndStatus(uId, otpStatus, status);
		}
		catch (Exception e) {
			logger.error("Exception : Couldn't update devicce OTP status for UID : "+uId+ "with Status "+status, e);
			throw new ServiceException(e);
		}
		return updateCount;
	}


	@Override
	public int setDeviceStatusZeroByUserId(int uId) throws ServiceException {
		int updateCount = 0;
		 try {
			 updateCount = deviceMasterDao.setDeviceStatusZeroByUserId(uId);
		 }catch (Exception e) {
			logger.error("Exception : Couldn't update! " +e);
			throw new ServiceException(e);
		}
		return updateCount;
	}

	
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteDeviceInfoByUserIdAndRegCode(int userId, String regCode) throws ServiceException {
		try {
			deviceMasterDao.deleteDeviceInfoByUserIdAndRegCode(userId, regCode);
		} catch (Exception e) {
			logger.error("Exception : Couldn't delete! " +e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean isUserAccountReset(int userId) throws ServiceException {
		boolean isDBReset = false;
		try {
			int count = deviceMasterDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, userId);
			if(count == 0){
				isDBReset = true;
			}
		} catch (Exception e) {
			logger.error("Exception : not able to check user details! " +e);
			throw new ServiceException(e);
		}
		return isDBReset;
	}

	
	@Override
	public int countByStatusAndUserId(int status, int userId) throws ServiceException {
		int count = 0;
		try {
			 count = deviceMasterDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, userId);
		} catch (Exception e) {
			logger.error("Exception : not able to get the count! " +e);
			throw new ServiceException(e);
		}
		return count;
	}

	
	@Override
	public int updateDeviceInfoStatusByUserIdAndRegCodeAndPinCode(int userId, int status, String regCode, String pin)
			throws ServiceException {
		int updateCount = 0;
		try{
			updateCount = deviceMasterDao.updateDeviceInfoStatusByUserIdAndRegCodeAndPinCode(userId, status, regCode, pin);
		}
		catch (Exception e) {
			logger.error("Exception : Couldn't update devicceInfo status for UID : "+userId+ "with Status "+status, e);
			throw new ServiceException(e);
		}
		return updateCount;
	}

	
	
}
