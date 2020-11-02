package com.gemalto.eziomobile.demo.webhelper.atm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.dto.ATMQRCodeDTO;
import com.gemalto.eziomobile.demo.dto.AtmAccessCodeDTO;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;

@Component
public class ATMWebHelper {

	@Autowired
	private URLUtil urlUtil;

	@Autowired
	CommonWebHelper commonWebHelper;

	private static final LoggerUtil logger = new LoggerUtil(ATMWebHelper.class.getClass());


	/**Validate request body for ATM AccessCode OTP validation
	 * @param atmAccessCodeDTO
	 * @return
	 */
	public boolean validateRequestBodyForATMAccessCode(AtmAccessCodeDTO atmAccessCodeDTO){

		boolean isValid = false;
		if(atmAccessCodeDTO.getUserId() != null && atmAccessCodeDTO.getFromAccountNo() != null &&
				atmAccessCodeDTO.getAmount() != 0 && atmAccessCodeDTO.getOtpValue() != null){

			if(!atmAccessCodeDTO.getUserId().equals("") && !atmAccessCodeDTO.getFromAccountNo().equals("") &&
					atmAccessCodeDTO.getAmount() != 0 && !atmAccessCodeDTO.getOtpValue().equals("")){
				isValid = true;
			}
		}
		return isValid;
	}


	/**Helper method to validate AMT : AccessCode otp
	 * @param userId
	 * @param otpValue
	 * @return boolean values : True/False
	 */
	public boolean isOTPValidatedForATMCashCode(String userId, String otpValue){

		String pushresponseData = "";
		boolean isOTPValidate = false;

		String XmlDataVerif = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<AuthenticationRequest><UserID>" + userId + "</UserID><OTP>" + otpValue
				+ "</OTP><OpenSession>false</OpenSession></AuthenticationRequest>";

		logger.info("ATM By Access code: " + XmlDataVerif);

		try {
			String url = urlUtil.casServerURL();
			logger.info("[ATM Access Code] URL : "+url);

			//URL pushobj = new URL("http://10.10.84.139:80/saserver/master/api/auth/otb");

			URL pushobj = new URL(url);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");

			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty("User-Agent", "Mozilla/5.0");
			pushcon.setRequestProperty("Content-type", "text/xml");
			pushcon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			pushcon.setDoOutput(true);

			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(XmlDataVerif);
			pushpw.flush();
			pushpw.close();
			// For POST only - END
			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[ATM Access Code] POST Response Code :: " + pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) { 

				logger.info("ATM Access Code : Success!");
				isOTPValidate = true;

				BufferedReader pushin = new BufferedReader( new InputStreamReader(pushcon.getInputStream()));

				String pushinputLine;

				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}

				pushin.close();
				pushresponseData = _pushresponse.toString();
			} else {

				logger.info("ATM Access Code : Failed!");

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return isOTPValidate;
	}

	/**
	 * This method is used to validate atm qr code otp from mobile
	 * 
	 * @param atmQrCodeDTO
	 * @return boolean
	 */
	public boolean validateAtmQrCode(ATMQRCodeDTO atmQrCodeDTO){

		//		Transaction Data Get from DB//
		String transactiondata = "";

		boolean qrCodeVerified = false;

		String sDataToSignHexa = "";

		transactiondata = atmQrCodeDTO.getChallenge() + atmQrCodeDTO.getUserId()+ atmQrCodeDTO.getAtmId()
		+atmQrCodeDTO.getFromAccNo()+ atmQrCodeDTO.getAmount();

		logger.info("QR code transactiondata : "+transactiondata);

		/*String _newtransactiondata = transactiondata;
		int chklen_transactiondata = (transactiondata.length() % 2);

		if (chklen_transactiondata == 1)
			_newtransactiondata += "0";*/

		sDataToSignHexa = commonWebHelper.getSHA1(commonWebHelper.asciiToHex(transactiondata));

		String _pushurl = urlUtil.casServerURL();
		String pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<AuthenticationRequest><UserID>" + atmQrCodeDTO.getUserId() + "</UserID>"
				+ "<OTP>" + atmQrCodeDTO.getOtpValue()
				+ "</OTP><OcraData><ServerChallenge><Value>" + sDataToSignHexa
				+ "</Value></ServerChallenge></OcraData></AuthenticationRequest>";
		
		logger.info("[validateAtmQrCode] pushXmlData : "+pushXmlData);
		
		String pushresponseData;
		//String responseDataToSendBackVerif;
		try {
			logger.info("atm QR code, before server call.......... ");

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

			logger.info("atm QR code, making a server call.......... ");

			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty("User-Agent", "Mozilla/5.0");
			pushcon.setRequestProperty("Content-type", "text/xml");
			pushcon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);

			logger.info("atm QR code, done.......... ");

			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);
			if (pushresponseCode == HttpURLConnection.HTTP_OK) { //success

				logger.info("QR Code Case success / OK.........");

				qrCodeVerified = true;

				BufferedReader pushin = new BufferedReader(
						new InputStreamReader(pushcon.getInputStream())); //getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("QR Code Case Success Response : " + pushresponseData);

			} else {
				logger.info("QR Code Case NOT-OK response");
				qrCodeVerified = false;

				BufferedReader pushin = new BufferedReader(
						new InputStreamReader(pushcon.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("Error  : " + pushresponseData);
				logger.info("qrCodeVerified : "+qrCodeVerified);
			}

		} catch (Exception e) {
			qrCodeVerified = false;
			logger.error("Error occurred while validating atm QR code!!! "+qrCodeVerified );
			e.printStackTrace();
			pushresponseData = e.toString();

		}

		return qrCodeVerified;

	}

	/**
	 * This method is used to validate request body of atm qr code
	 * 
	 * @param atmQRCodeDTO
	 * @return boolean
	 */
	public boolean validateRequestBodyForATMQRCode(ATMQRCodeDTO atmQRCodeDTO){

		boolean isValid = false;
		if(atmQRCodeDTO.getUserId() != null && atmQRCodeDTO.getFromAccNo() != null 
				&& atmQRCodeDTO.getOtpValue() != null && 
				atmQRCodeDTO.getAtmId()!= null && atmQRCodeDTO.getChallenge()!= null){

			if(!atmQRCodeDTO.getUserId().equals("") && !atmQRCodeDTO.getFromAccNo().equals("")
					&& !atmQRCodeDTO.getOtpValue().equals("") &&
					!atmQRCodeDTO.getAtmId().equals("") && !atmQRCodeDTO.getChallenge().equals("")){
				isValid = true;
			}
		}
		return isValid;
	}
}
