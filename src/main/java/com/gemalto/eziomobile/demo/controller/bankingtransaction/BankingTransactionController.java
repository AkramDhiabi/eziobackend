package com.gemalto.eziomobile.demo.controller.bankingtransaction;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.BankingTransferDTO;
import com.gemalto.eziomobile.demo.dto.TokenDeviceInfo;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.QRTokenMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.qrtokenmaster.QRTokenmasterService;
import com.gemalto.eziomobile.demo.util.MspUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;
import com.gemalto.eziomobile.webhelper.oobs.OOBSWebHelper;
import com.gemalto.mno.qrip.QRToken;
import com.gemalto.mno.qrip.QRTokenUsage;
import com.gemalto.mno.qrip.QRTokenUtil;
import com.gemalto.mno.qrip.QRTokenVersion;
import com.gemalto.mno.qrip.QRtokenAlgo;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.QR_CODE_ENCRYPTED;

@RestController
public class BankingTransactionController {

	private static final LoggerUtil logger = new LoggerUtil(BankingTransactionController.class.getClass());


	@Autowired
	CommonWebHelper commonWebHelper;

	@Autowired
	OOBSWebHelper oobsWebHelper;

	@Autowired
	CASWebHelper casWebHelper;

	@Autowired
	private URLUtil urlUtil;

	@Autowired
	QRTokenmasterService qrTokenmasterService;

	public static byte[] SWYSV1 = {(byte)0xE3, (byte)0x07, (byte)0x53, (byte)0x57,
			(byte)0x59, (byte)0x53, (byte)0x20, (byte)0x56, (byte)0x31};

	public static byte[] TAG_ACCOUNT = {(byte)0xDF, (byte)0x03, (byte)0x0A};
	public static byte[] TAG_AMOUNT = {(byte)0x9F, (byte)0x03, (byte)0x0C};

	/**
	 * This  API will fetch the list of devices available for a
	 * user based on operation type
	 *
	 * @param operationType
	 * @param bankingTransferDetails: userID, toAccountNo, fromAccountNo, description, amount
	 * @return ResultStatus Template, HttpStatus Code and Response Code
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/initiatebankingtransaction.action/{operationType}", method = RequestMethod.POST)
	public ResultStatus getTokenDetails(@PathVariable String operationType, @RequestBody BankingTransferDTO bankingTransferDetails) throws ControllerException{

		logger.info("Entered in BankingTransactionController - getTokenDetails method");
		logger.info("operationType: "+operationType);
		logger.info("bankingTransferDetails: "+bankingTransferDetails.toString());

		boolean isValidMobileUser = false;

		Map<String, String> deviceList = new HashMap<>();
		ResultStatus resultStatus = new ResultStatus();
		TokenDeviceInfo tokenDeviceInfo = new TokenDeviceInfo();



		String transferStringForQR = null;
		String challengeCode = null;
		List<String> tokenList = new ArrayList<>();

		if(!(bankingTransferDetails != null && !bankingTransferDetails.getUserId().equals("") && bankingTransferDetails.getUserId() != null)) {
			// userId is null
			logger.error("userId is null");
			throw new ControllerException("userId is null");
		}
		logger.info("bankingTransferDetails : "+bankingTransferDetails.toString());
		try{
			// checking for valid operation type
			if(!(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER)||
					operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY)||
					operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS))) {

				// operation is not valid
				logger.info("operation type is not valid................");

				//tokenList will be empty
				tokenDeviceInfo.setTokenAvailable(tokenList);
				logger.info(CommonOperationsConstants.TOKEN_DEVICE_INFO_LABEL +tokenDeviceInfo.toString());

				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
				resultStatus.setTemplateObject(tokenDeviceInfo);
				return resultStatus;
			}

			casWebHelper.authenticateCASever();

			// fetching device list for the user
			deviceList = commonWebHelper.getDeviceStatus(bankingTransferDetails.getUserId());
			logger.info("deviceList: "+deviceList.toString());

			// checking for registered mobile device for the user
			isValidMobileUser = oobsWebHelper.isUserRegisteredOnOOBServer(bankingTransferDetails.getUserId());
			logger.info("isvalidMobileUser: "+isValidMobileUser);

			String qrtokenName = null;
			if(deviceList.containsKey(CommonOperationsConstants.QRTOKEN_DEVICE_NAME_ATTR)) {
				qrtokenName = deviceList.get(CommonOperationsConstants.QRTOKEN_DEVICE_NAME_ATTR);
				logger.info("print qrtokenName: "+ qrtokenName);
				deviceList.remove(CommonOperationsConstants.QRTOKEN_DEVICE_NAME_ATTR);
			}

			if(!(!deviceList.isEmpty() && (isValidMobileUser || !isValidMobileUser) && (!(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS)))) ){
				// device list is empty
				logger.info("deviceList is empty...........");
				logger.info(CommonOperationsConstants.TOKEN_DEVICE_INFO_LABEL + tokenDeviceInfo);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setTemplateObject(tokenDeviceInfo);

				return resultStatus;
			}

			switch (operationType) {
				case EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY:
					String payeeName = bankingTransferDetails.getBeneficiaryName();
					String payeeAcc = bankingTransferDetails.getBeneficiaryAccount();

					// generating QR code using beneficiaryName and beneficiaryAccount
					transferStringForQR = MspUtil.encryptQRData("add-payee,"+payeeName+","+payeeAcc, QR_CODE_ENCRYPTED);
					break;
				case EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER:
					String fromAccount = bankingTransferDetails.getFromAccountNo();
					String toAccount = bankingTransferDetails.getToAccountNo();
					String amount = bankingTransferDetails.getAmount();

					// generating QR code using toAccountNo, fromAccountNo and amount
					transferStringForQR = MspUtil.encryptQRData("fund-transfer,"+fromAccount+","+toAccount+","+amount, QR_CODE_ENCRYPTED);
					break;

				default:
					break;
			}

			logger.info("\n[initiatebankingtransaction] mspUtil.QR_CODE_ENCRYPTED : " + QR_CODE_ENCRYPTED);
			logger.info("\ntransferStringForQR : ["+operationType+"] "+transferStringForQR);


			// setting device types in tokenDeviceInfo
			List<String> tokenDevices = new ArrayList<>(deviceList.values());
			for(String token: tokenDevices){
				tokenList.add(token);
			}
			logger.info("tokenDevices: "+tokenDevices);
			logger.info("tokenList: "+tokenList);
			tokenDeviceInfo.setTokenAvailable(tokenList);

			logger.info("mobile supported....");
			tokenDeviceInfo.setMobileSupported(isValidMobileUser);
			tokenDeviceInfo.setQrcodeEncryptedData(transferStringForQR);

			// checking for hardware devices
			if(!((deviceList.containsKey("displaycardpadDeviceEB")||deviceList.containsKey("displaycardpadDevice")||
					deviceList.containsKey("picoDevice")||deviceList.containsKey("signerDevice")||deviceList.containsKey("qrtokenDevice")||
					deviceList.containsKey("flexDevice")) && !operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS))) {
				// no hardware token found
				logger.info("no hardware tokens with/without mobile");
				logger.info(CommonOperationsConstants.TOKEN_DEVICE_INFO_LABEL + tokenDeviceInfo);
				resultStatus.setStatusCode(HttpStatus.ACCEPTED);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setTemplateObject(tokenDeviceInfo);

				return resultStatus;
			}

			if(deviceList.containsKey("signerDevice")||deviceList.containsKey("flexDevice")){
				logger.info("deviceList contains signer or/and flex");

				switch (operationType) {
					case EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER:
						challengeCode = commonWebHelper.getChallenge(10,6);//6 - CHanllenge lenght, 10 -template (Money transfer)
						break;
					case EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY:
						challengeCode = commonWebHelper.getChallenge(22,6);//Only used for Flex and Siner tokens ()6 - lenght of CHanllenge, 22 - template(add beneficiary)
						break;

					default:
						break;
				}
				logger.info("challengeCode: "+challengeCode);
				tokenDeviceInfo.setChallengeCode(challengeCode);

			}

			if(deviceList.containsKey("qrtokenDevice")) {

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
				logger.info("qrtoken_randommode : " + qrtoken_randommode);

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
				logger.info("qrtoken_encryptionmode : " + qrtoken_encryptionmode);

				String qrtoken_seedDpuk = null;
				String qrtoken_seedOTP = null;

				switch (qrtoken_encryptionmode) {
					case ENCRYPTION_DFF_NO:
						qrtoken = new QRToken(qrTokenVersion, randomness);
						break;
					case ENCRYPTION_DFF_DYNAMIC_KEY:
						qrtokenAlgo = urlUtil.getQrtoken_algo();
						key = commonWebHelper.getKey(qrtokenAlgo, false);
						logger.info("dynamic key : " + QRTokenUtil.bytesToHexString(key));
						qrtoken_seedDpuk = urlUtil.getQrtoken_seedDpuk();
						QRAuthCode = commonWebHelper.getDPUKHexCASever(randomness, qrtokenName, qrtoken_seedDpuk);
						qrtoken = new QRToken(qrTokenVersion, qrtokenAlgo, randomness, QRAuthCode, key);
						break;
					case ENCRYPTION_DFF_CUSTOMER_KEY:
						qrtokenAlgo = urlUtil.getQrtoken_algo();
						key = commonWebHelper.getKey(qrtokenAlgo, true);
						logger.info("customer key : " + QRTokenUtil.bytesToHexString(key));
						qrtoken = new QRToken(qrTokenVersion, qrtokenAlgo, randomness, key);
						break;
					default:
						break;
				}

				qrtoken_seedOTP = urlUtil.getQrtoken_seedOTP();
				byte[] apduData = null;
				byte application = (byte) Integer.parseInt(qrtoken_seedOTP);
				QRTokenUsage qrcodeusage = QRTokenUsage.FULL_FREE_TEXT;

				ByteArrayOutputStream transaction_FREE_TEXT = new ByteArrayOutputStream();
				if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER)) {
					transaction_FREE_TEXT.write(SWYSV1, 0, SWYSV1.length);
					transaction_FREE_TEXT.write(TAG_ACCOUNT, 0, TAG_ACCOUNT.length);
					transaction_FREE_TEXT.write(QRTokenUtil.stringToBytesASCII(bankingTransferDetails.getToAccountNo()), 0, 10);
					transaction_FREE_TEXT.write(TAG_AMOUNT, 0, TAG_AMOUNT.length);
					transaction_FREE_TEXT.write(QRTokenUtil.stringToBytesASCII(commonWebHelper.padAmount(bankingTransferDetails.getAmount())), 0, 12);
				}

				if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY)) {
					transaction_FREE_TEXT.write(SWYSV1, 0, SWYSV1.length);
					transaction_FREE_TEXT.write(TAG_ACCOUNT, 0, TAG_ACCOUNT.length);
					transaction_FREE_TEXT.write(QRTokenUtil.stringToBytesASCII(bankingTransferDetails.getBeneficiaryAccount()), 0, 10);
				}

				apduData =  transaction_FREE_TEXT.toByteArray();

				String sQRCodeData = QRTokenUtil.bytesToHexString(qrtoken.generateQRCodeData(qrcodeusage, application, apduData));
				String sQRCodeVersionUsed = commonWebHelper.getStringQRCodeVersion(qrtoken.getQRCodeTokenVersionUsed());
				String sTransactionHash = qrtoken.getSigningBuffer(urlUtil.getQrtoken_hashMode());

				logger.info("sQRCodeVersionUsed : " + sQRCodeVersionUsed);
				logger.info("sQRCodeData : " + sQRCodeData);
				logger.info("sTransactionHash : " + sTransactionHash);

				String transactionType = null;

				if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER)) {
					transactionType = EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER_12;
				}

				if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY)) {
					transactionType = EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY_11;
				}

				String userId = bankingTransferDetails.getUserId();

				int count = qrTokenmasterService.countByUserId(userId);

				if(count>0) {
					qrTokenmasterService.deleteQRTokenDetailsByUserId(userId);
				}

				logger.info("ATM : QRToken code info saved!");

				QRTokenMasterInfo qrTokenMasterInfo = new QRTokenMasterInfo();
				qrTokenMasterInfo.setQrcodeVersion(sQRCodeVersionUsed);
				qrTokenMasterInfo.setTransactionHash(sTransactionHash);
				qrTokenMasterInfo.setTransactionType(transactionType);
				qrTokenMasterInfo.setUserId(bankingTransferDetails.getUserId());

				qrTokenmasterService.saveQRTokenMasterInfo(qrTokenMasterInfo);

				tokenDeviceInfo.setTokenqrcodeEncryptedData(sQRCodeData);
				tokenDeviceInfo.setTokenqrcodeEncryptedVersion(sQRCodeVersionUsed);

				//attach sQRCodeData and sQRCodeVersionUsed to the response
				// job done!

			}

			logger.info("hardware tokens with/without mobile");
			logger.info(CommonOperationsConstants.TOKEN_DEVICE_INFO_LABEL + tokenDeviceInfo.toString());
			resultStatus.setStatusCode(HttpStatus.ACCEPTED);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
			resultStatus.setTemplateObject(tokenDeviceInfo);

		}catch(Exception e){
			logger.error("Exception occurred in BankingTransactionController - getTokenDetails method");
			throw new ControllerException(e);
		}
		return resultStatus;

	}
	/**
	 * This method is for handling exception for Banking Transfer
	 *
	 * @return errorMessage, HttpStatusCode and RsesponseCode
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus moneyTranasferErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.NO_CONTENT);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
