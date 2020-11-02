package com.gemalto.eziomobile.demo.service.singdatamaster;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.SigndataMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.SignDataInfo;

@Service
public interface SigndatamasterService {
	
	void saveSigndataInfo(SignDataInfo signDataInfo) throws ServiceException;
	
	SignDataInfo findSigndataByUserId(int userId) throws ServiceException;
	
	void deleteSigndataByUserId(int userId) throws ServiceException;

}
