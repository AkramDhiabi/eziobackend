package com.gemalto.eziomobile.demo.service.userregistration;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.UserRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface UserRegistrationService {
	
	int countByUsername(String username) throws ServiceException;
	
	UserRegistrationDTO createUserAccount(UserRegistrationDTO userRegistrationDTO) throws ServiceException;

	boolean updateStatusByUsernameAndActivationKey(String username, String activationKey) throws ServiceException;
	
	UserRegistrationDTO findUserRegistrationInfoByUsernameAndActivationKey(String username, String activationKey) throws ServiceException;
	
	UserRegistrationDTO findUserRegistrationInfoByUsername(String username) throws ServiceException;
	
	boolean isActivationKeyValid(String username, String activationKey) throws ServiceException;

	boolean removeAfterVerification(UserRegistrationDTO userRegistrationDTO) throws ServiceException;
}
