package com.gemalto.eziomobile.demo.controller.userpreferencemaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.gemalto.eziomobile.demo.model.UserPreferenceInfo;
import com.gemalto.eziomobile.demo.service.groupmaster.GroupmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.service.userpreferencemaster.UserpreferenceMasterService;

@RestController
public class UserPreferenceMasterController {

	private static final LoggerUtil logger = new LoggerUtil(UserPreferenceMasterController.class.getClass());
	
	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private UserpreferenceMasterService userpreferenceMasterService;

	@RequestMapping(value = "/getpreferences.userpreference.user/{userId}", method = RequestMethod.GET)
	public ResultStatus getUserPreferenceDetails (@PathVariable String userId)  throws ControllerException {
		
//		String sec_login = "00";
//		int groupId = 0;
//		String groupName = null;
		int status = 1;
		
		ResultStatus resultStatus = new ResultStatus();
		UserpreferenceMasterDTO userpreferenceMasterDTO = null;
		int uId;
		try {
			uId = usermasterService.findUidByUserId(userId);
			
			//find sender's groupID and groupName  -- previous demo p2p was set based on groupname in present web app it will be based on user permission hence commenting out
//			groupId = usermasterService.getUserGroupIdByUid(uId);
//			groupName = groupService.findGroupNameByGroupId(groupId);
//			logger.info("Sender Group Name : " + groupName);
			
			userpreferenceMasterDTO = userpreferenceMasterService.findUserPreferenceInfoByUserIdAndStatus(uId, status);
			if(userpreferenceMasterDTO != null){
				logger.info("userpreferenceMasterDTO : "+userpreferenceMasterDTO.toString());
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(userpreferenceMasterDTO);
				
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				resultStatus.setTemplateObject(userpreferenceMasterDTO);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("Unable to get user preference settings!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	
	@RequestMapping(value = "/updatepreferences.userpreference.user", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus updateUserPreferenceDetails (@RequestBody UserpreferenceMasterDTO userpreferenceMasterDTO)  throws ControllerException {
		ResultStatus resultStatus = new ResultStatus();
		
		boolean isuserpreferenceUpdated = false;
		try {
			isuserpreferenceUpdated = userpreferenceMasterService.updateUserPreferenceInfo(userpreferenceMasterDTO);
			logger.info("isuserpreferenceUpdated : "+isuserpreferenceUpdated);
			if(isuserpreferenceUpdated){
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(isuserpreferenceUpdated);
			}else {
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_EXPECTATION_FAILED_417);
				resultStatus.setStatusCode(HttpStatus.EXPECTATION_FAILED);
				resultStatus.setTemplateObject(isuserpreferenceUpdated);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("Unable to update user preference settings!");
			throw new ControllerException(e);
		}
		
		return resultStatus;
	}
	
	@ExceptionHandler(ControllerException.class)
	public ResultStatus userPreferenceHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
