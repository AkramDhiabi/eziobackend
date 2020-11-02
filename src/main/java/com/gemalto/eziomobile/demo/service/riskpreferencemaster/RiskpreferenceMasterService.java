package com.gemalto.eziomobile.demo.service.riskpreferencemaster;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface RiskpreferenceMasterService {
	
	boolean updateRiskPreferenceBySecMode(int userId, int securityMode) throws ServiceException;

}
