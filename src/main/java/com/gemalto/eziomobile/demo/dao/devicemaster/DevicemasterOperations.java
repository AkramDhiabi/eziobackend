package com.gemalto.eziomobile.demo.dao.devicemaster;

public interface DevicemasterOperations {
	
	int setDeviceOTPStatusZeroByUserId(int uId);
	
	int updateDeviceOTPStatusByUserIdAndStatus(int uId, int otpStatus, int status);
	
	int setDeviceStatusZeroByUserId(int uId);
	
	String findDevicePinByUserIdAndStatusAndRegCode(int uId, int status, String regCode);
	
	int updateDeviceInfoStatusByUserIdAndRegCodeAndPinCode(int userId, int status, String regCode, String pin);

}
