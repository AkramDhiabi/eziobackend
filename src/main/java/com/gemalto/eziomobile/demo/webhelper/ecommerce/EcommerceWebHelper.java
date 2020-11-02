package com.gemalto.eziomobile.demo.webhelper.ecommerce;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.CardManagementDTO;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.cardmanagement.CardManagementService;
import com.gemalto.eziomobile.demo.util.URLUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.COOKIE_PROP;
import static com.gemalto.eziomobile.webhelper.cas.CASWebHelper.headerJsession;

@Component
public class EcommerceWebHelper {

	private static final LoggerUtil logger = new LoggerUtil(EcommerceWebHelper.class.getClass());

	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private CardManagementService cardManagementService;

	/**
	 * Method to validate DCV payment through CAS
	 * 
	 * @param userId
	 * @param panNo
	 * @param cvv
	 * @return
	 */
	public boolean validateDCVTransaction(String userId, String panNo, String cvv) {
		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";
		boolean isDCVverified = false;

		try {
			logger.info("[validateDCVTransaction]........1");

			pushXmlData = "<CardSecurityCodeVerificationRequest>" + "<UserID>" + userId + "</UserID>" + "<PAN>" + panNo
					+ "</PAN><PSN>00</PSN>" + "<SecurityCode>" + "<Code>" + cvv + "</Code>"
					+ "<CodeType>DCVX2</CodeType>" + "</SecurityCode>" + "</CardSecurityCodeVerificationRequest>";

			logger.info("[validateDCVTransaction] pushXmlData : " + pushXmlData);

			// _pushurl = "http://10.10.84.139/saserver/master/api/auth/emv";
			_pushurl = urlUtil.dcvValidationURL();
			logger.info("[validateDCVTransaction] _pushurl : " + _pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty("Content-type", "text/xml");

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("POST Response Code :: " + pushresponseCode);

			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("POST Response Code1 :: " + pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {
				// success
				logger.info("[validateDCVTransaction] Success!!");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				// print result
				pushresponseData = _pushresponse.toString();
				isDCVverified = true;

			} else {
				logger.info("[validateDCVTransaction] Failed!!");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			}

		} catch (Exception e) {
			logger.error("DCV payment validation failed!");
			e.printStackTrace();
		}
		return isDCVverified;
	}
	
	
	public boolean validateEcommerce3DSPayment(int uId, int amount, String panNo){
		
		boolean isValidTransaction = false;
		
		try {
			CardManagementDTO cardManagementDTO = cardManagementService.findCardManagementInfoByUserIdAndPanNo(uId, panNo);
			logger.info("cardManagementDTO : "+cardManagementDTO.toString());

			if(cardManagementDTO != null){
				
				logger.info("[3DS] Validating Transaction stage - 1.....");
				if (cardManagementDTO.getPanNo() != null && ((cardManagementDTO.getCardStatus().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_ON))
						&& cardManagementDTO.getOnlineTransaction().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_ON)))
				{
					logger.info("[3DS] Validating Transaction stage - 2.....");
					if ( ((cardManagementDTO.getSpendLimitTransactionStatus().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_OFF))||
							((cardManagementDTO.getSpendLimitTransactionStatus().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_ON) 
									&& cardManagementDTO.getAmountLimitPerTransaction() >= amount))) ) {
						
						isValidTransaction = true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("3DS payment validation failed!");
			e.printStackTrace();
		}
		return isValidTransaction;
	}

	
	
	
}
