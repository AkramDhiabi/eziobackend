package com.gemalto.eziomobile.demo.service.cardmanagement;

import org.springframework.stereotype.Service;


import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.PanDCVListInfo;

@Service
public interface DCVActivationService {
		
	PanDCVListInfo findPanDCVListInfoByPanNo(String panNo) throws ServiceException;
}
