package com.gemalto.eziomobile.demo.controller.mobileregistration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.MobileRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.DeviceMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.devicemaster.DevicemasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.MspUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.eps.EPSWebHelper;
import com.gemalto.eziomobile.webhelper.oobs.OOBSWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.QR_CODE_ENCRYPTED;

@RestController
public class MobileRegistrationController {

	@Autowired
	private EPSWebHelper epsWebHelper;

	@Autowired
	private OOBSWebHelper oobsWebHelper;

	@Autowired
	private DevicemasterService devicemasterService;

	@Autowired
	private UsermasterService usermasterService;

	@Autowired
	private URLUtil urlUtil;

	/*@Autowired
	private MspUtil mspUtil;*/

	private static final LoggerUtil logger = new LoggerUtil(MobileRegistrationController.class.getClass());



	/** This REST will be hit from Web App to Enroll GAOC device
	 * @param userId
	 * @return ResultStatus with ResponseCode, messsage and Encrypted QR code data
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/mobileregistration.web.action/{userId}", method = RequestMethod.POST)
	public ResultStatus enrollMobileStepOne(@PathVariable("userId") String userId) throws ControllerException {

		ResultStatus resultStatus = new ResultStatus();
		MobileRegistrationDTO mobileRegistrationDTO = new MobileRegistrationDTO();
		String eps_registrationCode = "";
		String eps_pin = "";
		String eps_tokenId = "";
		String qrCodeData = "";
		String jspURL = "";
		boolean isMobileEnrolledOnOOBS = false;


		try {
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[enrollMobileStepOne] backendConfiguration : "+backendConfiguration);
			
			int uId = usermasterService.findUidByUserId(userId);
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
				// 1. Check EPS DB, number of enrollment
				jspURL = urlUtil.getAndUpdateTokenCount();
				logger.info("jspURL : "+jspURL);

				RestTemplate restTemplate = new RestTemplate();
				// Build URL
				StringBuilder url = new StringBuilder().append(jspURL).append("?userId=" + userId);

				// Call service
				String result = restTemplate.getForObject(url.toString(), String.class);
				logger.info("[HSQL DB call response - Web call] result : "+result);
			break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				break;
			default:
				break;
			}

			// 2. Enroll Mobile on EPS server, Enroll EPS (GAOC) -> get reg code
			// userID and PIN and store them in devicemaster
			Map<String, String> epsData = epsWebHelper.doMobileEPSEnrollmentStepOne(userId);
			if (epsData != null) {

				logger.info("[enrollMobileStepOne] epsData : "+epsData);

				eps_registrationCode = epsData.get("registrationCode");
				eps_pin = epsData.get("pin");
				eps_tokenId = epsData.get("tokenId");

			// 3. Register mobile on OOBS Server, Enroll OOBS with EPS reg
			// code
			isMobileEnrolledOnOOBS = oobsWebHelper.isMobileRegistrationDoneOnOOBS(userId, eps_registrationCode);
			if (isMobileEnrolledOnOOBS) {

				try {
					logger.info("[MobileRegistrationController] mspUtil.QR_CODE_ENCRYPTED : " + QR_CODE_ENCRYPTED);
					qrCodeData = MspUtil.encryptQRData(userId + "," + eps_registrationCode, QR_CODE_ENCRYPTED);
					logger.info("[MobileRegistrationController] qrCodeData : "+qrCodeData);

					mobileRegistrationDTO.setPin(eps_pin);
					mobileRegistrationDTO.setRegCode(eps_registrationCode);
					mobileRegistrationDTO.setTokenId(eps_tokenId);
					mobileRegistrationDTO.setQrCodeData(qrCodeData);
					
					DeviceMasterInfo deviceMasterInfo = new DeviceMasterInfo();
					deviceMasterInfo.setUserId(uId);
					deviceMasterInfo.setRegCode(eps_registrationCode);
					deviceMasterInfo.setPin(eps_pin);
					deviceMasterInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);

					devicemasterService.saveDeviceInfo(deviceMasterInfo);
					logger.info("Saved in device master............");

				} catch (IOException | GeneralSecurityException e) {
					logger.error("encryptQRData Exception!"+e);
					throw new ControllerException(e);
				}
				resultStatus.setMessage(EzioMobileDemoConstant.MOBILE_REGISTRATION_DONE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				resultStatus.setTemplateObject(mobileRegistrationDTO);
			} else {
				resultStatus.setMessage(EzioMobileDemoConstant.MOBILE_REGISTRATION_MOBILE_NOT_REGISTERED_ON_OOBS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				resultStatus.setTemplateObject(mobileRegistrationDTO);
			}
		} else {
			resultStatus.setMessage(EzioMobileDemoConstant.MOBILE_REGISTRATION_MOBILE_ENROLLMENT_ERROR);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
			resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
			resultStatus.setTemplateObject(mobileRegistrationDTO);
		}
	} catch (ServiceException e) {
		logger.error("[MobileRegistrationController] enrollMobile, Exception!");
		throw new ControllerException(e);
	}
	return resultStatus;
}



/** This is step two, after scanning QR code from Web app,
 * and after successful response, this API will be hit by mobile to
 * Enroll GALO device/tokens 
 * @@param registrationCode
 * @param userId
 * @return will be String with user RegistrationCode, PIN and TokenID for the particular User.
 * @throws ControllerException
 */
@RequestMapping(value = "/mobileregistration.mobile.action/{registrationCode}/{userId}", method = RequestMethod.POST)
public String enrollMobileStepTwo(@PathVariable("registrationCode") String registrationCode, @PathVariable("userId") String userId) throws ControllerException {

	String responseDataToSendBack = "";
	String jspURL = "";
	try {
		String backendConfiguration = urlUtil.getBackendConfiguration();
		logger.info("[enrollMobileStepTwo] backendConfiguration : "+backendConfiguration);
		
		switch (backendConfiguration) {
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
			// 1. Check EPS DB, number of enrollment
			jspURL = urlUtil.getAndUpdateTokenCount();
			logger.info("jspURL : "+jspURL);

			RestTemplate restTemplate = new RestTemplate();
			// Build URL
			StringBuilder url = new StringBuilder().append(jspURL).append("?userId=" + userId);

			// Call service
			String result = restTemplate.getForObject(url.toString(), String.class);
			logger.info("[HSQL DB call response - Mobile call] result : "+result);
		break;
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
			break;
		default:
			break;
		}
		// 2. Enroll GALO seed with DBPIN 
		int uId = usermasterService.findUidByUserId(userId);
		String uPIN = devicemasterService.findPinByUserIdAndStatusAndRegCode(uId, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, registrationCode);
		if(uPIN != null && !uPIN.equals("")){
			responseDataToSendBack = epsWebHelper.doMobileEnrollmentStepTwo(userId, uPIN);
		}else{
			responseDataToSendBack = EzioMobileDemoConstant.STR_RESPONSE_CODE_401;
		}
		logger.info("[MobileRegistrationController - mobile] responseDataToSendBack : "+responseDataToSendBack);
	} catch (Exception e) {
		logger.error("[EPSController] enrollMobile, Exception!");
		throw new ControllerException(e);
	}
	return responseDataToSendBack;

}

/**
 * Exception handler
 * 
 * @return ResultStatus object with response code
 */
@ExceptionHandler(ControllerException.class)
public ResultStatus mobileRegistrationErrorHandler() {
	ResultStatus status = new ResultStatus();
	status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
	status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
	status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
	return status;
}

}
