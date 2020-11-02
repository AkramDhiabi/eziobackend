package com.gemalto.eziomobile.demo.service.devicemaster;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.DeviceMasterInfo;

@Service
public interface DevicemasterService {
	
	void saveDeviceInfo(DeviceMasterInfo deviceMasterInfo) throws ServiceException;
	
	List<DeviceMasterInfo> getDevicesByUserId(int uId) throws ServiceException;
	
	int setDeviceOTPStatusZeroByUserId(int uId) throws ServiceException;
	
	int updateDeviceOTPStatusByUserIdAndStatus(int uId, int otpStatus, int status) throws ServiceException;
	
	int setDeviceStatusZeroByUserId(int uId) throws ServiceException;
	
	String findPinByUserIdAndStatusAndRegCode(int uId, int status, String regCode) throws ServiceException;
	
	List<DeviceMasterInfo> getDeviceListByUserIdAndStatus(int uId, int status) throws ServiceException;

	void deleteDeviceInfoByUserIdAndRegCode(int userId, String regCode) throws ServiceException;
	
	boolean isUserAccountReset(int userId) throws ServiceException;
	
	int countByStatusAndUserId(int status, int userId) throws ServiceException;
	
	int updateDeviceInfoStatusByUserIdAndRegCodeAndPinCode(int userId, int status, String regCode, String pin) throws ServiceException;
}
