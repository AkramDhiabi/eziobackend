package com.gemalto.eziomobile.demo.service.userregistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.userregistration.UserRegistrationDao;
import com.gemalto.eziomobile.demo.dto.UserRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.UserRegistrationInfo;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;
import com.gemalto.eziomobile.demo.util.UserRegistrationUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

	private static final LoggerUtil logger = new LoggerUtil(UserRegistrationServiceImpl.class);

	@Autowired
	private UserRegistrationDao userRegistrationDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/*
	 * Method will save user's information in DB and will return persisted
	 * userRegistration Object which will be converted into DTO and will be
	 * returned.
	 */
	@Override
	public UserRegistrationDTO createUserAccount(UserRegistrationDTO userRegistrationDTO) throws ServiceException {

		UserRegistrationInfo userRegistrationInfo = new UserRegistrationInfo();
		UserRegistrationDTO userRegDTO = null;
		try {
			
			String activationKey = UserRegistrationUtil.generateKey();
			
			userRegistrationInfo.setEmailaddress(userRegistrationDTO.getEmailAddress());
			userRegistrationInfo.setFirstname(userRegistrationDTO.getFirstName());
			userRegistrationInfo.setLastname(userRegistrationDTO.getLastName());
			userRegistrationInfo.setActivationkey(activationKey);
			userRegistrationInfo.setCompanyname(userRegistrationDTO.getCompanyName());
			userRegistrationInfo.setCountry(userRegistrationDTO.getCountry());
			userRegistrationInfo.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
			userRegistrationInfo.setUsername(userRegistrationDTO.getUsername());
			userRegistrationInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_0);
			userRegistrationInfo.setLastupdate(new Date());

			UserRegistrationInfo savedUserRegInfo = userRegistrationDao.save(userRegistrationInfo);
			logger.info("savedUserRegInfo : " + savedUserRegInfo.toString());

			if (savedUserRegInfo.getId() != 0) {
				userRegDTO = new UserRegistrationDTO();
				
				userRegDTO.setUsername(savedUserRegInfo.getUsername());
				userRegDTO.setPassword(savedUserRegInfo.getPassword());
				userRegDTO.setFirstName(savedUserRegInfo.getFirstname());
				userRegDTO.setLastName(savedUserRegInfo.getLastname());
				userRegDTO.setEmailAddress(savedUserRegInfo.getEmailaddress());
				userRegDTO.setActivationKey(savedUserRegInfo.getActivationkey());
				userRegDTO.setCompanyName(savedUserRegInfo.getCompanyname());
				userRegDTO.setStatus(savedUserRegInfo.getStatus());
			}

		} catch (Exception e) {
			logger.error("Exception : Unable to save new user information!", e);
			throw new ServiceException(e);
		}
		return userRegDTO;
	}

	/* 
	 * 
	 */
	@Override
	public boolean updateStatusByUsernameAndActivationKey(String username, String activationKey)
			throws ServiceException {
		boolean isUpdated;
		try {
			isUpdated = userRegistrationDao.updateStatusByUsernameAndActivationKey(username, activationKey);
			if (isUpdated)
				isUpdated = true;

		} catch (Exception e) {
			logger.error("Exception : Unable to update UserRegistrationInfo status!", e);
			throw new ServiceException(e);
		}
		return isUpdated;
	}

	/* 
	 * 
	 */
	
	@Override
	public UserRegistrationDTO findUserRegistrationInfoByUsernameAndActivationKey(String username, String activationKey)
			throws ServiceException {
		UserRegistrationInfo userRegistrationInfo;
		UserRegistrationDTO userRegistrationDTO = null;
		try {
			userRegistrationInfo = userRegistrationDao.findUserRegistrationInfoByUsernameAndActivationkey(username,
					activationKey);
			if (userRegistrationInfo != null) {
				userRegistrationDTO = new UserRegistrationDTO();

				userRegistrationDTO.setUsername(userRegistrationInfo.getUsername());
				userRegistrationDTO.setPassword(userRegistrationInfo.getPassword());
				userRegistrationDTO.setFirstName(userRegistrationInfo.getFirstname());
				userRegistrationDTO.setLastName(userRegistrationInfo.getLastname());
				userRegistrationDTO.setEmailAddress(userRegistrationInfo.getEmailaddress());
				userRegistrationDTO.setActivationKey(userRegistrationInfo.getActivationkey());
				userRegistrationDTO.setCompanyName(userRegistrationInfo.getCompanyname());
				userRegistrationDTO.setStatus(userRegistrationInfo.getStatus());
				userRegistrationDTO.setLastUpdate(
						ConvertDateToStringDate.convertDateToString(userRegistrationInfo.getLastupdate()));
			}
		} catch (Exception e) {
			logger.error("Exception : couldn't find user information for username : " + username
					+ " AND activationKey : " + activationKey, e);
			throw new ServiceException(e);
		}
		return userRegistrationDTO;
	}

	/* 
	 * 
	 */
	@Override
	public UserRegistrationDTO findUserRegistrationInfoByUsername(String username) throws ServiceException {
		UserRegistrationInfo userRegistrationInfo;
		UserRegistrationDTO userRegistrationDTO = null;
		try {
			userRegistrationInfo = userRegistrationDao.findUserRegistrationInfoByUsername(username);
			if (userRegistrationInfo != null) {
				userRegistrationDTO = new UserRegistrationDTO();

				userRegistrationDTO.setUsername(userRegistrationInfo.getUsername());
				userRegistrationDTO.setPassword(userRegistrationInfo.getPassword());
				userRegistrationDTO.setFirstName(userRegistrationInfo.getFirstname());
				userRegistrationDTO.setLastName(userRegistrationInfo.getLastname());
				userRegistrationDTO.setEmailAddress(userRegistrationInfo.getEmailaddress());
				userRegistrationDTO.setActivationKey(userRegistrationInfo.getActivationkey());
				userRegistrationDTO.setCompanyName(userRegistrationInfo.getCompanyname());
				userRegistrationDTO.setStatus(userRegistrationInfo.getStatus());
				userRegistrationDTO.setLastUpdate(
						ConvertDateToStringDate.convertDateToString(userRegistrationInfo.getLastupdate()));
			}
		} catch (Exception e) {
			logger.error("Exception : couldn't find user information for username : " + username, e);
			throw new ServiceException(e);
		}
		return userRegistrationDTO;
	}

	/* 
	 * 
	 */
	@Override
	public int countByUsername(String username) throws ServiceException {
		int count;
		try {
			count = userRegistrationDao.countByUsername(username);
		} catch (Exception e) {
			logger.error("Exception : Unable to find user information count for username : " + username, e);
			throw new ServiceException(e);
		}
		return count;
	}

	
	
	@Override
	public boolean isActivationKeyValid(String username, String activationKey) throws ServiceException {
		boolean isValid;
		try {
			isValid = userRegistrationDao.isActivationKeyValid(username, activationKey);
		} catch (Exception e) {
			logger.error("Exception : Unable to find user information for username : " + username +" activationKey : "+activationKey, e);
			throw new ServiceException(e);
		}
		return isValid;
	}

	@Override
	@Transactional
	public boolean removeAfterVerification(UserRegistrationDTO userRegistrationDTO) throws ServiceException {
		boolean isDeleted = false;
		try
		{
			long a = userRegistrationDao.deleteByUsername(userRegistrationDTO.getUsername());
			isDeleted = a == 1;
		}
		catch (Exception e)
		{
			logger.error("Exception: Unable to delete user in registration table");
			throw new ServiceException(e);
		}
		return isDeleted;
	}

}
