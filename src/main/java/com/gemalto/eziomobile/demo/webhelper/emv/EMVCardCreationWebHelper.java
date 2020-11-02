package com.gemalto.eziomobile.demo.webhelper.emv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class EMVCardCreationWebHelper {


	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private CASWebHelper casWebHelper;

	private static final LoggerUtil logger = new LoggerUtil(EMVCardCreationWebHelper.class.getClass());

	
	/**
	 * @param panNumber
	 * @param userId
	 */
	public boolean createEMVCards(String panNumber, String userId) {
		
		logger.info("[EMV Card Creation] PanNumber : "+panNumber);
		logger.info("[EMV Card Creation] userId : "+userId);
		
		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";
		String HeaderJsession="";
		boolean isEMVCardCreated = false;
		
		HeaderJsession = CASWebHelper.headerJsession;
		logger.info("[Create EMV cards] headerJsession : "+HeaderJsession);
		
		logger.info("EMVCardCreationWebHelper - EMV Card creation");
		pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><EmvToken>"
				+ "<PAN>" + panNumber+ "</PAN>"
				+ "<PSN>00</PSN>"
				+ "<CardHolderName>" + userId+ "</CardHolderName>"
				+ "<Counter>0</Counter>"
				+ "<ActivationDate>2013-01-02T18:04:05+08:00</ActivationDate>"
				+ "<ExpirationDate>2030-01-02T18:04:05+08:00</ExpirationDate>"
				+ "<Key>CTVS test mchip2-</Key>"
				+ "<EmvPolicy>Test MasterCard CAP Policy</EmvPolicy>"
				+ "</EmvToken>";
		
		try {
			//_pushurl = "http://10.10.84.139:80/saserver/master/api/devices/emv/";
			
			_pushurl = urlUtil.emvCardCreatationURL();
			logger.info("[EMV card Create URL] : "+_pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML);
			pushcon.setRequestProperty(COOKIE,HeaderJsession);

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("[EMV Card Creation] pushresponseCode : "+pushresponseCode);
			
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("[EMV Card Creation] pushresponseData : "+pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_CREATED) { // success
				logger.info("EMC Card creation SUCCESS!");
				
				isEMVCardCreated = true;
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			} else {
				
				isEMVCardCreated = false;
				logger.info("EMC Card creation FAILED!");
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
			isEMVCardCreated = false;
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		logger.info("[api call finish] - Card Creation finished");
		return isEMVCardCreated;
			
	}

	
	
	/**
	 * @param userId
	 * @param panNumber
	 */
	public boolean linkEMVCards(String userId, String panNumber) {

		String _pushurl = "";
		String pushresponseData = "";
		boolean isEMVCardLinked = false;
		String HeaderJsession="";
		HeaderJsession = CASWebHelper.headerJsession;
		logger.info("[Link EMV cards ] headerJsession : "+HeaderJsession);
		
		logger.info("[api call Started] - Link Card to user");
		
		try {
			//_pushurl = "http://10.10.84.139:80/saserver/master/api/users/" + userId;
			_pushurl = urlUtil.emvCardLinkURL();
			_pushurl = _pushurl+userId;
			logger.info("[EMV card Link URL] URL : "+_pushurl);
			
			// logger.info(_pushurl);
			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_X_WWW_PROP_VALUE);
			pushcon.setRequestProperty(CONTENT_LENGTH_PROP, XXX_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP,HeaderJsession);

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			String _writedata = "_method=link&pan=" + panNumber + "&psn=00";
			pushpw.write(_writedata);
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("[EMV Card Link] pushresponseCode : "+pushresponseCode);
			
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("[EMV Card Link] pushresponseData : "+pushresponseData);
			
			if (pushresponseCode == HttpURLConnection.HTTP_OK) { // success
				
				logger.info("EMV Card Link SUCCESS!");
				
				isEMVCardLinked = true;
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			} else {
				
				logger.info("EMV Card Link FAILED!");
				
				isEMVCardLinked = false;
				
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
			isEMVCardLinked = false;
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		logger.info("[api call finish] Link Card finished");
		return isEMVCardLinked;
	}

	
	
	/**
	 * @param panNumber
	 */
	public boolean activeEMVCards(String panNumber) {
		String _pushurl = "";
		String pushresponseData = "";
		boolean isEMVCardActivated = false;
		String HeaderJsession = "";
		HeaderJsession = CASWebHelper.headerJsession;
		logger.info("[Active EMV cards] headerJsession : "+HeaderJsession);

		logger.info("[api call Started] - Active the Card started");
		
		try {
			//_pushurl = "http://10.10.84.139/saserver/master/api/devices/emv/" + panNumber + ":00";
			_pushurl = urlUtil.emvCardActivationURL();
			logger.info("[EMV card activation] _pushurl : "+_pushurl);
			
			_pushurl = _pushurl.replace(PAN_NUMBER_DIAMAND, panNumber);
			logger.info("[EMV card activation] updated _pushurl : "+_pushurl);
			
			// logger.info(_pushurl);
			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_X_WWW_PROP_VALUE);
			pushcon.setRequestProperty(CONTENT_LENGTH_PROP, XXX_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP,HeaderJsession);

			// For POST only - START
			pushcon.setDoInput(true);
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write("_method=activate");
			// logger.info("_method=activate");
			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			logger.info("[EMV Card activation] pushresponseCode : "+pushresponseCode);
			
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("[EMV Card activation] pushresponseData : "+pushresponseData);
			
			if (pushresponseCode == HttpURLConnection.HTTP_OK) { // success
				
				logger.info("EMV Card Activation SUCCESS!");
				
				isEMVCardActivated = true;
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			} else {
				
				logger.info("EMV Card Activation FAILED!");
				
				isEMVCardActivated = false;
				
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
			isEMVCardActivated = false;
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		logger.info("[api call finish] - Active the Card done");
		return isEMVCardActivated;
	}

	/**Revoke EMV cards from CAS for given PanNo
	 * @param panNo
	 * @param headerJsession
	 * @return Success : true / Failed : false
	 */
	public boolean revokeEMVCapDevice(String panNo, String headerJsession){
		
		String pushresponseData = "";
		String _pushurl = "";
		boolean isRevoked = false;
		try  
		{
			logger.info("Revoke EMV card - [deleting] card "+panNo+" started....");
	
			_pushurl = urlUtil.emvCardActivationURL();
			logger.info("[revokeEMVCapDevice] _pushurl : "+_pushurl);
			
			_pushurl = _pushurl.replace(PAN_NUMBER_DIAMAND, panNo);
			logger.info("UPDATED URL : [revokeEMVCapDevice] _pushurl : "+_pushurl);
			
			URL pushobj = new URL(_pushurl);  
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_X_WWW_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, headerJsession);
			pushcon.setDoInput(true);  
			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write("_method=revoke");
			pushpw.flush();
			pushpw.close();
			
			// For POST only - END
			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("[Revoke EMV Card CAP Devices] pushresponseData : "+pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) 
			{ 
				logger.info("[REVOKE EMV CARD] : SUCCESS!");
				isRevoked = true;
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getInputStream())); //getErrorStream()
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			} 
			else 
			{
				logger.info("[REVOKE EMV CARD] : FAILED!");
				isRevoked = false;
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getErrorStream())); 
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("Delete EMV card - Revoke Error:"+pushresponseData);
			}
			logger.info("Revoke EMV card - [deleting] card "+panNo+" finished!");
		}
		catch (Exception e)  
		{  
			e.printStackTrace();  
			pushresponseData = e.toString();
			logger.info("Revoke EMV card error:"+pushresponseData);
		}
		return isRevoked;
	}
	
	
	/**Remove EMV cards from CAS for given PanNo and userId
	 * @param panNo
	 * @param userId
	 * @param headerJsession
	 * @return Success : true / Failed : false
	 */
	public boolean removeEMVCapDevice(String panNo, String userId, String headerJsession){
		
		String pushresponseData = "";
		String _pushurl = "";
		boolean isRemoved = false;
		try  
		{
			logger.info("Remove EMV card - [deleting] card "+panNo+" started....");
	
			_pushurl = urlUtil.emvCardActivationURL();
			logger.info("[removeEMVCapDevice] _pushurl : "+_pushurl);
			
			_pushurl = _pushurl.replace(PAN_NUMBER_DIAMAND, panNo);
			logger.info("UPDATED URL : [removeEMVCapDevice] _pushurl : "+_pushurl);
			//_pushurl = "http://10.10.84.139:80/saserver/master/api/devices/emv/"+panNo+":00";
			
			URL pushobj = new URL(_pushurl);  
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, APPLICATION_X_WWW_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, headerJsession);
			pushcon.setDoInput(true);  
			pushcon.setDoOutput(true);
			
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write("_method=remove&userId="+userId);
			pushpw.flush();
			pushpw.close();
			
			// For POST only - END
			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);
			logger.info("[Remove EMV Card CAP Devices] pushresponseData : "+pushresponseData);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) 
			{ 
				logger.info("[REMOVE EMV CARD] : SUCCESS!");
				isRemoved = true;
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getInputStream())); //getErrorStream()
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			} 
			else 
			{
				logger.info("[REMOVE EMV CARD] : FAILED!");
				isRemoved = false;
				
				BufferedReader pushin = new BufferedReader(new InputStreamReader(
						pushcon.getErrorStream())); 
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("Delete EMV card - Revoke Error:"+pushresponseData);
			}
			logger.info("Remove EMV card - [deleting] card "+panNo+" finished!");
		}
		catch (Exception e)  
		{  
			e.printStackTrace();  
			pushresponseData = e.toString();
			logger.info("Remove EMV card error:"+pushresponseData);
		}
		return isRemoved;
	}
	
	/**Call revoke and remove EMV cards methods in sequence to 
	 * perform operations
	 * @param userId
	 * @param panNo
	 * @return true/false
	 */
	public boolean deleteEMVCards(String userId, String panNo){
		
		casWebHelper.authenticateCASever();
		
		String headerJsession = CASWebHelper.headerJsession;
		boolean isEMVCardsDeleted = false;
		
		boolean isRevoked = false;
		boolean isRemoved = false;
		//boolean isDeleted = false;
		
		if(headerJsession != null && !headerJsession.isEmpty()){
			isRevoked = revokeEMVCapDevice(panNo, headerJsession);
			isRemoved = removeEMVCapDevice(panNo, userId, headerJsession);
			//isDeleted = deleteEMVCapDevice(panNo, headerJsession);
			
			logger.info("[deleteEMVCards] isRevoked : "+isRevoked);
			logger.info("[deleteEMVCards] isRemoved : "+isRemoved);
			//logger.info("[deleteEMVCards] isDeleted : "+isDeleted);
			
			if(isRemoved && isRevoked)
				isEMVCardsDeleted = true;
		}else{
			isEMVCardsDeleted = false;
		}
		return isEMVCardsDeleted;
	}
	
	
	
	/** Replacement of dcv.jsp - Operation 01 
	 * enrollEMVCard method will enroll new EMV cards with EPS,
	 * Generate RegCode, PIN and tokenID that will be sent back to mobile
	 * @param userId
	 * @param devicePIN
	 * @return String JSON data
	 */
	public String enrollEMVCard(String userId, String devicePIN){

		logger.info("\n [DCV] Operation 01");
		
			//Code added for DCV migration
			String UserRegistrationCodeEPS = "";
			String UserPIN = "";
			
			String responseDataToSendBack="";
			String responseData="";
	
			String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<oathToken xmlns=\"http://gemalto.com/ezio/mobile/eps/api\">"
						+ "<userId>"+userId+"</userId>"
						+ "<oathId><manufacturerId>GA</manufacturerId>"
							+ "<tokenType>DV</tokenType>"
						+ "</oathId>"
						+ "<pin>"+devicePIN+"</pin>"
								+ "<domain>dcv</domain>"
								+ "<activationDate>2013-01-01T07:59:59+08:00</activationDate>"
								+ "<expirationDate>2030-01-01T07:59:59+08:00</expirationDate>"
								+ "<activationState>ACTIVE</activationState>"
								+ "<externalProvisioningMeta>"
								+ "     <propertyEntry><key>sas.oath.policy</key>"
								+ "          <value>OTB Policy-DCVV - 3R</value>"
								+ "     </propertyEntry>"
								+ "     <propertyEntry><key>sas.device.type</key>"
								+ "          <value>7</value>"
								+ "     </propertyEntry>"
								+ "     <propertyEntry><key>sas.oathDeviceType</key>"
								+ "          <value>OTB</value>"
								+ "     </propertyEntry>"
								+ "</externalProvisioningMeta>"
								+ "</oathToken>";
			
			logger.info("XmlData : "+xmlData);
			try  
			  {  
					String enrollURL = urlUtil.mobileEnrollmentEPSURL();
					logger.info("enrollURL : "+enrollURL);
					
					//URL obj = new URL("http://10.10.84.139:8081/enroller/api/enrollment/oath/enroll");
					URL obj = new URL(enrollURL);
					
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty(CONTENT_TYPE_PROP, "application/xml");
				  	con.setRequestProperty("Accept", "application/xml");

				  // For POST only - START
					con.setDoInput(true);  
					con.setDoOutput(true);
					PrintWriter pw = new PrintWriter(con.getOutputStream());
					pw.write(xmlData);
					pw.flush();
					pw.close();
					// For POST only - END
					
					int responseCode = con.getResponseCode();
					responseData = Integer.toString(responseCode);
					
					if (responseCode == HttpURLConnection.HTTP_OK) { //success
						BufferedReader in = new BufferedReader(new InputStreamReader(
								con.getInputStream()));
						
						String inputLine;
						StringBuffer _response = new StringBuffer();
			
						while ((inputLine = in.readLine()) != null) {
							_response.append(inputLine);
						}
						in.close();
						responseData = _response.toString();
						
						if(!responseData.equals(""))
						{
							DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
							factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

							//save regcode and pin in session
							DocumentBuilder newDocumentBuilder2 = factory.newDocumentBuilder();
							Document doc2 = newDocumentBuilder2.parse(new ByteArrayInputStream(responseData.getBytes()));
							
							NodeList registrationCodeNode = doc2.getElementsByTagName("registrationCode");
							NodeList pinNode = doc2.getElementsByTagName("pin");
							NodeList tokenIDNode = doc2.getElementsByTagName("tokenId");
							
							UserRegistrationCodeEPS = registrationCodeNode.item(0).getFirstChild().getNodeValue();
							UserPIN = pinNode.item(0).getFirstChild().getNodeValue(); 
							String tokenId = tokenIDNode.item(0).getFirstChild().getNodeValue();
							
							responseDataToSendBack+="{\"data\":{\"regcode\":[\""+UserRegistrationCodeEPS+"\"],\"pin\":[\""+UserPIN+"\"],\"tokenid\":[\""+tokenId+"\"]";
							responseDataToSendBack+="}}";					
						}
					} 
					else {
						logger.info("POST request not worked");
					}
			  }  
			  catch (Exception e)  
			  {  
				responseData = e.toString();
				logger.info("dcv error: "+responseData);
			  } 	
			return responseDataToSendBack;
	}
	
	
	
	/**Replacement of dcv.jsp - Operation - 02
	 * @param userId
	 * @param panNumber
	 * @param tokenId
	 * @return
	 */
	public boolean linkCardToUser(String userId, String panNumber, String tokenId) {
		
		String pushresponseData="";
		String pushXmlData = "";
		String _pushurl ="";
		String headerJsession = "";
		
		boolean isCardLinked = false;
		
		  // Linking the card with OTB device (LINK with OTB device)
		  logger.info("DCV [api call 2 start] - LINK with OTB device started");
	      pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
	      		+ "<EmvToken>"
	      			+ "<PAN>"+panNumber+"</PAN>"
	      			+ "<PSN>00</PSN>"
	      			+ "<CardHolderName>"+userId+"</CardHolderName>"
	      			+ "<Counter>0</Counter>"
	      			+ "<ActivationDate>2013-01-02T18:04:05+08:00</ActivationDate>"
	      			+ "<ExpirationDate>2030-01-02T18:04:05+08:00</ExpirationDate>"
	      			+ "<Key>CTVS test mchip2-</Key>"
	      			+ "<EmvPolicy>Test MasterCard CAP Policy</EmvPolicy>"
	      			+ "<OtbToken>"
	      				+ "<PrintedTag>"+tokenId+"</PrintedTag>"
	      			+ "</OtbToken>"
	      		+ "</EmvToken>";
	      logger.info(pushXmlData);
	      
		  try  
		  {  
			  headerJsession = CASWebHelper.headerJsession;
			  
				//for this operation
			  	//_pushurl = "http://10.10.84.139:80/saserver/master/api/devices/emv/"+panNo+":00";
				
			  	// for activeEMVCards method - emvCardActivationURL()
				//_pushurl = "http://10.10.84.139/saserver/master/api/devices/emv/" + panNumber + ":00";
				
			  	_pushurl = urlUtil.emvCardActivationURL();
				logger.info("[EMV card link] _pushurl : "+_pushurl);
				
				_pushurl = _pushurl.replace(PAN_NUMBER_DIAMAND, panNumber);
				logger.info("[EMV card link] updated _pushurl : "+_pushurl);
				
				URL pushobj = new URL(_pushurl);  
				HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
				pushcon.setRequestMethod("POST");
				pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
				pushcon.setRequestProperty(COOKIE_PROP, headerJsession);
				
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

				if (pushresponseCode == HttpURLConnection.HTTP_OK) 
				{ //success
					
					isCardLinked = true;
					BufferedReader pushin = new BufferedReader(new InputStreamReader(
							pushcon.getInputStream()));
					
					String pushinputLine;
					StringBuffer _pushresponse = new StringBuffer();
		
					while ((pushinputLine = pushin.readLine()) != null) {
						_pushresponse.append(pushinputLine);
					}
					pushin.close();
					//logger.info(response.toString());
					pushresponseData = _pushresponse.toString();
					
					//update status as active in database 
					//dcv_active='1' based on PanNO and userID
					// status update done
				} 
				else 
				{
					BufferedReader pushin = new BufferedReader(new InputStreamReader(
							pushcon.getErrorStream())); 
					String pushinputLine;
					StringBuffer _pushresponse = new StringBuffer();
					while ((pushinputLine = pushin.readLine()) != null) {
						_pushresponse.append(pushinputLine);
					}
					pushin.close();
					pushresponseData = _pushresponse.toString();
					//logger.info("POST request not worked");
				}
		  }  
		  catch (Exception e)  
		  {  
			e.printStackTrace();  
			pushresponseData = e.toString();
		  }
		  
		  logger.info("DCV [api call 2 finished] - LINK with OTB device done");
		return isCardLinked;
		
	}
	
}
