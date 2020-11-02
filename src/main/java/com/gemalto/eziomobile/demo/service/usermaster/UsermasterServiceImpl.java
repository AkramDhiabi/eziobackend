package com.gemalto.eziomobile.demo.service.usermaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.usermaster.UsermasterDao;
import com.gemalto.eziomobile.demo.dto.UserMasterDTO;
import com.gemalto.eziomobile.demo.dto.UserRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;

@Service
public class UsermasterServiceImpl implements UsermasterService{
	
	@Autowired
	private UsermasterDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final LoggerUtil logger = new LoggerUtil(UsermasterServiceImpl.class.getClass());
	
	
	@Override
	public boolean isValidUser(String username, String password) throws ServiceException {
		UserMasterInfo userMasterInfo;

		try {
			userMasterInfo = userDao.findUserInfoByUserId(username);
		} catch (Exception e) {
			logger.error("Invalid username or password!", e);
			throw new ServiceException(e);
		}

		return passwordEncoder.matches(password, userMasterInfo.getPassword());
	}
	
	@Override
	public UserMasterInfo findUserInfoByUid(int uId) throws ServiceException {
		Optional<UserMasterInfo> userInfo;
		try {
			userInfo = userDao.findById(uId);
		} catch (Exception e) {
			logger.error("Couldn't find user object with Uid : "+uId, e);
			throw new ServiceException(e);
		}
		return userInfo.get();
	}

	@Override
	public UserMasterInfo findUserInfoByUserId(String username) throws ServiceException {
		UserMasterInfo userInfo = new UserMasterInfo();
		try {
			userInfo = userDao.findUserInfoByUserId(username);
		} catch (Exception e) {
			logger.error("Couldn't find user object with Uid : "+username, e);
			throw new ServiceException(e);
		}
		return userInfo;
	}
	
	
	@Override
	public UserMasterInfo saveUserInfo(UserMasterInfo userMasterInfo) throws ServiceException {
		UserMasterInfo userInfo = new UserMasterInfo();
		try {
			userInfo = userDao.save(userMasterInfo);
		} catch (Exception e) {
			logger.error("Couldn't save user object: "+userMasterInfo.toString(), e);
			throw new ServiceException(e);
		}
		return userInfo;
	}

	
	@Override
	public int getUserGroupIdByUid(int uId) throws ServiceException {
		int groupId = 0;
		try {
			Optional<UserMasterInfo> userMasterInfo = userDao.findById(uId);
			if(userMasterInfo.isPresent())
				groupId = userMasterInfo.get().getGroupId();
			
			logger.info("groupId : "+groupId);
		} catch (Exception e) {
			logger.error("Couldn't find user group for Uid: "+uId, e);
			throw new ServiceException(e);
		}
		return groupId;
	}


	@Override
	public List<UserMasterInfo> getUsersListByGroupId(int groupId) throws ServiceException {
		List<UserMasterInfo> usersList = new ArrayList<UserMasterInfo>();
		try {
			usersList = userDao.getUsersListByGroupId(groupId);
		} catch (Exception e) {
			logger.error("Unable to get users list for groupID: "+groupId, e);
			throw new ServiceException(e);
		}
		return usersList;
	}


	@Override
	public int findUidByUserId(String userId) throws ServiceException {
		int uId = 0;
		try {
			uId = userDao.findUidByUserId(userId);
		} catch (Exception e) {
			logger.error("Invalid UserId, couldn't find data for : "+userId, e);
				throw new ServiceException(e);
		}
		return uId;
	}


	@Override
	public String findUserIdByUid(int uId) throws ServiceException {
		String userId = "";
		try {
			userId = userDao.findUserIdByUid(uId);
		} catch (Exception e) {
			logger.error("Couldn't find any UserId for UID : "+uId, e);
				throw new ServiceException(e);
		}
		return userId;
	}

	@Override
	public int updateUserRoleByUid(int uId,String userRole) throws ServiceException {
		int update = 0;
		try {
			update = userDao.updateUserRoleByUserId(uId, userRole);
		} catch (Exception e) {
			logger.error("Couldn't save user object: "+userRole, e);
			throw new ServiceException(e);
		}
		return update;
	}

	@Override
	public String findUserRoleByUserId(int uId) throws ServiceException {
		UserMasterInfo userMasterInfo;
		String userRole = null;
		try{
			userMasterInfo = userDao.findUserRoleByUId(uId);
			userRole = userMasterInfo.getUserRole();
		}catch(Exception e){
			logger.error("Couldn't find userRole: "+userRole, e);
			throw new ServiceException(e);
		}
		return userRole;
	}

	@Override
	public int countByStatusAndUserId(int status, int uId) throws ServiceException {
		int count = 0;
		try {
			// count = userDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, userId);
			count = userDao.countByStatusAndUId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
		} catch (Exception e) {
			logger.error("Exception : not able to get the count! " +e);
			throw new ServiceException(e);
		}
		return count;
	}

	
	
	@Override
	public boolean saveUserInfoForNewUser(UserRegistrationDTO userRegistrationDTO) throws ServiceException {
		boolean isSaved = false;
		UserMasterInfo userMasterInfo = new UserMasterInfo();
		try {
			userMasterInfo.setUserId(userRegistrationDTO.getUsername());
			userMasterInfo.setPassword(userRegistrationDTO.getPassword());
			userMasterInfo.setGroupId(1);
			userMasterInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
			userMasterInfo.setEmailAddress(userRegistrationDTO.getEmailAddress());
			
			UserMasterInfo newUserInfo = userDao.save(userMasterInfo);
			if(newUserInfo != null){
				isSaved = true;
			}
		} catch (Exception e) {
			logger.error("Exception : not able to get the count! " +e);
			throw new ServiceException(e);
		}
		return isSaved;
	}

	@Override
	public int countByUserId(String userId) throws ServiceException {
		int count = 0;
		try {
			count = userDao.countByUserId(userId);
		} catch (Exception e) {
			logger.error("Exception : Unable to find data count for : "+userId ,e);
			throw new ServiceException(e);
		}
		return count;
	}

	/*
	 *
	 */
	@Override
	public String findEmailAddressByUId(int uId) throws ServiceException {
		String emailAddress = "";
		try {
			emailAddress = userDao.findEmailAddressByUId(uId);
		} catch (Exception e) {
			logger.error("Exception : Unable to find user EmailAddress! UserId : "+uId ,e);
			throw new ServiceException(e);
		}
		return emailAddress;
	}

	/*
	 *
	 */
	@Override
	public UserMasterDTO findUserInfoByUId(int uId) throws ServiceException {
		UserMasterDTO userMasterDTO = null;
		UserMasterInfo userMasterInfo = null;
		try {
			userMasterInfo = userDao.findUserInfoByUId(uId);
			if(userMasterInfo != null){
				userMasterDTO = new UserMasterDTO();
				userMasterDTO.setEmailAddress(userMasterInfo.getEmailAddress());
				userMasterDTO.setUserId(userMasterInfo.getUserId());
				userMasterDTO.setPassword(userMasterInfo.getPassword());
			}
		} catch (Exception e) {
			logger.error("Exception : Unable to find user EmailAddress! UserId : "+uId ,e);
			throw new ServiceException(e);
		}
		return userMasterDTO;
	}


	@Override
	public List<UserMasterDTO> findUserInfoByEmailAddress(String emailAddress) throws ServiceException {
		List<UserMasterInfo> userInfoList = new ArrayList<>();
		List<UserMasterDTO> userInfoDTOList = new ArrayList<>();
		
		try {
			userInfoList = userDao.findUserInfoByEmailAddress(emailAddress);
			if(userInfoList.size() >0 ){
				userInfoList.forEach(item->{
					UserMasterDTO userMasterDTO = new UserMasterDTO();
					
					userMasterDTO.setEmailAddress(item.getEmailAddress());
					userMasterDTO.setuId(item.getuId());
					userMasterDTO.setUserId(item.getUserId());
					userMasterDTO.setPassword(item.getPassword());
					userMasterDTO.setPermission(item.getUserRole());

					userInfoDTOList.add(userMasterDTO);
				});
			}
		} catch (Exception e) {
			logger.error("Exception : Unable to find userInfo for  EmailAddress : "+emailAddress ,e);
			throw new ServiceException(e);
		}
		return userInfoDTOList;
	}

	@Override
	public boolean existsByEmailAddress(String emailAddress) throws ServiceException {
		boolean isExist = false;
		try {
			isExist = userDao.existsByEmailAddress(emailAddress);
		} catch (Exception e) {
			logger.error("Exception : Unable to find EmailAddress : "+emailAddress ,e);
			throw new ServiceException(e);
		}
		return isExist;
	}
	
	@Override
	public boolean updatePasswordByUsername(int uId, String newPassword) throws ServiceException {
		boolean isUpdated = false;
		try {
			isUpdated = userDao.updatePasswordByUsername(uId, passwordEncoder.encode(newPassword));
		} catch (Exception e) {
			logger.error("Exception : Unable to update UserMasterInfo Password!", e);
			throw new ServiceException(e);
		}
		return isUpdated;
	}

	@Override
	public boolean updatePasswordByEmailAndRecoverToken(String emailAddress, String recoverToken, String newPassword) throws ServiceException {
		boolean isUpdated = false;
		try {
			isUpdated = userDao.updatePasswordByEmailAndRecoverToken(emailAddress, recoverToken, passwordEncoder.encode(newPassword));
		} catch (Exception e) {
			logger.error("Exception : Unable to update UserMasterInfo Password!", e);
			throw new ServiceException(e);
		}
		return isUpdated;
	}

	@Override
	public boolean updateRecoverTokenByEmailAndRecoverToken(String emailAddress, String recoverToken) throws ServiceException {
		boolean isUpdated = false;
		try {
			isUpdated = userDao.updateRecoverTokenByEmailAndRecoverToken(emailAddress, recoverToken);
		} catch (Exception e) {
			logger.error("Exception : Unable to update UserMasterInfo Password!", e);
			throw new ServiceException(e);
		}
		return isUpdated;
	}

	@Override
	public boolean validatePassword(int uId, String currPassword) throws ServiceException {
		boolean isUpdated = false;
		String dbPassword;
		try {
			dbPassword = userDao.findPasswordByUId(uId);
			if(dbPassword != null && passwordEncoder.matches(currPassword, dbPassword)) {
				isUpdated = true;
			}
		} catch (Exception e) {
			logger.error("Exception : Not a valid Password!", e);
			throw new ServiceException(e);
		}
		return isUpdated;
	}
	
	@Override
	public boolean updateEmailByUsername(int uId, String emailAddr) throws ServiceException {
		boolean isUpdated = false;
		try {
			isUpdated = userDao.updateEmailByUsername(uId, emailAddr);
		} catch (Exception e) {
			logger.error("Exception : Unable to update UserMasterInfo emailAddress!", e);
			throw new ServiceException(e);
		}
		return isUpdated;
	}

    @Override
    public boolean updateRecoverTokenByUsername(String recoverToken, int uId) throws ServiceException {
		boolean isUpdated = false;
		try {
			isUpdated = userDao.updateRecoverTokenByUsername(recoverToken, uId);
		} catch (Exception e) {
			logger.error("Exception : Unable to update UserMasterInfo emailAddress!", e);
			throw new ServiceException(e);
		}
		return isUpdated;
    }

    @Override
    public boolean isRecoverTokenValid(String emailAddress, String recoverToken) throws ServiceException {
		boolean isValid = false;
		try {
			isValid = userDao.isRecoverTokenValid(emailAddress, recoverToken);
		} catch (Exception e) {
			logger.error("Exception : Unable to find user information for emailAddress : " + emailAddress +" token : "+recoverToken, e);
			throw new ServiceException(e);
		}
		return isValid;
    }
}
