package com.gemalto.eziomobile.demo.webhelper.eps;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.util.PinGenerationUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class EPSWebHelper {

	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private PinGenerationUtil pinGenUtil;
	private DocumentBuilder builder;

	private static final LoggerUtil logger = new LoggerUtil(EPSWebHelper.class);
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public EPSWebHelper() {
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Cannot initiate a new document builder: {}.", e);
		}
	}

	/**To Enroll GAOC device
	 * @param userId
	 * @return
	 */
	public Map<String, String> doMobileEPSEnrollmentStepOne(String userId) {

		String responseData = "";
		String epsURL = "";
		String userRegistrationCodeEPS = "";
		String userPIN = "";
		String XmlData = "";
		Map<String, String> enrollmentData = null;
		
		try {
			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[doMobileEPSEnrollmentStepOne] backendConfiguration : "+backendConfiguration);
			
			switch (backendConfiguration) {
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
				XmlData = XML_VERSION
						+ OATH_TOKEN_XML
						+ USER_ID_OPEN_XML + userId+ USER_ID_CLOSE_XML
						+ OATH_ID_OPEN_XML
							+ MANUFACTURER_ID_GA_XML
							+ "<tokenType>OC</tokenType>"
						+ OATH_ID_CLOSE_XML
						+ "<domain>sas</domain>"
						+ ACTIVATION_DATE_2013_XML
						+ EXPIRATION_DATE_2024_XML
						+ ACTIVATION_STATE_ACTIVE_XML
						+ EXTERNAL_PROVISIONING_META_OPEN_XML
							+ PROPERTY_ENTRY_OPEN_XML
								+ KEY_SAS_XML
								+ VALUE_OTB_XML
							+ PROPERTY_ENTRY_CLOSE_XML
						+ EXTERNAL_PROVISIONING_META_CLOSE_XML
						+ OATH_TOKEN_CLOSE_XML;
				break;
			case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
				
				int pin = pinGenUtil.generatePIN();
				logger.info("[ID-Cloud Config] pin........1 : "+pin);
				
				userPIN = String.valueOf(pin);
				logger.info("[ID-Cloud Config] userPIN........2 : "+userPIN);
				
				
				XmlData = XML_VERSION
							+ OATH_TOKEN_XML
							+ USER_ID_OPEN_XML +userId+ USER_ID_CLOSE_XML
							+ OATH_ID_OPEN_XML
								+ MANUFACTURER_ID_GA_XML
								+ "<tokenType>OC</tokenType>"
							+ OATH_ID_CLOSE_XML
							+ PIN_OPEN_XML +userPIN+ PIN_CLOSE_XML   //=> provided by the method
							+"<domain>gtoeziodemo</domain>"
							+ ACTIVATION_DATE_2013_XML
							+ EXPIRATION_DATE_2024_XML
							+ ACTIVATION_STATE_ACTIVE_XML
							+ EXTERNAL_PROVISIONING_META_OPEN_XML
								+ PROPERTY_ENTRY_OPEN_XML
									+ KEY_SAS_XML
									+ VALUE_OTB_XML
								+ PROPERTY_ENTRY_CLOSE_XML
								+ PROPERTY_ENTRY_OPEN_XML
									+ "<key>sas.otb.policy</key>"
									+ "<value>OTB Policy - CR</value>"
								+ PROPERTY_ENTRY_CLOSE_XML
							+ EXTERNAL_PROVISIONING_META_CLOSE_XML
							+ OATH_TOKEN_CLOSE_XML;
					break;
			default:
				break;
			}

			epsURL = urlUtil.mobileEnrollmentEPSURL();
			logger.info("[Mobile EPS Enrollment] epsURL : "+epsURL);
			
			URL obj = new URL(epsURL);
			
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			switch (backendConfiguration) {
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
					con.setRequestMethod("POST");
					con.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
					con.setRequestProperty(ACCEPT_PROP, APPLICATION_XML_PROP_VALUE);
					con.setRequestProperty(AUTHORIZATION, BASIC_AUTH_EPS);
					break;
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
					con.setRequestMethod("POST");
					con.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
					con.setRequestProperty(ACCEPT_PROP, APPLICATION_XML_PROP_VALUE);
					con.setRequestProperty(AUTHORIZATION, BASIC_AUTH_EPS);
					break;
				default:
					break;
			}	
			// For POST only - START
			con.setDoInput(true);
			con.setDoOutput(true);
			PrintWriter pw = new PrintWriter(con.getOutputStream());
			pw.write(XmlData);
			pw.flush();
			pw.close();
			// For POST only - END

			int responseCode = con.getResponseCode();
			responseData = Integer.toString(responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				
				logger.info("[Mobile EPS Enrollment] Success!");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String inputLine;
				StringBuffer _response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					_response.append(inputLine);
				}
				in.close();
				responseData = _response.toString();
				logger.info("Response data : {}", responseData);
				
				//save regcode and pin in session
				Document doc2 = builder.parse(new ByteArrayInputStream(responseData.getBytes()));
				
				NodeList registrationCodeNode = doc2.getElementsByTagName(REGISTRATION_CODE_ELEMENT);
				NodeList pinNode = doc2.getElementsByTagName("pin");
				NodeList tokenIDNode = doc2.getElementsByTagName(TOKEN_ID_ELEMENT);
				
				userRegistrationCodeEPS = registrationCodeNode.item(0).getFirstChild().getNodeValue();
				userPIN = pinNode.item(0).getFirstChild().getNodeValue(); 
				String tokenId = "";
				tokenId = tokenIDNode.item(0).getFirstChild().getNodeValue();
				
				enrollmentData = new HashMap<>();
				
				enrollmentData.put(REGISTRATION_CODE_ELEMENT, userRegistrationCodeEPS);
				enrollmentData.put("pin", userPIN);
				enrollmentData.put(TOKEN_ID_ELEMENT, tokenId);

			} else {
				logger.info("[Mobile EPS Enrollment] Failed!");
				logger.info("[Mobile EPS Enrollment] responseCode : "+ responseCode);
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				responseData = _pushresponse.toString();
			}

		} catch (Exception e) {
			logger.error("[Mobile EPS Enrollment] Exception!", e);
			e.printStackTrace();
			responseData = e.toString();
		}
		return enrollmentData;
	}
	
	
	
	
	/**
	 * @param userId
	 * @param uPIN
	 * @return will be String with user RegistrationCode, PIN and TokenID for the particular User.
	 */
	@SuppressWarnings("finally")
	public String doMobileEnrollmentStepTwo(String userId, String uPIN){
		
		String userRegistrationCodeEPS = "";
		String userPIN = "";
		String tokenId = "";
		String responseData="";
		String responseDataToSendBack = "";
		String epsURL = "";
		String xmlData = "";
		
		try  
		  {  
				String backendConfiguration = urlUtil.getBackendConfiguration();
				logger.info("[doMobileEnrollmentStepTwo] backendConfiguration : "+backendConfiguration);
				
				switch (backendConfiguration) {
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
					xmlData = XML_VERSION
							+ OATH_TOKEN_XML
							+ USER_ID_OPEN_XML +userId+ USER_ID_CLOSE_XML
							+ OATH_ID_OPEN_XML
								+ "<manufacturerId>GA</manufacturerId>"
								+ "<tokenType>LO</tokenType>"
							+ OATH_ID_CLOSE_XML
							+ PIN_OPEN_XML + uPIN + PIN_CLOSE_XML
									+ "<domain>login</domain>"
									+ ACTIVATION_DATE_2013_XML
									+ EXPIRATION_DATE_2024_XML
									+ ACTIVATION_STATE_ACTIVE_XML
									+ EXTERNAL_PROVISIONING_META_OPEN_XML
										+ PROPERTY_ENTRY_OPEN_XML
											+ KEY_SAS_XML
											+ VALUE_OTB_XML
										+ PROPERTY_ENTRY_CLOSE_XML
									+ EXTERNAL_PROVISIONING_META_CLOSE_XML
							+ OATH_TOKEN_CLOSE_XML;
					break;
				case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:

					xmlData = XML_VERSION
							+ OATH_TOKEN_XML
							+ USER_ID_OPEN_XML +userId+ USER_ID_CLOSE_XML
							+ OATH_ID_OPEN_XML
								+ "<manufacturerId>GA</manufacturerId>"
								+ "<tokenType>LO</tokenType>"
							+ OATH_ID_CLOSE_XML
							+ PIN_OPEN_XML +uPIN+ PIN_CLOSE_XML
									+ "<domain>gtoeziodemo</domain>"
									+ ACTIVATION_DATE_2013_XML
									+ EXPIRATION_DATE_2024_XML
									+ ACTIVATION_STATE_ACTIVE_XML
									+ EXTERNAL_PROVISIONING_META_OPEN_XML
										+ PROPERTY_ENTRY_OPEN_XML
											+ KEY_SAS_XML
											+ VALUE_OTB_XML
										+ PROPERTY_ENTRY_CLOSE_XML
										+ PROPERTY_ENTRY_OPEN_XML
											+ "<key>sas.otb.policy</key>"
											+ "<value>OTB Policy - TB</value>"
										+ PROPERTY_ENTRY_CLOSE_XML
									+ EXTERNAL_PROVISIONING_META_CLOSE_XML
							+ OATH_TOKEN_CLOSE_XML;
					break;
		
				default:
					break;
				}
			
				epsURL = urlUtil.mobileEnrollmentEPSURL();
				logger.info("[Mobile EPS Enrollment - StepTwo] epsURL : "+epsURL);
				URL obj = new URL(epsURL);

				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				switch (backendConfiguration) {
					case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
						con.setRequestMethod("POST");
						con.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
						con.setRequestProperty(ACCEPT_PROP, APPLICATION_XML_PROP_VALUE);
						break;
					case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
						con.setRequestMethod("POST");
						con.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_XML_PROP_VALUE);
						con.setRequestProperty(ACCEPT_PROP, APPLICATION_XML_PROP_VALUE);
						con.setRequestProperty(AUTHORIZATION, BASIC_AUTH_EPS);
						break;
					default:
						break;
				}

				// For POST only - START
				con.setDoInput(true);
				con.setDoOutput(true);
				try (PrintWriter pw = new PrintWriter(con.getOutputStream())) {
				  pw.write(xmlData);
				  pw.flush();
				}

			    // For POST only - END
				int responseCode = con.getResponseCode();
				responseData = Integer.toString(responseCode);
				if (responseCode == HttpURLConnection.HTTP_OK) {//success

					logger.info("[doMobileEnrollmentStepTwo] Success!");

					StringBuffer _response;
					try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
						String inputLine;
						_response = new StringBuffer();
						while ((inputLine = in.readLine()) != null) {
							_response.append(inputLine);
						}
					}
					responseData = _response.toString();
					if(!responseData.equals(""))
					{
						//save regcode and pin in session
						Document doc2;
						try (ByteArrayInputStream bInput = new ByteArrayInputStream(responseData.getBytes())) {
							doc2 = builder.parse(bInput);
						}
						NodeList registrationCodeNode = doc2.getElementsByTagName(REGISTRATION_CODE_ELEMENT);
						NodeList pinNode = doc2.getElementsByTagName("pin");
						NodeList tokenIDNode = doc2.getElementsByTagName(TOKEN_ID_ELEMENT);
					
						userRegistrationCodeEPS = registrationCodeNode.item(0).getFirstChild().getNodeValue();
						userPIN = pinNode.item(0).getFirstChild().getNodeValue(); 
						tokenId = tokenIDNode.item(0).getFirstChild().getNodeValue();
						responseDataToSendBack+= DATA_REGCODE_LABEL + userRegistrationCodeEPS + PIN_LABEL + userPIN + TOKENID_LABEL + tokenId + END_LABEL;
						responseDataToSendBack+="}}";					
						logger.info("[doMobileEnrollmentStepTwo] response data : "+responseDataToSendBack);
					}
				} else {
					logger.info("[doMobileEnrollmentStepTwo] Failed!");
					logger.info("POST request not worked");
					responseDataToSendBack+= DATA_REGCODE_LABEL +userRegistrationCodeEPS+ PIN_LABEL +userPIN+ TOKENID_LABEL +tokenId+ END_LABEL;
					responseDataToSendBack+="}}";
				}
		  }  
		  catch (Exception e)  
		  {  
			responseDataToSendBack+= DATA_REGCODE_LABEL +userRegistrationCodeEPS+ PIN_LABEL +userPIN+ TOKENID_LABEL +tokenId+ END_LABEL;
			responseDataToSendBack+="}}";
			responseData = e.toString();
			logger.info("[doMobileEnrollmentStepTwo] Exception! Response data "+responseData);
		  }finally{
			  logger.info("[doMobileEnrollmentStepTwo] [Final block] response data : "+responseDataToSendBack);
			  //out.print(responseDataToSendBack);

		 }
		return responseDataToSendBack;
	}
}
