package com.gemalto.eziomobile.demo.controller.accountmaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.AccountMasterDTO;
import com.gemalto.eziomobile.demo.dto.TransactionInfoDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.master.MasterService;
import com.gemalto.eziomobile.demo.service.panmaster.PanmasterService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;
import com.gemalto.eziomobile.demo.util.JsonToMapConvertUtil;
import com.gemalto.eziomobile.demo.webhelper.accountmanagement.AccountManagementWebHelper;

@RestController
public class AccountMasterController {

	private static final LoggerUtil logger = new LoggerUtil(AccountMasterController.class.getClass());

	@Autowired
	private MasterService masterService;

	@Autowired
	private UsermasterService userMasterService;

	@Autowired
	private AccountmasterService accountMasterService;

	@Autowired
	private TransactionmasterService transactionService;

	@Autowired
	private AccountManagementWebHelper accountWebHelper;

	@Autowired
	private PanmasterService panService;

	/**
	 * This REST is to get list of account based on userId (user_uid)
	 * 
	 * @param userId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getaccouts.user", method = RequestMethod.GET)
	public ResultStatus getUserAccounts(@RequestParam String userId) throws ControllerException {

		logger.info(CommonOperationsConstants.USER_ID_LABEL + userId);
		ResultStatus resultStatus = new ResultStatus();
		List<AccountMasterInfo> accountsList = new ArrayList<AccountMasterInfo>();

		try {
			int uId = userMasterService.findUidByUserId(userId);
			accountsList = masterService.findAccountByUserId(uId);

			if (accountsList != null && accountsList.size() != 0) {
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(accountsList);
			} else {
				resultStatus.setTemplateObject(accountsList);
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			logger.info("Unable find data for userId : " + userId, e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 * This REST is to get list of account based on userId (user_uid), will club
	 * Credit card accounts in single object and savings and current account in
	 * one object.
	 * 
	 * @param userId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getaccounts.user.clubed", method = RequestMethod.GET)
	public ResultStatus getUserAccountsClubed(@RequestParam String userId) throws ControllerException {

		logger.info(CommonOperationsConstants.USER_ID_LABEL + userId);

		int uId = 0;
		List<AccountMasterInfo> accountsList_Card = new ArrayList<>();
		List<AccountMasterInfo> accountsList_Savings = new ArrayList<>();

		List<AccountMasterDTO> creditAccountList = new ArrayList<>();
		List<AccountMasterDTO> debitAccountList = new ArrayList<>();

		JSONObject mainObj = new JSONObject();
		ResultStatus resultStatus = new ResultStatus();

		Map<String, Object> resMap = new HashMap<>();

		try {
			uId = userMasterService.findUidByUserId(userId);
			accountsList_Card = accountMasterService.findAccountByUserIdAndAccountNameOrderByAccountNameAsc(uId,
					EzioMobileDemoConstant.ACCOUNT_TYPE_CARD);
			accountsList_Savings = accountMasterService.findAccountByUserIdAndAccountNameOrderByAccountNameAsc(uId,
					EzioMobileDemoConstant.ACCOUNT_TYPE_SAVINGS);

			if (accountsList_Card.size() != 0 && accountsList_Savings.size() != 0) {

				creditAccountList = accountWebHelper.prepareAccountList(accountsList_Card);
				debitAccountList = accountWebHelper.prepareAccountList(accountsList_Savings);

				logger.info(" accountsList by UId and accountName : CREDIT : " + creditAccountList.toString());
				logger.info(" accountsList by UId and accountName : SAVINGS :" + debitAccountList.toString());

				JSONObject cardJSON = new JSONObject();
				JSONObject savingOrCurrentJSON = new JSONObject();

				cardJSON.put("atype", EzioMobileDemoConstant.ACCOUNT_TYPE_PAY_CREDIT_CARD);
				cardJSON.put("details", creditAccountList);

				savingOrCurrentJSON.put("atype", EzioMobileDemoConstant.ACCOUNT_TYPE_CURRENT_OR_SAVINGS);
				savingOrCurrentJSON.put("details", debitAccountList);

				JSONArray jsonArray = new JSONArray();
				jsonArray.put(cardJSON);
				jsonArray.put(savingOrCurrentJSON);

				mainObj.put("uid", uId);
				mainObj.put("accountdetails", jsonArray);

				logger.info("Main JSON : " + mainObj.toString());

				resMap = JsonToMapConvertUtil.jsonToMap(mainObj);

				resultStatus.setTemplateObject(resMap);
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
			} else {
				resultStatus.setTemplateObject(resMap);
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			logger.error("Exception : Something went wrong! Couldn't find the account details.", e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 * This REST is to get transactions history based on UseriD(user_uid) and
	 * accountNo
	 * 
	 * @param userId
	 * @param accountNo
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getaccountsummary.user", method = RequestMethod.GET)
	public ResultStatus getUserAccountSummary(@RequestParam String userId, @RequestParam String accountNo)
			throws ControllerException {

		logger.info("userUId : " + userId);
		logger.info("accountNo : " + accountNo);
		ResultStatus resultStatus = new ResultStatus();
		int uId = 0;
		List<TransactionInfoDTO> transactionsList = new ArrayList<TransactionInfoDTO>();
		try {
			uId = userMasterService.findUidByUserId(userId);
			transactionsList = transactionService.findTop20TransactionsByUserIdAndFromAccountNoAndStatus(uId, accountNo,
					EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
			if (transactionsList != null && transactionsList.size() != 0) {

				AccountMasterDTO accountMasterDTO = accountMasterService.findAccountByAccountNoAndUserId(accountNo,
						uId);

				JSONObject mainObj = new JSONObject();
				Map<String, Object> resMap = new HashMap<>();

				mainObj.put("accountInfo", accountMasterDTO);
				mainObj.put("currentDate", ConvertDateToStringDate.getCurrentLocalDateTimeStamp());
				mainObj.put("transactionsData", transactionsList);

				logger.info("Main JSON : " + mainObj.toString());

				resMap = JsonToMapConvertUtil.jsonToMap(mainObj);

				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(resMap);
			} else {
				resultStatus.setTemplateObject(transactionsList);
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			logger.error("Unable to fecth account summary for account no : " + accountNo + " and userId : " + uId, e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 * API to get transaction history of CARD (VISA/MASTERCARD)
	 * 
	 * @param userId
	 * @param cardNo
	 * @param noOfTransactions
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/transactionhistory.card.action/{userId}/{cardNo}/{noOfTransactions}", method = RequestMethod.GET)
	public ResultStatus getTransactionHistoryOfAccount(@PathVariable("userId") String userId,
			@PathVariable("cardNo") String cardNo, @PathVariable("noOfTransactions") int noOfTransactions)
			throws ControllerException {

		ResultStatus resultStatus = new ResultStatus();
		JSONObject mainObj = new JSONObject();
		Map<String, Object> resMap = new HashMap<>();

		int uId;
		try {
			uId = userMasterService.findUidByUserId(userId);
			String accountNo = panService.findAccountNoByPanNoAndUserId(cardNo, uId);
			List<TransactionInfoDTO> transactionsList = transactionService.findTopNTransactionsForCard(uId, accountNo,
					noOfTransactions, cardNo);
			logger.info("list : " + transactionsList.toString());

			if (!transactionsList.isEmpty() && transactionsList.size() != 0) {

				AccountMasterDTO accountMasterDTO = accountMasterService.findAccountByAccountNoAndUserId(accountNo,
						uId);

				mainObj.put("accountInfo", accountMasterDTO);
				mainObj.put("currentDate", ConvertDateToStringDate.getCurrentLocalDateTimeStamp());
				mainObj.put("transactionsData", transactionsList);

				logger.info("[getTransactionHistoryOfAccount] Main JSON : " + mainObj.toString());

				resMap = JsonToMapConvertUtil.jsonToMap(mainObj);

				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
				resultStatus.setTemplateObject(resMap);
			} else {
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				resultStatus.setTemplateObject(resMap);
			}

		} catch (ServiceException | JSONException e) {
			logger.error("Unable to find transaction history for cardNo: " + cardNo, e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 * REST call, to save account information
	 * 
	 * @param accountMasterInfo
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/saveaccountinfo", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus saveUserAccount(@RequestBody final AccountMasterInfo accountMasterInfo)
			throws ControllerException {

		logger.info("userUId : " + accountMasterInfo.getAccountName());
		ResultStatus resultStatus = new ResultStatus();
		try {
			accountMasterService.saveUserAccountInfo(accountMasterInfo);

			resultStatus.setMessage("account has been saved successfully");
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
			resultStatus.setStatusCode(HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Unable to save account information with AccountNo: " + accountMasterInfo.getAccountNo(), e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}


	/**
	 * This REST is to get list of account based on userId (user_uid) and
	 * operationType
	 *
     * @param operationType
	 * @param userId
	 *            , operationType
	 * @return
	 * @throws ControllerException
	 */

	@RequestMapping(value = "/getuseraccountdetails.user.action", method = RequestMethod.GET)
	public ResultStatus getUserAccountsBasedOnType(@RequestParam String operationType, @RequestParam String userId)
			throws ControllerException {

		logger.info(CommonOperationsConstants.USER_ID_LABEL + userId);
		logger.info("operationType : " + operationType);

		ResultStatus resultStatus = new ResultStatus();
		int accType = 0;
		List<AccountMasterInfo> toAccountList = new ArrayList<AccountMasterInfo>();
		List<AccountMasterInfo> fromAccountList = new ArrayList<AccountMasterInfo>();
		Map<String, List<AccountMasterInfo>> accountMap = new HashMap<>();
		try {
			int uId = userMasterService.findUidByUserId(userId);
			if (operationType.equalsIgnoreCase(EzioMobileDemoConstant.ACCOUNT_TRANSFER_TYPE_DOMESTIC)) {
				accType = EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0;
			} else if (operationType.equalsIgnoreCase(EzioMobileDemoConstant.ACCOUNT_TRANSFER_TYPE_EXTERNAL)) {
				accType = EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1;
			}

			fromAccountList = accountMasterService.findAccountByTypeAndStatusAndUserIdOrderByAccountNameAsc(
					EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
			toAccountList = accountMasterService.findAccountByTypeAndStatusAndUserIdOrderByAccountNameAsc(accType,
					EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);

			accountMap.put("toAccountList", toAccountList);
			accountMap.put("fromAccountList", fromAccountList);

			logger.info("toAccountList: " + toAccountList.toString());
			logger.info("fromAccountList: " + fromAccountList.toString());
			logger.info("accountMap: " + accountMap.toString());

			if (!accountMap.isEmpty()) {
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setTemplateObject(accountMap);
			} else {
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
			}
		} catch (Exception e) {
			logger.info("Unable to get the details!", e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	@ExceptionHandler(ControllerException.class)
	public ResultStatus accountMasterErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}

}
