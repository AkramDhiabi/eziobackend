package com.gemalto.eziomobile.demo.controller.p2pcontroller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import com.gemalto.eziomobile.demo.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.P2PMasterDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.OOBSMessageMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.model.SignDataInfo;
import com.gemalto.eziomobile.demo.service.devicemaster.DevicemasterService;
import com.gemalto.eziomobile.demo.service.groupmaster.GroupmasterService;
import com.gemalto.eziomobile.demo.service.oobsmessagemaster.OOBSMessagemasterService;
import com.gemalto.eziomobile.demo.service.p2pmaster.P2PmasterService;
import com.gemalto.eziomobile.demo.service.singdatamaster.SigndatamasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.service.userpreferencemaster.UserpreferenceMasterService;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.demo.webhelper.p2p.P2PWebHelper;
import com.gemalto.eziomobile.demo.webhelper.transactionmanagement.TransactionManagementWebHelper;
import com.gemalto.eziomobile.webhelper.oobs.OOBSWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.DATA_STATUS_LABEL;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.IS_BENEF_VALID_LABEL;

@RestController
public class P2pController {

	private static final LoggerUtil logger = new LoggerUtil(P2pController.class.getClass());

	@Autowired
	private P2PmasterService p2pService;
	
	@Autowired
	private UsermasterService userService;
	
	@Autowired
	private CommonWebHelper commonWebHelper;
	
	@Autowired
	private P2PWebHelper p2pWebHelper;
	
	@Autowired
	private DevicemasterService deviceService;
	
	@Autowired
	private GroupmasterService groupService;
	
	@Autowired
	private UserpreferenceMasterService userPreferenceService;
	
	@Autowired
	private OOBSWebHelper oobsWebHelper;
	
	@Autowired
	private SigndatamasterService signMasterService;
	
	@Autowired
	private OOBSMessagemasterService oobsMessageService;
	
	@Autowired
	private TransactionManagementWebHelper transactionWebHelper;
	
	@Autowired
	private URLUtil urlUtil;
	
	@RequestMapping(value = "/validatep2p.payment.otp/{userId}/{otpValue}", method = RequestMethod.POST)
	public String validateOTP(@PathVariable String userId, @PathVariable String otpValue)throws ControllerException{
		
		String msg = "";
		String amount = "";
		String fromAccount = "";
		String beneficiaryUserId = "";
		
		String transactiondata = "";
		String _newTransactionData = "";
		
		String responseDataToSendBackVerif = "";
		
		String senderGroupName = "";
		String beneficiaryGroupName = "";
		
		String pendingMsgId = "";
		String beneficiaryAccountName = "Pay Credit Card";
		
		int challenge = 0;
		
		boolean isOTPVerfied = false;
		boolean isUserAccountReset = false;
		boolean isDBReset = false;
		boolean isGroupNameSame = false;
		boolean isBeneficiaryValid = false;
		boolean isUserRegOnOOBServer = false;
		
		boolean isPendingTransection = false;
		
		Map<String, Object> resultMap = new HashMap<>();
		
		try {
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[validateOTP] backendConfiguration : "+backendConfiguration);
			
			int uId = userService.findUidByUserId(userId);

			//get user(sender) p2p info object
			P2PMasterDTO p2pInfo = p2pService.findP2pInfoByUserId(uId);

			if(p2pInfo == null) {
				//P2P Request is empty
				//to something..
				return DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_NOK + "\"}}";
			}
				
			amount = p2pInfo.getAmount();
			challenge = p2pInfo.getChallenge();
			fromAccount = p2pInfo.getFromAccountNo();
			beneficiaryUserId = p2pInfo.getBenificiaryUserId();

			if (p2pInfo.getMsg() != null && !p2pInfo.getMsg().equals("")) {
				msg = p2pInfo.getMsg();
				logger.info("Msg : " + msg);
			}
				
			//delete p2p data for given userId
			p2pService.deleteP2pInfoByUserId(uId);

			transactiondata = challenge + fromAccount + amount;
			_newTransactionData = transactiondata;

			int chklen_transactiondata = (_newTransactionData.length() % 2);
			if (chklen_transactiondata == 1) {
				_newTransactionData += "0";
			}

			logger.info("\n \n FromAccount : " + fromAccount + "\n Beneficiary User Id : "
					+ beneficiaryUserId + "\n Amount : " + amount + "\n OTP : " + otpValue + "\n Challenge : "
					+ challenge + "\n \n");

			//validate OTP
			isOTPVerfied = commonWebHelper.validateOTP(userId, otpValue, _newTransactionData);
				
			if (!isOTPVerfied) {
				logger.info("+++++++++++ OTP is not verified for P2P +++++++");
				return DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_NOK + "\"}}";
			}

			logger.info("\n OTP verified from p2p.jsp....");

			//check if user account has reset or not
			//by checking CAS server with list of devices
			isUserAccountReset = p2pWebHelper.isUserAccountReset(beneficiaryUserId);

			//get beneficiary uId (int) to check account has reset or not
			int beneficiaryUID = userService.findUidByUserId(beneficiaryUserId);
			isDBReset = deviceService.isUserAccountReset(beneficiaryUID);

			//find sender's groupID and groupName
			int senderGroupId = userService.getUserGroupIdByUid(uId);
			senderGroupName = groupService.findGroupNameByGroupId(senderGroupId);
			logger.info("Sender Group Name : " + senderGroupName);


			//checking beneficiary group name is same as the sender's group name or not
			//if its NOT, money wont be sent.
			beneficiaryGroupName = getBeneficiaryGroupName(beneficiaryGroupName, beneficiaryUID);

			if (beneficiaryGroupName.equalsIgnoreCase(senderGroupName)) {
				isGroupNameSame = true;
			}

			logger.info("\n isUserAccountReset.......... : " + isUserAccountReset);
			logger.info("isReset (DB Check).......... : " + isDBReset);
			logger.info("isGroupName same (DB Check)....... : " + isGroupNameSame + "\n");

			//If beneficiary user account is not Reset. Also, sender's and beneficary's group is same.
			isBeneficiaryValid = !isUserAccountReset && !isDBReset && isGroupNameSame;

			logger.info(" IsBeneficiaryValid (DB Check).......... : " + isBeneficiaryValid + "\n \n");

			//If beneficiary is not valid, they have reset their account, senders and beneficiary group is not same
			if (!isBeneficiaryValid) {
				logger.info("\n Beneficary user reset their account, senders and beneficiary group is not same");
				logger.info("\n User reset his account...." + beneficiaryUserId);
				return DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_OK + IS_BENEF_VALID_LABEL + isBeneficiaryValid + "\"}}";
			}

			//id
			String beneficiaryP2PSetting = "";
			boolean isP2PNotificationOn= false;

			beneficiaryP2PSetting = userPreferenceService.findSecP2pNotificationByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, beneficiaryUID);
			if(beneficiaryP2PSetting.equals("02")) {
				isP2PNotificationOn = true;
			}

			// Notification is not sent
			if (!(isP2PNotificationOn && !beneficiaryUserId.equalsIgnoreCase(userId))) {
				logger.info("\n p2p_notification setting is OFF for...." + beneficiaryUserId);
				transactionWebHelper.updateDBForP2PTransactionOperation(uId, userId, beneficiaryUID, beneficiaryUserId, Integer.parseInt(amount), fromAccount);
				return DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_OK + IS_BENEF_VALID_LABEL + isBeneficiaryValid + "\"}}";
			}

			logger.info("\n P2P notification setting is ON for U_ID  : " + beneficiaryUserId);

			//check beneficiary user on OOBS server
			isUserRegOnOOBServer = oobsWebHelper.isUserRegisteredOnOOBServer(beneficiaryUserId);

			// complete the transaction, if user is not on OOBS server
			if (!isUserRegOnOOBServer) {
				logger.info("\n beneficiary user is not on OOBS server...." + beneficiaryUserId);
				transactionWebHelper.updateDBForP2PTransactionOperation(uId, userId, beneficiaryUID, beneficiaryUserId, Integer.parseInt(amount), fromAccount);
				return DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_OK + IS_BENEF_VALID_LABEL + isBeneficiaryValid + "\"}}";
			}

			logger.info("\n Beneficiary registered on OOBS........");

			//Before we used to check SigndataMaster to check pending transaction
			//Now, we do check OOBSMessageMaster
			OOBSMessageMasterInfo oobsMessageMasterInfo = oobsMessageService.findOOBSDataByUserId(userId);

			//If OOBSMessageMasterInfo is not empty, there is a pending transaction
			if (oobsMessageMasterInfo != null && (oobsMessageMasterInfo.getMessageId() != null)) {

				pendingMsgId = oobsMessageMasterInfo.getMessageId();
				logger.info("[P2P OTP validation]"+EzioMobileDemoConstant.OOBS_PENDING_TRANSACTION_MSG);
			}

			//Will check OOBS Queue, is there is pending transaction or not
			if (pendingMsgId != null && !pendingMsgId.equals("")) {
				//isPendingTransection = true;

				logger.info("\n There is a Pending Transection for P2P transaction.......");
				//P2PWebHelper.transactionListUpdate(userid, beneficiaryUserId, amount, fromAccount);
				isPendingTransection = oobsWebHelper.deleteMessageFromOOBSQueue(pendingMsgId);
			}

			if (!isPendingTransection) {
				//send notification and update transaction list
				logger.info("\n There is no pending transaction for P2P operation......");

				// update transaction list..
				resultMap = transactionWebHelper.updateDBForP2PTransactionOperation(uId, userId,
						beneficiaryUID, beneficiaryUserId, Integer.parseInt(amount), fromAccount);

				//get beneficiary account no from session.
				String beneficiaryAccountNo = (String) resultMap.get("beneficiaryAccountNo");
				logger.info("P2P - OTP validation API -- beneficiaryAccountNo : "+beneficiaryAccountNo);

				UUID uid = UUID.randomUUID();
				String trans_id = uid.toString();
				long epochTime = System.currentTimeMillis() / 1000;

				String strmsg = "{'type' : 'P2P', 'from':'" + userId + "', 'etime':'" + epochTime
						+ "','amount':'" + amount + "','message':'" + msg
						+ "','title':'Gemalto','benefaccountname':'" + beneficiaryAccountName
						+ "','benefaccountnum':'" + beneficiaryAccountNo + "'}";

				logger.info("\n strmsg before sending data : " + strmsg);

				DatatypeConverter dc = null;
				byte[] decodedBytes = strmsg.getBytes();
				String pushMsgData = dc.printBase64Binary(decodedBytes);

				Map<String, String> sendNotificationData = oobsWebHelper.sendNotification(pushMsgData, beneficiaryUserId);
				saveSignDataInfoIfNotificationIsSent(userId, amount, fromAccount, beneficiaryUserId, challenge, uId, trans_id, sendNotificationData);

				return DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_OK + IS_BENEF_VALID_LABEL + isBeneficiaryValid + "\"}}";
			}

			//if there is a pending transaction, will just update transaction list, without
			//sending notification to beneficiary
			transactionWebHelper.updateDBForP2PTransactionOperation(uId, userId, beneficiaryUID, beneficiaryUserId, Integer.parseInt(amount), fromAccount);

			responseDataToSendBackVerif = DATA_STATUS_LABEL + EzioMobileDemoConstant.STATUS_OK + IS_BENEF_VALID_LABEL + isBeneficiaryValid + "\"}}";

		} catch (Exception e) {
			logger.error("Exception : Unable to validate payment!");
			throw new ControllerException(e);
		}
		return responseDataToSendBackVerif;
	}

	private String getBeneficiaryGroupName(String beneficiaryGroupName, int beneficiaryUID) throws ServiceException {
		int beneficiaryGroupId = userService.getUserGroupIdByUid(beneficiaryUID);
		if (beneficiaryGroupId != 0) {
			beneficiaryGroupName = groupService.findGroupNameByGroupId(beneficiaryGroupId);
			logger.info("\n Beneficiary Group Name : " + beneficiaryGroupName);
		}
		return beneficiaryGroupName;
	}

	/**
	 *
	 * @param userId
	 * @param amount
	 * @param fromAccount
	 * @param beneficiaryUserId
	 * @param challenge
	 * @param uId
	 * @param trans_id
	 * @param sendNotificationData
	 * @throws ServiceException
	 */
	private void saveSignDataInfoIfNotificationIsSent(@PathVariable String userId, String amount, String fromAccount, String beneficiaryUserId, int challenge, int uId, String trans_id, Map<String, String> sendNotificationData) throws ServiceException {
		boolean isNotificationSent = Boolean.parseBoolean(sendNotificationData.get("isNotificationSent"));

		//if, notification sent, update singdatamaster table,
		//and send response to mobile device.
		if (isNotificationSent) {

			logger.info("\n Notification is sent to.... " + beneficiaryUserId);

			String pushMessageId = sendNotificationData.get("pushMessageId");

			logger.info("P2P jsp, operation 04, pushMessageId : " + pushMessageId);

			String signdata = challenge + ";p2p;" + fromAccount + ";" + amount;
			String description = "From :" + userId + " Amt:$" + amount;


			SignDataInfo signDataInfo = new SignDataInfo();
			signDataInfo.setMsgid(pushMessageId);
			signDataInfo.setSingdata(signdata);
			signDataInfo.setTransactionId(trans_id);
			signDataInfo.setUserId(uId);
			signDataInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
			signDataInfo.setDescription(description);
			signDataInfo.setHasheddata("p2p");
			signDataInfo.setTransactionDate(new Date());

			signMasterService.saveSigndataInfo(signDataInfo);
			logger.info("[p2p OTP validation] Singndatamaster saved!!");
		}
	}


	@ExceptionHandler(ControllerException.class)
	public ResultStatus p2pErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}

}
