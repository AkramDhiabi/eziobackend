package com.gemalto.eziomobile.demo.webhelper.batchtransfer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

@Component
public class BatchTransferWebHelper {

	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private CommonWebHelper commonWebHelper;
	
	private static final LoggerUtil logger = new LoggerUtil(BatchTransferWebHelper.class.getClass());

	
	/**
	 * @param userId
	 */
	public Map<String, String> getListOfTokensByUserId(String userId) {
		
		String pushresponseData = "";
		String _pushurl = "";
		String getTokenListURL = "";
		String tokenName = "";
		NodeList nList = null;
		Map<String, String> mapData = null;
		
		String headerJsession = CASWebHelper.headerJsession;
		
		logger.info("\n Batch Transfer, Get list of tokens is executing......");

		try {
			
			getTokenListURL = urlUtil.getListOfTokensURL();
			
			//replaces <userID> with userId in URL
			_pushurl = getTokenListURL.replace("<userID>",userId);
			logger.info("Replaced URL : "+ _pushurl);
			
			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
			
			pushcon.setRequestMethod("GET");
			pushcon.setRequestProperty("Cookie", headerJsession);
			pushcon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			//pushcon.setRequestProperty("Authorization", "Basic ZGVmYXVsdDpkZWZhdWx0");
			
			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {
				
				logger.info("Batch Transfer, Get list of tokens, Success");
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("[Batch Transfer, Get list of tokens] pushResponseData : "+ pushresponseData);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);

				logger.info(
						"[Batch Transfer, Get list of tokens] XML response OOBS........ 1 : " + usersdoc.toString());
				
				usersdoc.getDocumentElement().normalize();
				logger.info("Root element :" + usersdoc.getDocumentElement().getNodeName());

				nList = usersdoc.getElementsByTagName("Token");
				logger.info("nList size : "+nList.getLength());
				logger.info("----------------------------\n");

				mapData = new HashMap<>();
				
				if(nList.getLength()>0){
					
					for (int temp = 0; temp < nList.getLength(); temp++) {
			        	
			        	String count = String.valueOf(temp);
			        	logger.info("count : "+count);
			        	
			            Node nNode = nList.item(temp);

			            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			                Element eElement = (Element) nNode;
			                String tokenType = "";
			                
			                tokenName = eElement.getElementsByTagName("TokenName").item(0).getTextContent();
			                logger.info("\nTokenName : " + tokenName);
			                
			                tokenType = eElement.getElementsByTagName("TokenType").item(0).getTextContent();
			                logger.info("TokenType : " + tokenType);
			               
			               if(tokenType.equalsIgnoreCase("1")){
			            	   mapData.put(CommonOperationsConstants.TOKEN_NAME_ATTR, tokenName);
			            	   break;
			               }
			               else
			            	   mapData.put(CommonOperationsConstants.TOKEN_NAME_ATTR, EzioMobileDemoConstant.NO_TOKEN_FOUND);
			            }
			        }
				}else{
					mapData.put(CommonOperationsConstants.TOKEN_NAME_ATTR, EzioMobileDemoConstant.NO_TOKEN_FOUND);
				}
				logger.info("\n----------------------------");
				logger.info("MapData : "+mapData.toString());
				logger.info("----------------------------\n");
			} 
			else {
				logger.info("\n [Batch Transfer, Get list of tokens] Failure");

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();
				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
				logger.info("\n [Batch Transfer, Get list of tokens] pushresponseData on fail 2: " + pushresponseData);
			}

		} catch (Exception e) {
			logger.error("Error : [Batch-Transfer] Unable to find tokens for UserId : "+userId);
			e.printStackTrace();
		}
		return mapData;
	}
	
	
	/**
	 * @param otp
	 * @param userId
	 * @param dataHex
	 * @param tokenName
	 * @return
	 */
	public boolean validateOTPForBatchTransfer(String otp, String userId, String dataHex, String tokenName) {
		
		String responseData="";
		boolean isValidate = false;
		
			try {
				logger.info("Start : Batch transfer OTP validation..");
				responseData = commonWebHelper.getSHA1("E30753575953205631"+dataHex);
				logger.info("[Batch transfer] DATA OUT: " + responseData);

					String XmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<AuthenticationRequest>"
								+ "<UserID>"+userId+"</UserID>"
								+ "<OTP>"+otp+"</OTP>"
								+ "<OcraData>"
									+ "<ServerChallenge>"
									+ "<Value>"+responseData+"</Value>"
									+ "</ServerChallenge>"
								+ "</OcraData>"
							+ "</AuthenticationRequest>";
					
					logger.info("DATA OUT: " + XmlData+"\n");
					 
					String url = urlUtil.validateOTPURL();
					logger.info("[Batch Transfer] Validate OTP URL: "+url);
						
					URL obj = new URL(url);  
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-type", "text/xml");    
					// For POST only - START
					con.setDoInput(true);  
					con.setDoOutput(true);
					PrintWriter pw = new PrintWriter(con.getOutputStream());
					pw.write(XmlData);
					pw.flush();
					pw.close();
					
					// For POST only - END
					int responseCode = con.getResponseCode();
					logger.info("[Validate OTP] responseCode : "+responseCode);
					
					responseData = Integer.toString(responseCode);
					if (responseCode == HttpURLConnection.HTTP_OK) { //success
						isValidate = true;
					}else {
						isValidate = false;
					}
					logger.info("End : Batch transfer OTP validation..");
				}  
				catch(Exception e)
				{
					//out.print("ERROR_AUTHENT");
					isValidate = false;
					logger.info("ERROR");
					e.printStackTrace();
				}
			return isValidate; 
		}
	
	
}
