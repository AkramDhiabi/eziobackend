package com.gemalto.eziomobile.demo.service.usermaster;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.UserMasterDTO;
import com.gemalto.eziomobile.demo.dto.UserRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;

@Service
public interface UsermasterService {

	UserMasterInfo findUserInfoByUid(int uId) throws ServiceException;
	
	UserMasterInfo findUserInfoByUserId(String username) throws ServiceException;
	
	UserMasterInfo saveUserInfo(UserMasterInfo userMasterInfo) throws ServiceException;
	
	boolean isValidUser(String username, String password) throws ServiceException;
	
	int getUserGroupIdByUid(int uId) throws ServiceException;
	
	List<UserMasterInfo> getUsersListByGroupId(int groupId) throws ServiceException;

	int findUidByUserId(String userId) throws ServiceException;
	
	String findUserIdByUid(int uId) throws ServiceException;

	int updateUserRoleByUid (int uId, String userRole) throws ServiceException;
	
	String findUserRoleByUserId(int uId) throws ServiceException;
	
	int countByStatusAndUserId(int status, int userId) throws ServiceException;
	
	int countByUserId(String userId) throws ServiceException;
	
	boolean saveUserInfoForNewUser(UserRegistrationDTO userRegistrationDTO) throws ServiceException;

	String findEmailAddressByUId(int uId) throws ServiceException;

	UserMasterDTO findUserInfoByUId(int uId) throws ServiceException;

	List<UserMasterDTO> findUserInfoByEmailAddress(String emailAddress) throws ServiceException;
	
	boolean existsByEmailAddress(String emailAddress) throws ServiceException;
	
	boolean updatePasswordByUsername(int uId, String newPassword) throws ServiceException;

	boolean updatePasswordByEmailAndRecoverToken(String emailAddress, String recoverToken, String newPassword) throws ServiceException;

	boolean updateRecoverTokenByEmailAndRecoverToken(String emailAddress, String recoverToken) throws ServiceException;

	boolean validatePassword(int uId, String currPassword) throws ServiceException;
	
	boolean updateEmailByUsername(int uId, String emailAddr) throws ServiceException;

	boolean updateRecoverTokenByUsername(String recoverToken, int uId) throws ServiceException;

    boolean isRecoverTokenValid(String emailAddress, String recoverToken) throws ServiceException;
}
