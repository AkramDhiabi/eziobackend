package com.gemalto.eziomobile.demo.service.atm;

import java.util.Date;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.atm.AtmQRCodeDao;
import com.gemalto.eziomobile.demo.dto.ATMQRCodeDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AtmQRCodeInfo;
import com.gemalto.eziomobile.demo.model.CallbackDataInfo;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.AsyncResponse;

@Service
public class AtmQRCodeServiceImpl implements AtmQRCodeService{

@Autowired
AtmQRCodeDao atmQRCodeDao;

@Autowired
UsermasterService usermasterService;

private static final LoggerUtil logger = new LoggerUtil(AtmQRCodeServiceImpl.class.getClass());


	@Override
	public void saveQRCodeDetails(ATMQRCodeDTO atmQRCodeDTO) throws ServiceException{
		AtmQRCodeInfo atmQRCodeInfo = new AtmQRCodeInfo();
		try {
			int uid = usermasterService.findUidByUserId(atmQRCodeDTO.getUserId());
			atmQRCodeInfo.setUserId(uid);
			atmQRCodeInfo.setAtmId(atmQRCodeDTO.getAtmId());
			atmQRCodeInfo.setChallenge(atmQRCodeDTO.getChallenge());
			atmQRCodeInfo.setAmount(Integer.valueOf(atmQRCodeDTO.getAmount()));
			atmQRCodeInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_0);
			atmQRCodeInfo.setGenerationDate(new Date());

			atmQRCodeDao.save(atmQRCodeInfo);
		} catch (Exception e) {
			logger.error("Unable save atm qr code information!"+ e);
			throw new ServiceException(e);
		}
		
	}

	
	/* 
	 * Method to get QR code data by userId, ATM-ID and Challenge  
	 * @Return : ATMQRCodeDTO object
	 */
	@Override
	public ATMQRCodeDTO findAtmQRCodeInfoByUserIdAndAtmIdAndChallenge(String userId, String atmId, String Challenge)
			throws ServiceException {
		ATMQRCodeDTO atmQrCodeDTO = new ATMQRCodeDTO();
		try{
			
			int uid = usermasterService.findUidByUserId(userId);
			AtmQRCodeInfo atmQRCodeInfo = atmQRCodeDao.findAtmQRCodeInfoByUserIdAndAtmIdAndChallenge(uid, atmId, Challenge);
			
			logger.info("[Service] atmQRCodeInfo : "+atmQRCodeInfo.toString());
			
			if(atmQRCodeInfo!=null){
				atmQrCodeDTO.setUserId(userId);
				atmQrCodeDTO.setAtmId(atmQRCodeInfo.getAtmId());
				atmQrCodeDTO.setChallenge(atmQRCodeInfo.getChallenge());
				atmQrCodeDTO.setAmount(String.valueOf(atmQRCodeInfo.getAmount()));
			}
			
		}catch(Exception e){
			logger.info("Unable to get atm qr code details");
			throw new ServiceException();
		}
		
		return atmQrCodeDTO;
	}

	
	
	
	public boolean updateAtmQRCodeStatusByUserIdAndAtmId(int status, String atmId, int uId) throws ServiceException{
		boolean flag = false;
		try{
			flag = atmQRCodeDao.updateAtmQRCodeStatusByUserIdAndAtmId(status,atmId,uId);
		}catch(Exception e){
			logger.info("Unable to update atm qr code details");
			throw new ServiceException();
		}
		return flag;
	}


	@Async
	@Override
	public Future<Integer> findAtmQrCodeStatusByUserIdAndAtmId(int userId, String atmId) throws ServiceException {
		int status = 0;
		AsyncResponse<Integer> response = new AsyncResponse<Integer>();
		try{
			
			AtmQRCodeInfo atmQRCodeInfo = atmQRCodeDao.findAtmQRCodeStatusByUserIdAndAtmId(userId,atmId);
			
			response.complete(atmQRCodeInfo.getStatus());
		logger.info("status in service layer : "+status);
		}catch(Exception e){
			logger.info("Unable to get atm qr code status");
			
			response.completeExceptionally(e);
		}
		return response;
	}


	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteAtmQRCodeDetailsByUserId(int userId) throws ServiceException {
	
		try{
			atmQRCodeDao.deleteAtmQRCodeInfoByUserId(userId);
			logger.info("qr code details deleted succussfully from db");
		}catch(Exception e){
			logger.error("Unable to delete qr code details");
			throw new ServiceException();
		}
		

	}


	@Override
	public int countByUserId(int userId) throws ServiceException {
		int count = 0;
		try{
			count = atmQRCodeDao.countByUserId(userId);
		}catch(Exception e){
			logger.info("Unable to update atm qr code details");
			throw new ServiceException();
		}
		return count;
	}

}
