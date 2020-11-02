package com.gemalto.eziomobile.demo.service.panmaster;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.CardIssuanceDTO;
import com.gemalto.eziomobile.demo.dto.PanMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.PanMasterInfo;

@Service
public interface PanmasterService {

	int countByStatusAndUserId(int status, int userId) throws ServiceException;

	List<PanMasterInfo> findPanInfoByUserId(int uId) throws ServiceException;
	
	List<PanMasterDTO> findListOfPanByUserId(int uId, String getCardsFor) throws ServiceException;
	
	String findAccountNoByPanNoAndUserId(String panNo, int userId) throws ServiceException;

	PanMasterDTO findPanInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
	
	void saveNewCardInfo(CardIssuanceDTO cardIssuanceDTO) throws ServiceException;
	
	void deletePanInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException;
	
	int countByStatusAndUserIdAndPanNoAndPanType(int status, int userId, String panNo, int panType) throws ServiceException; 
}
