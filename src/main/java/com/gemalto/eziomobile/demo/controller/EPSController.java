package com.gemalto.eziomobile.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.master.MasterService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.service.userpreferencemaster.UserpreferenceMasterService;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.login.LoginWebHelper;

@RestController
public class EPSController {
	
	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private MasterService masterService;
	
	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private LoginWebHelper loginWebHelper;
	
	@Autowired
	private TransactionmasterService transactionmasterService;
	
	@Autowired
	private UserpreferenceMasterService userpreferenceService;
	
	@Autowired
	private AccountmasterService accountmasterService;
	
	private static final LoggerUtil logger = new LoggerUtil(EPSController.class.getClass());
	
	
	@RequestMapping(value = "/reset.useraccount.action/{userId}", method = RequestMethod.POST)
	public ResultStatus getEPSTokenData(@PathVariable("userId") String userId) throws ControllerException {

		ResultStatus resultStatus = new ResultStatus();
	
		try {
			int uId = usermasterService.findUidByUserId(userId);
			
			masterService.deleteDeviceMasterData(uId);
			masterService.deleteAccountsByTypeAndUid(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1, uId);
			masterService.deleteCardManagementDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
			masterService.deletePanMasterDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
			masterService.deleteRiskPreferenceByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, String.valueOf(uId));
			masterService.deleteUserPreferenceByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
			masterService.deleteTransactionsDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
			masterService.deleteSignDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
			
			masterService.resetAccountBalanceByUid(uId);
			
			masterService.createUserPreferenceMasterData(uId);
			masterService.createRiskPreferenceMasterData(uId);
			
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
			resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
			resultStatus.setStatusCode(HttpStatus.OK);
			
		} catch (ServiceException e) {
			
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
			resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
			resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
			
			e.printStackTrace();
		}
		return resultStatus;
	}

	
	
	@RequestMapping(value = "/setup.useraccount.action/{userId}", method = RequestMethod.POST)
	public ResultStatus setUpUserDB(@PathVariable("userId") String userId) throws ControllerException {

		ResultStatus resultStatus = loginWebHelper.setupUserAccountsAndDB(userId);
		logger.info("[AuthenticateUser] resultStatus2 : "+resultStatus.toString());
		
		resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
		resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
		resultStatus.setStatusCode(HttpStatus.OK);
		
		return resultStatus;
	}


}
