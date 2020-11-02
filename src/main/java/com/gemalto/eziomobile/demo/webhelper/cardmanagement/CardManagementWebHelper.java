package com.gemalto.eziomobile.demo.webhelper.cardmanagement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.dto.DCVTokenDTO;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.PanDCVListInfo;
import com.gemalto.eziomobile.demo.service.cardmanagement.DCVActivationService;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;


@Component
public class CardManagementWebHelper {

	private static String headerJsession = "";
	private DocumentBuilder builder;
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private static final LoggerUtil logger = new LoggerUtil(CardManagementWebHelper.class.getClass());
	
	@Autowired
	private URLUtil urlUtil;

	@Autowired
	private DCVActivationService dCVActivationService;

	public CardManagementWebHelper() {

		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Cannot create new factory {}", e);
		}

	}

	/**
	 * @param userID
	 * @return
	 */
	public List<DCVTokenDTO> getPANNumber(String userID){
		
		List<String> lUserDevice = getDCVdevice(userID);
		logger.info("[CardManagementWebHelper] - lUserDevice size : "+lUserDevice.size());
		logger.info("[CardManagementWebHelper] - lUserDevice data : "+lUserDevice.toString());
		
		List<DCVTokenDTO> dcvPanList = new ArrayList<>();
		String dcvPAN = "";
		
		if(lUserDevice != null){
			for (String dcvToken : lUserDevice) {
				
				logger.info("getPANfromDCVdevice.............] "+ dcvPAN.toString());
				DCVTokenDTO dcvTokenDTO = new DCVTokenDTO();
				
				dcvPAN = getPANfromDCVdevice(dcvToken);
				if(dcvPAN != null && !dcvPAN.isEmpty()){
					
					dcvTokenDTO.setDcvCardNo(dcvPAN);
					dcvTokenDTO.setDcvTokenId(dcvToken);
					
					logger.info("[CardManagementWebHelper] Pan : "+dcvPAN+"\n");
					dcvPanList.add(dcvTokenDTO);
				}
			}
		}
		return dcvPanList;
	}
	
	
	/**
	 * @param userID
	 * @return
	 */
	public List<String> getDCVdevice(String userID){
	
		String url = "";
		List<String> lUserDevice = new ArrayList<>();

		synchronized (headerJsession) {
			headerJsession = CASWebHelper.headerJsession;
		}
		if(headerJsession.isEmpty() && headerJsession == null)
			return null;
		
		String pushresponseData = "";
	
		try {
			
			url = urlUtil.findUserFromCASServerURL();
			String _pushurl = url+userID+"?tokenListFormat=tokens";
			
			logger.info("[CardManagementWebHelper] getDCVdevice : "+_pushurl);
			
		  	URL pushobj = new URL(_pushurl);  
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("GET");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, headerJsession);

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {
			
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();

				DocumentBuilder builder = factory.newDocumentBuilder();

				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);
					
				logger.info(XML_RESPONSE_ACTIVATE_DEVICES_MESSAGE + usersdoc.toString());
				
				NodeList nList = usersdoc.getElementsByTagName("TokenName");
				NodeList nListType = usersdoc.getElementsByTagName("TokenType");
								
				for (int i = 0; i < nList.getLength(); i++) 
				{	
					if(nListType.item(i).getChildNodes().item(0).getNodeValue().equals("7") 
							&& nList.item(i).getChildNodes().item(0).getNodeValue().substring(0, 4).equals(CommonOperationsConstants.PREFIX_DCV_PHYSIQUE)){
						lUserDevice.add(nList.item(i).getChildNodes().item(0).getNodeValue());
					}
				}

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			pushresponseData = e.toString();
			return null;
		}
		return lUserDevice;
	}
	
	
	
	/**
	 * @param deviceID
	 * @return
	 */
	public String getPANfromDCVdevice(String deviceID){

		synchronized (headerJsession) {
			headerJsession = CASWebHelper.headerJsession;
		}
		if(headerJsession.isEmpty() && headerJsession != null)
			return null;
		
		String sPan = "";
		String url = "";
		String pushresponseData = "";
		
		try {
			//String url = getDeviceByDeviceIdURL(EzioMobileDemoConstant.EZIO_PROPERTIES_PATH, context);
			//String _pushurl = "http://10.10.84.139:80/saserver/master/api/devices/" + deviceID + "?TokenType=7";
			
			url = urlUtil.getDeviceByDeviceIdURL();
			
			String _pushurl = url+deviceID+"?TokenType=7";
			logger.info("[getPANfromDCVdevice: authenticate.............] " + _pushurl);
			
		  	URL pushobj = new URL(_pushurl);  
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("GET");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP, headerJsession);

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {
			
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
					
				logger.info(XML_RESPONSE_ACTIVATE_DEVICES_MESSAGE + usersdoc.toString());
				
				NodeList nList = usersdoc.getElementsByTagName("EmvSeed");
				Node nNode = nList.item(0);
				Element eElement = (Element) nNode;
				sPan = eElement.getElementsByTagName("FilteredPan").item(0).getTextContent(); 
			
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			pushresponseData = e.toString();
			return null;
		}
		return sPan;
	}
	
	
	/**
	 * @param PAN
	 * @param PSN
	 * @param sharedSecret
	 * @param cardHolderName
	 * @param TokenName
	 * @param SeedName
	 * @param ExpDate
	 * @param context
	 * @return
	 */
	public int createDCVdevice(String PAN, String PSN, String sharedSecret, String cardHolderName, String TokenName, String SeedName, String ExpDate){
		int iStatusPanNumber = CommonOperationsConstants.GENERAL_ERROR;
		logger.info("createDCVdevice() in CardManagementWebHelper");
		try{
			// check PAN first
			iStatusPanNumber = checkPANNumber(PAN);
			logger.info("createDCVdevice() -- iStatusPanNumber :"+ iStatusPanNumber);
		}catch(Exception e){
			return iStatusPanNumber;
		}
		
		if(iStatusPanNumber != CommonOperationsConstants.ALL_OK){
			return iStatusPanNumber;
		}
		if(PSN==null || sharedSecret==null || cardHolderName==null || TokenName==null || SeedName==null || ExpDate==null 
			|| PSN.isEmpty() || sharedSecret.isEmpty() || cardHolderName.isEmpty() || TokenName.isEmpty() || SeedName.isEmpty() || ExpDate.isEmpty() 
			|| PSN.length() != CommonOperationsConstants.CARD_PSN_LEN || TokenName.length() != CommonOperationsConstants.TOKEN_LEN){
			return CommonOperationsConstants.BAD_LENGTH;
		}
		
		int iFinalStatus = CommonOperationsConstants.GENERAL_ERROR;
		

		String pushXmlData = "";
		String _pushurl = "";
		String pushresponseData = "";

		synchronized (headerJsession) {
			headerJsession = CASWebHelper.headerJsession;
		}
		if(headerJsession.isEmpty() && headerJsession != null)
			return CommonOperationsConstants.GENERAL_ERROR;
		logger.info("[Create DCV cards] headerJsession : "+headerJsession);
		
		logger.info("CardManagementWebHelper - DCV Card creation");
		

		
		 pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Token><TokenName>" + TokenName + "</TokenName><TokenType>7</TokenType><ExpirationDate>" 
							 + ExpDate + "</ExpirationDate><SeedList><OtbSeed><SeedName>" + SeedName + "</SeedName><SharedSecret>" + sharedSecret 
							 + "</SharedSecret><Key>OATH Sample Master Key</Key><OtbPolicy>OTB Policy-DCVV - PC</OtbPolicy></OtbSeed><EmvSeed><PAN>" + PAN + "</PAN><PSN>" + PSN + "</PSN>" 
							 + "<CardHolderName>" + cardHolderName + "</CardHolderName><Counter>0</Counter><Key>CTVS test mchip2-</Key><EmvPolicy>Test MasterCard CAP Policy</EmvPolicy>"
							 + "</EmvSeed></SeedList></Token>";
							 
		logger.info("createDCVdevice pushXmlData : " + pushXmlData);
		// Now create!!!!
		try {
			 _pushurl = urlUtil.getDeviceByDeviceIdURL();
			 
			 logger.info("[CommonWebHelper - createDCVdevice] pushurl: "+_pushurl);
			
		  	URL pushobj = new URL(_pushurl);  
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty(CONTENT_TYPE_PROP, TEXT_XML_PROP_VALUE);
			pushcon.setRequestProperty(COOKIE_PROP,headerJsession);

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
			// Expected result should be 201
			if (pushresponseCode == (int) 201 || pushresponseCode == HttpURLConnection.HTTP_OK) {
			
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				
				iFinalStatus = CommonOperationsConstants.ALL_OK;
			
			} else {
		
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				
				iFinalStatus = CommonOperationsConstants.GENERAL_ERROR;
			}

		} catch (Exception e) {
			e.printStackTrace();
			pushresponseData = e.toString();
			iFinalStatus = CommonOperationsConstants.GENERAL_ERROR;
		}
		return iFinalStatus;
	}
	

	/*
	// DCVV
	// FOR PHYSICAL CARDS
	*/
	
	@SuppressWarnings("finally")
	public int checkPANNumber(String PAN) throws Exception{
		
		logger.info("checkPANNumber() in CardManangementwebHelper");
		if(PAN==null || PAN.isEmpty() || PAN.length() != CommonOperationsConstants.CARD_PAN_LEN){
			return CommonOperationsConstants.GENERAL_ERROR;
		}
		

		int iStatus = CommonOperationsConstants.INVALID_PAN_NUMBER;
	
		//String _pushurl = "http://10.10.84.139:80/saserver/master/api/devices/";
		try{

			String backendConfiguration = urlUtil.getBackendConfiguration();
			logger.info("[MobileEnrollmentStepOne] backendConfiguration : "+backendConfiguration);
			
			String _pushurl = urlUtil.getDeviceByDeviceIdURL();
			logger.info("checkPANNumber() - _pushurl "+ _pushurl);
			
			String pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<TokenSearchCriteria><PAN>" + PAN + "</PAN></TokenSearchCriteria>";
			
	
			
			PanDCVListInfo panDCVListInfo = dCVActivationService.findPanDCVListInfoByPanNo(PAN);
			logger.info("checkPANNumber() - panDCVListInfo "+ panDCVListInfo.toString());
			String pushresponseData = "";
			if(panDCVListInfo!= null) {
				logger.info("checkPANNumber() - panDCVListInfo not null");
				
				//Authenticate to CAS first!
				synchronized (headerJsession) {
					headerJsession = CASWebHelper.headerJsession;
				}
				if(headerJsession.isEmpty() && headerJsession != null) {
					return CommonOperationsConstants.GENERAL_ERROR;
					
				}else{
					// Now we are going to see if this PAN is already created in CAS
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

					if (pushresponseCode == HttpURLConnection.HTTP_OK){
						
						logger.info("checkPANNumber : HTTP_OK");
					
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
					
						logger.info(XML_RESPONSE_ACTIVATE_DEVICES_MESSAGE + usersdoc.toString());
						
						// Just parse the response
						
						NodeList nList = usersdoc.getElementsByTagName("TokenList");
						Node nNode = nList.item(0);
						Element eElement = (Element) nNode;
						String sTotalAvailable = eElement.getAttribute("TotalAvailable");
						logger.info("TotalAvailable : " + sTotalAvailable);
						
						// if 0 it means OK of course
						

						if(sTotalAvailable == null || sTotalAvailable.isEmpty()){
							if(backendConfiguration.equals(EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD)) {
								iStatus = CommonOperationsConstants.ALL_OK;
							}else {
								iStatus = CommonOperationsConstants.GENERAL_ERROR;
							}
						}else if(Integer.parseInt(sTotalAvailable) == 0){
							iStatus = CommonOperationsConstants.ALL_OK;
						}else if(Integer.parseInt(sTotalAvailable) > 0){
							// It can be also an EMV CAP device (token type =1) and in this case it would be OK as well
							NodeList _nList = usersdoc.getElementsByTagName("Token");
							// We parse all the token list from the result
							for (int temp = 0; temp < _nList.getLength(); temp++) {
								nNode = _nList.item(temp);
								logger.info("\nCurrent Element :" + nNode.getNodeName());

								if (nNode.getNodeType() == Node.ELEMENT_NODE) {
									eElement = (Element) nNode;
									String sTokenNamehere = "";
									sTokenNamehere = eElement.getElementsByTagName("TokenName").item(0).getTextContent(); 
									logger.info("sTokenNamehere : " + sTokenNamehere);
									if(eElement.getElementsByTagName("TokenType").item(0).getTextContent().equals("7") && sTokenNamehere.substring (0,4).equals(CommonOperationsConstants.PREFIX_DCV_PHYSIQUE)){
										// if device with type 7 is found then the card is already associated (physical card)
										iStatus = CommonOperationsConstants.CARD_ALREADY_ASSOCIATED;
										break;
									}else{
										// else it is ok!
										iStatus = CommonOperationsConstants.ALL_OK;
									}
								}
							}
					} else {
						// else timeout or something else
						iStatus = CommonOperationsConstants.GENERAL_ERROR;
					}
				}
				}
			}else{
				// PAN is not in the shared list so INVALID!
				iStatus = CommonOperationsConstants.INVALID_PAN_NUMBER;
			}
		}catch(Exception e){
			logger.error("ERROR");
			logger.error(" <------- " + "checkPANNumber" + " ----------------->");
			iStatus = CommonOperationsConstants.GENERAL_ERROR;
		}
		return iStatus;
	}
	
		
	/**
	 * @param serialNO
	 * @return
	 */
	public String generatePhysicalDCVDeviceName(String serialNO){
		// maybe not unique but... 
		return "GAPC" + serialNO;
	}
	
	
	/**
	 * @param serialNO
	 * @return
	 */
	public String generatePhysicalDCVSeedName(String serialNO){
		// maybe not unique but... 
		return "Seed" + serialNO;
	}
	
	/**
	 * @return
	 */
	public String getSharedSecret(){
		// key in clear
		// 000102030405060708090A0B0C0D0E0F000102030405060708090A0B0C0D0E0F
		return "f1d75e4f0d37c22ce1f68fb810397a2fb449826f603680bcac88b17266c1608d1eede3e92446de8c";
	}
	

}
