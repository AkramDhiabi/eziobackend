package com.gemalto.eziomobile.demo.controller.userregistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.UserRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.EzioDemoEmailInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.service.userregistration.UserRegistrationService;
import com.gemalto.eziomobile.demo.util.EzioDemoEmailUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.util.UserRegistrationUtil;

@RestController
public class UserRegistrationController {

	private static final LoggerUtil logger = new LoggerUtil(UserRegistrationController.class);
	private static final String MSG_TIRETS = "-------------------------\n";

	@Autowired
	private UserRegistrationService userRegistrationService;
	
	@Autowired
	private UsermasterService userMasterService;
	
	@Autowired
	private EzioDemoEmailUtil emailUtil;
	
	@Autowired
	private URLUtil urlUtil;
	
	
	/**
	 * @param userId user id
	 * @return resultStatus
	 * @throws ControllerException
	 */
	@GetMapping(value = "/checkusername.userregistration.action")
	public ResultStatus isUsernameExist(@RequestParam String userId) throws ControllerException{
		ResultStatus resultStatus = new ResultStatus();
		int userMasterCount;
		int userRegCount;
		
		boolean isUserTaken = false;
		try {
			 userMasterCount = userMasterService.countByUserId(userId);
			 logger.info("userMasterCount : "+userMasterCount);
			 
			 userRegCount = userRegistrationService.countByUsername(userId);
			 logger.info("userRegCount : "+userRegCount);
			 
			 if(userMasterCount > 0 || userRegCount > 0)
				 isUserTaken = true;
			 
			 if (!isUserTaken) {
				resultStatus.setMessage(EzioMobileDemoConstant.USER_REGISTRATION_USER_AVAILABLE_MSG);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(isUserTaken);
			} else {
				resultStatus.setMessage(EzioMobileDemoConstant.USER_REGISTRATION_USER_EXIST_MSG);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_CONFLICT_409);
				resultStatus.setStatusCode(HttpStatus.CONFLICT);
				resultStatus.setTemplateObject(isUserTaken);
			}
			 
		} catch (Exception e) {
			logger.error("Unable to process new user account request!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	
	/**
	 * @param userRegistrationDTO
	 * @return resultStatus
	 * @throws ControllerException
	 */
	@PostMapping(value = "/createaccount.user.action", consumes = "application/json")
	public ResultStatus createUserAccount(@RequestBody UserRegistrationDTO userRegistrationDTO) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		int userMasterCount;
		int userRegCount;
		boolean isUserTaken = false;
		
		try {
			//Validate UserRegistrationDTO
			if(userRegistrationDTO.getEmailAddress()!=null && userRegistrationDTO.getUsername()!=null &&
					userRegistrationDTO.getPassword() != null){
				
				//Check if user is already taken
				 userMasterCount = userMasterService.countByUserId(userRegistrationDTO.getUsername());
				 logger.info("[createaccount] userMasterCount : "+userMasterCount);
				 
				 userRegCount = userRegistrationService.countByUsername(userRegistrationDTO.getUsername());
				 logger.info("[createaccount] userRegCount : "+userRegCount);
				 
				 if(userMasterCount > 0 || userRegCount > 0)
					 isUserTaken = true;
				
				 if(!isUserTaken){
					 UserRegistrationDTO savedUserInfo = userRegistrationService.createUserAccount(userRegistrationDTO);
						
						String rootURL = urlUtil.getRootURL();
						logger.info("rootURL : "+rootURL);
						logger.info(MSG_TIRETS);
						
						String activationURL = UserRegistrationUtil.getActivationURL(rootURL, savedUserInfo.getUsername(), savedUserInfo.getActivationKey());
						logger.info("activationURL : "+activationURL);
						logger.info(MSG_TIRETS);
						
						StringBuilder emailContent = emailUtil.prepareEmailContentForNewAccount(savedUserInfo.getUsername(), savedUserInfo.getPassword(), activationURL);
						logger.info("emailContent : "+emailContent);
						logger.info(MSG_TIRETS);
						
						EzioDemoEmailInfo emailInfo = new EzioDemoEmailInfo();
						emailInfo.setToEmailAddress(savedUserInfo.getEmailAddress());
						emailInfo.setEmailContent(emailContent.toString());
						emailInfo.setEmailSubject(EzioMobileDemoConstant.EMAIL_SUBJECT_SUPPORT_NEW_ACCOUNT);
						
						boolean isEmailSent = emailUtil.sendMail(emailInfo);
						//boolean isEmailSent = SendEmail.send(savedUserInfo.getEmailAddress(), EzioMobileDemoConstant.EMAIL_SUBJECT_SUPPORT_NEW_ACCOUNT, emailContent);
						
						logger.info("isEmailSent : "+isEmailSent);
						logger.info(MSG_TIRETS);
						
						if(isEmailSent){
							resultStatus.setTempObject(isEmailSent);
							resultStatus.setTemplateObject(savedUserInfo.getEmailAddress());
							resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
							resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
							resultStatus.setStatusCode(HttpStatus.OK);
						}else{
							resultStatus.setTempObject(isEmailSent);
							resultStatus.setTemplateObject(savedUserInfo.getEmailAddress());
							resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
							resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
							resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
						}
				 }else{
					 resultStatus.setMessage(EzioMobileDemoConstant.USER_REGISTRATION_USER_EXIST_MSG);
					 resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_CONFLICT_409);
					 resultStatus.setStatusCode(HttpStatus.CONFLICT);
				 }
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.error("Unable to process new user account request!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	/**
	 * @return status
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus userRegistrationErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
