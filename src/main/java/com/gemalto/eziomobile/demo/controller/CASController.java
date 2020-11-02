package com.gemalto.eziomobile.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.CardManagementDTO;
import com.gemalto.eziomobile.demo.dto.TransactionInfoDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.cardmanagement.CardManagementService;
import com.gemalto.eziomobile.demo.service.devicemaster.DevicemasterService;
import com.gemalto.eziomobile.demo.service.oobsmessagemaster.OOBSMessagemasterService;
import com.gemalto.eziomobile.demo.service.panmaster.PanmasterService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;

@RestController
@Controller
public class CASController {

	private static final LoggerUtil logger = new LoggerUtil(CASController.class.getClass());

	@Autowired
	private OOBSMessagemasterService oobsService;

	@Autowired
	private UsermasterService userServices;

	@Autowired
	private AccountmasterService accountMasterServices;

	@Autowired
	private DevicemasterService deviceService;

	@Autowired
	private PanmasterService panService;

	@Autowired
	private CardManagementService cardService;

	@Autowired
	private TransactionmasterService transactionService;

	@RequestMapping(value = "/deleteOOBSData.action", method = RequestMethod.POST)
	public void deleteOOBSData(@RequestParam("userId") String userId) throws ControllerException {
		try {
			oobsService.deleteOOBSMessageDataByUserId(userId);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/getcardandpan.data.action", method = RequestMethod.GET)
	public ResultStatus getCardAndPanData(@RequestParam("userId") String userId, @RequestParam("panNo") String panNo)
			throws ControllerException {
		ResultStatus resultStatus = new ResultStatus();

		try {
			logger.info("----------- getcardandpan ---------------");

			int uId = userServices.findUidByUserId(userId);
			logger.info("uId : " + uId);

			String accountNo = panService.findAccountNoByPanNoAndUserId(panNo, uId);
			logger.info("accountNo : " + accountNo);

			CardManagementDTO cardManagementInfo = cardService.findCardManagementInfoByUserIdAndPanNo(uId, panNo);
			logger.info("cardManagementInfo : " + cardManagementInfo.toString());

			logger.info("----------- getcardandpan ---------------");
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return resultStatus;
	}

	@RequestMapping(value = "/deletedevicedata.action/{userId}/{regCode}", method = RequestMethod.DELETE)
	public void deleteDeviceDataByUserIdAndRegCode(@PathVariable("userId") String userId,
			@PathVariable("regCode") String regCode) throws ControllerException {

		int uId;
		try {
			uId = userServices.findUidByUserId(userId);
			deviceService.deleteDeviceInfoByUserIdAndRegCode(uId, regCode);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	
	
	@RequestMapping(value = "/gettransactionofcard.action/{userId}/{cardNo}/{noOfTransactions}", method = RequestMethod.GET)
	public ResultStatus getlasttransaction(@PathVariable("userId") String userId,
			@PathVariable("cardNo") String cardNo, @PathVariable("noOfTransactions") int noOfTransactions)
			throws ControllerException {
		ResultStatus resultStatus = new ResultStatus();
		int uId;
		try {
			uId = userServices.findUidByUserId(userId);
			String accountNo = panService.findAccountNoByPanNoAndUserId(cardNo, uId);
			List<TransactionInfoDTO> list = transactionService.findTopNTransactionsForCard(uId, accountNo, noOfTransactions, cardNo);

			logger.info("list : " + list.toString());

			resultStatus.setStatusCode(HttpStatus.OK);
			resultStatus.setTemplateObject(list);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return resultStatus;
	}

	
	
	@RequestMapping(value = "/accountServices.action", method = RequestMethod.POST)
	public JSONObject testAccountServices(@RequestParam("userId") String userId) throws ControllerException {

		int uId;
		List<AccountMasterInfo> accountsList_CardType = new ArrayList<>();
		List<AccountMasterInfo> accountsList_Savings = new ArrayList<>();
		JSONObject mainObj = new JSONObject();

		try {
			uId = userServices.findUidByUserId(userId);
			accountsList_CardType = accountMasterServices.findAccountByUserIdAndAccountNameOrderByAccountNameAsc(uId,
					EzioMobileDemoConstant.ACCOUNT_TYPE_CARD);
			accountsList_Savings = accountMasterServices.findAccountByUserIdAndAccountNameOrderByAccountNameAsc(uId,
					EzioMobileDemoConstant.ACCOUNT_TYPE_SAVINGS);

			logger.info(" accountsList by UId and accountName : CREDIT : " + accountsList_CardType.toString());
			logger.info(" accountsList by UId and accountName : SAVINGS :" + accountsList_Savings.toString());

			JSONObject cardJSON = new JSONObject();
			JSONObject savingOrCurrentJSON = new JSONObject();

			cardJSON.put("atype", "Pay credit card accounts");
			cardJSON.put("details", accountsList_CardType);

			savingOrCurrentJSON.put("atype", "Current/Savings accounts");
			savingOrCurrentJSON.put("details", accountsList_Savings);

			JSONArray ja = new JSONArray();
			ja.put(cardJSON);
			ja.put(savingOrCurrentJSON);

			mainObj.put("uid", uId);
			mainObj.put("accountdetails", ja);

			String mainJSON = mainObj.toString();
			logger.info("Main JSON : " + mainObj.toString());

			ResultStatus resultStatus = new ResultStatus();
			resultStatus.setTemplateObject(mainJSON);

			logger.debug("resultStatus : ", resultStatus);

		} catch (ServiceException | JSONException e) {
			e.printStackTrace();
		}
		return mainObj;
	}

}
