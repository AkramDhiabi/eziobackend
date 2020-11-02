package com.gemalto.eziomobile.demo.dao.devicemaster;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.DeviceMasterInfo;

@Repository
public interface DevicemasterDao extends CrudRepository<DeviceMasterInfo, Integer>, DevicemasterOperations{
	
	List<DeviceMasterInfo> getDevicesByUserId(int uId);
	
	List<DeviceMasterInfo> getDeviceListByUserIdAndStatus(int uId, int status);
	
	DeviceMasterInfo findDeviceByUserIdAndRegCode(int userId, String regCode);
	
	void deleteDeviceInfoByUserId(int uId);
	
	void deleteDeviceInfoByUserIdAndRegCode(int userId, String regCode);

	int countByStatusAndUserId(int status, int userId);
}
