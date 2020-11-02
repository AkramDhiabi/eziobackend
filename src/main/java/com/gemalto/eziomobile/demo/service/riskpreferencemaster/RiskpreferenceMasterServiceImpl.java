package com.gemalto.eziomobile.demo.service.riskpreferencemaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.dao.riskpreferencemaster.RiskpreferenceMasterDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;

@Service
public class RiskpreferenceMasterServiceImpl implements RiskpreferenceMasterService{
	
	@Autowired
	RiskpreferenceMasterDao riskpreferenceMasterDao;

	private static final LoggerUtil logger = new LoggerUtil(RiskpreferenceMasterServiceImpl.class.getClass());
	
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public boolean updateRiskPreferenceBySecMode(int userId, int securityMode) throws ServiceException {
		boolean isupdated = false;
		try {
			int count = riskpreferenceMasterDao.updateRiskPreferenceBySecMode(userId, securityMode);
			if(count>0) {
				isupdated = true;
			}
		} catch (Exception e) {
			logger.info("Unable to update RiskPreference with securityMode for userId: "+userId+" "+securityMode+" "+ e);
			throw new ServiceException(e);
		}
		return isupdated;
	}

}
