package com.gemalto.eziomobile.demo.controller.cardmanagement;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.webhelper.cardmanagement.CardManagementWebHelper;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.demo.webhelper.tokenmanagement.TokenManagementWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;



@RestController
public class DCVActivationController {
	
	@Autowired
	private CommonWebHelper commonWebHelper;
	
	@Autowired
	private TokenManagementWebHelper tokenManagementWebHelper;
	
	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private CardManagementWebHelper cardManagementWebHelper;
	
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	
	
	private static final LoggerUtil logger = new LoggerUtil(DCVActivationController.class.getClass());
	
	/**GEMALTO DCV card Activation
	 * @param userId
	 * @return ResultStatus with list of cards
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/DCVcard.activation.actions/{userId}/{panNo}", method = RequestMethod.POST)
	public ResultStatus activateDCVCard(@PathVariable("userId") String userId , @PathVariable("panNo") String panNo) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
	//	List<PanMasterDTO> panInfoList = null;
		int uId;
		
		try {
			if(userId != null && !userId.equals("")){
				
				String sPAN = panNo;
				String PSN = CommonOperationsConstants.PSN;
				logger.info("UserId in DCVActivationController: "+ userId);
				logger.info("panNo in DCVActivationController: "+ panNo);
				
				
				uId = usermasterService.findUidByUserId(userId);
			// 	panInfoList = panmasterService.findListOfPanByUserId(uId);
					
					casWebHelper.authenticateCASever();
				/*	List<DCVTokenDTO> dcvPanList = new ArrayList<>();
					
					dcvPanList = cardManagementWebHelper.getPANNumber(userId);
					logger.info("panList : "+dcvPanList.toString());
					*/
			
					
					String sRandom = commonWebHelper.getChallenge(10, 6);
					String sTokenName = cardManagementWebHelper.generatePhysicalDCVDeviceName("00" + sRandom);
					String sSeedName = cardManagementWebHelper.generatePhysicalDCVSeedName("00" + sRandom);
					
						
					int iRes2 = cardManagementWebHelper.createDCVdevice(sPAN,PSN,cardManagementWebHelper.getSharedSecret(),userId,sTokenName,sSeedName, 
																	"2019-07-29 15:51:19.000 +0200");
					
					
					if(iRes2 == CommonOperationsConstants.BAD_LENGTH){
						resultStatus.setMessage(EzioMobileDemoConstant.DCV_ACTIVATION_MSG_TYPE_BAD_LENGTH);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
						resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
					}else if(iRes2 == CommonOperationsConstants.INVALID_PAN_NUMBER){
						resultStatus.setMessage(EzioMobileDemoConstant.DCV_ACTIVATION_MSG_TYPE_INVALID_PAN_NUMBER);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NOT_FOUND_404);
						resultStatus.setStatusCode(HttpStatus.NOT_FOUND);
					}else if(iRes2 == CommonOperationsConstants.CARD_ALREADY_ASSOCIATED){
						resultStatus.setMessage(EzioMobileDemoConstant.DCV_ACTIVATION_MSG_TYPE_CARD_ALREADY_ASSOCIATED);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_FORBIDDEN_403);
						resultStatus.setStatusCode(HttpStatus.FORBIDDEN);
					}else if(iRes2 == CommonOperationsConstants.GENERAL_ERROR){
						resultStatus.setMessage(EzioMobileDemoConstant.DCV_ACTIVATION_MSG_TYPE_GENERAL_ERROR);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NOT_FOUND_404);
						resultStatus.setStatusCode(HttpStatus.NOT_FOUND);
					}else if(iRes2 == CommonOperationsConstants.ALL_OK){
						tokenManagementWebHelper.linkDevices(userId, sTokenName);
						tokenManagementWebHelper.activateDevices(userId, sTokenName);
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
					}else {
						resultStatus.setMessage(EzioMobileDemoConstant.DCV_ACTIVATION_MSG_TYPE_UNKNOWN);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NOT_FOUND_404);
						resultStatus.setStatusCode(HttpStatus.NOT_FOUND);
					}
										
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
					resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				}
			
		}catch (ServiceException e) {
				e.printStackTrace();
				logger.error("Unable to find user cards!");
				throw new ControllerException(e);
		}finally {
			//resultStatus.setTemplateObject(panInfoList);
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

