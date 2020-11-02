package com.gemalto.eziomobile.demo.webhelper.p2p;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

@Component
public class P2PWebHelper {
	
	
	private static final LoggerUtil logger = new LoggerUtil(P2PWebHelper.class.getClass());
	
	@Autowired
	private URLUtil urlUtil;
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;

	public P2PWebHelper() {
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Cannot parse XML {}.", e);
		}
	}

	
	
	/**
	 * This method will check whether beneficiary user has reset their account
	 * or not. By checking device registered.
	 *
	 * @param beneficiaryId
	 * @return will be true - if account is reset / false - if account is not
	 *         reset.
	 */
	public boolean isUserAccountReset(String beneficiaryId) {
		boolean isUserAccountReset = false;
		String pushresponseData = "";
		String _pushurl = "";
		String casURL = "";
		String headerJsession = "";
		
		logger.info("[isUserAccountReset] isUserDeviceRegisterOnCASServer checking.....");

		try {
			
			headerJsession = CASWebHelper.headerJsession;
			logger.info("[isUserAccountReset] headerJsession : "+headerJsession);
			
			casURL = urlUtil.findUserFromCASServerURL();
			
			_pushurl = casURL + beneficiaryId;
			logger.info("_pushURL : " + _pushurl);
			
			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

			pushcon.setRequestMethod("GET");
			pushcon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			pushcon.setRequestProperty("Cookie", headerJsession);

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[isUserAccountReset] Response Code : " + pushresponseData);
			pushresponseData = Integer.toString(pushresponseCode);

			logger.info("[isUserAccountReset] pushresponseData: "+ pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {

				logger.info("[isUserAccountReset] isUserDeviceRegisterOnCASServer - SUCCESS!");

				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()
				String pushinputLine = "";
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();

				pushresponseData = _pushresponse.toString();
				logger.info("[isUserAccountReset] isUserDeviceRegisterOnCASServer - pushresponseData : " + pushresponseData);


				InputSource is = new InputSource(new StringReader(pushresponseData));
				Document usersdoc = builder.parse(is);

				NodeList nList = usersdoc.getElementsByTagName("TokenName");
				NodeList nListType = usersdoc.getElementsByTagName("TokenType");
											
				for (int i = 0; i < nList.getLength(); i++) 
				{	
					//OTB device type is "3"
					if(nListType.item(i).getChildNodes().item(0).getNodeValue().equals("3") || nListType.item(i).getChildNodes().item(0).getNodeValue().equals("7")){
						isUserAccountReset = false;	
						logger.info("At least 1 token with type 3 or 7 found");
						break;
					}
				}				
			} else {

				logger.info("[isUserAccountReset] isUserDeviceRegisterOnCASServer - FAILED!");

				isUserAccountReset = false;

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
			logger.info("[isUserAccountReset] Exception!");
			e.printStackTrace();
		}
		return isUserAccountReset;
	}
	
}
