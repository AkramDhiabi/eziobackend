package com.gemalto.eziomobile.demo.service.atm;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.AtmAccessCodeDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface AtmAccessCodeService {
	
	public String findAccesscodeByUserId(int userId) throws ServiceException;
	
	public AtmAccessCodeDTO findAtmAccessCodeInfoByUserIdAndAccessCode(int userId, String accessCode) throws ServiceException;
	
	public void deleteATMAccessCodeDetailsByUserId(int userId) throws ServiceException;
	
	int countByUserId(int userId) throws ServiceException;
	
	void saveATMCashCodeInfo(AtmAccessCodeDTO atmAccessCodeDTO) throws ServiceException;
	
}
