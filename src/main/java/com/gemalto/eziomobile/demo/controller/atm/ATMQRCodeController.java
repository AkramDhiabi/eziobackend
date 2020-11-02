package com.gemalto.eziomobile.demo.controller.atm;

import java.util.concurrent.Future;

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
import com.gemalto.eziomobile.demo.dto.ATMQRCodeDTO;
import com.gemalto.eziomobile.demo.dto.TransactionInfoDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.atm.AtmQRCodeService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.ATMUtil;
import com.gemalto.eziomobile.demo.util.MspUtil;
import com.gemalto.eziomobile.demo.webhelper.atm.ATMWebHelper;
import com.gemalto.eziomobile.demo.webhelper.transactionmanagement.TransactionManagementWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.QR_CODE_ENCRYPTED;

@RestController
public class ATMQRCodeController {


	@Autowired
	AtmQRCodeService atmQRCodeService;

	@Autowired
	CASWebHelper casWebHelper;
	
	@Autowired
	ATMWebHelper atmWebHelper;


	@Autowired
	private TransactionManagementWebHelper transactionHelper;


	@Autowired
	private TransactionmasterService transactionmasterService;

	@Autowired
	private AccountmasterService accountmasterService;

	@Autowired
	private UsermasterService usermasterService;

	private static final LoggerUtil logger = new LoggerUtil(ATMQRCodeController.class.getClass());

	/**
	 * This API is use to insert atm qr code details in db and generating qr code
	 * 
	 * @param userId
	 * @param amount
	 * @return result status containing response code, http status, message
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/insertqrcodedetails.user/{userId}/{amount}", method = RequestMethod.POST)
	public ResultStatus insertQrCodeDetails (@PathVariable String userId,  @PathVariable String amount) 
			throws ControllerException {

		logger.info("Entered in ATMQRCodeController - insertQrCodeDetails");
		logger.info("userId : "+userId);
		logger.info("amount: "+amount);

		int amt = 0;
		String atmId = String.valueOf(ATMUtil.generateAtmIdForAtm());
		String challenge = String.valueOf(ATMUtil.generateChallengeForAtm());
		
		ATMQRCodeDTO atmQRCodeDetails = new ATMQRCodeDTO();
		ResultStatus resultStatus = new ResultStatus();
		int count = 0;
		int uid = 0;

		try{
			uid = usermasterService.findUidByUserId(userId);
			count = atmQRCodeService.countByUserId(uid);
			
			if (count>0){
					atmQRCodeService.deleteAtmQRCodeDetailsByUserId(uid);
				}
				
			if(amount!= null && !amount.equals("")){
				amt = Integer.valueOf(amount);

				atmQRCodeDetails.setUserId(userId);
				atmQRCodeDetails.setAtmId(atmId);
				atmQRCodeDetails.setChallenge(challenge);
				atmQRCodeDetails.setAmount(amount);

				atmQRCodeService.saveQRCodeDetails(atmQRCodeDetails);

				String atmStringForQR = 
						MspUtil.encryptQRData(userId+","+atmId+","+challenge+","+amt, QR_CODE_ENCRYPTED);

				atmQRCodeDetails.setQrCodeForAtm(atmStringForQR);

				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(atmQRCodeDetails);
			}

		}catch(Exception e){
			logger.error("Exception occurred in ATMQRCodeController - insertQrCodeDetails");
			throw new ControllerException(e);
		}		
		return resultStatus;

	}

	/**
	 * This API is used to validate otp from mobile for atm qr code
	 * 
	 * @param atmQRCodeDTO
	 * @return String
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/validateotp.atm.qrcode.action", method = RequestMethod.POST, consumes = "application/json")
	public String validateAtmQRCode(@RequestBody ATMQRCodeDTO atmQRCodeDTO) 
			throws ControllerException {

		logger.info("[Controller - validateAtmQRCode] atmQRCodeDTO : "+atmQRCodeDTO.toString());
		
		boolean isQrCodeVerified = false;
		int uid = 0;
		int amt = 0;
		boolean isRequestBodyValid = false;
		
		//ATMQRCodeDTO atmQrCodeData = new ATMQRCodeDTO();
		String responseDataToSendBackVerif = "";
		
		try{
				isRequestBodyValid = atmWebHelper.validateRequestBodyForATMQRCode(atmQRCodeDTO);
				
				if(isRequestBodyValid){
					
					ATMQRCodeDTO atmQrCodeData = atmQRCodeService.findAtmQRCodeInfoByUserIdAndAtmIdAndChallenge(atmQRCodeDTO.getUserId(),
							atmQRCodeDTO.getAtmId(), atmQRCodeDTO.getChallenge());
					
					uid = usermasterService.findUidByUserId(atmQRCodeDTO.getUserId());
					
					if(atmQrCodeData != null){
						atmQrCodeData.setOtpValue(atmQRCodeDTO.getOtpValue());
						atmQrCodeData.setFromAccNo(atmQRCodeDTO.getFromAccNo());
						
						logger.info("[Controller - validateAtmQRCode] atmQrCodeDetails: "+atmQrCodeData.toString());
						
						isQrCodeVerified = atmWebHelper.validateAtmQrCode(atmQrCodeData);
						logger.info("[validateAtmQRCode] isQrCodeVerified : "+isQrCodeVerified);

						if(isQrCodeVerified){

							atmQRCodeService.updateAtmQRCodeStatusByUserIdAndAtmId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1,atmQRCodeDTO.getAtmId(),uid);

							String amount = atmQrCodeData.getAmount();

								amt = Integer.valueOf(amount);
								
								TransactionInfoDTO transactionData = new TransactionInfoDTO();

								transactionData = transactionHelper.prepareTransactionDataObject(uid, atmQRCodeDTO.getFromAccNo(),
										EzioMobileDemoConstant.CASH_WITHDRAWAL,
										amt, 0, EzioMobileDemoConstant.CASH_WITHDRAWAL);

								transactionmasterService.saveTransaction(transactionData);
								accountmasterService.updateSenderAccount(amt, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, atmQRCodeDTO.getFromAccNo(),uid);
							
								responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL2 +EzioMobileDemoConstant.STATUS_OK+ CommonOperationsConstants.CLOSE_LABEL;
						
						}else {
							logger.info("Otp value is not validated....");
							atmQRCodeService.updateAtmQRCodeStatusByUserIdAndAtmId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_2, atmQRCodeDTO.getAtmId(), uid);
							responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL2 + EzioMobileDemoConstant.STATUS_NOK + CommonOperationsConstants.CLOSE_LABEL;
						}
					}else{
						logger.info("Couldn't find any data for userId : "+atmQRCodeDTO.getUserId()+"ATM ID : "+atmQRCodeDTO.getAtmId());
						responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL2 + EzioMobileDemoConstant.STATUS_NOK + CommonOperationsConstants.CLOSE_LABEL;
					}
				}else{
					logger.info("Request body for atm qr code is not correct.....");
					responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL2 + EzioMobileDemoConstant.STATUS_NOK + CommonOperationsConstants.CLOSE_LABEL;
				}
		}catch(Exception e){
			logger.error("Exception occurred in ATMQRCodeController - validateAtmQrCode");
			//throw new ControllerException(e);
			responseDataToSendBackVerif = CommonOperationsConstants.DATA_STATUS_LABEL2 + EzioMobileDemoConstant.STATUS_NOK + CommonOperationsConstants.CLOSE_LABEL;
		}
		return responseDataToSendBackVerif;

	}

	/**
	 * This API used to read status present in atmqrcodemaster 
	 * 
	 * @param userId
	 * @param atmId
	 * @return result status containg response code, http status and status 
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/readatmqrcodestatus.user/{userId}/{atmId}", method = RequestMethod.GET)
	public ResultStatus readQRCodeStatus(@PathVariable String userId, @PathVariable String atmId) 
			throws ControllerException {

		int  qrCodeStatus = 0;
		int uid = 0;
		ResultStatus resultStatus = new ResultStatus();

		try{

					uid = usermasterService.findUidByUserId(userId);
					for (int i = 0; i< 60; i++){
					
						Thread.sleep(1000);
						Future<Integer> asyncResponse = atmQRCodeService.findAtmQrCodeStatusByUserIdAndAtmId(uid, atmId);
						qrCodeStatus = asyncResponse.get();
						
						if(qrCodeStatus!=0)
							break;
					}

					if(qrCodeStatus == EzioMobileDemoConstant.EZIO_STATUS_VALUE_1){
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
						resultStatus.setTemplateObject(qrCodeStatus);
					}else if(qrCodeStatus == EzioMobileDemoConstant.EZIO_STATUS_VALUE_2){
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
						resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
						resultStatus.setTemplateObject(qrCodeStatus);
					}else if(qrCodeStatus == EzioMobileDemoConstant.EZIO_STATUS_VALUE_0){
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
						resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
						resultStatus.setTemplateObject(qrCodeStatus);
					}
			logger.info("qr code status: "+qrCodeStatus);
		}catch(Exception e){
			logger.error("Exception occurred in ATMQRCodeController - readQRCodeStatus");
			throw new ControllerException(e);
		}finally {
			try {
				atmQRCodeService.deleteAtmQRCodeDetailsByUserId(uid);
			} catch (ServiceException e) {
				logger.error("Unable to delete ATM QR Code data!");
				e.printStackTrace();
			}
		}

		return resultStatus;

	}

/*	*//**
	 * This API is used to delete details of user present in db
	 * 
	 * @param userId
	 * @return
	 * @throws ControllerException
	 *//*
	@RequestMapping(value = "/deleteatmqrcodedetails.user/{userId}", method = RequestMethod.POST)
	public ResultStatus deleteQRCodeDetails(@PathVariable String userId) 
			throws ControllerException {

		ResultStatus resultStatus = new ResultStatus();
		int uid = 0;
		try{
			logger.info("userId : "+userId);
			uid = usermasterService.findUidByUserId(userId);
			atmQRCodeService.deleteAtmQRCodeDetailsByUserId(uid);
			logger.info("deleted...............");
			resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
			resultStatus.setStatusCode(HttpStatus.OK);

		}catch(Exception e){
			logger.error("Exception occcurred in ATMQRCodeController - deleteQRCodeDetails");
			e.printStackTrace();
			throw new ControllerException(e);
		}
		return resultStatus;
	}*/


	@ExceptionHandler(ControllerException.class)
	public ResultStatus atmErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
