package com.gemalto.eziomobile.demo.controller.userregistration;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.UserRegistrationDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.service.userregistration.UserRegistrationService;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.ACTIVATION_ERROR_ATTR;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.RESULT_STATUS_ATTR;

@Controller
public class UserRegistrationOperationController {


	private static final LoggerUtil logger = new LoggerUtil(UserRegistrationController.class.getClass());
	public static final String ACTIVATION = "activation";


	@Autowired
	private UserRegistrationService userRegistrationService;
	
	@Autowired
	private UsermasterService userMasterService;
	
	
	/**
	 * @param userId
	 * @param key
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/activateaccount.user.action", method = RequestMethod.GET)
	public String activateUserAccount(@RequestParam String userId, @RequestParam String key, Model model) throws ControllerException{
		ResultStatus resultStatus = new ResultStatus();
		boolean flag = false;
		try {
			if (!(userId != null && !userId.equals("") && key != null && !key.equals(""))) {
				return ACTIVATION_ERROR_ATTR;
			}

			//check if key is valid,
			//Key is valid for 3 days only
			flag = userRegistrationService.isActivationKeyValid(userId, key);
			logger.info("Is Activation Key Valid : "+flag);

			//if valid key, update UserRegistration status = 1
			if(!flag){
				return ACTIVATION_ERROR_ATTR;
			}

			flag = userRegistrationService.updateStatusByUsernameAndActivationKey(userId, key);
			logger.info("Is Status update [UserRegistrationInfo] : "+flag);

			if(!flag){
				return ACTIVATION_ERROR_ATTR;
			}

			UserRegistrationDTO userRegistrationDTO = userRegistrationService.findUserRegistrationInfoByUsernameAndActivationKey(userId, key);

			//Update userMasterInfo with new user details
			flag = userMasterService.saveUserInfoForNewUser(userRegistrationDTO);
			if(flag){
				return ACTIVATION;
			}

			return ACTIVATION_ERROR_ATTR;
		} catch (Exception e) {
			logger.error("Internal error: " + e);
			throw new ControllerException(e);
		}
	}
}
