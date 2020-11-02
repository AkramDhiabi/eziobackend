package com.gemalto.eziomobile.webhelper.cas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.URLUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class CASWebHelper {

	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	UsermasterService usermasterService;
	
	@Autowired 
	TransactionmasterService transactionmasterService;
	
	@Autowired
	AccountmasterService accountmasterService;

	public static final String PREFIX_DEVICE_MOBILE_SIGNATURE_GAOC = "GAOC";
	public static final String PREFIX_DEVICE_QRTOKEN = "GAQT";
	
	private static final LoggerUtil logger = new LoggerUtil(CASWebHelper.class.getClass());
	public static String headerJsession = "";
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;

	public CASWebHelper() {
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Cannot initiate a new document builder: {}.", e);
		}
	}

	/**
	 * Authenticate CAS server and get headerJsession value
	 */
	public void authenticateCASever() {

		logger.info("CAS server : Authenticating....");
		logger.info("CAS USername : " + urlUtil.getCASUsername());

		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";
		String authenticateCasServerURL = "";

		try {
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[MobileEnrollmentStepOne] backendConfiguration : "+backendConfiguration);
			
			pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><AuthenticationRequest><UserID>"
					+ urlUtil.getCASUsername() + "</UserID><Password>" + urlUtil.getCASPassword()
					+ "</Password><OpenSession>true</OpenSession></AuthenticationRequest>";
			logger.info("pushXMLdata .... " + pushXmlData);

			authenticateCasServerURL = urlUtil.casServerAuthenticationURL();
			logger.info("authenticateCasServerURL : " + authenticateCasServerURL);

			_pushurl = authenticateCasServerURL;
			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, "text/xml");

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("POST Response Code for CAS server authentication : " + pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) { // success

				logger.info("Success CAS server authentication......");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();

				String HeaderJsessionStr = "";
				String[] _array = null;
				
				switch (backendConfiguration) {
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
					HeaderJsessionStr = "";
					HeaderJsessionStr = pushcon.getHeaderField("Set-Cookie");
					_array = HeaderJsessionStr.split(";");
					synchronized (headerJsession) {
						headerJsession = _array[0];
					}

					logger.info("HeaderJsession in Nimbus: " + headerJsession);
				break;
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
					HeaderJsessionStr = "";

					List<String> cookies = pushcon.getHeaderFields().get("Set-Cookie");
					for (String temp : cookies) {
						setHeaderJSession(temp);
					}	
						logger.info("HeaderJsession in idcloud: " + headerJsession);
					break;
				default:
					break;
				}
				

			} else {

				logger.info("CAS server authentication has been failed!");
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
			logger.info("Exception : CAS server authentication error!", e);
			pushresponseData = e.toString();
		}

	}

	/**
	 *
	 * @param temp
	 */
	private void setHeaderJSession(String temp) {
		String[] _array;
		if(temp.contains("JSESSIONID")){
			_array = temp.split(";");
			synchronized (headerJsession) {
				headerJsession = _array[0];
			}
		}
	}

	/**
	 * This method is to check, User (Beneficiary/Current user) is registered on
	 * CAS or not
	 * 
	 * @param userId
	 * @return will be true - if user is registered on CAS will be false - if
	 *         user is not registered on CAS
	 */
	public boolean isUserRegisterOnCASServer(String userId) {
		boolean isUserRegister = false;
		String pushresponseData = "";
		//String _pushurl = "";
		String casURL = "";
		String stateCode = "";

		try {
			casURL = urlUtil.findUserFromCASServerURL();
			logger.info("casURL : "+casURL);
			logger.info("UserId, to find on CAS : "+userId);
			
			casURL += userId;
			logger.info("Updated casURL : "+casURL);
			
			//_pushurl = casURL + userId;
			logger.info("[isUserRegisterOnCASServer call] pushURL : " + casURL);
			logger.info("[isUserRegisterOnCASServer call] HeaderJsession" + headerJsession);

			URL pushobj = new URL(casURL);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

			pushcon.setRequestMethod("GET");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_X_WWW_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, headerJsession);

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[isUserRegisterOnCASServer call] GET Response Code : " + pushresponseData);
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[isUserRegisterOnCASServer call] pushresponseData from CAS : " + pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {

				logger.info("[isUserRegisterOnCASServer call] Success!");

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()
				String pushinputLine = "";
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();

				pushresponseData = _pushresponse.toString();
				logger.info("[isUserRegisterOnCASServer call] pushResponseData : " + pushresponseData);

				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);

				NodeList _nList = usersdoc.getElementsByTagName("State");
				Element element = (Element) _nList.item(0);
				stateCode = element.getLastChild().getTextContent();

				logger.info("[isUserRegisterOnCASServer call] User State code : " + stateCode);

				if (!stateCode.isEmpty() && stateCode != null
						&& stateCode.equals(EzioMobileDemoConstant.CAS_USER_STATE_ACTIVE)) {
					isUserRegister = true;
				}

			} else {

				logger.info("[isUserRegisterOnCASServer call] Failed!");

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
			logger.info("Exception [isUserRegisterOnCASServer call] : ");
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return isUserRegister;
	}

	/**
	 * @param userId
	 * @return
	 */
	public Map<String, String> getDeviceCountForLogin(String userId) {

		int tokenCount = 0;
		int iDeviceType_1_Count = 0;
		int iDeviceType_3_Count = 0;
		int iDeviceType_OtherDevice_Count = 0;

		String pushresponseData = "";
		String _pushurl = "";
		String casURL = "";

		Map<String, String> supportDeviceMap = new HashMap<String, String>();

		try {
			casURL = urlUtil.findUserFromCASServerURL();
			_pushurl = casURL + userId;
			logger.info("[getDeviceCountForLogin call] pushURL : " + _pushurl);
			logger.info("[getDeviceCountForLogin call] HeaderJsession" + headerJsession);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

			pushcon.setRequestMethod("GET");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_X_WWW_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, headerJsession);

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[getDeviceCountForLogin call] GET Response Code : " + pushresponseData);
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[getDeviceCountForLogin call] pushresponseData from CAS : " + pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {

				logger.info("[getDeviceCountForLogin call] Success!");

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()
				String pushinputLine = "";
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();

				pushresponseData = _pushresponse.toString();
				logger.info("[getDeviceCountForLogin call] pushResponseData : " + pushresponseData);

				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);

				usersdoc.getDocumentElement().normalize();

				NodeList tokenNameList = usersdoc.getElementsByTagName("TokenName");
				logger.info("TokenName Length : " + tokenNameList.getLength());

				NodeList tokenTypeList = usersdoc.getElementsByTagName("TokenType");
				logger.info("TokenName Length : " + tokenTypeList.getLength());

				tokenCount = tokenNameList.getLength();
				supportDeviceMap.put("tokenCount", String.valueOf(tokenCount));

				for (int i = 0; i < tokenNameList.getLength(); i++) {
					logger.info("\n" + tokenNameList.item(i).getChildNodes().item(0).getNodeValue() + " at position : ["
							+ i + "]  Type : " + tokenTypeList.item(i).getChildNodes().item(0).getNodeValue());

					switch (tokenTypeList.item(i).getChildNodes().item(0).getNodeValue()) {

					case EzioMobileDemoConstant.DEVICE_MOBILE_TYPE_1:
						iDeviceType_1_Count++;
						supportDeviceMap.put("deviceWithType_1", String.valueOf(iDeviceType_1_Count));
						
						if(tokenNameList.item(i).getChildNodes().item(0).getNodeValue().substring(0, 4).equalsIgnoreCase(PREFIX_DEVICE_QRTOKEN)){
							supportDeviceMap.put("qrtokenName", tokenNameList.item(i).getChildNodes().item(0).getNodeValue());
						}
						break;
					case EzioMobileDemoConstant.DEVICE_MOBILE_TYPE_3:
					case EzioMobileDemoConstant.DEVICE_MOBILE_TYPE_8:
						if(tokenNameList.item(i).getChildNodes().item(0).getNodeValue().substring(0, 4).equalsIgnoreCase(PREFIX_DEVICE_MOBILE_SIGNATURE_GAOC)){
							iDeviceType_3_Count++;
							supportDeviceMap.put("deviceWithType_3", String.valueOf(iDeviceType_3_Count));
							break;
						}
						else{
							iDeviceType_OtherDevice_Count++;
							supportDeviceMap.put("deviceWithType_Other", String.valueOf(iDeviceType_OtherDevice_Count));
							break;
						}
						
					default:
						break;
					}
				}
				logger.info("\nfinal map : " + supportDeviceMap.toString());
			} else {

				logger.info("[getDeviceCountForLogin call] Failed!");

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
			logger.info("Exception [getDeviceCountForLogin call] : ");
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return supportDeviceMap;
	}

	
	
	
	
	/**
	 * This method creates and activates a new user
	 * 
	 * @param userId
	 * @return boolean
	 */
	public boolean createAndActiveUserOnCAS(String userId) {

		String pushresponseData = "";
		String _pushurl = "";
		Boolean isCreated = false;
		String pushXmlData = "<User><UserID>"+userId+"</UserID></User>";
		
		//<?xml version=\"1.0\" encoding=\"UTF-8\"?><User><UserID>"+userId+"</UserID></User>";
		try {
			_pushurl = urlUtil.findUserFromCASServerURL();
			//_pushurl = createUserURL + userId;
			logger.info("[createAndActiveUserOnCAS call] pushURL : " + _pushurl);
			logger.info("[createAndActiveUserOnCAS call] HeaderJsession : " + headerJsession);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, "text/xml");
			pushcon.setRequestProperty(COOKIE_PROP,headerJsession);

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			try (PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream())) {
				pushpw.write(pushXmlData);
				pushpw.flush();
			}
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("[createAndActiveUserOnCAS] pushresponseCode : "+pushresponseCode);
			
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("[createAndActiveUserOnCAS] pushresponseData : "+pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_CREATED) { // success
				logger.info("[createAndActiveUserOnCAS call] Success!");

				isCreated = true;
				StringBuffer _pushresponse;
				try (BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream()))) {
					String pushinputLine = "";
					_pushresponse = new StringBuffer();

					while ((pushinputLine = pushin.readLine()) != null) {
						_pushresponse.append(pushinputLine);
					}
				}

				pushresponseData = _pushresponse.toString();
				logger.info("[createAndActiveUserOnCAS call] pushResponseData : " + pushresponseData);
			} else {
				logger.info("[createAndActiveUserOnCAS call] Failed!");
				isCreated = false;
				StringBuffer _pushresponse;
				try (BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()))) {
					String pushinputLine;
					_pushresponse = new StringBuffer();
					while ((pushinputLine = pushin.readLine()) != null) {
						_pushresponse.append(pushinputLine);
					}
				}
			}
		} catch (Exception e) {
			logger.info("Exception [createAndActiveUserOnCAS call] error!!! : {} ", e);
			e.printStackTrace();
		}
		finally {
			logger.info("isCreated: "+isCreated);
		}
		return isCreated;
	}

}
