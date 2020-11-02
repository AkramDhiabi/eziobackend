package com.gemalto.eziomobile.demo.service.userpreferencemaster;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.UserpreferenceMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;


@Service
public interface UserpreferenceMasterService {

	String findSecLoginByStatusAndUserId(int status, int userId) throws ServiceException;
	
	String findSecTxOtherByStatusAndUserId(int status, int userId) throws ServiceException;
	
	String findSecTxOwnAccByStatusAndUserId(int status, int userId) throws ServiceException;
	
	String findSecAddPayeeByStatusAndUserId(int status, int userId) throws ServiceException;
	
	String findSecEcommerce3dsByStatusAndUserId(int status, int userId) throws ServiceException;
	
	String findSecP2pNotificationByStatusAndUserId(int status, int userId) throws ServiceException;
	
	String findSecWebNotificationByStatusAndUserId(int status, int userId) throws ServiceException; 
	
	String findSecMobileBankingByStatusAndUserId(int status, int userId) throws ServiceException;
	
	UserpreferenceMasterDTO findUserPreferenceInfoByUserIdAndStatus(int userId, int status) throws ServiceException;
	
	boolean updateUserPreferenceInfo(UserpreferenceMasterDTO userpreferenceMasterDTO) throws ServiceException;
}
