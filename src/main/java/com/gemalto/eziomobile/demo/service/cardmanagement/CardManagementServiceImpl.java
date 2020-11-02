package com.gemalto.eziomobile.demo.service.cardmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.cardmanagementmaster.CardmanagementDao;
import com.gemalto.eziomobile.demo.dto.CardManagementDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.CardManagementInfo;

@Service
public class CardManagementServiceImpl implements CardManagementService{
	
	private static final LoggerUtil logger = new LoggerUtil(CardManagementServiceImpl.class.getClass());
	
	@Autowired
	private CardmanagementDao cardManagementDao;

	/* 
	 * 
	 */
	@Override
	public CardManagementDTO findCardManagementInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		CardManagementDTO cardManagementDTO = null;
		
		try {
			CardManagementInfo cardManagementInfo = cardManagementDao.findCardManagementInfoByUserIdAndPanNo(userId, panNo);
			if(cardManagementInfo != null){
				
				cardManagementDTO = new CardManagementDTO();
				
				cardManagementDTO.setUserId(cardManagementInfo.getUserId());
				cardManagementDTO.setPanNo(cardManagementInfo.getPanNo());
				cardManagementDTO.setCardStatus(cardManagementInfo.getCardStatus());
				cardManagementDTO.setInternationalTravel(cardManagementInfo.getInternationalTravel());
				cardManagementDTO.setOnlineTransaction(cardManagementInfo.getOnlineTransaction());
				cardManagementDTO.setSpendLimitTransactionStatus(cardManagementInfo.getSpendLimitTransactionStatus());
				cardManagementDTO.setAmountLimitPerTransaction(cardManagementInfo.getAmountLimitPerTransaction());
				cardManagementDTO.setSpendLimitMonthStatus(cardManagementInfo.getSpendLimitMonthStatus());
				cardManagementDTO.setAmountLimitPerMonth(cardManagementInfo.getAmountLimitPerMonth());
				cardManagementDTO.setStatus(cardManagementInfo.getStatus());
			}
		} catch (Exception e) {
			logger.info("Exception : Unable to find CardManagement data for PanNo : "+ panNo ,e);
			throw new ServiceException(e);
		}
		return cardManagementDTO;
	}


	@Override
	public boolean updateCardStatusByUserIdAndPanNo(int userId, String panNo, String cardStatusValue)
			throws ServiceException {
		boolean isCardStatusUpdated = false;
		try {
			isCardStatusUpdated = cardManagementDao.updateCardStatusByUserIdAndPanNo(userId, panNo, cardStatusValue);
		} catch (Exception e) {
			logger.info("Exception : Unable to update Card status data for PanNo : "+ panNo ,e);
			throw new ServiceException(e);
		}
		return isCardStatusUpdated;
	}


	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public boolean updateCardManagementInfo(CardManagementDTO cardManagementDTO) throws ServiceException {
			boolean isCardSettingsUpdated = false;
			try {
				int cardId = cardManagementDao.findIdByUserIdAndPanNo(cardManagementDTO.getUserId(), cardManagementDTO.getPanNo());
				logger.info("cardId : "+cardId);
				
				CardManagementInfo cardManagementInfo = new CardManagementInfo();
				cardManagementInfo.setId(cardId);
				cardManagementInfo.setAmountLimitPerMonth(cardManagementDTO.getAmountLimitPerMonth());
				cardManagementInfo.setAmountLimitPerTransaction(cardManagementDTO.getAmountLimitPerTransaction());
				cardManagementInfo.setCardStatus(cardManagementDTO.getCardStatus());
				cardManagementInfo.setInternationalTravel(cardManagementDTO.getInternationalTravel());
				cardManagementInfo.setOnlineTransaction(cardManagementDTO.getOnlineTransaction());
				cardManagementInfo.setPanNo(cardManagementDTO.getPanNo());
				cardManagementInfo.setSpendLimitMonthStatus(cardManagementDTO.getSpendLimitMonthStatus());
				cardManagementInfo.setSpendLimitTransactionStatus(cardManagementDTO.getSpendLimitTransactionStatus());
				cardManagementInfo.setUserId(cardManagementDTO.getUserId());
				cardManagementInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
				
				CardManagementInfo cardManagementInfo2 = cardManagementDao.save(cardManagementInfo);
				logger.info("Update Object : "+cardManagementInfo2.toString());
				isCardSettingsUpdated = true;
				
			} catch (Exception e) {
				logger.info("Exception : Unable to update Card Settings!" ,e);
				throw new ServiceException(e);
			}
			return isCardSettingsUpdated;
	}


	@Override
	public String findCardStatusByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		String cardStatus = "";
		try {
			cardStatus = cardManagementDao.findCardStatusByUserIdAndPanNo(userId, panNo);
		} catch (Exception e) {
			logger.info("Exception : Unable to find Card Status!" ,e);
			throw new ServiceException(e);
		}
		return cardStatus;
	}


	@Override
	public void saveCardManagementInfo(int userId, String cardType, String panNo) throws ServiceException {
		try {
			CardManagementInfo cardManagementInfo = new CardManagementInfo();
			
				cardManagementInfo.setUserId(userId);
				cardManagementInfo.setPanNo(panNo);
				cardManagementInfo.setCardStatus("ON");
				cardManagementInfo.setInternationalTravel("ON");
				cardManagementInfo.setOnlineTransaction("ON");
				cardManagementInfo.setSpendLimitTransactionStatus("OFF");
				cardManagementInfo.setAmountLimitPerTransaction(0);
				cardManagementInfo.setSpendLimitMonthStatus("OFF");
				cardManagementInfo.setAmountLimitPerMonth(0);
				cardManagementInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
				
			cardManagementDao.save(cardManagementInfo);
			logger.info("Saved : Card Management data for PanNo : "+panNo+"AND userId : "+userId);
			
		} catch (Exception e) {
			logger.info("Exception : Unable save Card Management Info for PanNo :"+panNo+ "And cardType : "+cardType ,e);
			throw new ServiceException(e);
		}
	}


	/* 
	 * 
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteCardManagementInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		try {
			
			cardManagementDao.deleteCardManagementInfoByUserIdAndPanNo(userId, panNo);
			logger.info("Deleted : Card Management data for PanNo : "+panNo+" AND userId : "+userId);
			
		} catch (Exception e) {
			logger.info("Exception : Unable delete Card Management Info for PanNo :"+panNo ,e);
			throw new ServiceException(e);
		}
		
	}


	@Override
	public int countByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		int count = 0;
		try {
			count = cardManagementDao.countByUserIdAndPanNo(userId, panNo);
		} catch (Exception e) {
			logger.info("Exception : Unable to find Card Management Info for PanNo :"+panNo ,e);
			throw new ServiceException(e);
		}
		return count;
	}
	
	

}
