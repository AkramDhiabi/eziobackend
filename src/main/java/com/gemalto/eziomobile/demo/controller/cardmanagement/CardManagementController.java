package com.gemalto.eziomobile.demo.controller.cardmanagement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.CardManagementDTO;
import com.gemalto.eziomobile.demo.dto.DCVTokenDTO;
import com.gemalto.eziomobile.demo.dto.PanMasterDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.cardmanagement.CardManagementService;
import com.gemalto.eziomobile.demo.service.panmaster.PanmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.webhelper.cardmanagement.CardManagementWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

@RestController
public class CardManagementController {
	
	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private CardManagementWebHelper cardManagementWebHelper;
	
	@Autowired
	private CardManagementService cardManagementService;
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private PanmasterService panmasterService;
	
	private static final LoggerUtil logger = new LoggerUtil(CardManagementController.class.getClass());
	
	
	/**
	 * @param userId
	 * @param panInfoList
	 * @return
	 */
	public List<PanMasterDTO> addDCVCardToTheListOfCards(String userId, List<PanMasterDTO> panInfoList){
		
		casWebHelper.authenticateCASever();
		List<DCVTokenDTO> dcvPanList = new ArrayList<>();
		
		dcvPanList = cardManagementWebHelper.getPANNumber(userId);
		logger.info("panList : "+dcvPanList.toString());
		
		if(!dcvPanList.isEmpty() && dcvPanList.size() != 0){
			int deviceCountDCV = 1;
			for (int i = 0; i < dcvPanList.size(); i++) {
				
				PanMasterDTO panMasterDTO = new PanMasterDTO();
				panMasterDTO.setAccountNo("-");
				panMasterDTO.setPanNo(dcvPanList.get(i).getDcvCardNo());
				panMasterDTO.setTokenId(dcvPanList.get(i).getDcvTokenId());
				panMasterDTO.setPanType(EzioMobileDemoConstant.PAN_TYPE_GEMALTO_DCV+deviceCountDCV);
				panMasterDTO.setIsDCV_Active(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
				panMasterDTO.setExpDate(EzioMobileDemoConstant.EZIO_DCV_CARDS_EXP_DATE);
				
				panInfoList.add(panMasterDTO);
				deviceCountDCV++;
			}
		}
		return panInfoList;
	}
	
	
	/**Get cards for E-commerce, VISA, MASTERCARD and GEMALTO DCV cards
	 * @param userId
	 * @return ResultStatus with list of cards
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getcards.ecommerce.actions/{userId}/{operationType}", method = RequestMethod.GET)
	public ResultStatus getCardsForEcommerce(@PathVariable("userId") String userId, @PathVariable("operationType") String operationType) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		List<PanMasterDTO> panInfoList = null;
		int uId;
			try {
				if(userId != null && !userId.equals("")){
					
					uId = usermasterService.findUidByUserId(userId);
					logger.info("[getCardsForEcommerce] uId : "+uId);
					
				 	panInfoList = panmasterService.findListOfPanByUserId(uId, EzioMobileDemoConstant.CARDMANAGEMENT_GET_CARD_ECOMMERCE);
				 	
				 	if(!panInfoList.isEmpty() && panInfoList.size() != 0){
				 		
							if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE_DCV))
								panInfoList = addDCVCardToTheListOfCards(userId, panInfoList);
							
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
	
	

	
	/**Get user cards, VISA, MASTERCARD and GEMALTO DCV cards
	 * @param userId
	 * @return ResultStatus with list of cards
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getcards.wallet.actions/{userId}", method = RequestMethod.GET)
	public ResultStatus getUserCards(@PathVariable("userId") String userId) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		List<PanMasterDTO> panInfoList = null;
		int uId;
			try {
				if(userId != null && !userId.equals("")){
					
					uId = usermasterService.findUidByUserId(userId);
				 	panInfoList = panmasterService.findListOfPanByUserId(uId, EzioMobileDemoConstant.CARDMANAGEMENT_GET_CARD_MY_WALLET);
				 	
				 	if(!panInfoList.isEmpty() && panInfoList.size() != 0){
						
						casWebHelper.authenticateCASever();
						List<DCVTokenDTO> dcvPanList = new ArrayList<>();
						
						dcvPanList = cardManagementWebHelper.getPANNumber(userId);
						logger.info("panList : "+dcvPanList.toString());
						
						if(!dcvPanList.isEmpty() && dcvPanList.size() != 0){
							int deviceCountDCV = 1;
							for (int i = 0; i < dcvPanList.size(); i++) {
								
								PanMasterDTO panMasterDTO = new PanMasterDTO();
								panMasterDTO.setAccountNo("-");
								panMasterDTO.setPanNo(dcvPanList.get(i).getDcvCardNo());
								panMasterDTO.setTokenId(dcvPanList.get(i).getDcvTokenId());
								panMasterDTO.setPanType(EzioMobileDemoConstant.PAN_TYPE_GEMALTO_DCV+deviceCountDCV);
								panMasterDTO.setIsDCV_Active(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
								
								panInfoList.add(panMasterDTO);
								deviceCountDCV++;
							}
						}
						
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
	
	
	/** API to freeze and unfreeze user card
	 * @param userId
	 * @param cardNo
	 * @return ResultStatus
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/freezeandunfreezeusercard.action/{userId}/{cardNo}/{isFreeze}", method = RequestMethod.POST)
	public ResultStatus freezeUserCard(@PathVariable("userId") String userId, @PathVariable("cardNo") String cardNo,
			@PathVariable("isFreeze") Boolean isFreeze) throws ControllerException{
		ResultStatus resultStatus = new ResultStatus();
		try {
			int uId = usermasterService.findUidByUserId(userId);
			String statusValue = "";
			if(isFreeze){
				statusValue = EzioMobileDemoConstant.CARD_MANAGEMENT_OFF;
			}else if(!isFreeze){
				statusValue = EzioMobileDemoConstant.CARD_MANAGEMENT_ON;
			}
			
			boolean flag = cardManagementService.updateCardStatusByUserIdAndPanNo(uId, cardNo, statusValue);
			logger.info("IsCardFreezed : "+flag);
			if(flag){
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(flag);
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				resultStatus.setTemplateObject(flag);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to freeze user card!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	
	/**
	 * @param userId
	 * @param cardNo
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getcarddetails.user.action/{userId}/{cardNo}", method = RequestMethod.GET)
	public ResultStatus getUserCardDetails(@PathVariable("userId") String userId, @PathVariable("cardNo") String cardNo) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		PanMasterDTO panMasterDTO = null;
		int uId;
		try {
			uId = usermasterService.findUidByUserId(userId);
			panMasterDTO = panmasterService.findPanInfoByUserIdAndPanNo(uId, cardNo);
			if(panMasterDTO != null){
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(panMasterDTO);
				
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				resultStatus.setTemplateObject(panMasterDTO);
			}
			resultStatus.setTemplateObject(panMasterDTO);
		} catch (ServiceException e) {
			logger.error("Unable to get user card details!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	
	
	/**
	 * @param userId
	 * @param cardNo
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getcardsettings.user.action/{userId}/{cardNo}", method = RequestMethod.GET)
	public ResultStatus getUserCardSettings(@PathVariable("userId") String userId, @PathVariable("cardNo") String cardNo) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		CardManagementDTO cardManagementDTO = null;
		int uId;
		try {
			uId = usermasterService.findUidByUserId(userId);
			cardManagementDTO = cardManagementService.findCardManagementInfoByUserIdAndPanNo(uId, cardNo);
			if(cardManagementDTO != null){
				logger.info("PanMasterDTO : "+cardManagementDTO.toString());
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(cardManagementDTO);
				
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				resultStatus.setTemplateObject(cardManagementDTO);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("Unable to get user card settings!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	

	/**
	 * @param cardManagementDTO
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/updatecardsettings.user.action", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus updateUserCardSettings(@RequestBody CardManagementDTO cardManagementDTO) throws ControllerException{
		ResultStatus resultStatus = new ResultStatus();
		boolean isUpdated = false;
		try {
			
			if(cardManagementDTO.getUserId()!= 0 && cardManagementDTO.getPanNo()!= null && !cardManagementDTO.getPanNo().isEmpty()){
				
				int count = cardManagementService.countByUserIdAndPanNo(cardManagementDTO.getUserId(), cardManagementDTO.getPanNo());
				if(count >0){
					isUpdated = cardManagementService.updateCardManagementInfo(cardManagementDTO);
					logger.info("isUpdated : "+isUpdated);
					if(isUpdated){
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
						resultStatus.setTemplateObject(isUpdated);
					}else {
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_EXPECTATION_FAILED_417);
						resultStatus.setStatusCode(HttpStatus.EXPECTATION_FAILED);
						resultStatus.setTemplateObject(isUpdated);
					}
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
					resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				}
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("Unable to update card settings!");
			throw new ControllerException(e);
		}
		
		return resultStatus;
	}
	
	
	@ExceptionHandler(ControllerException.class)
	public ResultStatus cardManagementMasterErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
	
}
