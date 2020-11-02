package com.gemalto.eziomobile.demo.controller.atm;

import java.util.UUID;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.AtmAccessCodeDTO;
import com.gemalto.eziomobile.demo.dto.TransactionInfoDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.atm.AtmAccessCodeService;
import com.gemalto.eziomobile.demo.service.cardmanagement.CardManagementService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.ATMUtil;
import com.gemalto.eziomobile.demo.webhelper.atm.ATMWebHelper;
import com.gemalto.eziomobile.demo.webhelper.transactionmanagement.TransactionManagementWebHelper;


@RestController
public class ATMAccessCodeController {

	@Autowired
	private AtmAccessCodeService atmAccessCodeService;
	
	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private TransactionManagementWebHelper transactionHelper;
	
	@Autowired
	private TransactionmasterService transactionmasterService;
	
	@Autowired
	private AccountmasterService accountmasterService;
	
	@Autowired
	CardManagementService cardManagementService;
	
	@Autowired
	private ATMWebHelper atmWebHelper;
	
	private static final LoggerUtil logger = new LoggerUtil(ATMAccessCodeController.class.getClass());

	@RequestMapping(value = "/validateaccesscode.user/{userId}/{accessCode}", method = RequestMethod.POST)
	public ResultStatus insertAccessCodeDetails (@PathVariable String userId,  @PathVariable String accessCode) 
			throws ControllerException {
		ResultStatus resultStatus = new ResultStatus();
		
		AtmAccessCodeDTO atmAccessCodeDTO = new AtmAccessCodeDTO();
	
		int accCode = 0;
		int uId = 0;
		logger.info("accessCode: " + accessCode);
		int acc_Code = Integer.parseInt(accessCode); 
		logger.info("acc_Code: " + acc_Code);

		try {
			uId = usermasterService.findUidByUserId(userId);			
			String accesscode = atmAccessCodeService.findAccesscodeByUserId(uId);
			accCode = Integer.parseInt(accesscode); 
			logger.info("Access Code from Db: " + accCode);

			// comparing generated access codes with user entered code,
			if (accCode == acc_Code && accCode != 0) {
				logger.info(" access code matched");
				atmAccessCodeDTO = atmAccessCodeService.findAtmAccessCodeInfoByUserIdAndAccessCode(uId, accessCode);
				String fromAcc = atmAccessCodeDTO.getFromAccountNo();
				int amtValue = atmAccessCodeDTO.getAmount();
				logger.info("fromAcc: " + fromAcc);
				logger.info("amtValue " + amtValue);
				if(!fromAcc.equals("") && amtValue!=0){

				TransactionInfoDTO transactionData = new TransactionInfoDTO();

				transactionData = transactionHelper.prepareTransactionDataObject(uId, fromAcc,
						EzioMobileDemoConstant.CASH_WITHDRAWAL,
						amtValue, 0, EzioMobileDemoConstant.CASH_WITHDRAWAL);

				transactionmasterService.saveTransaction(transactionData);
				accountmasterService.updateSenderAccount(amtValue, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1,fromAcc,uId);
				atmAccessCodeService.deleteATMAccessCodeDetailsByUserId(uId);
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(atmAccessCodeDTO);
				
				}
			} else {
				logger.info("invalidCode");
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				resultStatus.setTemplateObject(atmAccessCodeDTO);
			}

		}catch(Exception e){
			logger.error("Exception occurred in ATMAccessCodeController - validateaccesscode");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	
	/**API to validate OTP and generate 6-digit random Access Code
	 * @param atmAccessCodeDTO
	 * @return String of data needed on mobile
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/validateotp.atm.accesscode.action", method = RequestMethod.POST, consumes = "application/json")
	public String validateATMAccessCodeOTP(@RequestBody AtmAccessCodeDTO atmAccessCodeDTO) throws ControllerException{
		
		logger.info("[validateATMAccessCodeOTP] atmAccessCodeDTO : "+atmAccessCodeDTO.toString());
		
		String responseDataToSendBackVerif = "";
		String trans_id = "";
		boolean isOTPValidated = false;
		boolean isValidBody = false;
		
		try {
			
			isValidBody = atmWebHelper.validateRequestBodyForATMAccessCode(atmAccessCodeDTO);
			logger.info("[validateATMAccessCodeOTP] isValidBody : "+isValidBody);
			
			if(isValidBody){
					
					isOTPValidated = atmWebHelper.isOTPValidatedForATMCashCode(atmAccessCodeDTO.getUserId(), atmAccessCodeDTO.getOtpValue());
					//isOTPValidated = true;
					
					logger.info("[validateATMAccessCodeOTP] isOTPValidated : "+isOTPValidated);
					if(isOTPValidated){
						
						logger.info("ATM : Access code validation is successfull!");
						
						/* insert accesscode in DB */
						int uId = usermasterService.findUidByUserId(atmAccessCodeDTO.getUserId());
						atmAccessCodeDTO.setuId(uId);

						//Generate Access Code
						int accessCode = 0;
						accessCode = ATMUtil.generateAtmAccessCode();
						
						//set generated access code in DTO 
						atmAccessCodeDTO.setAccesscode(String.valueOf(accessCode));
						
						int count = atmAccessCodeService.countByUserId(uId);
						
						if(count>0)
							atmAccessCodeService.deleteATMAccessCodeDetailsByUserId(uId);

						atmAccessCodeService.saveATMCashCodeInfo(atmAccessCodeDTO);
						
						logger.info("ATM : Access code info saved!");
						
						UUID uid = UUID.randomUUID();
						trans_id = uid.toString();
						long epochTime = System.currentTimeMillis() / 1000;
						
						String ATMCode = "ATMCode";

						responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_OK
								+ "\",\"type\":\"" + ATMCode + "\",\"transactionid\":\"" + trans_id
								+ "\",\"etime\":\"" + epochTime + "\",\"AccessCode\":\"" + accessCode
								+ "\"}}";
					}else{
						logger.error("Exception : Couldn't validate the OTP!");
						responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_NOK + "\"}}";
					}
			}
			else{
				logger.error("Exception : Invalida Access code data!");
				responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_NOK + "\"}}";
			}
		} catch (Exception e) {
			logger.error("Exception : Couldn't validate the OTP!");
			//throw new ControllerException(e);
			responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_NOK + "\"}}";
		}
		return responseDataToSendBackVerif;
	}
	
	
	
	@ExceptionHandler(ControllerException.class)
	public ResultStatus atmErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
