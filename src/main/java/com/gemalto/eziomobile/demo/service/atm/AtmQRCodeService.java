package com.gemalto.eziomobile.demo.service.atm;

import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.ATMQRCodeDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.AtmQRCodeInfo;

@Service
public interface AtmQRCodeService {
	
	void saveQRCodeDetails (ATMQRCodeDTO atmQRCodeDTO) throws ServiceException;

	ATMQRCodeDTO findAtmQRCodeInfoByUserIdAndAtmIdAndChallenge(String userId, String atmId, String Challenge) throws ServiceException;

	boolean updateAtmQRCodeStatusByUserIdAndAtmId(int status, String atmId, int uId)throws ServiceException;

	Future<Integer> findAtmQrCodeStatusByUserIdAndAtmId(int userId, String atmId) throws ServiceException;

	void deleteAtmQRCodeDetailsByUserId(int userId) throws ServiceException;

	int countByUserId(int userId) throws ServiceException; 

}
