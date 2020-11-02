package com.gemalto.eziomobile.demo.controller.riskpreferencemaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.UserpreferenceMasterDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.riskpreferencemaster.RiskpreferenceMasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;

@RestController
public class RiskpreferenceMasterController {
	private static final LoggerUtil logger = new LoggerUtil(RiskpreferenceMasterController.class.getClass());
	
	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private RiskpreferenceMasterService riskpreferenceMasterService;

	@RequestMapping(value = "/updatepreferences.riskpreference.user", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus updateRiskPreferenceDetails (@RequestBody UserpreferenceMasterDTO userpreferenceMasterDTO)  throws ControllerException {
		ResultStatus resultStatus = new ResultStatus();
		
		boolean isriskpreferenceUpdated = false;
		int userIdcount = 0;
		int status = userpreferenceMasterDTO.getStatus();
		int userId = userpreferenceMasterDTO.getUserId();
		int securityMode = userpreferenceMasterDTO.getSec_mode();
		try {
			userIdcount = usermasterService.countByStatusAndUserId(status,userId);
			logger.info("print userIdcount :"+ userIdcount);
			isriskpreferenceUpdated = riskpreferenceMasterService.updateRiskPreferenceBySecMode(userId,securityMode);
			logger.info("isUpdated : "+isriskpreferenceUpdated);
			if(isriskpreferenceUpdated){
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(isriskpreferenceUpdated);
			}else {
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_EXPECTATION_FAILED_417);
				resultStatus.setStatusCode(HttpStatus.EXPECTATION_FAILED);
				resultStatus.setTemplateObject(isriskpreferenceUpdated);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("Unable to update risk preference settings!");
			throw new ControllerException(e);
		}
		
		return resultStatus;
	}
	
	@ExceptionHandler(ControllerException.class)
	public ResultStatus riskPreferenceHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
