package com.gemalto.eziomobile.demo.controller.cardmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.CardIssuanceDTO;
import com.gemalto.eziomobile.demo.dto.CardManagementDTO;
import com.gemalto.eziomobile.demo.dto.PanMasterDTO;
import com.gemalto.eziomobile.demo.dto.mobiledata.AccountListDTO;
import com.gemalto.eziomobile.demo.dto.mobiledata.CardListDTO;
import com.gemalto.eziomobile.demo.dto.mobiledata.MobileDataDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.cardissuance.CardIssuanceMasterService;
import com.gemalto.eziomobile.demo.service.cardmanagement.CardManagementService;
import com.gemalto.eziomobile.demo.service.panmaster.PanmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.SubStringUtil;
import com.gemalto.eziomobile.demo.webhelper.emv.EMVCardCreationWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

@RestController
public class CardIssuanceController {
	
	private static final LoggerUtil logger = new LoggerUtil(CardIssuanceController.class.getClass());
	
	@Autowired
	private UsermasterService userService;
	
	@Autowired
	private CardIssuanceMasterService cardIssuanceService;
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private EMVCardCreationWebHelper emvCardWebHelper;
	
	@Autowired
	private PanmasterService panMasterService;
	
	@Autowired
	private CardManagementService cardManagementService;
	
	@Autowired
	private AccountmasterService accountService;
	
	
	/**Get cards for cardIssuance
	 * @param userId
	 * @return ResultStatus with list of cards
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/cardIssuance.getcard.action/{userId}", method = RequestMethod.GET)
	public ResultStatus getCardsForEcommerce(@PathVariable("userId") String userId) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		List<CardIssuanceDTO> panInfoList = null;
		int uId;
			try {
				if(userId != null && !userId.equals("")){
					
					uId = userService.findUidByUserId(userId);
					logger.info("[getNewCards - CardIssuance] uId : "+uId);
					
				 	panInfoList = cardIssuanceService.findCardsByUserId(uId);
				 	
				 	if(!panInfoList.isEmpty() && panInfoList.size() != 0){
				 		
						logger.info("[getCardsForEcommerce - DCV] panInfoList : "+panInfoList.toString());
						
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
						resultStatus.setTemplateObject(panInfoList);
						
					}else{
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
						resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
						resultStatus.setTemplateObject(panInfoList);
					}
				}
			} catch (ServiceException e) {
				e.printStackTrace();
				logger.error("Unable to find user cards!");
				throw new ControllerException(e);
			} 
		return resultStatus;
	}
	
	
	
	/**
	 * @param userId
	 * @param panNo
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/digitalize.card.action/{userId}/{panNo}", method = RequestMethod.POST)
	 public ResultStatus createEMVCard(@PathVariable("userId") String userId, @PathVariable("panNo") String panNo) throws ControllerException{
		
		boolean isCreated = false;
		boolean isLinked = false;
		boolean isActivated = false;
		
		ResultStatus resultStatus = new ResultStatus();
		try {
			if(userId != null && !userId.equals("") && panNo != null && !panNo.equals("")){
				
				int uId = userService.findUidByUserId(userId);
				CardIssuanceDTO cardIssuanceDTO = cardIssuanceService.findCardInfoByUserIdAndPanNo(uId, panNo);
				
				if(cardIssuanceDTO != null){
					
					casWebHelper.authenticateCASever();
					
					isCreated = emvCardWebHelper.createEMVCards(panNo, userId);
					isLinked = emvCardWebHelper.linkEMVCards(userId, panNo);
					isActivated = emvCardWebHelper.activeEMVCards(panNo);
					
					//For local testing
					/*isActivated = true;
					isCreated = true;
					isLinked = true;*/
					
					if(isCreated && isLinked && isActivated){
						
						
							logger.info("cardIssuanceDTO : "+cardIssuanceDTO.toString());
							
							logger.info("Saving data in PanMaster for PanNo : "+panNo);
							panMasterService.saveNewCardInfo(cardIssuanceDTO);
							
							logger.info("Saving data in CardManagementMaster for PanNo : "+panNo);
							cardManagementService.saveCardManagementInfo(uId, EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_CARD_TYPE_VISA, panNo);
							
							logger.info("Saving data from CardIssuanceMaster for PanNo : "+panNo+" AND userId : "+userId);
							cardIssuanceService.deleteCardInfoByUserIdAndPanNo(uId, panNo);
							
							//start from here
							PanMasterDTO panMasterDTO = panMasterService.findPanInfoByUserIdAndPanNo(uId, panNo);
							
							CardManagementDTO cardManagementDTO = cardManagementService.findCardManagementInfoByUserIdAndPanNo(uId, panNo);
							
							AccountMasterInfo accountMasterInfo = accountService.findAccountByAccountNoAndUserIdAndType(panMasterDTO.getAccountNo(), uId, EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0);
							
							MobileDataDTO mobileDataDTO = new MobileDataDTO();
							CardListDTO card_list_DTO = new CardListDTO();
							AccountListDTO account_list_DTO = new AccountListDTO();
							
							List<CardListDTO> card_list = new ArrayList<>();
							List<AccountListDTO> account_list = new ArrayList<>();
							
							List<Map<String, String>> Spend_limit_per_month_list = new ArrayList<>();
							List<Map<String, String>> Spend_limit_per_transaction_list = new ArrayList<>();
							
							//Prepare data to set in MobileDataDTO
							//-------------------------------//
							//Prepare Map of Spend_limit_per_month with status and threshold value
							//Set this object in card_list object
							Map<String, String> Spend_limit_per_month = new HashMap<>();
							Spend_limit_per_month.put("status", cardManagementDTO.getSpendLimitMonthStatus());
							Spend_limit_per_month.put("threshold", String.valueOf(cardManagementDTO.getAmountLimitPerMonth()));
							Spend_limit_per_month_list.add(Spend_limit_per_month);
							
							//Prepare Map of Spend_limit_per_transaction with status and threshold value
							//Set this object in card_list object
							Map<String, String> Spend_limit_per_transaction = new HashMap<>();
							Spend_limit_per_transaction.put("status", cardManagementDTO.getSpendLimitTransactionStatus());
							Spend_limit_per_transaction.put("threshold", String.valueOf(cardManagementDTO.getAmountLimitPerTransaction()));
							Spend_limit_per_transaction_list.add(Spend_limit_per_transaction);
							
							card_list_DTO.setCard_ON_OFF(cardManagementDTO.getCardStatus());
							card_list_DTO.setSpend_limit_per_month(Spend_limit_per_month_list);
							card_list_DTO.setSpend_limit_per_transaction(Spend_limit_per_transaction_list);
							card_list_DTO.setInternational_travel(cardManagementDTO.getInternationalTravel());
							card_list_DTO.setOnline_transaction(cardManagementDTO.getOnlineTransaction());
							
							card_list_DTO.setPan(SubStringUtil.addSpaceInPanNo(panMasterDTO.getPanNo()));
							card_list_DTO.setCvv(panMasterDTO.getCardCVV());
							card_list_DTO.setDcven(panMasterDTO.getIsDCV_Active());
							card_list_DTO.setExpdate(panMasterDTO.getExpDate());
							card_list_DTO.setPanType(panMasterDTO.getPanTypeFlag());
							//-------------------------------//
							
							//set data in account_list object
							//-------------------------------//
							account_list_DTO.setAccount_name(accountMasterInfo.getAccountName());
							account_list_DTO.setAccount_no(accountMasterInfo.getAccountNo());
							account_list_DTO.setTotal(accountMasterInfo.getAccountBalance());
							account_list_DTO.setType(accountMasterInfo.getType());
							account_list_DTO.setCur(EzioMobileDemoConstant.CURRENCY_USD);
							//-------------------------------//
							
							card_list.add(card_list_DTO);
							account_list.add(account_list_DTO);
							
							////set data in MobileDataDTO object
							mobileDataDTO.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
							mobileDataDTO.setAccounts_list(account_list);
							mobileDataDTO.setCard_list(card_list);
							//............
							
							resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
							resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
							resultStatus.setStatusCode(HttpStatus.CREATED);
							resultStatus.setTemplateObject(mobileDataDTO);
						}
						else{
							resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
							resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
							resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
						}
				}
				else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				}
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : EMV_CARD_CREATION could not happan!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
		
	

	/**API to delete data from PanMaster, CardManagementMaster and EMV cards from CAS
	 * @param userId
	 * @param panNo
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/cardIssuance.removecard.action/{userId}/{panNo}", method = RequestMethod.POST)
	public ResultStatus deleteCards(@PathVariable("userId") String userId, @PathVariable("panNo") String panNo) throws ControllerException{
		ResultStatus resultStatus = new ResultStatus();
		String operationType = "";
		try {
			if(userId != null && !userId.isEmpty() && panNo != null && !panNo.isEmpty()){
				
				int uId = userService.findUidByUserId(userId);
				int count = panMasterService.countByStatusAndUserIdAndPanNoAndPanType(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId, panNo, EzioMobileDemoConstant.PAN_TYPE_NEW_PAN);
				logger.info("Pan count : "+count);
				
				if(count>0){
					
					boolean flag = emvCardWebHelper.deleteEMVCards(userId, panNo);
					logger.info("[Controller : deleteCards] flag : "+flag);
					
					if(flag){
						panMasterService.deletePanInfoByUserIdAndPanNo(uId, panNo);
						cardManagementService.deleteCardManagementInfoByUserIdAndPanNo(uId, panNo);
						
						logger.info("Data has been delete from PanMaster And CardManagement for PanNo : "+panNo);
						
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
					}else{
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
						resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
					}
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.NO_RECORD_FOUND_MSG);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
					resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				}
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : Unable to delete data of "+operationType+" !");
			throw new ControllerException(e);
		}
		
		return resultStatus;
	}
	
		 
	@ExceptionHandler(ControllerException.class)
	public ResultStatus cardIssuanceErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}

}
