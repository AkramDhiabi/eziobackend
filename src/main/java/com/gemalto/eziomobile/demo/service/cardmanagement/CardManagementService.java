package com.gemalto.eziomobile.demo.service.cardmanagement;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.CardManagementDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface CardManagementService {
	
	public CardManagementDTO findCardManagementInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
	
	boolean updateCardStatusByUserIdAndPanNo(int userId, String panNo, String cardStatusValue) throws ServiceException;
	
	boolean updateCardManagementInfo(CardManagementDTO cardManagementDTO) throws ServiceException;
	
	String findCardStatusByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
	
	void saveCardManagementInfo(int userId, String cardType, String panNo) throws ServiceException;
	
	void deleteCardManagementInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
	
	int countByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
}
