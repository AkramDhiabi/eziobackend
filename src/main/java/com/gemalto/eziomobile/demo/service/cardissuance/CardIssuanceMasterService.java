package com.gemalto.eziomobile.demo.service.cardissuance;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.CardIssuanceDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;

@Service
public interface CardIssuanceMasterService {
	
	void saveNewCardRequest(CardIssuanceDTO cardIssuanceDTO) throws ServiceException;

	int countByUserId(int userId) throws ServiceException;
	
	List<CardIssuanceDTO> findCardsByUserId(int userId) throws ServiceException;
	
	CardIssuanceDTO findCardInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
	
	void deleteCardInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
	
}
