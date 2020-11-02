package com.gemalto.eziomobile.demo.service.qrtokenmaster;


import org.springframework.stereotype.Service;
import com.gemalto.eziomobile.demo.exception.ServiceException;

import com.gemalto.eziomobile.demo.model.QRTokenMasterInfo;

@Service
public interface QRTokenmasterService {
	
	void saveQRTokenMasterInfo(QRTokenMasterInfo qrTokenMasterInfo) throws ServiceException;
	
	String findtransactionHashByUserId(String userId) throws ServiceException;
	
	String findtransactionHashByUserIdAndTranscationType(String userId) throws ServiceException;
	
	int countByUserId(String userId) throws ServiceException;

	void deleteQRTokenDetailsByUserId(String userId);
}
