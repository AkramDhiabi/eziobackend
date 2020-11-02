package com.gemalto.eziomobile.demo.webhelper.tokenmanagement;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.util.MultipartUtility;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class TokenManagementWebHelper {

	public static String HeaderJsession = "";
	
	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private CommonWebHelper commonWebHelper;
	
	private static final LoggerUtil logger = new LoggerUtil(TokenManagementWebHelper.class.getClass());

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;

	public TokenManagementWebHelper() {
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Cannot parse XML {}.", e);
		}
	}
	
	/**
	 * @param fileName
	 * @param uploadPath
	 */
	public boolean uploadBatchProvisioningFile(String fileName, String uploadPath) {
		
		String charset = "UTF-8";
		File uploadFile = new File(uploadPath+"/"+fileName);
		String requestURL = "";
		boolean isProvisioningFileUploaded = false;
		
		logger.info("[upload file] path : "+uploadFile.getAbsolutePath());
		logger.info("[upload file] name of file : "+uploadFile.getName()+"\n\n");
		
		try {
		
		requestURL =  urlUtil.uploadBatchProvisioningFileURL();
		logger.info("[uploadBatchProvisioningFile] requestURL : "+requestURL);

		synchronized (HeaderJsession) {
			HeaderJsession = CASWebHelper.headerJsession;
		}
		logger.info("[uploadBatchProvisioningFile] HeaderJsession : "+HeaderJsession);
			
		 MultipartUtility multipart = new MultipartUtility(requestURL, charset, HeaderJsession);

		 multipart.addFormField("fieldNameHere", uploadFile.getName());
		 multipart.addFilePart("fieldNameHere", uploadFile);

		 List < String > response = multipart.finish();
		 logger.info("[uploadBatchProvisioningFile] response : "+response.size());
		 
		 isProvisioningFileUploaded = true;
		 logger.info("SERVER REPLIED:");

		 for (String line: response) {
			 logger.info(line);
		 }
		} catch (IOException ex) {
			logger.error("IO Exception!", ex);
		}
		return isProvisioningFileUploaded;
		
	}
	
	/**
	 * @param passphraseKey
	 * @param deviceType
	 */
	public void startBatchProvisioning(String passphraseKey, String deviceType) {
		
		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";
		String policyType = "";
		
		logger.info("[Starting batch provisioning.............]");
		logger.info("[Start batch provisioning] passphraseKey : "+passphraseKey);
		
		if(deviceType.equalsIgnoreCase(CommonOperationsConstants.PREFIX_FLEX)){
			policyType = CommonOperationsConstants.FLEX_POLICY;
		}else if(deviceType.equalsIgnoreCase(CommonOperationsConstants.PREFIX_SIGNER)){
			policyType = CommonOperationsConstants.SIGNER_POLICY;
		}else if(deviceType.equalsIgnoreCase(CommonOperationsConstants.PREFIX_PICO)){
			policyType = CommonOperationsConstants.PICO_POLICY;
		}else if(deviceType.equalsIgnoreCase(CommonOperationsConstants.PREFIX_LAVA_TIME_BASED)){
			policyType = CommonOperationsConstants.LAVA_POLICY;
		}else if(deviceType.equalsIgnoreCase(CommonOperationsConstants.PREFIX_DISPLAY_CARD_PAD)){
			policyType = CommonOperationsConstants.DISPLAY_CARD_PAD_POLICY;
		}else if(deviceType.equalsIgnoreCase(CommonOperationsConstants.PREFIX_DISPLAY_CARD_PAD_EB)){
			policyType = CommonOperationsConstants.DISPLAY_CARD_PAD_POLICY_EB;
		}
	
		logger.info("<------ Policy Type : "+policyType+" -------->");
		
		pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><BatchProvisioningRequest><FailOnError>true</FailOnError><Key>OATH Random Key</Key><Policy>"+policyType+"</Policy><PBEPassword>"+passphraseKey+"</PBEPassword></BatchProvisioningRequest>";
		logger.info("[startBatchProvisioning] pushXmlData : "+pushXmlData);
		  try  
		  {
		  		synchronized (HeaderJsession) {
					HeaderJsession = CASWebHelper.headerJsession;
				}
			    logger.info("[Start batch provisioning] HeaderJsession : "+HeaderJsession);
		  		_pushurl = urlUtil.launchProvisioningURL();
		  		logger.info("[startBatchProvisioning]_pushurl :"+_pushurl);
		  		
		  		URL pushobj = new URL(_pushurl);  
				HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
				pushcon.setRequestMethod("POST");
				pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML);
				pushcon.setRequestProperty(COOKIE, HeaderJsession);
				
				// For POST only - START
				pushcon.setDoInput(true);  
				pushcon.setDoOutput(true);
				PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
				pushpw.write(pushXmlData);
				pushpw.flush();
				pushpw.close();
				// For POST only - END
				
				int pushresponseCode = pushcon.getResponseCode();
				logger.info("[Start batch provisioning] pushresponseCode : " + pushresponseCode);
				
				pushresponseData = Integer.toString(pushresponseCode);
				logger.info("[Start batch provisioning] pushresponseData : "+pushresponseData);

				if (pushresponseCode == HttpURLConnection.HTTP_OK) 
				{ //success
					
					logger.info("[Start batch provisioning] SUCCESS!!");
					BufferedReader pushin;
					try (InputStreamReader inputStreamReader = new InputStreamReader(pushcon.getInputStream())) {
						pushin = new BufferedReader(inputStreamReader);
					}

					String pushinputLine;
					StringBuffer _pushresponse = new StringBuffer();
		
					while ((pushinputLine = pushin.readLine()) != null) {
						_pushresponse.append(pushinputLine);
					}
					pushin.close();
					pushresponseData = _pushresponse.toString();
					
					logger.info("[Start batch provisioning] pushResponseData : " + pushresponseData);

					InputSource is = new InputSource(new StringReader(pushresponseData));
					Document usersdoc = builder.parse(is);

					logger.info("[Start batch provisioning] XML response : " + usersdoc.toString());
				} 
				else 
				{
					logger.info("[Start batch provisioning] FAILED!!");
					BufferedReader pushin = new BufferedReader(new InputStreamReader(
							pushcon.getErrorStream())); 
					String pushinputLine;
					StringBuffer _pushresponse = new StringBuffer();
					while ((pushinputLine = pushin.readLine()) != null) {
						_pushresponse.append(pushinputLine);
					}
					pushin.close();
					pushresponseData = _pushresponse.toString();
				}
		  }  
		  catch (Exception e)  
		  { 
			logger.info("[Start batch provisioning] Exception!!");
			e.printStackTrace();  
			//pushresponseData = e.toString();
		  }
	}


	/**
	 *
	 * @return
	 */
	public Map<String, String> getBatchProvisioningStatus() {

		String pushresponseData = "";
		String _pushurl = "";
		String provisioningStatusURL = "";
		
		String status = "";
		String duration = "";
		String startDate = "";
		String totalOperations = "";
		String failedOperations = "";

		synchronized (HeaderJsession) {
			HeaderJsession = CASWebHelper.headerJsession;
		}
		
		Map<String, String> statusMap = new HashMap<String, String>();

		try {
			provisioningStatusURL = urlUtil.provisioningStatusURL(); 
			//getProvisioningStatusURL(EzioMobileDemoConstant.EZIO_PROPERTIES_PATH, context);
			logger.info("[getBatchProvisioningStatus] provisioningStatusURL : "+provisioningStatusURL);
			
			_pushurl = provisioningStatusURL;
			logger.info("_pushURL : " + _pushurl);
			logger.info("\n [Provisioning Status] HeaderJsession "+ HeaderJsession);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

			pushcon.setRequestMethod("GET");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, "application/x-www-form-urlencoded");
			pushcon.setRequestProperty(COOKIE, HeaderJsession);

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[Provisioning Status] pushresponseData : "+ pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {

				logger.info("[Provisioning Status] SUCCESS!!");

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()
				String pushinputLine = "";
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();

				pushresponseData = _pushresponse.toString();
				logger.info("[Provisioning Status] Success.....1 : " + pushresponseData);

				DocumentBuilder builder = factory.newDocumentBuilder();
				logger.info("[Provisioning Status] Success.....2 ");

				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);
				logger.info("[Provisioning Status] Success.....3 ");
				logger.info("[Provisioning Status] XML response  : "+ usersdoc.toString());

				NodeList _nList = usersdoc.getElementsByTagName("BatchProvisioningStatus");

				Element err = (Element) _nList.item(0);
				
				status = err.getElementsByTagName("Status").item(0).getTextContent();
				duration = err.getElementsByTagName("Duration").item(0).getTextContent();
				startDate = err.getElementsByTagName("StartDate").item(0).getTextContent();
				totalOperations = err.getElementsByTagName("TotalOperations").item(0).getTextContent();
				failedOperations = err.getElementsByTagName("FailedOperations").item(0).getTextContent();

				logger.info("status : "+status);
				logger.info("duration : "+duration);
				logger.info("startDate : "+startDate);
				logger.info("totalOperations : "+totalOperations);
				logger.info("failedOperations : "+failedOperations);

				statusMap.put("status", status);
				statusMap.put("duration", duration);
				statusMap.put("startDate", startDate);
				statusMap.put("totalOperations", totalOperations);
				statusMap.put("failedOperations", failedOperations);

			} else {

				logger.info("[Provisioning Status] FAILED!!");

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
			logger.info("[Provisioning Status] Exception!!");
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return statusMap;
	}

	
	/**
	 * @param userId
	 * @param deviceID
	 */
	public boolean linkDevices(String userId, String deviceID) {
		
		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";
		boolean isDeviceLinked = false;
		
		logger.info("[Linking devices.............]");
		
		pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><TokenAction><Action>Link</Action><ActionData><UserID>"+userId+"</UserID></ActionData></TokenAction>";
		try
		{
			synchronized (HeaderJsession) {
			  HeaderJsession = CASWebHelper.headerJsession;
			}
			logger.info("[linkDevices] HeaderJsession : "+HeaderJsession);

			String url = urlUtil.linkDevicesURL();
			//getLinkDevicesURL(EzioMobileDemoConstant.EZIO_PROPERTIES_PATH, context);
			logger.info("[linkDevices] URL : "+url);

			_pushurl = url.replace("<xDeviceId>",deviceID);//replaces xDeviceID with deviceId in URL
			_pushurl = _pushurl.replace("<xDeviceType>", getDeviceTypeWithPrefix(deviceID));
			logger.info("[linkDevices] Replaced URL : "+ _pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, HeaderJsession);

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("POST Response Code [Link devices] : " + pushresponseCode);
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("pushresponseData [Link devices] : "+pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK)
			{ //success

				logger.info("[Link device] Success!!");
				StringBuffer _pushresponse = generatePushResponse(pushcon.getInputStream());
				pushresponseData = _pushresponse.toString();

				isDeviceLinked = true;
			}
			else
			{
				logger.info("[Link devices] Failed!!");
				StringBuffer _pushresponse = generatePushResponse(pushcon.getErrorStream());
				pushresponseData = _pushresponse.toString();
			}
		}
		catch (Exception e)
		{
		  	isDeviceLinked = false;
			logger.info("[Link devices] Exception!!");
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return isDeviceLinked;
	}
	
	
	
	/**
	 * @param userId
	 * @param deviceID
	 */
	public boolean activateDevices(String userId, String deviceID) {
		
		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";
		boolean isDeviceActivated = false;
		
		logger.info("[Activating devices.............]");
		
		pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><TokenAction><Action>Activate</Action></TokenAction>";
		try
		{
			synchronized (HeaderJsession) {
				HeaderJsession = CASWebHelper.headerJsession;
			}
			logger.info("[activateDevices] HeaderJsession : "+HeaderJsession);

			String url = urlUtil.linkDevicesURL();
			//getLinkDevicesURL(EzioMobileDemoConstant.EZIO_PROPERTIES_PATH, context);
			logger.info("[activateDevices] URL : "+url);

			_pushurl = url.replace("<xDeviceId>",deviceID);//replaces xDeviceID with deviceId in URL
			_pushurl = _pushurl.replace("<xDeviceType>", getDeviceTypeWithPrefix(deviceID));

			logger.info("Replaced URL : "+ _pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, HeaderJsession);

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("POST Response Code [Activate devices] : " + pushresponseCode);
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("pushresponseData [Activate devices] : "+pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK)
			{ //success

				logger.info("[Activate devices] Success!!");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getInputStream())); //getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();

				isDeviceActivated = true;
			}
			else
			{
				logger.info("[Activate devices] Failed!!");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			}
		}
		catch (Exception e)
		{
			isDeviceActivated = false;
			logger.info("[Activate devices] Exception!!");
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return isDeviceActivated;
	}
	
	
	
	/**
	 * @param deviceID
	 * @return
	 */
	public static boolean checkDeviceFormat(String deviceID){
		if(deviceID.length() != CommonOperationsConstants.TOKEN_NAME_LEN){
			return false;
		}
		switch(deviceID.substring(0,4)){
			case CommonOperationsConstants.PREFIX_FLEX:
			case CommonOperationsConstants.PREFIX_SIGNER:
			case CommonOperationsConstants.PREFIX_PICO:
			case CommonOperationsConstants.PREFIX_LAVA_TIME_BASED:
			case CommonOperationsConstants.PREFIX_LAVA_EVENT_BASED:
			case CommonOperationsConstants.PREFIX_DISPLAY_CARD_PAD:
			case CommonOperationsConstants.PREFIX_DISPLAY_CARD_PAD_EB:
			case CommonOperationsConstants.PREFIX_QRTOKEN:
				break;
			default:
				return false;
		}
		return true;
	}

	
	/**
	 * @param deviceID
	 * @return
	 */
	public static String getDeviceTypeWithPrefix(String deviceID){
		if(deviceID.length() != CommonOperationsConstants.TOKEN_NAME_LEN){
			return "";
		}
		
		switch(deviceID.substring(0,4)){
			case CommonOperationsConstants.PREFIX_FLEX:
			case CommonOperationsConstants.PREFIX_SIGNER:
			case CommonOperationsConstants.PREFIX_PICO:
			case CommonOperationsConstants.PREFIX_DISPLAY_CARD_PAD:
			case CommonOperationsConstants.PREFIX_DISPLAY_CARD_PAD_EB:
			case CommonOperationsConstants.PREFIX_QRTOKEN:
				return "1";
			case CommonOperationsConstants.PREFIX_LAVA_TIME_BASED:
			case CommonOperationsConstants.PREFIX_LAVA_EVENT_BASED:
			case CommonOperationsConstants.PREFIX_MOBILE_LOGIN:
			case CommonOperationsConstants.PREFIX_MOBILE_OC:
				return "3";
			case CommonOperationsConstants.PREFIX_DCV_PHYSIQUE:
			case CommonOperationsConstants.PREFIX_DCV_MOBILE:
				return "7";
			default:
				return "";
		}
	}
	
	/**
	 * @param userId
	 * @param deviceID : passed from input field
	 	public static final int BAD_FORMAT = 0x1F;
		public static final int TOKEN_NOT_FOUND = 0x2F;
		public static final int TOKEN_ALREADY_ASSOCIATED = 0x3F;
		public static final int ALL_OK = 0x0F;
	 */
	public int checkDevice(String userId, String deviceID) {

		String _pushurl = "";
		String pushresponseData = "";
		int sResponseCode = -1;
		String sTokenType="";
		String sTokenUserId="";
		String sTokenState="";
		StringBuffer _pushresponse;

		logger.info("[checkDevice: check format.............]");
		
		if(!checkDeviceFormat(deviceID)){
			return CommonOperationsConstants.BAD_FORMAT;
		}
		
		logger.info("[checkDevice: authenticate.............]");
		
		casWebHelper.authenticateCASever();
		synchronized (HeaderJsession) {
			HeaderJsession = CASWebHelper.headerJsession;
		}
			
		if(HeaderJsession.isEmpty()){
			return CommonOperationsConstants.GENERAL_ERROR;
		}
		
		logger.info("[checkDevice: authenticated.............]");
		
		  try  
		  {  
			  logger.info("[checkDevice] HeaderJsession : "+HeaderJsession);
				// should not be hardcoded
			  	
			  	String getDeviceURL = urlUtil.getDeviceByDeviceIdURL(); 
			  	//getDeviceByDeviceIdURL(EzioMobileDemoConstant.EZIO_PROPERTIES_PATH, context);
		  		logger.info("[checkDevice] getDeviceURL : "+getDeviceURL);
			  	
			  	_pushurl = getDeviceURL + deviceID;
		  		
			  	logger.info("attachDeviceToUser: get URL : "+ _pushurl);

		  		URL pushobj = new URL(_pushurl);  
				HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
				pushcon.setRequestMethod("GET");
				pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
				pushcon.setRequestProperty(COOKIE_PROP, HeaderJsession);
				
				int pushresponseCode = pushcon.getResponseCode();
				
				pushresponseData = Integer.toString(pushresponseCode);
				logger.info("pushresponseData [attachDeviceToUser] : "+pushresponseData);

				switch (pushresponseCode) {
					case HttpURLConnection.HTTP_OK:
						BufferedReader pushin = new BufferedReader(new InputStreamReader(
								pushcon.getInputStream())); //getErrorStream()

						String pushinputLine;
						_pushresponse = new StringBuffer();

						while ((pushinputLine = pushin.readLine()) != null) {
							_pushresponse.append(pushinputLine);
						}
						pushin.close();
						pushresponseData = _pushresponse.toString();

						logger.info("[checkDevice] pushResponseData : " + pushresponseData);


						InputSource is = new InputSource(new StringReader(pushresponseData));
						Document usersdoc = builder.parse(is);

						logger.info("[checkDevice] XML response : " + usersdoc.toString());

						NodeList _nList = usersdoc.getElementsByTagName("Token");
						Element err = (Element) _nList.item(0);
						Node tmpNode = null;

						//Will get token type, normally it should be 1, multiseed device
						sTokenType = err.getElementsByTagName("TokenType").item(0).getTextContent();
						tmpNode = err.getElementsByTagName("User").item(0);

						//if not linked to user
						if(tmpNode != null){
							sTokenUserId = tmpNode.getTextContent();
						}

						//ACTIVE, initialized, revoked or blocked
						sTokenState = err.getElementsByTagName("State").item(0).getTextContent();
						break;
					case CommonOperationsConstants.HTTP_TOKEN_NOT_FOUND:
						// token not found
						logger.info("[checkDevice] TOKEN not found!!");
						sResponseCode = CommonOperationsConstants.TOKEN_NOT_FOUND;
						break;
					default:
						logger.info("[checkDevice] Failed!!");
						_pushresponse = generatePushResponse(pushcon.getErrorStream());
						pushresponseData = _pushresponse.toString();

						//On fail
						sResponseCode = CommonOperationsConstants.GENERAL_ERROR;
						break;

				}
		  }  
		  catch (Exception e)  
		  { 
			logger.info("[checkDevice] Exception!!");
			e.printStackTrace();  
			pushresponseData = e.toString();
			sResponseCode = CommonOperationsConstants.GENERAL_ERROR;
		  }

		  if(sResponseCode != -1){
			return sResponseCode;
		  }
		  
		  logger.info("[checkDevice: start checking.............]");

		return returnCommonOperations(sTokenType, sTokenUserId, sTokenState);
	}

	/**
	 *
	 * @param sTokenType
	 * @param sTokenUserId
	 * @param sTokenState
	 * @return
	 */
	private int returnCommonOperations(String sTokenType, String sTokenUserId, String sTokenState) {
		if(sTokenType.isEmpty() || sTokenState.isEmpty() || (!sTokenType.equals("1") && !sTokenType.equals("2") && !sTokenType.equals("3"))){
			return CommonOperationsConstants.GENERAL_ERROR;
		}else if(!sTokenUserId.equals("")){
			return CommonOperationsConstants.TOKEN_ALREADY_ASSOCIATED;
		}else if(!sTokenState.equals(CommonOperationsConstants.STATE_INITIALIZED) && !sTokenState.equals(CommonOperationsConstants.STATE_ACTIVED)){
			// only actived or initialized
			return CommonOperationsConstants.TOKEN_BLOCKED_OR_REVOKED;
		}else{
			if(sTokenState.equals("1")){
			   return CommonOperationsConstants.ALL_OK_INITIALIZED;
			}else{
				return CommonOperationsConstants.ALL_OK_ACTIVATED;
			}
		}
	}

	/**
	 *
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private StringBuffer generatePushResponse(InputStream stream) throws IOException {
		StringBuffer _pushresponse;
		try (BufferedReader pushinDefault = new BufferedReader(new InputStreamReader(stream))) {
			String pushinputLineDefault;
			_pushresponse = new StringBuffer();
			while ((pushinputLineDefault = pushinDefault.readLine()) != null) {
				_pushresponse.append(pushinputLineDefault);
			}
		}
		return _pushresponse;
	}


	/**
	 * @param tokenSerialNumber
	 * @param otpValue1
	 * @param otpValue2
	 * @return
	 */
	public boolean doTokenResynchronization(String tokenSerialNumber, String otpValue1, String otpValue2){
		
		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";
		boolean isTokenResyncronized = false;
		
		logger.info("[Token Resynchronization.....]");
		
		pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+"<TokenAction>"
					+"<Action>ResyncSeed</Action>"
					+"<ActionData>"
							+"<Seed>"
								+"<Name>"+tokenSerialNumber+"#1</Name>"
								+"<AuthType>otb</AuthType>"
							+"</Seed>"
							+"<AuthenticationData>"
								+"<OTPData>"
								+"<OTP1>"+otpValue1+"</OTP1>"
								+"<OTP2>"+otpValue2+"</OTP2>"
								+"</OTPData>"
							+"</AuthenticationData>"
					+"</ActionData>"
				+"</TokenAction>";

		try
		{
			logger.info("[Token Resynchronization] pushXmlData : "+pushXmlData);

			synchronized (HeaderJsession) {
				HeaderJsession = CASWebHelper.headerJsession;
			}
			logger.info("[activateDevices] HeaderJsession : "+HeaderJsession);

			_pushurl = urlUtil.getDeviceByDeviceIdURL()+tokenSerialNumber;
			logger.info("[Token Resynchronization] _pushurl : "+_pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, HeaderJsession);

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("[Token Resynchronization] POST Response Code : " + pushresponseCode);
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("[Token Resynchronization] pushresponseData : "+pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK)
			{ //success

				logger.info("[Token Resynchronization] Success!!");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getInputStream())); //getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();

				isTokenResyncronized = true;
			}
			else
			{
				logger.info("[Token Resynchronization] Failed!!");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			}
		}
		catch (Exception e)
		{
			isTokenResyncronized = false;
			logger.info("[Token Resynchronization] Exception!!");
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return isTokenResyncronized;
	}
	
	
	/**This method is to get the response object based on the device status
	 * @param userId
	 * @param otpValue
	 * @param otpValue2
	 * @param tokenSerialNumber
	 * @param operationType
	 * @return will get 409 response code only for Token-Activation not for resynchronization
	 */
	public ResultStatus getDeviceResponseStatus(String userId, String otpValue, String otpValue2, String tokenSerialNumber, String operationType){
		
		ResultStatus resultStatus = new ResultStatus();
		boolean isDeviceLinked = false;
		boolean isDeviceActivated = false;
		boolean isOTPValid = false;
	//	boolean isOTPValidationNeeded = true;
		Map<String,String> hm = new HashMap<String,String>();
		
		logger.info("[getDeviceResponseStatus] Token_operationType : "+operationType);
		logger.info("[getDeviceResponseStatus] userId : "+userId);
		
		int iRes = checkDevice(userId, tokenSerialNumber);

		if(iRes == CommonOperationsConstants.BAD_FORMAT){
			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_BAD_FORMAT);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
			resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
		}else if(iRes == CommonOperationsConstants.TOKEN_NOT_FOUND){
			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_TOKEN_NOT_FOUND);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NOT_FOUND_404);
			resultStatus.setStatusCode(HttpStatus.NOT_FOUND);
		}else if(iRes == CommonOperationsConstants.TOKEN_ALREADY_ASSOCIATED && operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TOKEN_ACTIVATION)){
			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_TOKEN_ALREADY_ASSOCIATED);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_CONFLICT_409);
			resultStatus.setStatusCode(HttpStatus.CONFLICT);
		}else if(iRes == CommonOperationsConstants.GENERAL_ERROR){
			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_GENERAL_ERROR);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_FORBIDDEN_403);
			resultStatus.setStatusCode(HttpStatus.FORBIDDEN);
		}else if(iRes == CommonOperationsConstants.TOKEN_BLOCKED_OR_REVOKED){
			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_TOKEN_BLOCKED_OR_REVOKED);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NOT_FOUND_404);
			resultStatus.setStatusCode(HttpStatus.NOT_FOUND);
		}
		
		
		else if(iRes == CommonOperationsConstants.ALL_OK_INITIALIZED){
			
			if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TOKEN_ACTIVATION)){
				if(tokenSerialNumber.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN)) {
					String headerJsession = null;
					headerJsession = CASWebHelper.headerJsession;
					if(headerJsession == null){
						return null;
					}
					hm = commonWebHelper.getTokenList(headerJsession, userId);
					String sTempTokenName = null;
					Set set = hm.entrySet();
					Iterator iter = set.iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						sTempTokenName = (String) entry.getKey();
						logger.info("print sTempTokenName :"+ sTempTokenName);
						if(sTempTokenName.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN)) {
							resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_LINK_OR_ACTIVATION_FAILED);
							resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
							resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
							return resultStatus;
						}
					}
				}
		
				isDeviceLinked = linkDevices(userId, tokenSerialNumber);
				isDeviceActivated = activateDevices(userId, tokenSerialNumber);
				
				if(isDeviceLinked && isDeviceActivated){
					isOTPValid = validateOTP(userId, otpValue, tokenSerialNumber);

					if(isOTPValid){
						logger.info("[activateTokenOfUser]");
						resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_INITIALIZED);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
					}else{
						resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_INITIALIZED_OTP_VALIDATION_FAILED);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
					}
				}else{
					logger.error("Linking device or Activation is failed!  isDeviceLinked : "+isDeviceLinked+" -- isDeviceActivated : "+isDeviceActivated);
					
					resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_LINK_OR_ACTIVATION_FAILED);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				}
			}
			else if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TOKEN_RESYNCHRONIZATION)){
				
				resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_RESYNCHRONIZATION_MSG_TYPE_FAILED);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
			}
		}
		
		
		else if(iRes == CommonOperationsConstants.ALL_OK_ACTIVATED){
			
			if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TOKEN_ACTIVATION)){
				if(tokenSerialNumber.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN)) {
					String headerJsession = null;
					headerJsession = CASWebHelper.headerJsession;
					if(headerJsession == null){
						return null;
					}
					hm = commonWebHelper.getTokenList(headerJsession, userId);
						String sTempTokenName = null;
						Set set = hm.entrySet();
						Iterator iter = set.iterator();
							while (iter.hasNext()) {
								Map.Entry entry = (Map.Entry) iter.next();
								sTempTokenName = (String) entry.getKey();
								logger.info("print sTempTokenName :"+ sTempTokenName);
								if(sTempTokenName.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN)) {
									resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_LINK_OR_ACTIVATION_FAILED);
									resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
									resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);					
									return resultStatus;
								}
							}					
				}
				
				isDeviceLinked = linkDevices(userId, tokenSerialNumber);
				if(isDeviceLinked){
					isOTPValid = validateOTP(userId, otpValue, tokenSerialNumber);
					if(isOTPValid){
						resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_ACTIVATED);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
					}else{
						resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_ACTIVATED_OTP_VALIDATION_FAILED);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.OK);
					}
				}else{
					logger.error("Linking device is failed!  isDeviceLinked : "+isDeviceLinked);
					
					resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_LINK_DEVICE_TO_USER_FAILED);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				}
			}
			else if(operationType.equalsIgnoreCase(EzioMobileDemoConstant.OPERATION_TOKEN_RESYNCHRONIZATION)){
				
				//Resynchronizations call
				boolean isTokenResynchronized = doTokenResynchronization(tokenSerialNumber, otpValue, otpValue2);
				logger.info("[getDeviceStatus] isTokenResynchronization : "+isTokenResynchronized);
				
				if(isTokenResynchronized){
					resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_RESYNCHRONIZATION_MSG_TYPE_DONE);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
					resultStatus.setStatusCode(HttpStatus.OK);
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_RESYNCHRONIZATION_MSG_TYPE_FAILED);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
					resultStatus.setStatusCode(HttpStatus.OK);
				}
			}
		}else{
			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_MANAGMENT_MSG_TYPE_UNKNOWN);
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_UNKNOWN_500);
			resultStatus.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		resultStatus.setTemplateObject(tokenSerialNumber);
		
		return resultStatus;
 	}

	/**
	 *
	 * @param userId
	 * @param otpValue
	 * @param tokenSerialNumber
	 * @return
	 */
	private boolean validateOTP(String userId, String otpValue, String tokenSerialNumber) {
		boolean isOTPValid;
		if (tokenSerialNumber.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN)) {
			isOTPValid = true;
		} else {
			isOTPValid = commonWebHelper.validateOTP(userId, otpValue);
		}
		return isOTPValid;
	}


	//LIST OF TOKENS
	// Better to have a CAS request for this... 
	public String getTokenType(String sTokenName){
		switch(sTokenName.substring(0,4)){
			case CommonOperationsConstants.PREFIX_DEVICE_FLEX:
			case CommonOperationsConstants.PREFIX_DEVICE_SIGNER:
			case CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN:
			case CommonOperationsConstants.PREFIX_DEVICE_PICO:
			case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD_EB:
			case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD:
				return "1";
			case CommonOperationsConstants.PREFIX_DEVICE_LAVA:
			case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_LOGIN:
			case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_SIGNATURE_TRANSACTION:
				return "3";
			case CommonOperationsConstants.PREFIX_DCV_PHYSIQUE:
			case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_DCV:
				return "7";
			default:
				if(sTokenName.substring(0,3).equals(CommonOperationsConstants.PREFIX_DEVICE_EMV)){
					return "4";
				}
				return "-1";
		}
	}
	

	/**
	** formatTokenList
	*/
	public JSONObject formatTokenList(String sUserID){
		
		if(sUserID == null || sUserID.isEmpty())
			return null;
		
		Iterator iter = null; // set.iterator();
		Set set = null; // map.entrySet();
		JSONObject joGlobalResult = new JSONObject();
		
		String headerJsession = null;
		headerJsession = CASWebHelper.headerJsession;
		if(headerJsession == null){
			logger.info("getResetMyAccountOptions 1");
			return null;
		}
		
		try{
			
			int iglobalCount = 0;
			Map <String, String> hm = new HashMap<String, String>();
			hm = commonWebHelper.getTokenList(headerJsession, sUserID);
			if(hm == null){
				logger.info("resetMyAccount 3");
				return null;
			}
			if(hm.isEmpty()){
				logger.info("resetMyAccount 4");
				joGlobalResult.put(COUNT_PROP, Integer.toString(iglobalCount));
				return joGlobalResult;
			}

			set = hm.entrySet();
			iter = set.iterator();

			String sTempTokenName = null;
			String sTempsTokenType = null;
			String sTempsTokenState = null;
			
			int iMobileTokens = 0;
			int iPhysicalDCVTokens = 0;
			int iPhysicalTokens = 0;
			
			List<JSONObject> mobileTokenList = new ArrayList<>();
			List<JSONObject> physicalDCVTokenList = new ArrayList<>();
			List<JSONObject> physicalTokenList = new ArrayList<>();
			
			//JSONObject responseJSONObj = new JSONObject();
			JSONArray responseJSONArray = new JSONArray();
			
			JSONObject joMobileTokens = new JSONObject();
			JSONObject joPhysicalDCVTokens = new JSONObject();
			JSONObject joPhysicalTokens = new JSONObject();
			
			//JSONArray jaGenericTempToken = new JSONArray();
			JSONObject joGenericTempToken = new JSONObject();
			
			//Parse all tokens and do the required action 
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				
				sTempTokenName = (String) entry.getKey();
				sTempsTokenType = (String) entry.getValue();
				
				if(sTempTokenName == null || sTempsTokenType == null){
					return null;
				}
				
				logger.info("sTempTokenName: " + sTempTokenName);
				logger.info("sTempsTokenType: " + sTempsTokenType);
				
				sTempsTokenState = commonWebHelper.getDeviceState(headerJsession, sTempTokenName, sTempsTokenType);

				if(sTempsTokenState == null || sTempsTokenState.equals("-1")){
					return null;
				}
				
				logger.info("sTempsTokenState: " + sTempsTokenState);
				
				joGenericTempToken = fillTokenInfo(sTempTokenName, sTempsTokenType, sTempsTokenState);
				
				if( isMobileAssimilated(sTempTokenName) || isEMVAssimilated(sTempTokenName)){
					
					mobileTokenList.add(joGenericTempToken);
					iMobileTokens++;
					logger.info("isMobileAssimilated || isEMVAssimilated" + joMobileTokens.toString() + COUNT_LABEL + Integer.toString(iMobileTokens));
				}
				else if(isPhysicalDCVAssimilated(sTempTokenName)){
					
					physicalDCVTokenList.add(joGenericTempToken);
					iPhysicalDCVTokens++;
					logger.info("isPhysicalDCVAssimilated" + joPhysicalDCVTokens.toString() + COUNT_LABEL + Integer.toString(iPhysicalDCVTokens));
				}
				else if(isPhysicalTokenAssimilated(sTempTokenName)){
					
					physicalTokenList.add(joGenericTempToken);
					iPhysicalTokens++;
					logger.info("isPhysicalTokenAssimilated" + joPhysicalTokens.toString() + COUNT_LABEL + Integer.toString(iPhysicalTokens));
				}		
			}
			
			if(mobileTokenList != null && mobileTokenList.size() != 0){
				
				joMobileTokens.put(COUNT_PROP, Integer.toString(iMobileTokens));
				logger.info("joMobileTokens" + joMobileTokens.toString() + COUNT_LABEL + Integer.toString(iMobileTokens));
				
				joMobileTokens.put("type", "Mobile tokens");
				joMobileTokens.put("list", mobileTokenList);
				
				responseJSONArray.put(joMobileTokens);
				iglobalCount++;
			}
			if(physicalDCVTokenList != null && physicalDCVTokenList.size() != 0){
				
				joPhysicalDCVTokens.put(COUNT_PROP, Integer.toString(iPhysicalDCVTokens));
				logger.info("joPhysicalDCVTokens" + joPhysicalDCVTokens.toString() + COUNT_LABEL + Integer.toString(iPhysicalDCVTokens));
				
				joPhysicalDCVTokens.put("type", "DCV tokens");
				joPhysicalDCVTokens.put("list", physicalDCVTokenList);
				
				responseJSONArray.put(joPhysicalDCVTokens);
				iglobalCount++;
				
			}
			if(physicalTokenList != null && physicalTokenList.size() != 0){
				
				joPhysicalTokens.put(COUNT_PROP, Integer.toString(iPhysicalTokens));
				logger.info("joPhysicalTokens" + joPhysicalTokens.toString() + COUNT_LABEL + Integer.toString(iPhysicalTokens));
				
				joPhysicalTokens.put("type", "Physical tokens");
				joPhysicalTokens.put("list", physicalTokenList);
				
				responseJSONArray.put(joPhysicalTokens);
				iglobalCount++;
			}
				
			joGlobalResult.put(COUNT_PROP, Integer.toString(iglobalCount));
			
			logger.info("[formatTokenList] responseJSONArray length : "+responseJSONArray.length());
			
			if(responseJSONArray != null && responseJSONArray.length() != 0)
				joGlobalResult.put("tokensList", responseJSONArray);
			else
				return null;
			
		}catch(Exception e){
			logger.error("ERROR" + e);
			return null;
		}finally {
			logger.info("[formatTokenList] joGlobalResult : "+joGlobalResult.toString());
		}
		return joGlobalResult;
	}
	
	
	
	
	public JSONObject fillTokenInfo(String sTokenName, String sTokenType, String sTokenState){
		JSONObject resJSON_Obj = new JSONObject();
		try{
			resJSON_Obj.put("name", sTokenName);
			resJSON_Obj.put("type", getTypeName(Integer.parseInt(sTokenType)));
			resJSON_Obj.put("state", getStringTokenState(sTokenState));
			resJSON_Obj.put("release", isRelease(sTokenName));
			logger.info("fillTokenInfo 00 " + resJSON_Obj.toString());
		}catch(Exception e){
			logger.info("fillTokenInfo 12");
			return null;
		}
		return resJSON_Obj;
	}
	
	
	
	
	public String getStringTokenState(String sTokenState){
		switch(Integer.parseInt(sTokenState)){	
			case CommonOperationsConstants.ACTIVE:
				logger.info("getStringTokenState ............1");
				return "Activated";
			case CommonOperationsConstants.INITIALIZED:
				logger.info("getStringTokenState ............2");
				return "Initialized";
			case CommonOperationsConstants.BLOCKED:
				logger.info("getStringTokenState ............3");
				return "Blocked";
			case CommonOperationsConstants.REVOKED:
				logger.info("getStringTokenState ............4");
				return "Revoked";
			case CommonOperationsConstants.LOCKED:
				logger.info("getStringTokenState ............5");
				return "Locked";
			default:
				logger.info("6");
				return "Unknown";
		}
	}
	
	
	
	public boolean isRelease(String sTokenName){
		switch(sTokenName.substring(0,4)){
			case CommonOperationsConstants.PREFIX_DCV_PHYSIQUE:
			case CommonOperationsConstants.PREFIX_DEVICE_FLEX:
			case CommonOperationsConstants.PREFIX_DEVICE_SIGNER:
			case CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN:
			case CommonOperationsConstants.PREFIX_DEVICE_PICO:
			case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD_EB:
			case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD:
			case CommonOperationsConstants.PREFIX_DEVICE_LAVA:
				return true;
			default:
				return false;
		}	
	}
	
	
	
	public String getTypeName(int iTokenType){
		switch(iTokenType){
			case CommonOperationsConstants.MULTI_SEED_DEVICE:
				return "Multiseed device";
			case CommonOperationsConstants.SINGLE_SEED_TOTP_DEVICE:
				return "OATH time based device";
			case CommonOperationsConstants.EZIO_MOBILE_PROTECTOR:
				return "Ezio Mobile Protector";
			case CommonOperationsConstants.EMV_DEVICE:
				return "EMV device";
			case CommonOperationsConstants.DCVV_DEVICE:
				return "DCV device";
			default:
				return "";
		}	
	}
	
	
	
	public int releaseToken(String sUserID, String sTokenName){
		if(sUserID == null || sUserID.isEmpty())
			return CommonOperationsConstants.GENERAL_ERROR;
		
		Iterator iter = null; // set.iterator();
		Set set = null; // map.entrySet();

		String headerJsession = null;
		headerJsession = CASWebHelper.headerJsession;
		if(headerJsession == null){
			logger.info("getResetMyAccountOptions 1");
			return CommonOperationsConstants.GENERAL_ERROR;
		}
		
		String sTempsTokenState = null;
		
		try{
			
			sTempsTokenState = commonWebHelper.getDeviceState(headerJsession, sTokenName, getTokenType(sTokenName));

			if(sTempsTokenState == null || sTempsTokenState.equals("-1")){
				return CommonOperationsConstants.GENERAL_ERROR;
			}
				
			logger.info("sTempsTokenState: " + sTempsTokenState);

			if(CommonOperationsConstants.ALL_OK != commonWebHelper.doJobSingleToken(headerJsession, sUserID, sTokenName, sTempsTokenState, true, true, true)){
				return CommonOperationsConstants.GENERAL_ERROR;	
			}
		}catch(Exception e){
			logger.error("ERROR" + e);
			return CommonOperationsConstants.GENERAL_ERROR;
		}
		return CommonOperationsConstants.ALL_OK;
	}
	
	
	
	public static  boolean isMobileAssimilated(String sTokenName){
		switch(sTokenName.substring(0,4)){
			case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_LOGIN:
			case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_DCV:
			case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_SIGNATURE_TRANSACTION:
				return true;
			default:
				return false;
		}
	}
	
	public static  boolean isPhysicalDCVAssimilated(String sTokenName){
		switch(sTokenName.substring(0,4)){
			case CommonOperationsConstants.PREFIX_DCV_PHYSIQUE:
				return true;
			default:
				return false;
		}
	}
	
	public static  boolean isEMVAssimilated(String sTokenName){
		switch(sTokenName.substring(0,3)){
			case CommonOperationsConstants.PREFIX_DEVICE_EMV:
				return true;
			default:
				return false;
		}
	}
	
	public static  boolean isPhysicalTokenAssimilated(String sTokenName){
		switch(sTokenName.substring(0,4)){
			case CommonOperationsConstants.PREFIX_DEVICE_FLEX:
			case CommonOperationsConstants.PREFIX_DEVICE_SIGNER:
			case CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN:
			case CommonOperationsConstants.PREFIX_DEVICE_PICO:
			case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD_EB:
			case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD:
			case CommonOperationsConstants.PREFIX_DEVICE_LAVA:
				return true;
			default:
				return false;
		}
	}
	
}
