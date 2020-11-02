package com.gemalto.eziomobile.demo.controller;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.UserMasterDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.CallbackDataInfo;
import com.gemalto.eziomobile.demo.model.PanMasterInfo;
import com.gemalto.eziomobile.demo.model.QRTokenMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;
import com.gemalto.eziomobile.demo.service.callbackdata.CallbackDataService;
import com.gemalto.eziomobile.demo.service.qrtokenmaster.QRTokenmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.demo.webhelper.login.LoginWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;
import com.gemalto.eziomobile.webhelper.oobs.OOBSWebHelper;
import com.gemalto.mno.qrip.QRToken;
import com.gemalto.mno.qrip.QRTokenUsage;
import com.gemalto.mno.qrip.QRTokenUtil;
import com.gemalto.mno.qrip.QRTokenVersion;
import com.gemalto.mno.qrip.QRtokenAlgo;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.HAS_DEVICE;

@RestController
public class LoginController {

	ScheduledExecutorService executor = null;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private UsermasterService usermasterService;

	@Autowired
	private CASWebHelper casWebHelper;

	@Autowired
	private OOBSWebHelper oobsWebHelper;

	@Autowired
	private LoginWebHelper loginWebHelper;

	@Autowired
	private URLUtil urlUtil;

	@Autowired
	private CommonWebHelper commonWebHelper;

	@Autowired
	private CallbackDataService callbackDataService;

	@Autowired
	private LoginWebHelper loginHelper;

	@Autowired
	QRTokenmasterService qrTokenmasterService;


	private static final LoggerUtil logger = new LoggerUtil(LoginController.class.getClass());

	public static byte[] TAG_LOGIN = {(byte)0xBF, (byte)0x19, (byte)0x00};

	public static byte[] SWYSV1 = {(byte)0xE3, (byte)0x07, (byte)0x53, (byte)0x57,
			(byte)0x59, (byte)0x53, (byte)0x20, (byte)0x56, (byte)0x31};

	/**
	 * @param username
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/checkusername.user", method = RequestMethod.POST)
	public ResultStatus authenticateUser(@RequestParam("username") String username) throws ControllerException {

		logger.info("Username : " + username);

		UserMasterInfo userMasterInfo = null;
		ResultStatus resultStatus = new ResultStatus();
		boolean flag = false;
		boolean isUserRegToOOBServer = false;
		Map<String, String> supportDeviceMap = new HashMap<String, String>();
		Map<String, Boolean> hasDeviceMap = new HashMap<String, Boolean>();
		Map<String, String> QRTokenDetails = new HashMap<String, String>();

		hasDeviceMap.put(HAS_DEVICE, false);
		try {
			userMasterInfo = usermasterService.findUserInfoByUserId(username);
			if (userMasterInfo != null) {
				QRTokenDetails.put("sQRCodeVersionUsed", null);
				QRTokenDetails.put("sQRCodeData", null);
				logger.info("Username" + username + " exist with id : " + userMasterInfo.getuId());

				resultStatus.setResponseCode(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB_1);
				resultStatus.setMessage(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				resultStatus.setTemplateObject(hasDeviceMap);
				resultStatus.setTempObject(QRTokenDetails);

				casWebHelper.authenticateCASever();
				flag = casWebHelper.isUserRegisterOnCASServer(username);
				if (flag) {

					supportDeviceMap = casWebHelper.getDeviceCountForLogin(username);

					String qrtoken_name = null;
					if(supportDeviceMap.containsKey("qrtokenName")) {
						qrtoken_name = supportDeviceMap.get("qrtokenName");
						supportDeviceMap.remove("supportDeviceMap");
					}
					/*
					 * supportDeviceMap.put("tokenCount", String.valueOf(0));
					 * supportDeviceMap.put("deviceWithType_1",
					 * String.valueOf(1));
					 * supportDeviceMap.put("deviceWithType_3",
					 * String.valueOf(2));
					 * supportDeviceMap.put("deviceWithType_Other",
					 * String.valueOf(2));
					 */


					int tokenCount = Integer.parseInt(supportDeviceMap.get("tokenCount"));
					if (tokenCount > 0) {

						logger.info("tokenCount : " + tokenCount);
						// if other tokens found with TokenType : 1 or 3 and
						// device prefix other than GAOC
						if (supportDeviceMap.containsKey("deviceWithType_1")
								|| supportDeviceMap.containsKey("deviceWithType_Other")) {
							hasDeviceMap.put(HAS_DEVICE, true);

							if(qrtoken_name != null) {

								QRToken qrtoken = null;
								byte[] randomness = null;
								final String ENCRYPTION_DFF_NO = "ENCRYPTION_DFF_NO";
								final String ENCRYPTION_DFF_DYNAMIC_KEY = "ENCRYPTION_DFF_DYNAMIC_KEY";
								final String ENCRYPTION_DFF_CUSTOMER_KEY = "ENCRYPTION_DFF_CUSTOMER_KEY";
								final String AUTO = "AUTO";
								final String FIXED = "FIXED";
								String fixed_randvalue = "";
								QRtokenAlgo qrtokenAlgo = null;
								byte[] QRAuthCode = null;
								byte[] key = null;

								String qrtoken_randommode = urlUtil.getQrtoken_randommode();
								QRTokenVersion qrTokenVersion = urlUtil.getQrtoken_qrcodeversion();
								logger.info("[LoginController] qrtoken_randommode : " + qrtoken_randommode);

								switch (qrtoken_randommode) {
									case AUTO:
										String hexString = commonWebHelper.getRandomHexString(8);
										randomness = QRTokenUtil.hexStringToByteArray(hexString);
										break;
									case FIXED:
										fixed_randvalue = urlUtil.getQrtoken_randomvalue();
										logger.info("fixed_randvalue : " + fixed_randvalue);
										randomness = QRTokenUtil.hexStringToByteArray(fixed_randvalue);
										break;
									default:
										break;// TODO handle error
								}

								String qrtoken_encryptionmode = urlUtil.getQrtoken_encryptionmode();
								logger.info("[LoginController] qrtoken_encryptionmode : " + qrtoken_encryptionmode);

								String qrtoken_seedDpuk = null;
								String qrtoken_seedOTP = null;

								switch (qrtoken_encryptionmode) {
									case ENCRYPTION_DFF_NO:
										qrtoken = new QRToken(qrTokenVersion, randomness);
										break;
									case ENCRYPTION_DFF_DYNAMIC_KEY:
										qrtokenAlgo = urlUtil.getQrtoken_algo();
										key = commonWebHelper.getKey(qrtokenAlgo, false);
										logger.info("[LoginController] dynamic key : " + QRTokenUtil.bytesToHexString(key));
										qrtoken_seedDpuk = urlUtil.getQrtoken_seedDpuk();
										logger.info("print qrtoken_seedDpuk:"+ qrtoken_seedDpuk);
										logger.info("print randomness:"+ QRTokenUtil.bytesToHexString(randomness));
										logger.info("print qrtoken_name:"+ qrtoken_name);
										QRAuthCode = commonWebHelper.getDPUKHexCASever(randomness, qrtoken_name, qrtoken_seedDpuk);


										qrtoken = new QRToken(qrTokenVersion, qrtokenAlgo, randomness, QRAuthCode, key);
										break;
									case ENCRYPTION_DFF_CUSTOMER_KEY:
										qrtokenAlgo = urlUtil.getQrtoken_algo();
										key = commonWebHelper.getKey(qrtokenAlgo, true);
										logger.info("[LoginController] customer key : " + QRTokenUtil.bytesToHexString(key));
										qrtoken = new QRToken(qrTokenVersion, qrtokenAlgo, randomness, key);
										break;
									default:
										break;// TODO handle error
								}

								qrtoken_seedOTP = urlUtil.getQrtoken_seedOTP();
								byte[] apduData = null;
								byte application = (byte) Integer.parseInt(qrtoken_seedOTP);
								QRTokenUsage qrcodeusage = QRTokenUsage.FULL_FREE_TEXT;

								ByteArrayOutputStream transaction_FREE_TEXT = new ByteArrayOutputStream();
								transaction_FREE_TEXT.write(SWYSV1, 0, SWYSV1.length);
								transaction_FREE_TEXT.write(TAG_LOGIN, 0, TAG_LOGIN.length);

								apduData =  transaction_FREE_TEXT.toByteArray();
								logger.info("[LoginController] apduData : " + QRTokenUtil.bytesToHexString(apduData));
								String sQRCodeData = QRTokenUtil.bytesToHexString(qrtoken.generateQRCodeData(qrcodeusage, application, apduData));

								logger.info("[LoginController] sQRCodeData : " + sQRCodeData);
								String sQRCodeVersionUsed = commonWebHelper.getStringQRCodeVersion(qrtoken.getQRCodeTokenVersionUsed());
								logger.info("[LoginController] sQRCodeVersionUsed : " + sQRCodeVersionUsed);
								String sTransactionHash = qrtoken.getSigningBuffer(urlUtil.getQrtoken_hashMode());
								logger.info("[LoginController] sTransactionHash : " + sTransactionHash);

								QRTokenDetails.put("sQRCodeVersionUsed", sQRCodeVersionUsed);
								QRTokenDetails.put("sQRCodeData", sQRCodeData);

								String transactionType = EzioMobileDemoConstant.OPERATION_TYPE_LOGIN_01;

								int count = qrTokenmasterService.countByUserId(username);
								logger.info("print the count :"+ count);
								if(count>0)
									qrTokenmasterService.deleteQRTokenDetailsByUserId(username);



								logger.info("[LoginController] : QRToken code info saved!");

								QRTokenMasterInfo qrTokenMasterInfo = new QRTokenMasterInfo();
								qrTokenMasterInfo.setQrcodeVersion(sQRCodeVersionUsed);
								qrTokenMasterInfo.setTransactionHash(sTransactionHash);
								qrTokenMasterInfo.setTransactionType(transactionType);
								qrTokenMasterInfo.setUserId(username);

								qrTokenmasterService.saveQRTokenMasterInfo(qrTokenMasterInfo);

								//attach sQRCodeData and sQRCodeVersionUsed to the response
							}

							isUserRegToOOBServer = oobsWebHelper.isUserRegisteredOnOOBServer(username);
							logger.info(" tokens found with TokenType : 1 or 3 and device prefix other than GAOC");

							if (isUserRegToOOBServer) {
								resultStatus.setResponseCode(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB_CAS_OOBS_3);
								resultStatus.setMessage(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB_CAS_OOBS);
								resultStatus.setStatusCode(HttpStatus.ACCEPTED);
								resultStatus.setTemplateObject(hasDeviceMap);
								resultStatus.setTempObject(QRTokenDetails);
							} else {
								resultStatus.setResponseCode(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB_CAS_2);
								resultStatus.setMessage(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB_CAS);
								resultStatus.setStatusCode(HttpStatus.ACCEPTED);
								resultStatus.setTemplateObject(hasDeviceMap);
								resultStatus.setTempObject(QRTokenDetails);
							}
						}
						// if tokens found with TokenType : 3 and device prefix
						// is GAOC
						else if (supportDeviceMap.containsKey("deviceWithType_3")) {
							logger.info("found with TokenType : 3 and device prefix is GAOC");
							hasDeviceMap.put(HAS_DEVICE, true);
							resultStatus.setResponseCode(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB_1);
							resultStatus.setMessage(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB);
							resultStatus.setStatusCode(HttpStatus.ACCEPTED);
							resultStatus.setTemplateObject(hasDeviceMap);
							resultStatus.setTempObject(QRTokenDetails);
						}
						// if other tokens found with TokenType : 3 and device
						// prefix other than GAOC
						else if (supportDeviceMap.containsKey("deviceWithType_Other")) {
							hasDeviceMap.put(HAS_DEVICE, true);
							resultStatus.setResponseCode(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB_1);
							resultStatus.setMessage(EzioMobileDemoConstant.EZIODEMO_USER_FOUND_IN_DB);
							resultStatus.setStatusCode(HttpStatus.ACCEPTED);
							resultStatus.setTemplateObject(hasDeviceMap);
							resultStatus.setTempObject(QRTokenDetails);
						}
					}
				}
			} else {
				resultStatus.setResponseCode(EzioMobileDemoConstant.EZIODEMO_USER_NOT_FOUND_0);
				resultStatus.setMessage(EzioMobileDemoConstant.EZIODEMO_USER_NOT_FOUND);
				resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
				resultStatus.setTemplateObject(hasDeviceMap);
				resultStatus.setTempObject(QRTokenDetails);
			}

		} catch (Exception e) {
			logger.info("Unable find user with username : " + username, e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 * @param hasDeviceMap
	 * @param QRTokenDetails
	 * @param eziodemoUserFoundInDb1
	 * @param eziodemoUserFoundInDb
	 * @param accepted
	 * @return
	 */
	private ResultStatus setResultStatus(Map<String, Boolean> hasDeviceMap, Map<String, String> QRTokenDetails, int eziodemoUserFoundInDb1, String eziodemoUserFoundInDb, HttpStatus accepted) {

		ResultStatus resultStatus = new ResultStatus();
		resultStatus.setResponseCode(eziodemoUserFoundInDb1);
		resultStatus.setMessage(eziodemoUserFoundInDb);
		resultStatus.setStatusCode(accepted);
		resultStatus.setTemplateObject(hasDeviceMap);
		resultStatus.setTempObject(QRTokenDetails);

		return resultStatus;
	}

	/**
	 * @param username
	 * @param QRTokenDetails
	 * @param qrtoken_name
	 * @throws ParseException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws ServiceException
	 */
	private void generateAndSaveQRToken(String username, Map<String, String> QRTokenDetails, String qrtoken_name) throws Exception {
		if(qrtoken_name != null) {

			QRToken qrtoken = null;
			byte[] randomness = null;
			final String ENCRYPTION_DFF_NO = "ENCRYPTION_DFF_NO";
			final String ENCRYPTION_DFF_DYNAMIC_KEY = "ENCRYPTION_DFF_DYNAMIC_KEY";
			final String ENCRYPTION_DFF_CUSTOMER_KEY = "ENCRYPTION_DFF_CUSTOMER_KEY";
			final String AUTO = "AUTO";
			final String FIXED = "FIXED";
			String fixed_randvalue = "";
			QRtokenAlgo qrtokenAlgo = null;
			byte[] QRAuthCode = null;
			byte[] key = null;

			String qrtoken_randommode = urlUtil.getQrtoken_randommode();
			QRTokenVersion qrTokenVersion = urlUtil.getQrtoken_qrcodeversion();
			logger.info("[LoginController] qrtoken_randommode : " + qrtoken_randommode);

			switch (qrtoken_randommode) {
				case AUTO:
					String hexString = commonWebHelper.getRandomHexString(8);
					randomness = QRTokenUtil.hexStringToByteArray(hexString);
					break;
				case FIXED:
					fixed_randvalue = urlUtil.getQrtoken_randomvalue();
					logger.info("fixed_randvalue : " + fixed_randvalue);
					randomness = QRTokenUtil.hexStringToByteArray(fixed_randvalue);
					break;
				default:
					break;
			}

			String qrtoken_encryptionmode = urlUtil.getQrtoken_encryptionmode();
			logger.info("[LoginController] qrtoken_encryptionmode : " + qrtoken_encryptionmode);

			String qrtoken_seedDpuk = null;
			String qrtoken_seedOTP = null;

			switch (qrtoken_encryptionmode) {
				case ENCRYPTION_DFF_NO:
					qrtoken = new QRToken(qrTokenVersion, randomness);
					break;
				case ENCRYPTION_DFF_DYNAMIC_KEY:
					qrtokenAlgo = urlUtil.getQrtoken_algo();
					key = commonWebHelper.getKey(qrtokenAlgo, false);
					logger.info("[LoginController] dynamic key : " + QRTokenUtil.bytesToHexString(key));
					qrtoken_seedDpuk = urlUtil.getQrtoken_seedDpuk();
					logger.info("print qrtoken_seedDpuk:"+ qrtoken_seedDpuk);
					logger.info("print randomness:"+ QRTokenUtil.bytesToHexString(randomness));
					logger.info("print qrtoken_name:"+ qrtoken_name);
					QRAuthCode = commonWebHelper.getDPUKHexCASever(randomness, qrtoken_name, qrtoken_seedDpuk);


					qrtoken = new QRToken(qrTokenVersion, qrtokenAlgo, randomness, QRAuthCode, key);
					break;
				case ENCRYPTION_DFF_CUSTOMER_KEY:
					qrtokenAlgo = urlUtil.getQrtoken_algo();
					key = commonWebHelper.getKey(qrtokenAlgo, true);
					logger.info("[LoginController] customer key : " + QRTokenUtil.bytesToHexString(key));
					qrtoken = new QRToken(qrTokenVersion, qrtokenAlgo, randomness, key);
					break;
				default:
					throw new Exception("Error: invalid Encryption mode: '" + qrtoken_encryptionmode + "'.");
			}

			qrtoken_seedOTP = urlUtil.getQrtoken_seedOTP();
			byte[] apduData = null;
			byte application = (byte) Integer.parseInt(qrtoken_seedOTP);
			QRTokenUsage qrcodeusage = QRTokenUsage.FULL_FREE_TEXT;

			ByteArrayOutputStream transaction_FREE_TEXT = new ByteArrayOutputStream();
			transaction_FREE_TEXT.write(SWYSV1, 0, SWYSV1.length);
			transaction_FREE_TEXT.write(TAG_LOGIN, 0, TAG_LOGIN.length);

			apduData =  transaction_FREE_TEXT.toByteArray();
			logger.info("[LoginController] apduData : " + QRTokenUtil.bytesToHexString(apduData));
			String sQRCodeData = QRTokenUtil.bytesToHexString(qrtoken.generateQRCodeData(qrcodeusage, application, apduData));

			logger.info("[LoginController] sQRCodeData : " + sQRCodeData);
			String sQRCodeVersionUsed = commonWebHelper.getStringQRCodeVersion(qrtoken.getQRCodeTokenVersionUsed());
			logger.info("[LoginController] sQRCodeVersionUsed : " + sQRCodeVersionUsed);
			String sTransactionHash = qrtoken.getSigningBuffer(urlUtil.getQrtoken_hashMode());
			logger.info("[LoginController] sTransactionHash : " + sTransactionHash);

			QRTokenDetails.put("sQRCodeVersionUsed", sQRCodeVersionUsed);
			QRTokenDetails.put("sQRCodeData", sQRCodeData);

			String transactionType = EzioMobileDemoConstant.OPERATION_TYPE_LOGIN_01;

			int count = qrTokenmasterService.countByUserId(username);
			logger.info("print the count :"+ count);
			if(count>0) {
				qrTokenmasterService.deleteQRTokenDetailsByUserId(username);
			}

			logger.info("[LoginController] : QRToken code info saved!");

			QRTokenMasterInfo qrTokenMasterInfo = new QRTokenMasterInfo();
			qrTokenMasterInfo.setQrcodeVersion(sQRCodeVersionUsed);
			qrTokenMasterInfo.setTransactionHash(sTransactionHash);
			qrTokenMasterInfo.setTransactionType(transactionType);
			qrTokenMasterInfo.setUserId(username);

			qrTokenmasterService.saveQRTokenMasterInfo(qrTokenMasterInfo);
		}
	}

	/**
	 * @param username
	 * @param password
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/authenticate.user", method = RequestMethod.POST)
	public ResultStatus authenticateUser(@RequestParam("username") String username,
										 @RequestParam("password") String password) throws ControllerException {

		logger.info("Username : " + username);
		logger.info("Password : " + password);
		boolean isUserCreated = false;
		boolean flag = false;
		UserMasterInfo userMasterInfo = null;
		ResultStatus resultStatus = new ResultStatus();
		UserMasterDTO userMasterDTO = null;
		try {
			if (usermasterService.isValidUser(username, password)) {
				userMasterInfo = usermasterService.findUserInfoByUserId(username);
				httpSession.setAttribute("userId", username);

				casWebHelper.authenticateCASever();

				flag = casWebHelper.isUserRegisterOnCASServer(username);

				logger.info("flag : "+flag);
				if(!flag){
					isUserCreated = casWebHelper.createAndActiveUserOnCAS(username);
					logger.info("isUserCreated: "+isUserCreated);

					int resultStatus1 = loginWebHelper.checkEMVForNonCASUser(username);
					logger.info("[AuthenticateUser] resultStatus1 : "+resultStatus1);

					if(resultStatus1 == -1) {
						throw new Exception();
					}

				}

				ResultStatus resultStatus2 = loginWebHelper.setupUserAccountsAndDB(username);
				logger.info("[AuthenticateUser] resultStatus2 : "+resultStatus2.toString());

				userMasterDTO = loginHelper.createUserMasterDTO(userMasterInfo);

				resultStatus.setTemplateObject(userMasterDTO);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setMessage(EzioMobileDemoConstant.VALID_USER);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				return resultStatus;
			} else {
				resultStatus.setTemplateObject(userMasterDTO);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setMessage(EzioMobileDemoConstant.INVALID_USERNAME_OR_PASSWORD_MSG);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				return resultStatus;
			}
		} catch (Exception e) {
			logger.info("Unable to get login!", e);
			throw new ControllerException(e);
		}
	}

	/**
	 * @param userId
	 * @param otpValue
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/validateotplogin.action", method = RequestMethod.POST)
	public ResultStatus validateOTPLogin(@RequestParam("username") String userId,
										 @RequestParam("otpValue") String otpValue) throws ControllerException {

		logger.info("userId : " + userId);
		logger.info("otpValue : " + otpValue);

		UserMasterInfo userMasterInfo = new UserMasterInfo();
		int iCurrentAuthMode = 0;
		boolean isValidUser = false;
		ResultStatus resultStatus = new ResultStatus();
		UserMasterDTO userMasterDTO = null;

		try{
			iCurrentAuthMode = AUTHMODE_USERNAME_AND_OTP_TIME_BASED;
			logger.info("<------- iCurrentAuthMode : " + iCurrentAuthMode + "-------->");

			isValidUser = loginWebHelper.checkLogin(userId, otpValue,iCurrentAuthMode);
			logger.info("[validateotplogin] isValidUser : " + isValidUser);

			if(!isValidUser) {
				String transactionHash = qrTokenmasterService.findtransactionHashByUserIdAndTranscationType(userId);
				if(transactionHash!=null) {
					logger.info("print transactionHash in Login ValidateOTP: "+transactionHash);
				}
				if (transactionHash!=null && !(transactionHash.isEmpty()) && commonWebHelper.validateOTP(userId, otpValue,transactionHash)) {

					logger.info("GAQT-> OTP Login verified ");
					isValidUser = true;
				}
			}

			if (!isValidUser && iCurrentAuthMode == LoginWebHelper.AUTHMODE_USERNAME_AND_OTP_TIME_BASED) {

				iCurrentAuthMode = LoginWebHelper.AUTHMODE_USERNAME_AND_OTP_EVENT_BASED;
				logger.info("<------- iCurrentAuthMode : " + iCurrentAuthMode + "-------->");

				isValidUser = loginWebHelper.checkLogin(userId, otpValue, iCurrentAuthMode);
			}

			if (isValidUser) {

				userMasterInfo =	usermasterService.findUserInfoByUserId(userId);

				ResultStatus resultStatus2 = loginWebHelper.setupUserAccountsAndDB(userId);
				logger.info("[AuthenticateUser] resultStatus2 : "+resultStatus2.toString());

				userMasterDTO = loginHelper.createUserMasterDTO(userMasterInfo);

				resultStatus.setTemplateObject(userMasterDTO);
				resultStatus.setMessage(EzioMobileDemoConstant.VALID_USER);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			} else {
				resultStatus.setTemplateObject(userMasterDTO);
				resultStatus.setMessage(EzioMobileDemoConstant.INVALID_USERNAME_OR_OTP_MSG);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			}
		}catch(Exception e){
			logger.error("Exception occurred in LoginController - validateOTPLogin ");
			throw new ControllerException(e);
		}

		return resultStatus;
	}

	/**
	 * @param userId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/senduserloginnotification.action", method = RequestMethod.POST)
	public ResultStatus sendLoginPushNotification(@RequestParam("userId") String userId) throws ControllerException {

		logger.info("userId : " + userId);

		Map<String, String> pushNotificationData = new HashMap<String, String>();
		ResultStatus resultStatus = new ResultStatus();

		String pushXmlData = "";
		String challenge = EzioMobileDemoConstant.CHALLENGE_30000003;
		String cleardata = "";
		String hashData = "";
		String hostURL = "";

		try {
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[sendLoginPushNotification] backendConfiguration : "+backendConfiguration);

			long epochTime = System.currentTimeMillis() / 1000;
			cleardata = userId + challenge + epochTime;

			UUID uid = UUID.randomUUID();
			String trans_id = uid.toString();

			hashData = commonWebHelper.getSHA1(commonWebHelper.asciiToHex(cleardata));
			logger.info("sendUserLoginNotification hashData : " + hashData);

			String _msgcontent = "{'type':'EzioDemoV2_Login','etime':'" + epochTime + "','username':'" + userId
					+ "','cleardata':'" + cleardata + "','hasheddata':'" + hashData
					+ "','title':'Gemalto', 'transectionid':'" + trans_id + "', 'security':'02', 'challenge': '"
					+ EzioMobileDemoConstant.CHALLENGE_30000003 + "'}";

			byte[] MsgInBytes = _msgcontent.getBytes();
			String msgcontent = DatatypeConverter.printBase64Binary(MsgInBytes);

			hostURL = urlUtil.getHostURL();
			logger.info("[Send login notification] hostURL : " + hostURL);

			switch (backendConfiguration) {
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
					pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
							+ "<ns2:VerifyTransactionRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\" xmlns:ns2=\"http://gemalto.com/ezio/mobile/oobs/api\">"
							+ "<providerId>eziomobileproviderid</providerId>" + "<notificationUserMessage>"
							+ "<message>A login request is pending for your approval</message>"
							+ "</notificationUserMessage>" + "<validityPeriodSecs>60</validityPeriodSecs>" + "<callbackUrl>"
							+ hostURL + "oobsCallBack.action</callbackUrl>" + "<callbackUser></callbackUser>"
							+ "<callbackPassword></callbackPassword>" + "<ns2:verifyMessage>" + "<ns2:locale>en</ns2:locale>"
							+ "<ns2:subject>Verify hello</ns2:subject>"
							+ "<ns2:contentType>text/plain; charset=UTF-8</ns2:contentType>" + "<ns2:content>" + msgcontent
							+ "</ns2:content>" + "</ns2:verifyMessage>" + "</ns2:VerifyTransactionRequest>";

					break;
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
					pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
							+ "<ns2:VerifyTransactionRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\" xmlns:ns2=\"http://gemalto.com/ezio/mobile/oobs/api\">"
							//		+ "<providerId>eziomobileproviderid</providerId>" + "<notificationUserMessage>"
							+ "<providerId>0</providerId>" + "<notificationUserMessage>"
							+ "<message>A login request is pending for your approval</message>"
							+ "</notificationUserMessage>" + "<validityPeriodSecs>60</validityPeriodSecs>" + "<callbackUrl>"
							+ hostURL + "oobsCallBack.action</callbackUrl>" + "<callbackUser></callbackUser>"
							+ "<callbackPassword></callbackPassword>" + "<ns2:verifyMessage>" + "<ns2:locale>en</ns2:locale>"
							+ "<ns2:subject>Verify hello</ns2:subject>"
							+ "<ns2:contentType>text/plain; charset=UTF-8</ns2:contentType>" + "<ns2:content>" + msgcontent
							+ "</ns2:content>" + "</ns2:verifyMessage>" + "</ns2:VerifyTransactionRequest>";

					break;
				default:
					break;
			}


			pushNotificationData = oobsWebHelper.sendNotification(pushXmlData, userId);
			logger.info("pushNotificationData : " + pushNotificationData.toString());

			boolean flag = Boolean.parseBoolean(pushNotificationData.get("isNotificationSent"));

			if (flag && (userId != null && !userId.equals(""))) {

				String messageId = pushNotificationData.get("pushMessageId");

				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setMessage(EzioMobileDemoConstant.PUSH_NOTIFICATION_SENT);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				resultStatus.setTemplateObject(messageId);

			} else {
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				resultStatus.setMessage(EzioMobileDemoConstant.PUSH_NOTIFICATION_NOT_SENT);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			}

		} catch (Exception e) {
			logger.info("Unable to send the notification with userId : " + userId + "\n", e);
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 * Web service endpoint to fetch a callback type
	 *
	 * If not found, the service returns an empty response body with HTTP status
	 * 404.
	 *
	 * @param id
	 *            A Long URL path variable containing the Greeting primary key
	 *            identifier.
	 * @param waitForAsyncResult
	 *            A boolean indicating if the web service should wait for the
	 *            asynchronous email transmission.
	 * @return ResultStatus object, if found, and a HTTP status code as
	 *         described in the method comment.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/getcallbackresponse.action", method = RequestMethod.POST)
	public ResultStatus getResponseCallBack(@RequestParam("userId") String userId,
											@RequestParam("messageId") String messageId,
											@RequestParam(value = "wait", defaultValue = "true") boolean waitForAsyncResult)
			throws InterruptedException, ExecutionException {

		logger.info("> getData for userID :" + userId);
		// boolean isValidate = false;
		UserMasterInfo userMasterInfo = new UserMasterInfo();
		CallbackDataInfo callbackDataInfo = null;
		UserMasterDTO userMasterDTO = null;
		try {
			userMasterInfo = usermasterService.findUserInfoByUserId(userId);

			if (waitForAsyncResult) {

				userMasterDTO = loginHelper.createUserMasterDTO(userMasterInfo);

				for (int i = 0; i < 60; i++) {

					logger.info(new Date() + " checking for response...." + i);
					Thread.sleep(1000);
					Future<CallbackDataInfo> asyncResponse = callbackDataService.sendAsyncWithResult(userId, messageId);
					callbackDataInfo = asyncResponse.get();

					// callbackDataInfo =
					// callbackDataService.findCallbackDataByMessageId(messageId);
					if (callbackDataInfo != null && callbackDataInfo.getResponseCode() != 0) {
						logger.info("-is OTP validated?   " + callbackDataInfo.toString());
						return new ResultStatus(HttpStatus.ACCEPTED, EzioMobileDemoConstant.RESPONSE_CODE_200,
								callbackDataInfo, userMasterDTO);
					}
				}
			} else {
				callbackDataService.getDataAsync(userId, messageId);
			}
		} catch (Exception e) {
			logger.error("A problem occurred sending the Greeting.", e);
			return new ResultStatus(EzioMobileDemoConstant.RESPONSE_CODE_NOT_OK, HttpStatus.NO_CONTENT,
					EzioMobileDemoConstant.RESPONSE_CODE_401);
		}

		if (null != callbackDataInfo && callbackDataInfo.getCallBackResult() != null)
			return new ResultStatus(HttpStatus.ACCEPTED, EzioMobileDemoConstant.RESPONSE_CODE_200, callbackDataInfo, userMasterDTO);
		else {
			Future<CallbackDataInfo> asyncResponse = callbackDataService.sendAsyncWithResult(userId, messageId);
			callbackDataInfo = asyncResponse.get();
			logger.info("-is OTP validated?   " + callbackDataInfo.toString());
		}
		return new ResultStatus(HttpStatus.NO_CONTENT, EzioMobileDemoConstant.RESPONSE_CODE_401, callbackDataInfo, userMasterDTO);
	}

	/**
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResultStatus getLoginPage(@RequestParam("userId") String userId, HttpServletRequest request) {

		ResultStatus resultStatus = new ResultStatus();
		// if()
		if (httpSession != null) {
			httpSession.invalidate();

			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
			resultStatus.setMessage("You have logged off successfully!");
			resultStatus.setStatusCode(HttpStatus.ACCEPTED);
		}
		return resultStatus;
	}


	/**
	 * @return
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus loginErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}

}
