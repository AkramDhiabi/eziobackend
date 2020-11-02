package com.gemalto.eziomobile.demo.controller.batchtransfer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.webhelper.batchtransfer.BatchTransferWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

@RestController
public class BatchTransferController {
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private BatchTransferWebHelper batchWebHelper;

	private static final LoggerUtil logger = new LoggerUtil(BatchTransferController.class.getClass());
	
	
	
	/**
	 * @param userId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/batchtransfer.checkdevice.user.action", method = RequestMethod.GET)
	public String checkTokenForUserId(@RequestParam("userId") String userId) throws ControllerException{
		Map<String, String> mapData = new HashMap<>();
		String tokenData = "";
		try {
			casWebHelper.authenticateCASever();
			mapData = batchWebHelper.getListOfTokensByUserId(userId);
			if(mapData != null){
				tokenData = mapData.get("tokenName");
			}else{
				tokenData = EzioMobileDemoConstant.NO_TOKEN_FOUND;
			}
		} catch (Exception e) {
			logger.error("Exception occurred : Unable to perform check token!");
			throw new ControllerException(e);
		}
		return tokenData;
	}
	
	
	/**
	 * @param otp
	 * @param userId
	 * @param dataHex
	 * @param tokenName
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/batchtransfer.validate.payment.action", method = RequestMethod.POST)
	public String validateTransferOTP(@RequestParam("otp") String otp, @RequestParam("userId") String userId, @RequestParam("dataHex") String dataHex,
			@RequestParam("tokenName") String tokenName) throws ControllerException {
		
		String result = "";
		boolean isValidate = false;
		
		if(otp != null && userId != null && dataHex !=null && tokenName != null){
			if(!otp.equals("") && !userId.equals("") && !dataHex.equals("") && !tokenName.equals("")){
				try {
					isValidate = batchWebHelper.validateOTPForBatchTransfer(otp, userId, dataHex, tokenName);
					logger.info("[Batch transfer - OTP validation] isValidate : "+isValidate);
					
					if(isValidate){
						result = EzioMobileDemoConstant.OK_AUTHENT;
					}else{
						result = EzioMobileDemoConstant.ERROR_AUTHENT;
					}
				} catch (Exception e) {
					logger.error("Exception occurred : Unable to validate transfer!");
					throw new ControllerException(e);
				}
			}else{
				result = EzioMobileDemoConstant.ERROR_AUTHENT;
			}
		}else{
			result = EzioMobileDemoConstant.ERROR_AUTHENT;
		}
		return result;
	}
	
	
	
	/**
	 * This method is for handling exception for Batch Transfer
	 * @return errorMessage, HttpStatusCode and RsesponseCode
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus batchTranasferErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.NO_CONTENT);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}

