package com.gemalto.eziomobile.demo.service.panmaster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.accountmaster.AccountmasterDao;
import com.gemalto.eziomobile.demo.dao.cardmanagementmaster.CardmanagementDao;
import com.gemalto.eziomobile.demo.dao.panmaster.PanMasterDao;
import com.gemalto.eziomobile.demo.dto.CardIssuanceDTO;
import com.gemalto.eziomobile.demo.dto.PanMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.model.PanMasterInfo;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;
import com.gemalto.eziomobile.demo.util.SubStringUtil;

@Service
public class PanmasterServiceImpl implements PanmasterService{
	
	private static final LoggerUtil logger = new LoggerUtil(PanmasterServiceImpl.class.getClass());
	
	@Autowired
	private PanMasterDao panMasterDao;
	
	@Autowired
	private AccountmasterDao accountMasterDao;
	
	@Autowired
	private CardmanagementDao cardManagementDao;

	@Override
	public int countByStatusAndUserId(int status, int userId) throws ServiceException {
		int count = 0;
		try {
			count = panMasterDao.countByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("Unable find pan count!", e);
			throw new ServiceException(e);
		}
		return count;
	}

	
	@Override
	public List<PanMasterInfo> findPanInfoByUserId(int uId) throws ServiceException {
		List<PanMasterInfo> listOfPan = new ArrayList<>();
		try {
			listOfPan = panMasterDao.findPanInfoByUserIdOrderByPanNoAsc(uId);
		} catch (Exception e) {
			logger.info("Unable find list of PAN numbers!", e);
			throw new ServiceException(e);
		}
		return listOfPan;
	}


	@Override
	public String findAccountNoByPanNoAndUserId(String panNo, int userId) throws ServiceException {
		String accountNo = "";
		try {
			accountNo = panMasterDao.findAccountNoByUserIdAndPanNo(userId, panNo);
		} catch (Exception e) {
			logger.info("Unable find AccountNO for PAN numbers : "+panNo, e);
			throw new ServiceException(e);
		}
		return accountNo;
	}


	@Override
	public List<PanMasterDTO> findListOfPanByUserId(int uId, String getCardsFor) throws ServiceException {
		
		List<PanMasterInfo> listOfPan = new ArrayList<>();
		List<PanMasterDTO> panList = new ArrayList<>();
		try {
			listOfPan = panMasterDao.findPanInfoByUserIdOrderByPanNoAsc(uId);
			if(!(!listOfPan.isEmpty() && listOfPan.size() != 0)){
				return  panList;
			}

			List pansList = listOfPan;
			AtomicInteger indxI = new AtomicInteger();
			indxI.getAndIncrement();
			AtomicInteger indxJ = new AtomicInteger();
			indxJ.getAndIncrement();
			listOfPan.forEach(item->{
				PanMasterDTO panMasterDTO = new PanMasterDTO();
				String regDate = ConvertDateToStringDate.convertDateToString(item.getRegistrationDate());
				int indx = pansList.indexOf(item);

				logger.info("print the value of itemvalue indx :"+ pansList.indexOf(item));
				logger.info("print the value of itemvalue  indxI :"+indxI);
				logger.info("print the value of itemvalue  indxJ :"+indxJ);

				AccountMasterInfo accountMasterInfo =  accountMasterDao.findAccountByAccountNoAndUserId(item.getAccountNo(), uId);
				String cardLast4Digits = SubStringUtil.getLastnCharacters(accountMasterInfo.getAccountNo(),4);
				String primaryLinkedAcc = accountMasterInfo.getAccountName()+" - "+cardLast4Digits;

				String cardStatus = cardManagementDao.findCardStatusByUserIdAndPanNo(uId, item.getPanNo());

				if(item.getPanNo().substring(0, 1).equals(EzioMobileDemoConstant.PAN_TYPE_VISA_PREFIX_4)){

					panMasterDTO = setPanMasterDTO(getCardsFor, indxI, item, regDate, primaryLinkedAcc, cardStatus, EzioMobileDemoConstant.PAN_TYPE_MY_VISA, EzioMobileDemoConstant.PAN_TYPE_VISA);

				}else if(item.getPanNo().substring(0, 1).equals(EzioMobileDemoConstant.PAN_TYPE_MASTERCARD_PREFIX_5)){

					panMasterDTO = setPanMasterDTO(getCardsFor, indxJ, item, regDate, primaryLinkedAcc, cardStatus, EzioMobileDemoConstant.PAN_TYPE_MY_MASTERCARD, EzioMobileDemoConstant.PAN_TYPE_MASTERCARD);
				}
				panList.add(panMasterDTO);
			});
		} catch (Exception e) {
			logger.info("Unable find list of PAN numbers!", e);
			throw new ServiceException(e);
		}
		return panList;
	}

	/**
	 *
	 * @param getCardsFor
	 * @param indxI
	 * @param item
	 * @param regDate
	 * @param primaryLinkedAcc
	 * @param cardStatus
	 * @param panTypeMyVisa
	 * @param panTypeVisa
	 * @return
	 */
	private PanMasterDTO setPanMasterDTO(String getCardsFor, AtomicInteger indxI, PanMasterInfo item, String regDate, String primaryLinkedAcc, String cardStatus, String panTypeMyVisa, String panTypeVisa) {
		PanMasterDTO panMasterDTO = new PanMasterDTO();

		panMasterDTO.setAccountNo(item.getAccountNo());
		panMasterDTO.setPanNo(item.getPanNo());

		switch (getCardsFor) {
			case EzioMobileDemoConstant.CARDMANAGEMENT_GET_CARD_MY_WALLET:
				panMasterDTO.setPanType(panTypeMyVisa + " #" + indxI);
				indxI.getAndIncrement();
				break;
			case EzioMobileDemoConstant.CARDMANAGEMENT_GET_CARD_ECOMMERCE:
				panMasterDTO.setPanType(panTypeVisa + " #" + indxI);
				indxI.getAndIncrement();
				break;
			default:
				break;
		}

		panMasterDTO.setCardCVV(item.getCardCVV());
		panMasterDTO.setExpDate(item.getExpDate());
		panMasterDTO.setRegistrationDate(regDate);
		panMasterDTO.setStatus(item.getStatus());
		panMasterDTO.setTokenId(item.getTokenId());
		panMasterDTO.setIsDCV_Active(item.getIsDCV_Active());
		panMasterDTO.setUserId(item.getUserId());
		panMasterDTO.setLinkedToAccount(primaryLinkedAcc);
		panMasterDTO.setCardFreezed(cardStatus.equalsIgnoreCase("ON") ? false : true);

		//Flag added to differentiate Generic and New cards/pan
		//PanType is int in Model/Info class
		//PanTypeFlag is Integer type int in DTO, 0 - Generic card / 1 - new cards (card issuance)
		panMasterDTO.setPanTypeFlag(item.getPanType());

		return panMasterDTO;
	}


	@Override
	public PanMasterDTO findPanInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		PanMasterDTO panMasterDTO = new PanMasterDTO();
		PanMasterInfo panMasterInfo = null;
		try {
			panMasterInfo = panMasterDao.findPanInfoByUserIdAndPanNo(userId, panNo);
			logger.info("panMasterInfo : "+panMasterInfo.toString());
			if(panMasterInfo != null){
				
				AccountMasterInfo accountMasterInfo =  accountMasterDao.findAccountByAccountNoAndUserId(panMasterInfo.getAccountNo(), userId);
				String cardLast4Digits = SubStringUtil.getLastnCharacters(accountMasterInfo.getAccountNo(),4);
				String primaryLinkedAcc = accountMasterInfo.getAccountName()+" - "+cardLast4Digits;
				String cardStatus = cardManagementDao.findCardStatusByUserIdAndPanNo(userId, panMasterInfo.getPanNo());
				
				if(panMasterInfo.getPanNo().substring(0, 1).equals(EzioMobileDemoConstant.PAN_TYPE_VISA_PREFIX_4)) {
					panMasterDTO.setPanType(EzioMobileDemoConstant.PAN_TYPE_MY_VISA);
				}else if(panMasterInfo.getPanNo().substring(0, 1).equals(EzioMobileDemoConstant.PAN_TYPE_MASTERCARD_PREFIX_5)){
					panMasterDTO.setPanType(EzioMobileDemoConstant.PAN_TYPE_MY_MASTERCARD);
				}
				panMasterDTO.setAccountNo(panMasterInfo.getAccountNo());
				panMasterDTO.setPanNo(panMasterInfo.getPanNo());
				panMasterDTO.setCardCVV(panMasterInfo.getCardCVV());
				panMasterDTO.setExpDate(panMasterInfo.getExpDate());
				panMasterDTO.setTokenId(panMasterInfo.getTokenId());
				panMasterDTO.setIsDCV_Active(panMasterInfo.getIsDCV_Active());
				panMasterDTO.setTokenId(panMasterInfo.getTokenId());
				panMasterDTO.setUserId(panMasterInfo.getUserId());
				panMasterDTO.setLinkedToAccount(primaryLinkedAcc);
				panMasterDTO.setRegistrationDate(ConvertDateToStringDate.convertDateToString(panMasterInfo.getRegistrationDate()));
				panMasterDTO.setCardFreezed(cardStatus.equalsIgnoreCase("ON")? false : true);
				
				//Flag added to differentiate Generic and New cards/pan
				//PanType is int in Model/Info class
				//PanTypeFlag is Integer type int in DTO, 0 - Generic card / 1 - new cards (card issuance)
				panMasterDTO.setPanTypeFlag(panMasterInfo.getPanType());
			}
		} catch (Exception e) {
			logger.info("Unable to find PAN details!", e);
			throw new ServiceException(e);
		}
		return panMasterDTO;
	}


	
	@Override
	public void saveNewCardInfo(CardIssuanceDTO cardIssuanceDTO) throws ServiceException {
		try {
			List<AccountMasterInfo> accountsList = new ArrayList<>();
			
			//List<PanMasterInfo> panInfoList = new ArrayList<>();
			
			accountsList = accountMasterDao.findAccountByUserIdAndTypeAndAccountNameOrderByAccountNameAsc(cardIssuanceDTO.getUserId(),
					EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0, EzioMobileDemoConstant.ACCOUNT_TYPE_CARD);
			logger.info("[saveNewCardInfo] accountsList : "+accountsList.toString());
			
			if(accountsList.size()>0 && !accountsList.isEmpty()){
				
				PanMasterInfo panMasterInfo = new PanMasterInfo();
				
				if(cardIssuanceDTO.getPanNo().substring(0, 1).equals(EzioMobileDemoConstant.PAN_TYPE_VISA_PREFIX_4)) {
					panMasterInfo.setAccountNo(accountsList.get(0).getAccountNo());
				}
				else if(cardIssuanceDTO.getPanNo().substring(0, 1).equals(EzioMobileDemoConstant.PAN_TYPE_MASTERCARD_PREFIX_5)){
					panMasterInfo.setAccountNo(accountsList.get(1).getAccountNo());
				}
				
				panMasterInfo.setUserId(cardIssuanceDTO.getUserId());
				panMasterInfo.setPanNo(cardIssuanceDTO.getPanNo());
				panMasterInfo.setCardCVV(cardIssuanceDTO.getCardCVV());
				panMasterInfo.setExpDate(cardIssuanceDTO.getExpDate());
				panMasterInfo.setIsDCV_Active(cardIssuanceDTO.getIsDCV_Active());
				panMasterInfo.setStatus(cardIssuanceDTO.getStatus());
				panMasterInfo.setTokenId(EzioMobileDemoConstant.DEPRECATED_TOKEN_ID);
				panMasterInfo.setRegistrationDate(ConvertDateToStringDate.convertStringToDate(cardIssuanceDTO.getCardRequestDate()));
				panMasterInfo.setPanType(cardIssuanceDTO.getPanType());
				
				logger.info("panMasterInfo : "+panMasterInfo.toString());
				
				//panInfoList.add(panMasterInfo);
				
				panMasterDao.save(panMasterInfo);
				
				logger.info("Saved : PanMaster data for PanNo : "+cardIssuanceDTO.getPanNo()+" AND Linked to AccountNo : "+accountsList.get(0).getAccountNo());
			}
		} catch (Exception e) {
			logger.info("Unable to find PAN details!", e);
			throw new ServiceException(e);
		}
		
	}


	/* 
	 * Delete entry from DB for given UserId and PanNo 
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deletePanInfoByUserIdAndPanNo(int userId, String panNo) throws ServiceException {
		try {
			panMasterDao.deletePanInfoByUserIdAndPanNo(userId, panNo);
			logger.info("Deleted : PanMaster data for PanNo : "+panNo+" AND userId : "+userId);
		} catch (Exception e) {
			logger.info("Exception : Unable to delete pan details for PanNo : "+panNo, e);
			throw new ServiceException(e);
		}
	}


	@Override
	public int countByStatusAndUserIdAndPanNoAndPanType(int status, int userId, String panNo, int panType) throws ServiceException {
		int count = 0;
		try {
			count = panMasterDao.countByStatusAndUserIdAndPanNoAndPanType(status, userId, panNo, panType);
		} catch (Exception e) {
			logger.info("Exception : Unable to get pan count for PanNo : "+panNo, e);
			throw new ServiceException(e);
		}
		return count;
	}

}
