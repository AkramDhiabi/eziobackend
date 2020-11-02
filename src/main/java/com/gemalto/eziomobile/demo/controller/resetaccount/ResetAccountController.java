package com.gemalto.eziomobile.demo.controller.resetaccount;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.ResetUserAccountDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.JsonToMapConvertUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

@RestController
public class ResetAccountController {
	
	@Autowired
	private CommonWebHelper commonWebHelper;
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private UsermasterService usermasterService;
	
	private static final LoggerUtil logger = new LoggerUtil(ResetAccountController.class.getClass());
	
	
	/** API to get user rest account options based on userId and userRoles
	 * @param userId
	 * @param userRole
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getuserresetaccount.options.action", method = RequestMethod.GET)
	public ResultStatus getUserRestAccountOptions(@RequestParam("userId") String userId) throws ControllerException {

		logger.info(" userId : "+userId);
		
		ResultStatus resultStatus = new ResultStatus();
		Map<String, Object> resMap = new HashMap<>();
		
		int uId;
		String userRole = "";
			
			try {
				
				if(userId != null && !userId.equals("")){
				
					uId = usermasterService.findUidByUserId(userId);
					userRole = usermasterService.findUserRoleByUserId(uId);
					
					logger.info("[getUserRestAccountOptions] userRole : "+userRole);
					
					casWebHelper.authenticateCASever();
					JSONObject resJSON_Obj = commonWebHelper.getResetMyAccountOptions(userId, userRole);
					
					if(resJSON_Obj != null){
						 resMap = JsonToMapConvertUtil.jsonToMap(resJSON_Obj);
						 logger.info("ResetController - [getUserRestAccountOptions] resMap : "+resMap.toString());
						 
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
						resultStatus.setTemplateObject(resMap);
					}else{
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
						resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
						resultStatus.setTemplateObject(resMap);
					}
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
					resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				}
				
			} catch (ServiceException | JSONException e) {
				e.printStackTrace();
				logger.error("Exception : Couldn't find data for reset account options! ");
				throw new ControllerException(e);
			}
			
			
		return resultStatus;
	}
	
	
	/** API to reset user account based on request body
	 * @param userId
	 * @param resetUserAccountDTO
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/resetuseraccount.action", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus resetUserAccounts(@RequestBody ResetUserAccountDTO resetUserAccountDTO) throws ControllerException {
		ResultStatus resultStatus = new ResultStatus();
		
		JSONObject resJSON_Obj = new JSONObject();
		
		String userId = "";
		boolean isMobile = false;
		boolean isDCVCards = false;
		boolean isPhysicalTokens =false ;
		boolean isRiskManagement = false;
		boolean isDemoData = false;
		
		logger.info("resetUserAccountDTO : "+resetUserAccountDTO.toString());
		try {
			if(resetUserAccountDTO != null){
				
				userId = resetUserAccountDTO.getUserId();
				isMobile = resetUserAccountDTO.isMobile();
				isDCVCards = resetUserAccountDTO.isDCVCards();
				isPhysicalTokens = resetUserAccountDTO.isPhysicalTokens();
				isRiskManagement = resetUserAccountDTO.isRiskManagement();
				isDemoData = resetUserAccountDTO.isDemoData();
				
				resJSON_Obj.put("userId", userId);
				resJSON_Obj.put("mobile", isMobile);
				resJSON_Obj.put("dcvcards", isDCVCards);
				resJSON_Obj.put("physicalTokens", isPhysicalTokens);
				resJSON_Obj.put("riskManagement", isRiskManagement);
				resJSON_Obj.put("demoData", isDemoData);
				
				casWebHelper.authenticateCASever();
				int statusValue = commonWebHelper.resetMyAccount(userId, resJSON_Obj);
				
				logger.info("Controller - [resetUserAccounts] statusValue : "+statusValue);
				logger.info("Controller - [resetUserAccounts] CommonOperationsConstants.ALL_OK : "+CommonOperationsConstants.ALL_OK);

				if(statusValue == CommonOperationsConstants.ALL_OK){
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
					resultStatus.setStatusCode(HttpStatus.OK);
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				}
			}
		} catch (Exception e) {
			logger.error("Exception : Unable to reset user account! ");
			e.printStackTrace();
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	
	/**
	 * @return
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus resetUserAccountErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}

}
