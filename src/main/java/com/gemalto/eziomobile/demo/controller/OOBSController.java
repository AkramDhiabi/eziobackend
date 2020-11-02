package com.gemalto.eziomobile.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.BankingValidationTransactionParametersDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.CallbackDataInfo;
import com.gemalto.eziomobile.demo.model.OOBSMessageMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.model.SignDataInfo;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.callbackdata.CallbackDataService;
import com.gemalto.eziomobile.demo.service.cardissuance.CardIssuanceMasterService;
import com.gemalto.eziomobile.demo.service.devicemaster.DevicemasterService;
import com.gemalto.eziomobile.demo.service.oobsmessagemaster.OOBSMessagemasterService;
import com.gemalto.eziomobile.demo.service.panmaster.PanmasterService;
import com.gemalto.eziomobile.demo.service.singdatamaster.SigndatamasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.service.userpreferencemaster.UserpreferenceMasterService;
import com.gemalto.eziomobile.demo.util.CardNumberGeneratorUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.util.XMLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.demo.webhelper.ecommerce.EcommerceWebHelper;
import com.gemalto.eziomobile.demo.webhelper.login.LoginWebHelper;
import com.gemalto.eziomobile.demo.webhelper.transactionmanagement.TransactionManagementWebHelper;
import com.gemalto.eziomobile.webhelper.oobs.OOBSWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.OPERATION_TYPE_MESSAGE;

@RestController
public class OOBSController {


	@Autowired
	private OOBSWebHelper oobsWebHelper;

	@Autowired
	private UsermasterService usermasterService;

	@Autowired
	private SigndatamasterService signDataService;
	
	@Autowired
	private PanmasterService panMasterService;

	@Autowired
	private OOBSMessagemasterService oobsMessagemasterService;

	@Autowired
	private CommonWebHelper commonWebHelper;

	@Autowired
	private CallbackDataService callbackDataService;
	
	@Autowired
	private DevicemasterService devicemasterService;
	
	@Autowired
	private LoginWebHelper loginWebHelper;
	
	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private XMLUtil xmlUtil;
	
	@Autowired
	private AccountmasterService accountService;
	
	@Autowired
	private UserpreferenceMasterService userpreferenceService;

	@Autowired
	private TransactionManagementWebHelper transactionWebHelper;
	
	@Autowired
	private EcommerceWebHelper ecommerceWebHelper;
	
	@Autowired
	private CardNumberGeneratorUtil cardGeneratorUtil;
	
	@Autowired
	private CardIssuanceMasterService cardIssuanceService;

	private static final LoggerUtil logger = new LoggerUtil(OOBSController.class.getClass());

	
	
	/** API to check if user has good notification profile registered on OOBS
	 * @param userId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/checkvalidoobsnotificationprofile.user/{userId}", method = RequestMethod.POST)
	public ResultStatus checkValidOobsNotificationProfile(@PathVariable(USER_ID_PARAM) String userId) throws ControllerException {
		
		boolean isUserRegToOOBServer = false;
		ResultStatus resultStatus = new ResultStatus();
		
		try {
			isUserRegToOOBServer = oobsWebHelper.isUserRegisteredOnOOBServer(userId);
			if(isUserRegToOOBServer){
				resultStatus.setMessage(EzioMobileDemoConstant.OOBS_PUSH_NOTIFICATION_PROFILE_AVAILABLE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.OOBS_PUSH_NOTIFICATION_PROFILE_NOT_AVAILABLE);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.info("Unable to check user notification profile!", e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}
	
	
	/**
	 * This method is to check any pending transactions in Database, OOBsMaster
	 * table based on messageId
	 * 
	 * @param userId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/checkpendingtransaction.action", method = RequestMethod.POST)
	public ResultStatus checkPendingTransaction(@RequestParam(USER_ID_PARAM) String userId) throws ControllerException {

		logger.info(USER_ID_LABEL + userId);
		OOBSMessageMasterInfo oobsMessageMasterInfo = null;
		ResultStatus resultStatus = new ResultStatus();

		try {
			oobsMessageMasterInfo = oobsMessagemasterService.findOOBSDataByUserId(userId);
			if (oobsMessageMasterInfo != null && (oobsMessageMasterInfo.getMessageId() != null)) {

				resultStatus.setMessage(EzioMobileDemoConstant.OOBS_PENDING_TRANSACTION_MSG);
				resultStatus.setResponseCode(EzioMobileDemoConstant.OOBS_PENDING_TRANSACTION);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			} else {
				resultStatus.setMessage(EzioMobileDemoConstant.OOBS_NO_PENDING_TRANSACTION_MSG);
				resultStatus.setResponseCode(EzioMobileDemoConstant.OOBS_NO_PENDING_TRANSACTION);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			}
		} catch (Exception e) {
			logger.info("Unable find any data with userId : " + userId + "\n", e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	
	/** API to send push notification based on userID and operationType
	 *  It will required a request body as well, which will be different for different operations
	 * @param userId
	 * @param operationType
	 * @param bankingTransactionsDTO
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/sendonlinetransaction.notification.action/{userId}/{operationType}", consumes = "application/json" ,method = RequestMethod.POST)
	public ResultStatus sendOnlineTransactionNotification(@PathVariable(USER_ID_PARAM) String userId, @PathVariable("operationType") String operationType,
														  @RequestBody BankingValidationTransactionParametersDTO bankingTransactionsDTO) throws ControllerException {
		
		logger.info(USER_ID_LABEL + userId);
		logger.info("Send notification - request body : "+bankingTransactionsDTO.toString());

		ResultStatus resultStatus = new ResultStatus();
		Map<String, String> pushNotificationData = new HashMap<String, String>();

		int uId = 0;
		String messageId = "";
		
		String pushXmlData = "";
		String challenge = EzioMobileDemoConstant.CHALLENGE_30000003;
		String cleardata = "";
		String hashData = "";
		String callBackURL = "";
		String _msgcontent = "";
		String message = "";
		String security = "";
		String amount = "";
		
		String fromAccountNo = "";
		String toAccountNo = "";
		
		String beneficiaryAccNo = "";
		String beneficiaryName = "";
		
		String cardNumber = "";
		String expDate = "";
		String cvv = "";
		
		String cardType = "";
		
		boolean isMobileReg = false;

		try {
			uId = usermasterService.findUidByUserId(userId);
			
			long epochTime = System.currentTimeMillis() / 1000;
			logger.info("epochTime : "+epochTime);
			
			switch (operationType) {
				case EzioMobileDemoConstant.OPERATION_TYPE_LOGIN:
					cleardata = userId + challenge + epochTime;
					break;
				case EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY:

					beneficiaryAccNo = bankingTransactionsDTO.getBeneficiaryAccount();
					beneficiaryName = bankingTransactionsDTO.getBeneficiaryName();
					cleardata = userId + challenge + epochTime + beneficiaryName + beneficiaryAccNo;
					break;
				case EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER:

					fromAccountNo = bankingTransactionsDTO.getFromAccountNo();
					toAccountNo = bankingTransactionsDTO.getToAccountNo();
					amount = bankingTransactionsDTO.getAmount();
					cleardata = userId + challenge + epochTime + fromAccountNo + toAccountNo + amount;
					break;
				case EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS:

					cardNumber = bankingTransactionsDTO.getCardNumber();
					cvv = bankingTransactionsDTO.getCvv();
					expDate = bankingTransactionsDTO.getExpDate().replaceAll("/","");

					logger.info("expDate : "+expDate);

					amount = bankingTransactionsDTO.getAmount();


					boolean flag = ecommerceWebHelper.validateEcommerce3DSPayment(uId, Integer.parseInt(amount), cardNumber);
					if(!flag){

						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
						resultStatus.setMessage(EzioMobileDemoConstant.TRANSACTION_REJECTED);
						resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);

						return resultStatus;
					}

					String cardDigit = cardNumber.substring(cardNumber.length()-4, cardNumber.length());

					logger.info("OPERATION_TYPE_ECOMMERCE3DS  - cardDigit : "+cardDigit);
					cleardata = userId+ challenge + epochTime + cardNumber.substring(cardNumber.length()-4, cardNumber.length()) + expDate + cvv + amount;
					break;

				case EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE:

					//check PanManster - limit upto 4 cards
					//including existing VISA and Master card
					int cardCount = panMasterService.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
					//check count in cardIssuanceMaster table
					int cardIssuanceCount = cardIssuanceService.countByUserId(uId);

					isMobileReg = oobsWebHelper.isUserRegisteredOnOOBServer(userId);
					logger.info("isMobileReg : "+isMobileReg);

					if(!isMobileReg){
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NOT_FOUND_404);
						resultStatus.setMessage(EzioMobileDemoConstant.OOBS_NO_MOBILE_FOUND);
						resultStatus.setStatusCode(HttpStatus.NOT_FOUND);
						return resultStatus;
					}
					if(cardCount == 4){
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
						resultStatus.setMessage(EzioMobileDemoConstant.CARD_ISSUANCE_MAX_CARD_LIMIT_REACHED);
						resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
						return resultStatus;
					}else if(cardIssuanceCount>0){
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
						resultStatus.setMessage(EzioMobileDemoConstant.CARD_ISSUANCE_REQUEST_PENDING);
						resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
						return resultStatus;
					}

					expDate = EzioMobileDemoConstant.EZIO_DCV_CARDS_EXP_DATE;
					cvv = EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_STATIC_CVV;
					cardType = bankingTransactionsDTO.getCardType();

					cardNumber = generateCardByCardType(cardType);
					logger.info("cardType : "+cardType+" --  cardNumber : "+cardNumber+" -- cardLength : "+cardNumber.length());
					cleardata = userId + challenge + +epochTime + cardNumber.substring(cardNumber.length()-4, cardNumber.length());
					break;

				default:
					break;
			}
			
			logger.info("[Data to sign] cleardata : "+cleardata);
			
			UUID uid = UUID.randomUUID();
			String trans_id = uid.toString();
			
			hashData = commonWebHelper.getSHA1(commonWebHelper.asciiToHex(cleardata));
			logger.info("sendUserLoginNotification hashData : " + hashData);

			// Read security value from DB
			switch (operationType) {
				case EzioMobileDemoConstant.OPERATION_TYPE_LOGIN:
					_msgcontent = "{'type':'EzioDemoV2_Login" + ETIME_LABEL + epochTime + USERNAME_LABEL + userId
							+ CLEARDATA_LABEL + cleardata + HASHEDDATA_LABEL + hashData
							+ TITLE_GEMALTO_LABEL + TRANSECTIONID_LABEL + trans_id + "', 'security':'02', 'challenge': '"
							+ challenge + "'}";
					message = EzioMobileDemoConstant.NOTIFICATION_MSG_TYPE_LOGIN;
					break;

				case EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY:

					security = userpreferenceService.findSecAddPayeeByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
					_msgcontent = TYPE_LABEL + EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY
							+ ETIME_LABEL + epochTime + USERNAME_LABEL + userId
							+ CLEARDATA_LABEL + cleardata + HASHEDDATA_LABEL +hashData+ TITLE_GEMALTO_LABEL
							+ TRANSECTIONID_LABEL + trans_id + SECURITY_LABEL + security + CHALLENGE_LABEL +challenge+"',"
							+ " 'payeename': '"+beneficiaryName+"', 'payeeaccount': '"+beneficiaryAccNo+"'}";
					message = EzioMobileDemoConstant.NOTIFICATION_MSG_TYPE_NEW_BENEFICIARY;
					break;

				case EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER:

					//check account type to read user preference accordingly
					//accountType = 1, Beneficiary account
					//accountType = 0, Own account
					int accountType = accountService.findTypeByStatusAndAccountNoAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, toAccountNo, uId);
					logger.info("Account type : "+accountType);

					if(accountType == EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1)
						security = userpreferenceService.findSecTxOtherByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
					else
						security = userpreferenceService.findSecTxOwnAccByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);

					_msgcontent = TYPE_LABEL +EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER
							+ ETIME_LABEL + epochTime + USERNAME_LABEL + userId + CLEARDATA_LABEL + cleardata
							+ HASHEDDATA_LABEL + hashData
							+ TITLE_GEMALTO_LABEL + TRANSECTIONID_LABEL + trans_id
							+ SECURITY_LABEL +security+ CHALLENGE_LABEL + challenge
							+ FROM_LABEL + fromAccountNo + TO + toAccountNo + AMOUNT_LABEL + amount + CLOSE_LABEL2;
					message = EzioMobileDemoConstant.NOTIFICATION_MSG_TYPE_MONEY_TRANSFER;
					break;

				case EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS:

					security = userpreferenceService.findSecEcommerce3dsByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
					_msgcontent = TYPE_LABEL + EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS + ETIME_LABEL + epochTime
							+ USERNAME_LABEL + userId + CLEARDATA_LABEL + cleardata + HASHEDDATA_LABEL + hashData
							+ TITLE_GEMALTO_LABEL + TRANSECTIONID_LABEL + trans_id + SECURITY_LABEL + security
							+ CHALLENGE_LABEL + challenge + CARD_NUMBER_LABEL + cardNumber
							+ EXP_DATE_LABEL + expDate + CVV_LABEL + cvv + AMOUNT_LABEL + amount + CLOSE_LABEL2;
					message = EzioMobileDemoConstant.NOTIFICATION_MSG_TYPE_ECOMMERCE3DS;
					break;

				case EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE:

					security = EzioMobileDemoConstant.AUTHENTICATION_TYPE_2FA;
					_msgcontent = TYPE_LABEL + EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE + ETIME_LABEL + epochTime
							+ USERNAME_LABEL + userId + CLEARDATA_LABEL + cleardata + HASHEDDATA_LABEL + hashData
							+ TITLE_GEMALTO_LABEL + TRANSECTIONID_LABEL+trans_id+ SECURITY_LABEL +security+""
							+ CHALLENGE_LABEL + challenge + CARD_NUMBER_LABEL + cardNumber
							+ EXP_DATE_LABEL + expDate + CVV_LABEL + cvv + CLOSE_LABEL2;

					message = EzioMobileDemoConstant.NOTIFICATION_MSG_TYPE_CARD_ISSUANCE;
					break;

				default:
					break;
			}
			
			logger.info(SEND_ONLINE_TRANSACTION_NOTIFICATION_LABEL);
			logger.info("OperationType : "+operationType);
			logger.info("_msgcontent : "+_msgcontent);
			logger.info("------------------------------------------------------------");

			byte[] MsgInBytes = _msgcontent.getBytes();
			String msgcontent = DatatypeConverter.printBase64Binary(MsgInBytes);

			callBackURL = urlUtil.verifyTxCallbackURL();
			logger.info("[Send login notification] callBackURL : " + callBackURL);

			pushXmlData = getPushXMLString(userId, operationType, callBackURL, message, msgcontent);

			logger.info(SEND_ONLINE_TRANSACTION_NOTIFICATION_LABEL);
			logger.info("pushXmlData : "+pushXmlData);
			logger.info("------------------------------------------------------------");

			pushNotificationData = oobsWebHelper.sendNotification(pushXmlData, userId);
			logger.info("pushNotificationData : " + pushNotificationData.toString());

			boolean flag = Boolean.parseBoolean(pushNotificationData.get("isNotificationSent"));

			setResultStatus(userId, resultStatus, pushNotificationData, flag);

		} catch (Exception e) {
			logger.info("Unable to send the notification with userId : " + userId + "\n", e);
			throw new ControllerException(e);
		}

		return resultStatus;
	}

	/**
	 *
	 * @param userId
	 * @param resultStatus
	 * @param pushNotificationData
	 * @param flag
	 */
	private void setResultStatus(@PathVariable(USER_ID_PARAM) String userId, ResultStatus resultStatus, Map<String, String> pushNotificationData, boolean flag) {
		String messageId;
		if (flag && (userId != null && !userId.equals(""))) {

			messageId = pushNotificationData.get("pushMessageId");

			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
			resultStatus.setMessage(EzioMobileDemoConstant.PUSH_NOTIFICATION_SENT);
			resultStatus.setStatusCode(HttpStatus.OK);
			resultStatus.setTemplateObject(messageId);

		} else {
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
			resultStatus.setMessage(EzioMobileDemoConstant.PUSH_NOTIFICATION_NOT_SENT);
			resultStatus.setStatusCode(HttpStatus.OK);
		}
	}

	/**
	 *
	 * @param userId
	 * @param operationType
	 * @param callBackURL
	 * @param message
	 * @param msgcontent
	 * @return
	 */
	private String getPushXMLString(@PathVariable(USER_ID_PARAM) String userId, @PathVariable("operationType") String operationType, String callBackURL, String message, String msgcontent) {
		String pushXmlData;
		if(operationType.equals(EzioMobileDemoConstant.OPERATION_TYPE_LOGIN) ||
				operationType.equals(EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY) ||
				operationType.equals(EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER) ||
				operationType.equals(EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS) ||
				operationType.equals(EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE)){
			pushXmlData = xmlUtil.getVerifyTransactionXML(userId, msgcontent, callBackURL, message);
		}else{
			pushXmlData = xmlUtil.getDispatchMessageXML(userId, msgcontent, callBackURL, message);
		}
		return pushXmlData;
	}

	/**
	 *
	 * @param cardType
	 * @return
	 */
	private String generateCardByCardType(String cardType) {
		String cardNumber = "";
		switch (cardType) {
			case EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_CARD_TYPE_VISA:
				cardNumber = cardGeneratorUtil.generate(EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_PREFIX_VISA, EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_VISA_LENGTH);
				break;
			case EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_CARD_TYPE_MASTER_CARD:
				cardNumber = cardGeneratorUtil.generate(EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_PREFIX_MASTER_CARD, EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_MASTER_CARD_LENGTH);
				break;
			case EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_CARD_TYPE_AMEX:
				cardNumber = cardGeneratorUtil.generate(EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_PREFIX_AMEX, EzioMobileDemoConstant.EZIO_CARD_ISSUANCE_AMEX_LENGTH);
				break;
			default:
				break;
		}
		return cardNumber;
	}


	/** API to delete pending transaction based on userId and messageId
	 * @param userId
	 * @param messageId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/deletependingtransaction.action", method = RequestMethod.POST)
	public ResultStatus deleteMessageFromDBAndOOBS(@RequestParam(USER_ID_PARAM) String userId, @RequestParam("messageId") String messageId) throws ControllerException {

		logger.info(USER_ID_LABEL + userId);
		OOBSMessageMasterInfo oobsMessageMasterInfo = null;
		ResultStatus resultStatus = new ResultStatus();

		try {
			int uId = usermasterService.findUidByUserId(userId);
			oobsMessageMasterInfo = oobsMessagemasterService.findOOBSDataByUserId(userId);
			if(oobsMessageMasterInfo != null){

				boolean flag = oobsWebHelper.deleteMessageFromOOBSQueue(messageId);

				oobsMessagemasterService.deleteOOBSMessageDataByUserId(userId);
				callbackDataService.deleteCallbackDataByUserId(userId);

				signDataService.deleteSigndataByUserId(uId);


				resultStatus.setMessage("MESSAGE_DELETED");
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			}
			else{
				resultStatus.setMessage("MESSAGE_NOT_DELETED");
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			}
		} catch (Exception e) {
			logger.info("Unable find any data with userId : " + userId + "\n", e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}




	/**
	 * @return Handle OOBS call back, which will be hit by OOBS server itself
	 * @throws ControllerException
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "/oobsCallBack.action", method = RequestMethod.POST)
	public void oobsCallBack(HttpServletRequest request, HttpServletResponse response)
			throws ControllerException {

		logger.info("Call back function executing....");

		try {
			
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[MobileEnrollmentStepOne] backendConfiguration : "+backendConfiguration);
			
			String extractPostRequestBody = "";

			// Traces request
			logger.debug("\n");
			logger.debug("callbackFromOobs_2 : ...");
			logger.debug("callbackFromOobs_2 : request.getPathInfo=" + request.getPathInfo());
			logger.debug("callbackFromOobs_2 : request.getContentLength=" + request.getContentLength());
			logger.debug("callbackFromOobs_2 : request.getContentType=" + request.getContentType());
			logger.debug("callbackFromOobs_2 : request.getContextPath=" + request.getContextPath());
			logger.debug("callbackFromOobs_2 : request.getLocalAddr=" + request.getLocalAddr());
			logger.debug("callbackFromOobs_2 : request.getLocalName=" + request.getLocalName());
			logger.debug("callbackFromOobs_2 : request.getLocalPort=" + request.getLocalPort());
			logger.debug("callbackFromOobs_2 : request.getMethod=" + request.getMethod());
			logger.debug("callbackFromOobs_2 : request.getProtocol=" + request.getProtocol());
			logger.debug("\n");

			// Traces
			System.out.println("Headers :");
			Enumeration headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = (String) headerNames.nextElement();
				logger.info(headerName + " = " + request.getHeader(headerName));
			}

			// Traces
			System.out.println("Parameters :");
			Enumeration params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String paramName = (String) params.nextElement();
				logger.info(paramName + " = " + request.getParameter(paramName));
			}

			// Traces
			extractPostRequestBody = traceExtractPostRequestBody(request);

			// Body xml Parsing
			if (!extractPostRequestBody.isEmpty()) {

				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

				// Xml parsing
				DocumentBuilder newDocumentBuilder2 = factory.newDocumentBuilder();
				Document doc2 = newDocumentBuilder2.parse(new ByteArrayInputStream(extractPostRequestBody.getBytes()));

				// Get "type"
				String type = null;
				String msgId = null;
				String userID = null;
				/*String msgType = null;*/
				int uId = 0;
				NodeList messageIdNode = null;

				type = doc2.getDocumentElement().getNodeName();

				if(null == type) {
					type = "";
				}

				logger.info("Callback type : " + type);

				msgId = getMessageID(doc2, type);

				/*
				 ** A for a transaction verify (online MT, add new B, eCommerce
				 * 3DS) B for a dispatch message (P2P)
				 */

				//
				switch (type.toLowerCase()) {
					case EzioMobileDemoConstant.VERIFYTRANSACTIONCALLBACK:
						userID = oobsMessagemasterService.findUserIdByMessageId(msgId);
						verifyTransactionCallback(doc2, userID, msgId);
						break;
					case EzioMobileDemoConstant.DELIVERYCALLBACK:
						logger.info("DeliveryCallback: ***********");
						/*
						 ** We reach this portion when a message is actually fetched so a mobile is there...
						 ** Normal use case is when we got a Notification callback before
						 ** but we have also to handle the case when the notification is not sent
						 ** For instance where the SM could not reach the GCM or APNS servers
						 */
						// we check there if the notification callback happened earlier for this message
						OOBSMessageMasterInfo oobsMessageMasterInfo = null;
						oobsMessageMasterInfo = oobsMessagemasterService.findOOBSDataByMessageId(msgId);

						if (oobsMessageMasterInfo == null) {
							logger.info("DeliveryCallback do notificationCallBback.....");
							notificationCallBback(doc2, msgId);
						} else {
							logger.info("DeliveryCallback : message is there, all OK");
						}
						break;
					case EzioMobileDemoConstant.READNOTIFYCALLBACK:
						userID = oobsMessagemasterService.findUserIdByMessageId(msgId);
						readNotifyCallback(msgId, userID);
						break;
					case EzioMobileDemoConstant.REPLYCALLBACK:
						logger.info("************ ReplyCallback: ***********");
						userID = oobsMessagemasterService.findUserIdByMessageId(msgId);
						replyCallback(doc2, userID);
						break;
					case EzioMobileDemoConstant.POSTCALLBACK:
						logger.info("************ PostCallback: ***********");
						postCallback(doc2);
						break;
					case EzioMobileDemoConstant.EXPIRECALLBACK:
						logger.info("************ ExpireCallback: ***********");
						userID = oobsMessagemasterService.findUserIdByMessageId(msgId);
						expireCallback(userID, msgId);
						break;
					case EzioMobileDemoConstant.USERMESSAGECALLBACK:
					case EzioMobileDemoConstant.ERRORREPORTCALLBACK:
					case EzioMobileDemoConstant.CLIENTNOTIFICATIONPROFILEUPDATEDCALLBACK:
					case EzioMobileDemoConstant.CLIENTREGISTEREDCALLBACK:
					case EzioMobileDemoConstant.CLIENTUNREGISTERCALLBACK:
					case EzioMobileDemoConstant.SIGNTRANSACTIONCALLBACK:
						logger.info("************ " + type + " : ***********");
						break;
					case EzioMobileDemoConstant.NOTIFICATIONCALLBACK:
						logger.info("************ NotificationCallback: ***********");

						/*
						 ** We must check the Notification state here:
						 * PENDING - not tried yet
						 * NOT_USED – has not been used. Normally because an earlier profile has succeeded
						 * NO_ERROR – the profile was successfully tried
						 * FAILURE – the profile was tried but failed
						 * PERMANENT_FAILURE – the profile was tried, but the channel indicated that the endpoint is invalid in a permanent fashion
						 *
						 * only NO_ERROR is allowed to be processed
						 */
						// parse the whole callback
						// if one clientID has the state "NO_ERROR" stop parsing (no need to parse the rest if any)
						// check for security that nothing is inside our OOB DB (otherwise do nothing)
						// then if nothing call notificationCallBback(doc2, msgId);

						NodeList nList2 = null;
						switch (backendConfiguration) {
							case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
								nList2 = doc2.getElementsByTagName("notificationResultList");
								logger.info("----------------------------" + nList2.getLength());
								break;
							case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
								nList2 = doc2.getElementsByTagName("ns2:notificationResultList");
								logger.info("----------------------------" + nList2.getLength());
								break;
							default:
								break;
						}

						String notificationState = "";

						List<String> stateList = notifyList(doc2, msgId, nList2);

						logger.info("mapDoc size : " + stateList.size() + "\n");
						break;
					default:
						logger.info("******** Unknown callback *********");
				}

			}
		} catch (Exception e) {
			logger.error("OOPS10");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	private String traceExtractPostRequestBody(HttpServletRequest request) {
		String extractPostRequestBody = "";
		logger.info("Row data :");
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			try(Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");) {
				extractPostRequestBody = s.hasNext() ? s.next() : "";
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.info("extractPostRequestBody = " + extractPostRequestBody);
		}
		return extractPostRequestBody;
	}

	/**
	 *
	 * @param doc2
	 * @param type
	 * @return
	 */
	private String getMessageID(Document doc2, String type) {
		String msgId = "";
		NodeList messageIdNode;
		if (type != null) {
			if (!type.equalsIgnoreCase("UserMessageCallback") && !type.equalsIgnoreCase("ErrorReportCallback")
					&& !type.equalsIgnoreCase("PostCallback") && !type.equalsIgnoreCase("ClientNotificationProfileUpdatedCallback") && !type.equalsIgnoreCase("ClientRegisteredCallback")
					&& !type.equalsIgnoreCase("ClientUnregisterCallback")) {
				// get msgID
				messageIdNode = doc2.getElementsByTagName("messageId");
				if (messageIdNode != null) {
					msgId = messageIdNode.item(0).getFirstChild().getNodeValue();
					if(msgId != null && !msgId.equals("")){
						logger.info("Callback: messageId: " + msgId);
					}
				}
				else {
					logger.info("messageId: NULL !!!, handle this error");
				}
			}
		} else {
			logger.info("type: NULL !!!, handle this error");
		}
		return msgId;
	}

	/**
	 *
	 * @param doc2
	 * @param msgId
	 * @param nList2
	 * @return
	 * @throws ServiceException
	 */
	private List<String> notifyList(Document doc2, String msgId, NodeList nList2) throws ServiceException {
		String notificationState = "";
		List<String> stateList = new ArrayList<String>();
		OOBSMessageMasterInfo oobsMessageMasterInfo;
		for (int temp = 0; temp < nList2.getLength(); temp++) {

			Node nNode = nList2.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				notificationState = eElement.getElementsByTagName("ns2:notificationState").item(0).getTextContent();
				logger.info("notificationState : " + notificationState);

				if (notificationState.equals("NO_ERROR")) {
					stateList.add(notificationState);
					logger.info("\n mapDoc : " + stateList.toString());

					//Check obbsdatamster db, check with msgId
					//if there is a pending tx don't do anything
					oobsMessageMasterInfo = oobsMessagemasterService.findOOBSDataByMessageId(msgId);

					if (oobsMessageMasterInfo == null) {
						logger.info("calling notificationCallBback method.....");
						notificationCallBback(doc2, msgId);
						break;
					} else {
						logger.error("NotificationCallback : There is a pending transaction!");
					}
				}
			}
			logger.info("temp : " + temp + " notificationState : " + notificationState);
		}
		return stateList;
	}


	/**
	 * TO DO Will be used for report backend later. HERE just to say that the
	 * message has been notified (STEP 0A)
	 * 
	 * @param doc
	 * @param msgId
	 */
	private void notificationCallBback(Document doc, String msgId) {

		logger.info("*********** NotificationCallback ***********");
		String userId = "";
		String msgType = "";
		String hashedData = "";
		String operationType = "";
		
		String signdata = "";
		String description = "";
		String trans_id = "";
		String challenge = EzioMobileDemoConstant.CHALLENGE_30000003;

		Map<String, String> mapData = null;
		Set set = null; // map.entrySet();
		Iterator iter = null; // set.iterator();

		if (msgId == null) {
			logger.info("NOTIFICATION CALLBACK : msgId found null!");
			return;
		}

		// STEP 1
		// get the userid from the msgid...
		mapData = oobsWebHelper.getInfoFromMesgID(msgId, true, false); // getInfoFromMesgID(msgId,
		logger.info("[Notification Callback] mapData : "+mapData.toString());

		if (mapData == null) {
			logger.info("NOTIFICATION CALLBACK : Map-Data found null!");
			return;
		}

		set = mapData.entrySet();
		iter = set.iterator();

		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (entry.getKey().equals("userID")) {
				userId = (String) entry.getValue();
				System.out.println("UserMessageCallback: *********** userId " + userId);
			} else if (entry.getKey().equals("msgtype")) {
				msgType = (String) entry.getValue();
				System.out.println("UserMessageCallback: *********** msgType " + msgType);
			}else if(entry.getKey().equals("hasheddata")){
				hashedData = (String) entry.getValue();
				System.out.println("UserMessageCallback: *********** hashedData " + hashedData);
			}else if (entry.getKey().equals("operationType")) {
				operationType = (String) entry.getValue();
				System.out.println("UserMessageCallback: *********** operationType " + operationType);
			}
		}

		if (!(userId != null && msgType != null && hashedData != null)) {
			logger.info("NOTIFICATION CALLBACK : UserID or MsgType found null!");
			return;
		}

		try {
			logger.info("-------- [Notification Callback] ---------");
			logger.info("UserId : "+userId+" --- Type : "+msgType+" ---- hashedData : "+hashedData);
			logger.info("------------------------------------------");
			// insert ok "00" means all OK
			//msgType 00 = P2P operation
			if(!msgType.equals("02") && operationType!=null && !operationType.equals("")){
				UUID uid = UUID.randomUUID();
				trans_id = uid.toString();
				description = operationType;
				signdata = challenge + ";"+operationType;
			}

			if(!msgType.equals("02")){

				int uId = usermasterService.findUidByUserId(userId);
				logger.info("[Notification Callback] UId : "+uId);

				saveSignDataInfo(msgId, hashedData, signdata, description, trans_id, uId);

				saveCallbackData(msgId, userId);
			}

			logger.info("inserting oobsdatamaster....");
			OOBSMessageMasterInfo oobsInfo = new OOBSMessageMasterInfo();
			oobsInfo.setMessageId(msgId);
			oobsInfo.setUserId(userId);
			oobsInfo.setMessageStatus(EzioMobileDemoConstant.OOBS_MESSAGE_STATUS_NO_ERROR_DETECTED); // Status : 00
			oobsInfo.setMessageType(msgType);

			oobsMessagemasterService.saveOOBSMessagemasterInfo(oobsInfo);
			logger.info("[Notification Callback] oobsdatamaster saved!!");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("OOPS09");
		}
	}

	/**
	 *
	 * @param msgId
	 * @param userId
	 * @throws ServiceException
	 */
	private void saveCallbackData(String msgId, String userId) throws ServiceException {
		CallbackDataInfo callbackDataInfo = new CallbackDataInfo();
		callbackDataInfo.setMessageId(msgId);
		callbackDataInfo.setUserId(userId);

		callbackDataService.saveCallbackData(callbackDataInfo);
		logger.info("[Notification Callback] callbackData saved!!");
	}

	/**
	 *
	 * @param msgId
	 * @param hashedData
	 * @param signdata
	 * @param description
	 * @param trans_id
	 * @param uId
	 * @throws ServiceException
	 */
	private void saveSignDataInfo(String msgId, String hashedData, String signdata, String description, String trans_id, int uId) throws ServiceException {
		SignDataInfo signDataInfo = new SignDataInfo();
		signDataInfo.setMsgid(msgId);
		signDataInfo.setSingdata(signdata);
		signDataInfo.setTransactionId(trans_id);
		signDataInfo.setUserId(uId);
		signDataInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
		signDataInfo.setDescription(description);
		signDataInfo.setHasheddata(hashedData);
		signDataInfo.setTransactionDate(new Date());

		signDataService.saveSigndataInfo(signDataInfo);
		logger.info("[Notification Callback] Singndatamaster saved!!");
	}

	/**
	 * Check if pending tx is there, if YES, execute code. Else return, don't do
	 * anything.
	 * 
	 * @param doc
	 * @param userId
	 */
	private void verifyTransactionCallback(Document doc, String userId, String msgId) {

		logger.info("*********** VerifyTransactionCallback ***********");
		String acceptedValue = "";
		String callBackResult = "";
		String msgType = "";
		int responseCode = 0;
		int uId = 0;
		Map<String, String> dataTOStore = new HashMap<String, String>();
		try {

			uId = usermasterService.findUidByUserId(userId);
			logger.info("[verifyTransactionCallback] UserID : "+userId);

			SignDataInfo signDataInfo = signDataService.findSigndataByUserId(uId);
			logger.info("[verifyTransactionCallback] SignDataInfo : "+signDataInfo.toString());

			if (msgId == null || msgId.equals("")) {
				return;
			}

			//get the type of msg from DB
			msgType = oobsMessagemasterService.findMsgTypeByMessageId(msgId);
			logger.info("[verifyTransactionCallback] msgType : "+msgType);

			logger.info("We have an entry in OOBS data master!");
			logger.info("[VerifyTransactionCallback] Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("accepted");
			if (nList.getLength() > 0) {
				acceptedValue = nList.item(0).getChildNodes().item(0).getNodeValue();
				System.out.println("acceptedValue : " + acceptedValue);
			}

			if(Boolean.parseBoolean(acceptedValue)){
				nList = doc.getElementsByTagName("entry");
				logger.info("\n----------------------------");
				logger.info("[VerifyTransactionCallback] Length of Meta : " + nList.getLength());
				logger.info("----------------------------\n");

				Map<String, String> mapData = new HashMap<>();
				mapData = generateTransactionsMap(nList);
				logger.info("\n mapData : " + mapData.toString());

				// If below condition TRUE, Get Sing or Hashed data from DB
				// (Signdata master table).
				//Transaction with 2FA mode.
				if (mapData.containsKey("HASH") && mapData.containsKey("OTP")) {

					String callBackHashedData = mapData.get("HASH");
					String otp = mapData.get("OTP");

					// compare with callback Hashdata and validate the OTP
					// compared data is OK, send 200 response to front-end
					if (signDataInfo.getHasheddata().equals(callBackHashedData)) {
						boolean isOTPValid = commonWebHelper.validateOTP(userId, otp, callBackHashedData);

						if (isOTPValid) {
							logger.info("Transaction validated with 2FA....");
							dataTOStore = oobsWebHelper.getInfoFromMesgID(msgId, true, true);
							doBasedOnOperationType(msgType, userId, uId, dataTOStore);

							callBackResult = EzioMobileDemoConstant.CALLBACK_RESULT_RESPONSE_VALID_OTP;
							responseCode = EzioMobileDemoConstant.RESPONSE_CODE_200;
						}
						else{
							callBackResult = EzioMobileDemoConstant.CALLBACK_RESULT_RESPONSE_INVALID_OTP;
							responseCode = EzioMobileDemoConstant.RESPONSE_CODE_401;
						}
					}else{
						callBackResult = EzioMobileDemoConstant.CALLBACK_RESULT_RESPONSE_INVALID_OTP;
						responseCode = EzioMobileDemoConstant.RESPONSE_CODE_401;
					}
				} else {
					logger.info("Transaction validated with 1FA....");
					dataTOStore = oobsWebHelper.getInfoFromMesgID(msgId, true, true);
					doBasedOnOperationType(msgType, userId, uId, dataTOStore);

					callBackResult = EzioMobileDemoConstant.CALLBACK_RESULT_RESPONSE_TRANSACTION_VALIDATED_WITH_1FA;
					responseCode = EzioMobileDemoConstant.RESPONSE_CODE_200;
				}
			}
			else {
				callBackResult = EzioMobileDemoConstant.CALLBACK_RESULT_RESPONSE_TRANSACTION_REJECTED;
				responseCode = EzioMobileDemoConstant.RESPONSE_CODE_401;
			}

			// every time delete OOBS data, signdata and callback data
			logger.info("[VerifyTransactionCallback] updating Callback data master...");
			callbackDataService.updateCallBackData(userId, msgId, callBackResult, responseCode);

			logger.info("[VerifyTransactionCallback] deleting message entry from OOBS Data Master...");
			oobsMessagemasterService.deleteOOBSMessageDataByUserId(userId);

			logger.info("[VerifyTransactionCallback] deleting message entry from Sign Data Master...");
			signDataService.deleteSigndataByUserId(uId);
		} catch (Exception e) {
			logger.info(
					"[VerifyTransactionCallback] Unable to delete message entry from OOBS-Data or Sign-Data Master...");
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param nList
	 * @return
	 */
	private Map generateTransactionsMap(NodeList nList) {
		Map<String, String> mapData = new HashMap<>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				String keyValue = eElement.getElementsByTagName("key").item(0).getTextContent();
				logger.info("[VerifyTransactionCallback] key : " + keyValue);

				String valueData = eElement.getElementsByTagName("value").item(0).getTextContent();
				logger.info("[VerifyTransactionCallback] valueData : " + valueData);

				mapData.put(keyValue, valueData);
			}
		}

		return mapData;
	}

	/**
	 * @param doc
	 * @param userId
	 * @return
	 */
	private void deliveryCallback(Document doc, String userId) {
		System.out.println("DeliveryCallback: ***********");
	}

	/**
	 * TO DO Will be used for report backend later. HERE the message has been FETCHED (STEP 2A)
	 * @param userId
	 * @param msgId
	 * @return
	 */
	private void readNotifyCallback(String msgId, String userId) {

		logger.info("*********** ReadNotifyCallback: ***********");
		logger.info("[ReadNotifyCallback] userId : "+userId);
		String msgtypelocal = "";
		try {
			int uId = usermasterService.findUidByUserId(userId);
			logger.info("[ReadNotifyCallback] uID : "+uId);

			// msgtypelocal = getMessageType(msgId);
			msgtypelocal = oobsMessagemasterService.findMsgTypeByMessageId(msgId);

			// P2P msgtype = 02 else 01
			if (msgtypelocal != null && msgtypelocal.equals("02")) {

				logger.info("[ReadNotifyCallback] deleting message entry from OOBS Data Master...");
				oobsMessagemasterService.deleteOOBSMessageDataByUserId(userId);

				logger.info("[ReadNotifyCallback] deleting message entry from Sign Data Master...");
		//		signDataService.deleteSigndataByUserId(uId); as no entry will be found

				logger.info("[ReadNotifyCallback] deleting message entry from Callback data master...");
		//		callbackDataService.deleteCallbackDataByUserId(userId); as no entry will be found
			} else {
				// not normal
				logger.info("[ReadNotifyCallback] NOT NORMAL!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("[ReadNotifyCallback] Unable to delete message entry from OOBS-Data or Sign-Data Master...");
		}
		//return resultStatus;
	}

	/**
	 * @param doc
	 * @param userId
	 */
	private void replyCallback(Document doc, String userId) {
		logger.info("*********** ReplyCallback: ***********");
		//return null;
	}

	/**Post Callback will happen for Mobile Registration
	 * @param doc
	 * @return
	 */
	private void postCallback(Document doc) {
		logger.info("*********** PostCallback ***********");

		String userId = null;
		String pinCode = null;
		String regCode = null;
		Map <String, String> mapData = new HashMap<String, String>();
		Set set = null; // map.entrySet();
		Iterator iter = null; // set.iterator();
		try {

			logger.info("[postTransactionCallback]");

			String content = null;
			NodeList nList = doc.getElementsByTagName("content");
			content = nList.item(0).getChildNodes().item(0).getNodeValue();
			if(content!=null && !content.equals("")){
				content = new String(DatatypeConverter.parseBase64Binary(content));
				
				logger.info("{postTransactionCallback} Content : "+content);
				
				try{
					JSONObject resJSON_Obj = new JSONObject(content);
					
					logger.info("{postTransactionCallback} resJSON_Obj : "+resJSON_Obj);
					
					 userId = resJSON_Obj.getString(USER_ID_PARAM);
					 pinCode = resJSON_Obj.getString(PIN_CODE_ATTR);
					 regCode = resJSON_Obj.getString(REG_CODE_ATTR);
					 mapData.put(USER_ID_PARAM, userId);
					 mapData.put(PIN_CODE_ATTR, pinCode);
					 mapData.put(REG_CODE_ATTR, regCode);
				
				}catch(Exception e){
					logger.error("{postTransactionCallback} Parsing Exception!");
					e.printStackTrace();
				}
			}
			if (mapData != null) {
				set = mapData.entrySet();
				iter = set.iterator();

				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					switch((String)entry.getKey()) {
						case USER_ID_PARAM:
							userId = (String) entry.getValue();
							logger.info("PostCallback: *********** userId " + userId);
							break;
						case PIN_CODE_ATTR:
							pinCode = (String) entry.getValue();
							logger.info("PostCallback: *********** pinCode " + pinCode);
							break;
						case REG_CODE_ATTR:
							regCode = (String) entry.getValue();
							logger.info("PostCallback: *********** regCode " + regCode);
							break;
						default:
							break;
					}
				}

				int uId = usermasterService.findUidByUserId(userId);
				
				//09/08/2018
				//Search for the record that match with UID, RegCode and PinCode 
				//And update the status with 1.
				//If Record is not found, do nothing, print some log
				int updateCount = devicemasterService.updateDeviceInfoStatusByUserIdAndRegCodeAndPinCode(uId, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, regCode, pinCode);
				if(updateCount>0)
					logger.info("Updated : DeviceInfo status for UID : "+userId+ "with Status : "+EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
				else
					logger.info("Coudn't Update : DevicceInfo status for UID : "+userId+ "with Status : "+EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
			}
		}catch(Exception e){
			logger.error("{postTransactionCallback} Exception!");
		}
	}

	/**
	 * TO DO GET MESSAGE ID, search if associated to a user. If so delete DB
	 * entry.
	 * 
	 * @param userId
	 * @return
	 */
	private void expireCallback(String userId, String msgId) {

		
		logger.info("*********** ExpireCallback ***********");
		logger.info("[ExpireCallback] userId : "+userId);
		String msgtypelocal = "";			
		try {
			int uId = usermasterService.findUidByUserId(userId);
			logger.info("[ReadNotifyCallback] uID : "+uId);
			// deleteMsgID(msgId);

			// msgtypelocal = getMessageType(msgId);
			msgtypelocal = oobsMessagemasterService.findMsgTypeByMessageId(msgId);

			logger.info("[ExpireCallback] deleting message entry from OOBS Data Master...");
			oobsMessagemasterService.deleteOOBSMessageDataByUserId(userId);

			// P2P msgtype = 02 else 01
			if (msgtypelocal != null &&  !msgtypelocal.equals("02")) {
			
				logger.info("[ExpireCallback] deleting message entry from Sign Data Master...");
				signDataService.deleteSigndataByUserId(uId);

				logger.info("[ExpireCallback] deleting message entry from Callback data master...");
				callbackDataService.deleteCallbackDataByUserId(userId);
			}
			
		} catch (Exception e) {
			logger.info("[ExpireCallback] deleting message entry from OOBS-Data or Sing-Data or Call-back-data Master...");
			e.printStackTrace();
		}
	}

	/**
	 * @param doc
	 * @param userId
	 */
	private void userMessageCallback(Document doc, String userId) {
		logger.info("*********** UserMessageCallback: ***********");

	}

	/**
	 * @param doc
	 * @param userId
	 */
	private void errorReportCallback(Document doc, String userId) {
		logger.info("*********** ErrorReportCallback: ***********");
	}

	/**Delete callback data from db as soon as the job is done
	 * @param userId
	 * @throws ControllerException
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = "/deletecallbackdata.action", method = RequestMethod.POST)
	public void deleteCallbackData(@RequestParam(USER_ID_PARAM) String userId) throws ControllerException{
		try {
			//callbackDataService.deleteCallbackDataByMessageId(messageId);
			callbackDataService.deleteCallbackDataByUserId(userId);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * @param msgType
	 * @param userId
	 * @param uId
	 * @param dataTOStore
	 */
	private void doBasedOnOperationType(String msgType, String userId, int uId, Map<String, String> dataTOStore){
		logger.info("msgType : "+msgType);
		
		//Add new beneficiary
		String payeeName = "";
		String payeeAccount = "";
		
		//Money Transfer
		String fromAccountNo = "";
		String toAccountNo = "";
		String amount = "";
		String panNo = "";
		
		boolean isDBUpdated = false;
		
		try {
			switch (msgType) {
				
			case EzioMobileDemoConstant.OPERATION_TYPE_LOGIN_01:
				logger.info(OPERATION_TYPE_MESSAGE + EzioMobileDemoConstant.OPERATION_TYPE_LOGIN_01);
				ResultStatus resultStatus = loginWebHelper.setupUserAccountsAndDB(userId);
				logger.info("[Setup user accounts and DB ] resultStatus : "+resultStatus.toString());
				break;
				
			case EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY_11:
				
				logger.info(OPERATION_TYPE_MESSAGE + EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY_11);
				payeeName = dataTOStore.get(PAYEE_NAME_ATTR);
				payeeAccount = dataTOStore.get(PAYEE_ACCOUNT_ATTR);
				
				isDBUpdated = transactionWebHelper.updateDBForAddBeneficiaryOperations(uId, payeeName, payeeAccount, EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY);
				logger.info("isBeneficiaryAdded : "+isDBUpdated);
				
				break;
				
			case EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER_12:
				
				logger.info(OPERATION_TYPE_MESSAGE + EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER_12);
				fromAccountNo = dataTOStore.get(FROM_ACCOUNT_NO_PARAM);
				toAccountNo = dataTOStore.get(TO_ACCOUNT_NO_PARAM);
				amount = dataTOStore.get(AMOUNT_PARAM);
				
				isDBUpdated = transactionWebHelper.updateDBForMoneyTransferOperations(uId, fromAccountNo, toAccountNo, Integer.parseInt(amount), EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER);
				logger.info("isMoneyTransferDone : " + isDBUpdated);
				
				break;
				
			case EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS_13:

				logger.info(OPERATION_TYPE_MESSAGE + EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS_13);

				panNo = dataTOStore.get(PAN_NO_PARAM);
				amount = dataTOStore.get(AMOUNT_PARAM);
				
				isDBUpdated = transactionWebHelper.updateDBForEcommereceOperations(uId, panNo, Integer.parseInt(amount), EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS);
				logger.info("isPaymentDone : "+isDBUpdated);
				
				break;
				
			case EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE_14:

				logger.info(OPERATION_TYPE_MESSAGE +EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE_14);
				
				panNo = dataTOStore.get(PAN_NO_PARAM);
				String cvv = dataTOStore.get(CVV_ATTR);
				String expDate = dataTOStore.get(EXP_DATE_ATTR);
				
				isDBUpdated = transactionWebHelper.updateDBForCardIssuanceOperations(uId, panNo, cvv, expDate, EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE);
				logger.info("isNewCardRequestUpdated : "+isDBUpdated);
				
				break;

			default:
				break;
			}
		}
		catch (Exception e) {
			logger.error(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
			e.printStackTrace();
		}
		
	}

	/**
	 * Exception handler
	 * @return ResultStatus object with response code 
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus oobsErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}

}
