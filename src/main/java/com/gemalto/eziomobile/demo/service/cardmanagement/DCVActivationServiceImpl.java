package com.gemalto.eziomobile.demo.service.cardmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.gemalto.eziomobile.demo.dao.cardmanagementmaster.DCVActivationDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;

import com.gemalto.eziomobile.demo.model.PanDCVListInfo;

@Service
public class DCVActivationServiceImpl implements DCVActivationService{
	
	private static final LoggerUtil logger = new LoggerUtil(DCVActivationServiceImpl.class.getClass());
	
	@Autowired
	private DCVActivationDao dCVActivationDao;

	/* 
	 * 
	 */
	

	@Override
	public PanDCVListInfo findPanDCVListInfoByPanNo(String panNo) throws ServiceException {
		
		PanDCVListInfo panDCVListInfo = null;
		logger.info("inside DCVActivationServiceImpl");
		panDCVListInfo = dCVActivationDao.findPanDCVListInfoByPanNo(panNo);

		logger.info(" print panDCVListInfo:" + panDCVListInfo.toString() );
		return panDCVListInfo;
	}
	
	

}
