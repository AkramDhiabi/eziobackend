package com.gemalto.eziomobile.demo.service.atm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.atm.ATMAccessCodeDao;
import com.gemalto.eziomobile.demo.dao.atm.AtmQRCodeDao;
import com.gemalto.eziomobile.demo.dto.AtmAccessCodeDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AtmAccessCodeInfo;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;

import java.util.Date;

@Service
public class AtmAccessCodeServiceImpl implements AtmAccessCodeService{
	
	@Autowired
	ATMAccessCodeDao atmAccessCodeDao;
	
	@Autowired
	AtmQRCodeDao atmQRCodeDao;
	
	@Autowired
	UsermasterService usermasterService;
	
	private static final LoggerUtil logger = new LoggerUtil(AtmAccessCodeServiceImpl.class.getClass());
	
	@Override
	public String findAccesscodeByUserId(int userId) throws ServiceException {
		String accessCode = "";
		try {
		logger.info("AtmCashCodeServiceImpl Service layer....");
		accessCode = atmQRCodeDao.findAccesscodeByUserId(userId);
		}catch (Exception e) {
			logger.info("Unable to find atm cash code details : "+ e);
			throw new ServiceException();
		}
		return accessCode;
	}
	

	
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteATMAccessCodeDetailsByUserId(int userId) throws ServiceException {
		try {
			
			logger.info("userId : "+userId);
			atmAccessCodeDao.deleteAtmAccesscodeInfoByUserId(userId);
			logger.info("atm access code details deleted succussfully from db");
		} catch (Exception e) {
			logger.info("Unable to delete atm cash code details "+ e);
			throw new ServiceException();
		}

	}
	
	@Override
	public AtmAccessCodeDTO findAtmAccessCodeInfoByUserIdAndAccessCode(int userId, String accessCode) throws ServiceException {
		AtmAccessCodeDTO atmAccessCodeDTO = null;
		
		try {
			AtmAccessCodeInfo atmAccessCodeInfo = atmAccessCodeDao.findAtmAccessCodeInfoByUserIdAndAccesscode(userId, accessCode);
			if(atmAccessCodeInfo != null){
				
				atmAccessCodeDTO = new AtmAccessCodeDTO();
				
				atmAccessCodeDTO.setuId(atmAccessCodeInfo.getUserId());
				atmAccessCodeDTO.setAccesscode(atmAccessCodeInfo.getAccesscode());
				atmAccessCodeDTO.setFromAccountNo(atmAccessCodeInfo.getFromAccountNo());
				atmAccessCodeDTO.setAmount(atmAccessCodeInfo.getAmount());
			}
		} catch (Exception e) {
			logger.info("Exception : Unable to find AtmAccessCodeDTO data for accessCode : "+ accessCode ,e);
			throw new ServiceException(e);
		}
		return atmAccessCodeDTO;
	}

	
	@Override
	public int countByUserId(int userId) throws ServiceException {
		int count = 0;
		try {
			count = atmAccessCodeDao.countByUserId(userId);
		} catch (Exception e) {
			logger.error("Exception : Unable to find any data for userId : "+userId);
			throw new ServiceException();
		}
		return count;
	}

	
	@Override
	public void saveATMCashCodeInfo(AtmAccessCodeDTO atmAccessCodeDTO) throws ServiceException {
		try {
			AtmAccessCodeInfo atmAccessCodeInfo = new AtmAccessCodeInfo();
			
			atmAccessCodeInfo.setUserId(atmAccessCodeDTO.getuId());
			atmAccessCodeInfo.setAccesscode(atmAccessCodeDTO.getAccesscode());
			atmAccessCodeInfo.setAmount(atmAccessCodeDTO.getAmount());
			atmAccessCodeInfo.setFromAccountNo(atmAccessCodeDTO.getFromAccountNo());
			atmAccessCodeInfo.setStatus(String.valueOf(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1));
			atmAccessCodeInfo.setCodeGenerationTime(new Date());
			
			atmAccessCodeDao.save(atmAccessCodeInfo);
			logger.info("Success : accessCodeInfo has been saved!");
			
		} catch (Exception e) {
			logger.error("Exception : Unable to save ATM accessCodeInfo!");
			throw new ServiceException();
		}
		
	}
	
}
