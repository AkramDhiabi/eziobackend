package com.gemalto.eziomobile.demo.controller.accountmaster;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.AccountNumberListDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.master.MasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.webhelper.accountmanagement.AccountManagementWebHelper;

@RestController
public class AccountOperationContoller {
	
	private static final LoggerUtil logger = new LoggerUtil(AccountOperationContoller.class.getClass());
	
	@Autowired
	MasterService masterService;
	
	@Autowired
	UsermasterService userMasterService;
	
	@Autowired
	AccountmasterService accountMasterService;
	
	@Autowired
	AccountManagementWebHelper accountWebHelper;
	
	
	
	/** Add new beneficiary, check if already exist for give userId
	 * @param userId
	 * @param payeeAcountNo
	 * @return true, if exist or false, if does not exist
	 * @throws ControllerException
	 */
	@RequestMapping(value = "checkbeneficiary.user.action", method = RequestMethod.GET)
	public ResultStatus checkBeneficiary(@RequestParam("userId") String userId, @RequestParam("payeeAccountNo") String payeeAcountNo)throws ControllerException{
		ResultStatus resultStatus = new ResultStatus();
		
		try {
			int uId = userMasterService.findUidByUserId(userId);
			
			boolean flag = accountMasterService.isPayeeAvailableByUserIdAndAccountNo(uId, payeeAcountNo);
			logger.info("Is Payee : "+flag);
			if(flag){
				resultStatus.setMessage(EzioMobileDemoConstant.BENEFICIARY_AVAILABLE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_CONFLICT_409);
				resultStatus.setStatusCode(HttpStatus.CONFLICT);
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.BENEFICIARY_NOT_AVAILABLE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
			}
			
		} catch (ServiceException e) {
			logger.error("Exception occured : checkbeneficiary !");
			throw new ControllerException(e);
		}
		
		return resultStatus;
	}

	/**
	 * This API is used for delete one or more beneficiary
	 * @param accountList
	 * @return resultstatus
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/deletebeneficiary.user.action", method = RequestMethod.POST)
	public ResultStatus deleteBeneficiary(@RequestBody AccountNumberListDTO accountList)throws ControllerException{
		
		logger.info("Entered into AccountOperationController - deleteBeneficiary ");
		
		int uid  = 0;
		ResultStatus resultStatus = new ResultStatus();
		AccountMasterInfo accountNumberInfoExist = new AccountMasterInfo();
		List<AccountMasterInfo> accountMasterInfoList = new ArrayList<>();
		
		try{
			if(accountList!=null && !accountList.getAccountList().isEmpty()){
				uid  = userMasterService.findUidByUserId(accountList.getUserId());
				
				for(String accountNumber : accountList.getAccountList()){
					
					logger.info("accountNumber : "+accountNumber);
					accountNumberInfoExist = accountMasterService.findAccountByAccountNoAndUserIdAndType(accountNumber, uid, EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1);
					
					logger.info("accountNumberInfoExist : "+accountNumberInfoExist);
					
					accountMasterInfoList.add(accountNumberInfoExist);
				}
					
				//	logger.info("isAccountNumberExist.getAccountNo() : "+isAccountNumberExist.getAccountNo());
					
					if(!accountMasterInfoList.isEmpty()){
						
						accountMasterService.deleteAccountByAccountNumberList(accountMasterInfoList);	
						logger.info("deleted successfully....");
						
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
					}else{
						logger.info("not deleted successfully.........");
						resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
						resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
					}						
				
			}else{
				logger.info("not deleted successfully.........");
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
			}
		}catch(Exception e){
			logger.error("Exception occurred.....not deleted.......");
			throw new ControllerException(e);
		}
		
		return resultStatus;
		
	}
	
	@ExceptionHandler(ControllerException.class)
	public ResultStatus accountMasterOperationErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
