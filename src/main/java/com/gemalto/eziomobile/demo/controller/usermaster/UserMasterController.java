package com.gemalto.eziomobile.demo.controller.usermaster;

import java.util.List;

import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.util.UserRegistrationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.PersonalInformationDTO;
import com.gemalto.eziomobile.demo.dto.UserMasterDTO;
import com.gemalto.eziomobile.demo.dto.UserRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.EzioDemoEmailInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.EzioDemoEmailUtil;

@RestController
public class UserMasterController {

	private static final LoggerUtil logger = new LoggerUtil(UserMasterController.class.getClass());

	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private EzioDemoEmailUtil emailUtil;

	@Autowired
	private URLUtil urlUtil;

	/**
	 * This method will set user role in usermasterinfo based on userId
	 * 
	 * @param userId
	 * @param userRole
	 * @return ResultStatus
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/setuserrole.user.action/{userId}/{userRole}", method = RequestMethod.POST)
	public ResultStatus setUserRole(@PathVariable("userId") String userId, @PathVariable("userRole") String userRole)
			throws ControllerException {

		logger.info("Entered in UserMasterController - setUserRole method");
		ResultStatus resultStatus = new ResultStatus();
		int uId = 0;
		UserMasterInfo usermasterInfo = new UserMasterInfo();
		int update = 0;
		try {
			uId = usermasterService.findUidByUserId(userId);
			logger.info("uId: " + uId);

			if(userRole.length()==3){
				update = usermasterService.updateUserRoleByUid(uId, userRole);
				logger.info("update: " + update);
				update = usermasterService.updateUserRoleByUid(uId, userRole);
				logger.info("update: " + update);

				if (update != 0) {

					usermasterInfo = usermasterService.findUserInfoByUid(uId);
					logger.info("usermasterInfo: " + usermasterInfo);

					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
					resultStatus.setStatusCode(HttpStatus.OK);
					resultStatus.setTemplateObject(usermasterInfo);
				} else {
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
					resultStatus.setTemplateObject(usermasterInfo);
				}
			}else{
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
			}
			

		} catch (Exception e) {
			logger.error("Unable to update usermasterinfo....");
			throw new ControllerException(e);
		}
		return resultStatus;

	}

	/**
	 * This method will fetch user role from usermasterinfo based on userId
	 * 
	 * @param userId
	 * @return ResultStatus
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getuserrole.user.action/{userId}", method = RequestMethod.GET)
	public ResultStatus getUserRole(@PathVariable("userId") String userId) 
			throws ControllerException {
		
		logger.info("Entered in UserMasterController - getUserRole method");
		ResultStatus resultStatus = new ResultStatus();
		int uId = 0;
		String  userRole = null;
		try{
			uId = usermasterService.findUidByUserId(userId);
			logger.info("uId: "+uId);
			
			userRole = usermasterService.findUserRoleByUserId(uId);
			logger.info("userRole: "+userRole);
			
			if(userRole!=null){
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(userRole);
			}else{
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				resultStatus.setTemplateObject(userRole);
			}
						
		}catch(Exception e){
			logger.error("Unable to fetch userRole....");
			throw new ControllerException(e);
		}
				return resultStatus;
	}
	
	
	
	/**
	 * @param requestBodyDTO
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/forgetuserdetails.user.action", consumes = "application/json" , method = RequestMethod.POST)
	public ResultStatus forgetUsernameOrPassword(@RequestBody UserRegistrationDTO requestBodyDTO) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		String emailContent = "";
		String emailAddress = "";
		boolean isEmailSent = false;
		
		logger.info("[forgetuserdetails] emailAddress : "+requestBodyDTO.getEmailAddress());
		
		try {
			if(!(requestBodyDTO.getEmailAddress() != null && !requestBodyDTO.getEmailAddress().isEmpty())) {
				logger.info("[forgetuserdetails] Record not found............6");

				resultStatus.setMessage(EzioMobileDemoConstant.NO_RECORD_FOUND_MSG);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);

				return resultStatus;
			}
				
			emailAddress = requestBodyDTO.getEmailAddress();

			boolean flag = usermasterService.existsByEmailAddress(emailAddress);
			logger.info("[forgetuserdetails] IsEmailValid : "+flag);

			if(!flag) {
				logger.info("[forgetuserdetails] Record not found............5");

				resultStatus.setMessage(EzioMobileDemoConstant.NO_RECORD_FOUND_MSG);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);

				return resultStatus;
			}

			//find email id and send username and password via email
			List<UserMasterDTO> userInfoList = usermasterService.findUserInfoByEmailAddress(emailAddress);
			if(userInfoList.isEmpty()){
				logger.info("[forgetuserdetails] Record not found............4");

				resultStatus.setMessage(EzioMobileDemoConstant.NO_RECORD_FOUND_MSG);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);

				return resultStatus;
			}

			String frontURL = urlUtil.getFrontURL();
			String recoverURL = UserRegistrationUtil.getRecoverUrl(frontURL, emailAddress);
			for (UserMasterDTO userMasterDTO : userInfoList) {
				String recoverKey = UserRegistrationUtil.generateKey();
				userMasterDTO.setRecoverToken(recoverKey);
				usermasterService.updateRecoverTokenByUsername(recoverKey, userMasterDTO.getuId());
			}

			emailContent = emailUtil.prepareEmailContentForForgetDetails(userInfoList, recoverURL);

			EzioDemoEmailInfo emailInfo = new EzioDemoEmailInfo();
			emailInfo.setToEmailAddress(userInfoList.get(0).getEmailAddress());
			emailInfo.setEmailContent(emailContent);
			emailInfo.setEmailSubject(EzioMobileDemoConstant.EMAIL_SUBJECT_SUPPORT_FORGET_USERNAME_PASSWORD);

			//Send email
			isEmailSent = emailUtil.sendMail(emailInfo);
			logger.info("[forgetuserdetails] isEmailSent : "+isEmailSent);

			if(isEmailSent){
				logger.info("[forgetuserdetails] Email sent............1");

				resultStatus.setTemplateObject(isEmailSent);
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);

				logger.info("[forgetuserdetails] Email sent............2");

				return resultStatus;
			}
			logger.info("[forgetuserdetails] Email not sent............3");

			resultStatus.setTemplateObject(isEmailSent);
			resultStatus.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
			resultStatus.setStatusCode(HttpStatus.EXPECTATION_FAILED);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_EXPECTATION_FAILED_417);
		} catch (Exception e) {
			logger.error("Unable to find user data in DB or could'n send email!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	
	
	
	
	
	
	@RequestMapping(value = "/personalInformation.update.user", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus updatePersonalInformation(@RequestBody PersonalInformationDTO updateMyDetailsDTO) throws ControllerException {

		boolean changepwd = false;
		boolean changeEmail = false;
		boolean validatepwd = false;
		String password = updateMyDetailsDTO.getCurrPassword();
		int uId = 0;
		logger.info(" print updateMyDetailsDTO : " + updateMyDetailsDTO.toString());
		ResultStatus resultStatus = new ResultStatus();
		try {
			uId = usermasterService.findUidByUserId(updateMyDetailsDTO.getUsername());
			if(updateMyDetailsDTO.getUpdateField().equals(CommonOperationsConstants.FIELD_TYPE_PASSWORD)){
				resultStatus = updatePasswordByUsername(updateMyDetailsDTO, validatepwd, password, uId);
			}else if(updateMyDetailsDTO.getUpdateField().equals(CommonOperationsConstants.FIELD_TYPE_EMAIL)) {
				resultStatus = updateEmailByUsername(updateMyDetailsDTO, validatepwd, password, uId);
			}else {
				resultStatus = null;
			}
		} catch (Exception e) {
			//		logger.error("Unable to save account information with AccountNo: " + accountMasterInfo.getAccountNo(), e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 *
	 * @param updateMyDetailsDTO
	 * @param validatepwd
	 * @param password
	 * @param uId
	 * @return
	 */
	private ResultStatus updateEmailByUsername(@RequestBody PersonalInformationDTO updateMyDetailsDTO, boolean validatepwd, String password, int uId) throws ServiceException {
		boolean changeEmail;
		ResultStatus resultStatus;
		if(password != null) {
			validatepwd = usermasterService.validatePassword(uId, password);
		}
		if(validatepwd) {
			changeEmail = usermasterService.updateEmailByUsername(uId, updateMyDetailsDTO.getEmailAddress());
			if(changeEmail) {
				resultStatus = setResultStatus(updateMyDetailsDTO, "Email updated successfully", EzioMobileDemoConstant.RESPONSE_CODE_200, HttpStatus.OK);
			}else {
				resultStatus = setResultStatus(updateMyDetailsDTO, "Email updation failed", EzioMobileDemoConstant.RESPONSE_CODE_401, HttpStatus.BAD_REQUEST);
			}
		}else {
			resultStatus = setResultStatus(updateMyDetailsDTO, "Password invalid", EzioMobileDemoConstant.RESPONSE_CODE_401, HttpStatus.BAD_REQUEST);
		}
		return resultStatus;
	}

	/**
	 *
	 * @param updateMyDetailsDTO
	 * @param validatepwd
	 * @param password
	 * @param uId
	 * @return
	 * @throws ServiceException
	 */
	private ResultStatus updatePasswordByUsername(@RequestBody PersonalInformationDTO updateMyDetailsDTO, boolean validatepwd, String password, int uId) throws ServiceException {
		boolean changepwd;
		ResultStatus resultStatus;
		if(password != null) {
			validatepwd = usermasterService.validatePassword(uId, password);
		}
		if(validatepwd) {
			changepwd = usermasterService.updatePasswordByUsername(uId, updateMyDetailsDTO.getNewPassword());
			logger.info("pprint the value of changepwd :" + changepwd);
			if(changepwd) {
				resultStatus = setResultStatus(updateMyDetailsDTO, "Password updated successfully", EzioMobileDemoConstant.RESPONSE_CODE_200, HttpStatus.OK);
			}else {
				resultStatus = setResultStatus(updateMyDetailsDTO, "Password updation failed", EzioMobileDemoConstant.RESPONSE_CODE_401, HttpStatus.BAD_REQUEST);
			}
		}	else {
			resultStatus = setResultStatus(updateMyDetailsDTO, "Password invalid", EzioMobileDemoConstant.RESPONSE_CODE_401, HttpStatus.BAD_REQUEST);
		}
		return resultStatus;
	}

	/**
	 *
	 * @param updateMyDetailsDTO
	 * @param s
	 * @param responseCode
	 * @param httpStatus
	 * @return
	 */
	private ResultStatus setResultStatus(@RequestBody PersonalInformationDTO updateMyDetailsDTO, String s, int responseCode, HttpStatus httpStatus) {
		ResultStatus resultStatus = new ResultStatus();
		resultStatus.setMessage(s);
		resultStatus.setResponseCode(responseCode);
		resultStatus.setStatusCode(httpStatus);
		resultStatus.setTemplateObject(updateMyDetailsDTO);
		return resultStatus;
	}

	/**
	 * Exception handler
	 * 
	 * @return ResultStatus object with response code
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus userMasterErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}

}
