package com.gemalto.eziomobile.demo.service.cardissuance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.dao.cardissuance.CardIssuanceMasterDao;
import com.gemalto.eziomobile.demo.dto.CardIssuanceDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.CardIssuanceInfo;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;

@Service
public class CardIssuanceMasterServiceImpl implements CardIssuanceMasterService{

	private static final LoggerUtil logger = new LoggerUtil(CardIssuanceMasterServiceImpl.class.getClass());

	@Autowired
	private CardIssuanceMasterDao cardIssuanceDao;

	
	
	@Override
	public void saveNewCardRequest(CardIssuanceDTO cardIssuanceDTO) throws ServiceException {
		try {
			CardIssuanceInfo cardIssuanceInfo = new CardIssuanceInfo();
			cardIssuanceInfo.setUserId(cardIssuanceDTO.getUserId());
			cardIssuanceInfo.setPanNo(cardIssuanceDTO.getPanNo());
			cardIssuanceInfo.setCardCVV(cardIssuanceDTO.getCardCVV());
			cardIssuanceInfo.setExpDate(cardIssuanceDTO.getExpDate());
			cardIssuanceInfo.setIsDCV_Active(cardIssuanceDTO.getIsDCV_Active());
			cardIssuanceInfo.setStatus(cardIssuanceDTO.getStatus());
			cardIssuanceInfo.setRegistrationDate(new Date());
			cardIssuanceInfo.setPanType(cardIssuanceDTO.getPanType());
			
			cardIssuanceDao.save(cardIssuanceInfo);
			
		} catch (Exception e) {
			logger.error("Exception : Couldn't save card request with CardNo : "+cardIssuanceDTO.getPanNo(), e);
			throw new ServiceException(e);
		}
	}

	
	
	@Override
	public int countByUserId(int userId) throws ServiceException {
		int count = 0;
		try {
			count = cardIssuanceDao.countByUserId(userId);
		} catch (Exception e) {
			logger.error("Exception : Couldn't get the count of the cards for userId : "+userId, e);
			throw new ServiceException(e);
		}
		return count;
	}

	
	
	@Override
	public List<CardIssuanceDTO> findCardsByUserId(int userId) throws ServiceException {
		 List<CardIssuanceInfo> cardInfoList = new ArrayList<>();
		 List<CardIssuanceDTO> cardsList = new ArrayList<>();
		try {
			cardInfoList = cardIssuanceDao.findCardsByUserId(userId);
			for (CardIssuanceInfo cardIssuanceInfo : cardInfoList) {
					
					CardIssuanceDTO cardIssuanceDTO = new CardIssuanceDTO();
				
					cardIssuanceDTO.setPanNo(cardIssuanceInfo.getPanNo());
					cardIssuanceDTO.setCardCVV(cardIssuanceInfo.getCardCVV());
					cardIssuanceDTO.setExpDate(cardIssuanceInfo.getExpDate());
					cardIssuanceDTO.setIsDCV_Active(cardIssuanceInfo.getIsDCV_Active());
					cardIssuanceDTO.setStatus(cardIssuanceInfo.getStatus());
					cardIssuanceDTO.setCardRequestDate(ConvertDateToStringDate.convertDateToString(cardIssuanceInfo.getRegistrationDate()));
					cardIssuanceDTO.setPanType(cardIssuanceInfo.getPanType());
					
					cardsList.add(cardIssuanceDTO);
			}
		} catch (Exception e) {
			logger.error("Exception : Couldn't get the count of the cards for userId : "+userId, e);
			throw new ServiceException(e);
		}
		return cardsList;
	}


	/* 
	 * 
	 */
	@Override
	public CardIssuanceDTO findCardInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		CardIssuanceDTO cardIssuanceDTO = null;
		try {
			CardIssuanceInfo cardIssuanceInfo = cardIssuanceDao.findCardInfoByUserIdAndPanNo(userId, panNo);
			if(cardIssuanceInfo != null){
				
				cardIssuanceDTO = new CardIssuanceDTO();
				cardIssuanceDTO.setCardCVV(cardIssuanceInfo.getCardCVV());
				cardIssuanceDTO.setCardRequestDate(ConvertDateToStringDate.convertDateToStringOther(cardIssuanceInfo.getRegistrationDate()));
				cardIssuanceDTO.setExpDate(cardIssuanceInfo.getExpDate());
				cardIssuanceDTO.setIsDCV_Active(cardIssuanceInfo.getIsDCV_Active());
				cardIssuanceDTO.setPanNo(cardIssuanceInfo.getPanNo());
				cardIssuanceDTO.setStatus(cardIssuanceInfo.getStatus());
				cardIssuanceDTO.setUserId(cardIssuanceInfo.getUserId());
				cardIssuanceDTO.setPanType(cardIssuanceInfo.getPanType());
			}
		} catch (Exception e) {
			logger.error("Exception : Couldn't find cardInfo for userId : "+userId+ CommonOperationsConstants.AND_PAN_NO_MESSAGE +panNo, e);
			throw new ServiceException(e);
		}
		return cardIssuanceDTO;
	}


	/*
	 *
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteCardInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		try {
			cardIssuanceDao.deleteCardInfoByUserIdAndPanNo(userId, panNo);
			logger.info("Deleted : CardIssuanceInfo for userId : "+userId+ CommonOperationsConstants.AND_PAN_NO_MESSAGE +panNo);
		} catch (Exception e) {
			logger.error("Exception : Could not delete CardInfo for userId : "+userId+ CommonOperationsConstants.AND_PAN_NO_MESSAGE +panNo, e);
			throw new ServiceException(e);
		}
	}

}
