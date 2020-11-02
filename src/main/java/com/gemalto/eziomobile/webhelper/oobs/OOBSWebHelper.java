package com.gemalto.eziomobile.webhelper.oobs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.gemalto.eziomobile.demo.util.ApplicationPropertiesUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class OOBSWebHelper {

	@Autowired
	private ApplicationPropertiesUtil applicationPropertiesUtil;

	@Autowired
	private URLUtil urlUtil;
	private static final LoggerUtil logger = new LoggerUtil(OOBSWebHelper.class.getClass());

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;

	public OOBSWebHelper() {
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Cannot parse XML {}.", e);
		}
	}
	
	/**
	 * @param xUserId
	 *            : this id can be used to check whether user has profile on OOB
	 *            server or not. It can be anyone, logged-in user(Current user)
	 *            or other user. NOTE : this xUserId, can be beneficiary Id for
	 *            P2P feature.
	 * @param xUserId
	 * @return
	 */
	public boolean isUserRegisteredOnOOBServer(String xUserId) {
		String backendConfiguration = urlUtil.getBackendConfiguration();
		logger.info("[isUserRegisteredOnOOBServer] backendConfiguration : "+backendConfiguration);
		boolean isUserRegisteredOnOOBServer = false;
		String pushresponseData = "";
		String _pushurl = "";
		String oobsURL = "";
		String notificationProfile = "";

		System.out.println("OOBS call starts......");

		try {
			oobsURL = urlUtil.pushNotificationURL();
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
					_pushurl = oobsURL + xUserId + "/applications/eziomobileappID/clients";
					break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				_pushurl = oobsURL + xUserId + "/applications/" + applicationPropertiesUtil.getApplicationsID() + "/clients";
				break;
			default:
				break;
			}
			URL pushobj = new URL(_pushurl);

			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
				pushcon.setRequestMethod("GET");
				pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_PROP_VALUE);
				break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				pushcon.setRequestMethod("GET");
				pushcon.setRequestProperty(ACCEPT_PROP, APPLICATION_XML_PROP_VALUE);
				pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_TOKEN_PROP_VALUE);
				break;
			default:
				break;
		}	
			

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {

				logger.info("OOBS User Success!");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("[isUserRegisteredToOOBS] pushResponseData : " + pushresponseData);


			    InputSource src = new InputSource();
			    src.setCharacterStream(new StringReader(pushresponseData));
			    Document doc = builder.parse(src);
		        doc.getDocumentElement().normalize();
				
		        // get all notification profiles
				NodeList nList2 = doc.getElementsByTagName("clients");
			    logger.info("No of notification profiles : "+ nList2.getLength());
				
			    for (int temp = 0; temp < nList2.getLength(); temp++) {
					
		        	Node nNode = nList2.item(temp);
		            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	
		                Element eElement = (Element) nNode;
	
		                NodeList  notificationP = eElement.getElementsByTagName("notificationProfile");
		                if(notificationP.getLength()>0) {
		                notificationProfile = eElement.getElementsByTagName("notificationProfile").item(0).getTextContent();
		                System.out.println("notificationProfile : " +notificationProfile);
		                }else {
		                	notificationProfile = "";
		                }
		                
		                switch (backendConfiguration) {
		                	case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
		                		if(notificationProfile != null && !notificationProfile.isEmpty() && 
		                		!notificationProfile.equalsIgnoreCase(EzioMobileDemoConstant.DEFAULT_NOTIFICATION_PROFILE)){
		                			isUserRegisteredOnOOBServer = true;
		                	break;
		                	}
		    			break;
		                	case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
		                		 logger.info("Passe par ici -----------------> ");
		                		if(notificationProfile != null && !notificationProfile.isEmpty() && 
		                		!notificationProfile.equalsIgnoreCase(applicationPropertiesUtil.getChannel())){
		                			isUserRegisteredOnOOBServer = true;
						break;
		                	}
		    				break;
		    			default:
		    				break;
		    			}
		            }
		        }
			} else {
				logger.info("\n[isUserRegisteredToOOBS] Failure");
				logger.info("\n[isUserRegisteredToOOBS] pushresponseData on fail 1: " + pushresponseData);

				isUserRegisteredOnOOBServer = false;
			}

		} catch (Exception e) {
			isUserRegisteredOnOOBServer = false;
			logger.error("[isUserRegisteredToOOBS] Exception!!");
			e.printStackTrace();
		}
		return isUserRegisteredOnOOBServer;
	}


	/**
	 * @param pushXmlData
	 * @param userId
	 * @return Map, which will be containing isPendingTransaction values and
	 *         pushMsgId
	 */
	public Map<String, String> sendNotification(String pushXmlData, String userId) {
		String backendConfiguration = urlUtil.getBackendConfiguration();
		logger.info("[sendNotification] backendConfiguration : "+backendConfiguration);
		boolean isNotificationSent = false;
		String pushresponseData = "";
		String _pushurl = "";
		String pushNotificationURL = "";
		String pushMessageId = "";

		Map<String, String> sendNotificatioData = new HashMap<String, String>();
		try {
			pushNotificationURL = urlUtil.pushNotificationURL();

			logger.info("[sendNotification] pushNotificationURL : " + pushNotificationURL);
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
				_pushurl = pushNotificationURL + userId + "/applications/eziomobileappID/verification";
				break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				_pushurl = pushNotificationURL + userId + "/applications/" + applicationPropertiesUtil.getApplicationsID() + "/verification";
				break;
			default:
				break;
			}			
			logger.info("[sendNotification] Updated - pushNotificationURL : " + _pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
				pushcon.setRequestMethod("POST");
				pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
				pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_PROP_VALUE);
				break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				pushcon.setRequestMethod("POST");
				pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
				pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_TOKEN_PROP_VALUE);
				break;
			default:
				break;
		}	
			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);

			logger.info("\n [sendNotification] checking.....");
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());

			pushpw.write(pushXmlData);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("[sendNotification] Push Response Code : " + pushresponseCode);
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) { // success

				logger.info("\n[sendNotification] Success!");
				logger.info("\n[sendNotification] pushresponseData : " + pushresponseData);

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();

				isNotificationSent = true;
				sendNotificatioData.put("isNotificationSent", String.valueOf(isNotificationSent));

				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);

				NodeList nList = usersdoc.getElementsByTagName("messageId");
				pushMessageId = nList.item(0).getChildNodes().item(0).getNodeValue();
				logger.info("\n\n[sendNotification] pushresponseData : " + pushresponseData);

				if (!pushMessageId.equals("") && pushMessageId != null)
					sendNotificatioData.put("pushMessageId", pushMessageId);

			} else {

				logger.info("\n[sendNotification] Failure");
				logger.info("\n[sendNotification] pushresponseData : " + pushresponseData);
				isNotificationSent = false;
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();

				sendNotificatioData.put("isNotificationSent", String.valueOf(isNotificationSent));
			}
		} catch (Exception e) {
			logger.error("[sendNotification] Exception!! :" + e);
			e.printStackTrace();
		}
		return sendNotificatioData;
	}

	/**
	 * Helper class to delete message from OOBS queue.
	 * 
	 * @param messsageId
	 * @return
	 */
	public boolean deleteMessageFromOOBSQueue(String messsageId) {
		String oobsQueueURL = "";
		String _pushurl = "";
		String pushresponseData = "";
		boolean isMessageDeleted = false;

		logger.info("[deleteMessageFromOOBSQueue] messsageId : " + messsageId);

		try {
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[deleteMessageFromOOBSQueue] backendConfiguration : "+backendConfiguration);
			oobsQueueURL = urlUtil.oobsQueueURL();
			logger.info("[deleteMessageFromOOBSQueue] oobsQueueURL : " + oobsQueueURL);

			_pushurl = oobsQueueURL + messsageId;
			logger.info("[deleteMessageFromOOBSQueue] _pushurl : " + _pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
				pushcon.setRequestMethod("DELETE");
				pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_PROP_VALUE);
				break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				pushcon.setRequestMethod("DELETE");
				pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_TOKEN_PROP_VALUE);
				break;
			default:
				break;
		}	

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {

				logger.info("[deleteMessageFromOOBSQueue] Success!!");

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()
				String pushinputLine = "";
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("[deleteMessageFromOOBSQueue] pushresponseData : " + pushresponseData);

				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);

				NodeList _nList = usersdoc.getElementsByTagName("statusCode");
				String statusCode = "";

				if (_nList.getLength() > 0) {
					statusCode = _nList.item(0).getChildNodes().item(0).getNodeValue();
					logger.info("[deleteMessageFromOOBSQueue] statusCode : " + statusCode);
				}

				if (statusCode.equals("0"))
					isMessageDeleted = true;
			}
		} catch (Exception e) {
			logger.error("Exception : Something went wrong while deleting message from OOBS queue!" + e);
		}
		return isMessageDeleted;
	}

	/**
	 * @param msgID
	 * @param getContent
	 * @return
	 * Notification callback - msgID, true, false
	 * Verification callback - msgID, true, true
	 */
	public Map<String, String> getInfoFromMesgID(String msgID, boolean getContent, boolean isToStore) {
		logger.info("\n getInfoFromMesgID....... \n");

		String _pushurl = "";
		String pushresponseData = "";
		int pushresponseCode;
		
		String userID = null;
		String content = null;
		String amount = "";
		String panNo = "";

		Map<String, String> hm = new HashMap<String, String>();

		try {
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[getInfoFromMesgID] backendConfiguration : "+backendConfiguration);
			_pushurl = urlUtil.oobsQueueURL(); // "http://10.10.84.139:8081/oobs-dispatcher/domains/default/messages/"
												// + msgID;
			_pushurl = _pushurl + msgID;
			logger.info("[getInfoFromMesgID] pushURL : " + _pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			switch (backendConfiguration) {
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
					pushcon.setRequestMethod("GET");
					pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
					pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_PROP_VALUE);
					break;
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
					pushcon.setRequestMethod("GET");
					pushcon.setRequestProperty(ACCEPT_PROP, APPLICATION_XML_PROP_VALUE);
					pushcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_TOKEN_PROP_VALUE);
					break;
				default:
					break;
			}
			pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			System.out.println("GET Response Code : " + pushresponseData);
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode != HttpURLConnection.HTTP_OK) {
				logger.error("\n getInfoFromMesgID failed!");
				return null;
			}

			System.out.println("\n getInfoFromMesgID OK");

			BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

			String pushinputLine;
			StringBuffer _pushresponse = new StringBuffer();

			while ((pushinputLine = pushin.readLine()) != null) {
				_pushresponse.append(pushinputLine);
			}
			pushin.close();
			pushresponseData = _pushresponse.toString();

			InputSource is = new InputSource(new StringReader(pushresponseData));
			Document usersdoc = builder.parse(is);

			NodeList nList = usersdoc.getElementsByTagName("userId");
			userID = nList.item(0).getChildNodes().item(0).getNodeValue();

			hm.put("userID", userID);

			if (userID != null && !userID.equals("")) {
				logger.info("\n userID OK! " + userID);
			}
			if (getContent) {
				nList = usersdoc.getElementsByTagName(CONTENT_ELEMENT);
				content = nList.item(0).getChildNodes().item(0).getNodeValue();
			}
		} catch (Exception e) {
			return null;
		}

		if (!getContent) {
			return hm;
		}

		if (content == null ||  content.equals("")) {
			return null;
		}

		DatatypeConverter dc = null;
		logger.info("\n [getInfoFromMesgID] line #391 content encoded OK! " + content);

		content = new String(dc.parseBase64Binary(content));
		logger.info("\n content line #394, decoded OK! " + content);

		JSONObject resJSON_Obj = null;
		try {
			resJSON_Obj = new JSONObject(content);
		} catch (JSONException e) {
			logger.error("Cannot convert this string into JSON. ", e);
		}

		try {
			if (resJSON_Obj.has("type")) {
				if(!isToStore)
					hm.put(MSGTYPE_PROP, "02");
				else{
					//to be implemented for p2p, getdata to update db.
				}

				logger.info("\n [getInfoFromMesgID] type p2p ");

				return hm;
			}

			if (!resJSON_Obj.has(CONTENT_ELEMENT)) {
				return null;
			}

			logger.info("\n content 2nd found");
			content = new String(dc.parseBase64Binary(resJSON_Obj.getString(CONTENT_ELEMENT)));
			logger.info("[getInfoFromMesgID] line #414 content : "+content);


			resJSON_Obj = new JSONObject(content);
			logger.info("\n [getInfoFromMesgID] no type found, login, MT, NB, 3DS or CardIssuance...");

			if(resJSON_Obj.has("type")){
				String operationType = (String) resJSON_Obj.get("type");

				logger.info("[getInfoFromMesgID] operationType : "+operationType);

				hm = populateHm(hm, isToStore, resJSON_Obj, operationType);
			}else{
				hm.put(MSGTYPE_PROP, "00");
			}
			if (resJSON_Obj.has(HASHEDDATA_PROP)) {
				logger.info("\n [getInfoFromMesgID] hasheddata found");
				hm.put(HASHEDDATA_PROP, resJSON_Obj.getString(HASHEDDATA_PROP));
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		return hm;
	}

    /**
     *
     * @param isToStore
     * @param resJSON_Obj
     * @param operationType
     * @return
     * @throws JSONException
     */
	private Map populateHm(Map hm, boolean isToStore, JSONObject resJSON_Obj, String operationType) throws JSONException {
		String amount;
		String panNo;
		switch (operationType) {
			case EzioMobileDemoConstant.OPERATION_TYPE_LOGIN:
				if(!isToStore){
					hm.put(MSGTYPE_PROP, "01");
					hm.put(OPERATION_TYPE_PROP, EzioMobileDemoConstant.OPERATION_TYPE_LOGIN);
				}
				break;
			case EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY:
				if(!isToStore){
					hm.put(MSGTYPE_PROP, "11");
					hm.put(OPERATION_TYPE_PROP, EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY);
				}else{
					String payeeName = (String) resJSON_Obj.get("payeename");
					String payeeAccount = (String) resJSON_Obj.get("payeeaccount");

					hm.put("payeeName", payeeName);
					hm.put("payeeAccount", payeeAccount);
				}
				break;
			case EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER:
				if(!isToStore){
					hm.put(MSGTYPE_PROP, "12");
					hm.put(OPERATION_TYPE_PROP, EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER);
				}else{
					String fromAccountNo = (String) resJSON_Obj.get("from");
					String toAccountNo = (String) resJSON_Obj.get("to");
					amount = (String) resJSON_Obj.get(AMOUNT_PARAM);

					hm.put(FROM_ACCOUNT_NO_PARAM, fromAccountNo);
					hm.put(TO_ACCOUNT_NO_PARAM, toAccountNo);
					hm.put(AMOUNT_PARAM, amount);
				}
				break;
			case EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS:
				if(!isToStore){
					hm.put(MSGTYPE_PROP, "13");
					hm.put(OPERATION_TYPE_PROP, EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS);
				}else{
					amount = (String) resJSON_Obj.get("amount");
					panNo = (String) resJSON_Obj.get("CardNumber");

					hm.put("panNo", panNo);
					hm.put(AMOUNT_PARAM, amount);
				}
				break;
			case EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE:
				if(!isToStore){
					hm.put(MSGTYPE_PROP, "14");
					hm.put(OPERATION_TYPE_PROP, EzioMobileDemoConstant.OPERATION_TYPE_CARD_ISSUANCE);
				}else{
					panNo = (String) resJSON_Obj.get("CardNumber");
					String cvv = (String) resJSON_Obj.get("CVV");
					String expDate = (String) resJSON_Obj.get("ExpDate");

					hm.put("panNo", panNo);
					hm.put("cvv", cvv);
					hm.put("expDate", expDate);
				}
				break;
			default:
				hm.put(MSGTYPE_PROP, "00");
				hm.put(OPERATION_TYPE_PROP, "00");
				break;
		}

		return hm;
	}


	/**
	 * @param userId
	 * @param userRegistrationCodeEPS
	 */
	public boolean isMobileRegistrationDoneOnOOBS(String userId, String userRegistrationCodeEPS) {

		String ooburl = "";
		String oobresponseData = "";
		String oobsXmlData ="";
		boolean isMobileRegistrationEnrollOOBS = false;
		
		String backendConfiguration = urlUtil.getBackendConfiguration();
		logger.info("[MobileEnrollmentStepOne] backendConfiguration : "+backendConfiguration);

		switch (backendConfiguration) {
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
			 oobsXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<RegisterRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\">"
					+ "<registrationSecurityMethod>REG_CODE</registrationSecurityMethod>"
					+ "<registrationCode>"+ userRegistrationCodeEPS+ "</registrationCode>"
							+ "<validityPeriodSecs>600</validityPeriodSecs>"
							+ "<notificationProfiles>"
								+ "<notificationProfile>" + applicationPropertiesUtil.getChannel() + "</notificationProfile>"
							+ "</notificationProfiles>"
					+ "</RegisterRequest>";
		break;
		case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
			 oobsXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<RegisterRequest xmlns=\"http://gemalto.com/ipms/dispatcher/api/transport\">"
					+ "<registrationSecurityMethod>REG_CODE</registrationSecurityMethod>"
					+ "<registrationCode>"+ userRegistrationCodeEPS+ "</registrationCode>"
							+ "<validityPeriodSecs>300</validityPeriodSecs>"
							+ "<notificationProfiles>"
								+ "<notificationProfile>" + applicationPropertiesUtil.getChannel() + "</notificationProfile>"
							+ "</notificationProfiles>"
					+ "</RegisterRequest>";
			break;
		default:
			break;
		}

		try {

			//ooburl = "http://10.10.84.139:8081/oobs-dispatcher/domains/default/users/" + userId + "/applications/eziomobileappID/register";
			ooburl = urlUtil.oobsMobileEnrollmentURL();

			logger.info("[mobileRegistrationEnrollOOBS] OOBS Enrollment URL : "+ooburl);
			
			ooburl = ooburl.replace("<userId>",userId);
			logger.info("[mobileRegistrationEnrollOOBS] OOBS Enrollment URL UPDATED : "+ooburl);
			
			URL oobobj = new URL(ooburl);
			HttpURLConnection oobcon = (HttpURLConnection) oobobj.openConnection();
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
				oobcon.setRequestMethod("POST");
				oobcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
				oobcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_PROP_VALUE);
				break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				oobcon.setRequestMethod("POST");
				oobcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
				oobcon.setRequestProperty(AUTHORIZATION_PROP, BASIC_TOKEN_PROP_VALUE);
				break;
			default:
				break;
		}
			// For POST only - START
			oobcon.setDoInput(true);
			oobcon.setDoOutput(true);
			PrintWriter oobpw = new PrintWriter(oobcon.getOutputStream());
			oobpw.write(oobsXmlData);
			oobpw.flush();
			oobpw.close();
			// For POST only - END

			int oobresponseCode = oobcon.getResponseCode();
			oobresponseData = Integer.toString(oobresponseCode);

			if (oobresponseCode == HttpURLConnection.HTTP_OK) { // success
				
				logger.info("[mobileRegistrationEnrollOOBS] Success!");
				
				isMobileRegistrationEnrollOOBS = true;
				BufferedReader oobin = new BufferedReader(new InputStreamReader(oobcon.getInputStream())); // getErrorStream()

				String oobinputLine;
				StringBuffer _oobresponse = new StringBuffer();

				while ((oobinputLine = oobin.readLine()) != null) {
					_oobresponse.append(oobinputLine);
				}
				oobin.close();
				oobresponseData = _oobresponse.toString();
				logger.info("[mobileRegistrationEnrollOOBS] oobresponseData : "+ oobresponseData);
				
			} else {
				logger.info("[mobileRegistrationEnrollOOBS] Failed!");
				BufferedReader oobin = new BufferedReader(new InputStreamReader(oobcon.getErrorStream()));
				String oobinputLine;
				StringBuffer _oobresponse = new StringBuffer();
				while ((oobinputLine = oobin.readLine()) != null) {
					_oobresponse.append(oobinputLine);
				}
				oobin.close();
				oobresponseData = _oobresponse.toString();
			}

		} catch (Exception e) {
			logger.error("[mobileRegistrationEnrollOOBS] Exception!");
			e.printStackTrace();
			oobresponseData = e.toString();
		}
		return isMobileRegistrationEnrollOOBS;
	}

}
