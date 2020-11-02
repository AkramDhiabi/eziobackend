package com.gemalto.eziomobile.demo.service.qrtokenmaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.dao.qrtokenmaster.QRTokenmasterDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.QRTokenMasterInfo;

@Service
public class QRTokenmasterServiceImpl implements QRTokenmasterService{
	
	private static final LoggerUtil logger = new LoggerUtil(QRTokenmasterServiceImpl.class.getClass());
	
	@Autowired
	QRTokenmasterDao qrtokenmasterDao;

	@Override
	public void saveQRTokenMasterInfo(QRTokenMasterInfo qrTokenMasterInfo) throws ServiceException {
		try {
			qrtokenmasterDao.save(qrTokenMasterInfo);
		} catch (Exception e) {
			logger.info("Unable save qrTokenMasterInfo information!"+ e);
			throw new ServiceException(e);
		}
		
	}

	@Override
	public String findtransactionHashByUserId(String userId) throws ServiceException {
		String transaction_hash = "";
		try {
		logger.info("QRTokenmasterServiceImpl Service layer....");
		transaction_hash = qrtokenmasterDao.findtransactionHashByUserId(userId);
		}catch (Exception e) {
			logger.info("Unable to find qrTokenMasterInfo details : "+ e);
			throw new ServiceException();
		}
		return transaction_hash;
	}

	@Override
	public String findtransactionHashByUserIdAndTranscationType(String userId) throws ServiceException {
		String transaction_hash = "";
		try {
		logger.info("QRTokenmasterServiceImpl Service layer....");
		transaction_hash = qrtokenmasterDao.findtransactionHashByUserIdAndTranscationType(userId);
		}catch (Exception e) {
			logger.info("Unable to find qrTokenMasterInfo details : "+ e);
			throw new ServiceException();
		}
		return transaction_hash;
	}


	@Override
	public int countByUserId(String userId) throws ServiceException {
		int count = 0;
		try {
			count = qrtokenmasterDao.countByUserId( userId);
		} catch (Exception e) {
			logger.info("Unable to find qrTokenMasterInfo counts with UserId: "+userId+e);
			throw new ServiceException(e);
		}
		return count;
	}


	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteQRTokenDetailsByUserId(String userId) {
		try {	
			logger.info("userId : "+userId);
			qrtokenmasterDao.deleteQRTokenMasterInfoByUserId(userId);
			logger.info("QRTokenMasterInfo details deleted succussfully from db");
		} catch (Exception e) {
			logger.info("Unable to delete atm cash code details "+ e);
			//throw new ServiceException(e);
		}
		
	}

}
