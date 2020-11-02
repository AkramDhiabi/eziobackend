package com.gemalto.eziomobile.demo.service.singdatamaster;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dao.signdatamaster.SigndatamasterDao;
import com.gemalto.eziomobile.demo.dto.SigndataMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.SignDataInfo;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;

@Service
public class SigndatamasterServiceImpl implements SigndatamasterService{
	
	private static final LoggerUtil logger = new LoggerUtil(SigndatamasterServiceImpl.class.getClass());
	
	@Autowired
	private SigndatamasterDao signDataDao;

	/* 
	 * 
	 */
	@Override
	public void saveSigndataInfo(SignDataInfo signDataInfo) throws ServiceException {
		
		try {
			signDataDao.save(signDataInfo);
		}  catch (Exception e) {
			logger.info("Unable to save sign data!", e);
			throw new ServiceException(e);
		}
	}

	@Override
	public SignDataInfo findSigndataByUserId(int userId) throws ServiceException {
		SignDataInfo signDataInfo = null;
		try {
			signDataInfo = signDataDao.findSigndataByUserId(userId);
		}  catch (Exception e) {
			logger.info("Unable to find signdata for UserID : "+userId, e);
			throw new ServiceException(e);
		}
		return signDataInfo;
	}

	@Override
	@Transactional
	public void deleteSigndataByUserId(int userId) throws ServiceException {
		try {
			signDataDao.deleteSigndataByUserId(userId);
		}  catch (Exception e) {
			logger.info("Exception : unable to delete signdata info with userID : "+userId, e);
			throw new ServiceException(e);
		}
	}
}
