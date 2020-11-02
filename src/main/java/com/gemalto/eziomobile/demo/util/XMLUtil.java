package com.gemalto.eziomobile.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class XMLUtil {


	@Autowired
	private URLUtil urlUtil;
	
	private static final LoggerUtil logger = new LoggerUtil(XMLUtil.class.getClass());
	
	/**
	 *  Login, 3DS, Money Transfer and Add beneficiary
	 * @param userId
	 * @param msgContent
	 * @param callBackURL
	 * @param message
	 * @return
	 */
	public String getVerifyTransactionXML(String userId, String msgContent, String callBackURL,String message) {
		String pushXmlData = "";
		
		String backendConfiguration = urlUtil.getBackendConfiguration();
		logger.info("[getVerifyTransactionXML] backendConfiguration : "+backendConfiguration);
		
		switch (backendConfiguration) {
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
			pushXmlData = XML_VERSION_STRING
					+ "<ns2:VerifyTransactionRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\" xmlns:ns2=\"http://gemalto.com/ezio/mobile/oobs/api\">"
					+ "<providerId>eziomobileproviderid</providerId>"
					+ NOTIFICATION_USER_MESSAGE_OPEN_STRING
					+ XML_OPEN_MESSAGE +message+ XML_CLOSE_MESSAGE
					+ NOTIFICATION_USER_MESSAGE_CLOSE_STRING
					+ VALIDITY_PERIOD_SECS_60_XML
					+ CALLBACK_URL_OPEN_XML +callBackURL+ CALLBACK_URL_CLOSE_XML
					+ CALLBACK_USER_XML
					+ CALLBACK_PASSWORD_XML
					+ "<ns2:verifyMessage>"
					+ "<ns2:locale>en</ns2:locale>"
					+ "<ns2:subject>Verify hello</ns2:subject>"
					+ "<ns2:contentType>text/plain; charset=UTF-8</ns2:contentType>"
					+ "<ns2:content>" + msgContent + "</ns2:content>"
					+ "</ns2:verifyMessage>" 
					+ "</ns2:VerifyTransactionRequest>";
		break;
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
			pushXmlData = XML_VERSION_STRING
					+ "<ns2:VerifyTransactionRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\" xmlns:ns2=\"http://gemalto.com/ezio/mobile/oobs/api\">"
					+ "<providerId>0</providerId>"
					+ NOTIFICATION_USER_MESSAGE_OPEN_STRING
					+ XML_OPEN_MESSAGE +message+ XML_CLOSE_MESSAGE
					+ NOTIFICATION_USER_MESSAGE_CLOSE_STRING
					+ VALIDITY_PERIOD_SECS_60_XML
					+ CALLBACK_URL_OPEN_XML +callBackURL+ CALLBACK_URL_CLOSE_XML
					+ CALLBACK_USER_XML
					+ CALLBACK_PASSWORD_XML
					+ "<ns2:verifyMessage>"
					+ "<ns2:locale>en</ns2:locale>"
					+ "<ns2:subject>Verify hello</ns2:subject>"
					+ "<ns2:contentType>text/plain; charset=UTF-8</ns2:contentType>"
					+ "<ns2:content>" + msgContent + "</ns2:content>"
					+ "</ns2:verifyMessage>" 
					+ "</ns2:VerifyTransactionRequest>";
			break;
		default:
			break;
		}
		return pushXmlData;
	}

	/** p2p
	 * @param userId
	 * @param msgContent
	 * @param callBackURL
	 * @param message
	 * @return
	 */
	public String getDispatchMessageXML(String userId, String msgContent, String callBackURL, String message){
		
		String backendConfiguration = urlUtil.getBackendConfiguration();
		logger.info("[getDispatchMessageXML] backendConfiguration : "+backendConfiguration);
		String pushXMLData = "";
		
		switch (backendConfiguration) {
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
			pushXMLData = XML_VERSION_STRING
					+"<DispatchMessageRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\" xmlns:ns2=\"http://gemalto.com/ezio/mobile/oobs/api\">"
					+"<providerId>eziomobileproviderid</providerId>"
					+ NOTIFICATION_USER_MESSAGE_OPEN_STRING
						+ XML_OPEN_MESSAGE +message+ XML_CLOSE_MESSAGE
					+ NOTIFICATION_USER_MESSAGE_CLOSE_STRING
					+ VALIDITY_PERIOD_SECS_60_XML
					+ CALLBACK_URL_OPEN_XML +callBackURL+ CALLBACK_URL_CLOSE_XML
					+ CALLBACK_USER_XML
					+ CALLBACK_PASSWORD_XML
					+"<messagePolicy>"
						+"<deliveryPolicy>ANY</deliveryPolicy>"
						+"<readNotificationPolicy>ANY</readNotificationPolicy>"
						+"<replyPolicy>NONE</replyPolicy>"
					+"</messagePolicy>"
					+"<content>'"+msgContent+"'</content>"
					+"<contentType>text/plain</contentType>"
					+"</DispatchMessageRequest>";
		break;
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
			pushXMLData = XML_VERSION_STRING
					+"<DispatchMessageRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\" xmlns:ns2=\"http://gemalto.com/ezio/mobile/oobs/api\">"
					+"<providerId>0</providerId>"
				//	+"<providerId>eziomobileproviderid</providerId>"
					+ NOTIFICATION_USER_MESSAGE_OPEN_STRING
						+ XML_OPEN_MESSAGE +message+ XML_CLOSE_MESSAGE
					+ NOTIFICATION_USER_MESSAGE_CLOSE_STRING
					+ VALIDITY_PERIOD_SECS_60_XML
					+ CALLBACK_URL_OPEN_XML +callBackURL+ CALLBACK_URL_CLOSE_XML
					+ CALLBACK_USER_XML
					+ CALLBACK_PASSWORD_XML
					+"<messagePolicy>"
						+"<deliveryPolicy>ANY</deliveryPolicy>"
						+"<readNotificationPolicy>ANY</readNotificationPolicy>"
						+"<replyPolicy>NONE</replyPolicy>"
					+"</messagePolicy>"
					+"<content>'"+msgContent+"'</content>"
					+"<contentType>text/plain</contentType>"
					+"</DispatchMessageRequest>";
			break;
		default:
			break;
		}		
		return pushXMLData;
		
	}

}
