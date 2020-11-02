package com.gemalto.eziomobile.demo.controller.tokenmanagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.TokenResynchronizationDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.util.JsonToMapConvertUtil;
import com.gemalto.eziomobile.demo.webhelper.tokenmanagement.TokenManagementOperations;
import com.gemalto.eziomobile.demo.webhelper.tokenmanagement.TokenManagementWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

@RestController
public class TokenManagementController {

	@Autowired
	ServletContext servletContext;
	
	@Autowired
    private ServletConfig config;
	
	@Autowired
	private HttpSession httpSession;
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private TokenManagementWebHelper tokenWebHelper;
	
	@Autowired
	private TokenManagementOperations tokenManagementOperations;
	
	
	private static final LoggerUtil logger = new LoggerUtil(TokenManagementController.class.getClass());
	private final static String ISO_ENCODING = "ISO-8859-1";
	
	
	
	/** REST API to upload provisioning file of token
	 * and to get the provisioning status
	 * @param provisioningFile
	 * @param passphraseKey
	 * @return ResultStatus object with Response Status and Message
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/uploadprovisioningfile.action/{userId}",consumes = {"multipart/form-data"}, method = RequestMethod.POST)
	public ResultStatus uploadProvisioningFile(@RequestParam("provisioningFile") MultipartFile provisioningFile, 
			@RequestParam("passphraseKey") MultipartFile passphraseKey, @PathVariable("userId") String userId,
			@RequestParam(value = "wait", defaultValue = "true") boolean waitForResult) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		Map<String, String> provisioningStatusMap = new HashMap<>();
		String fileContent = "";
		String uploadPath = "";
		String fileName = "";
		String provisioningStatus = "";

		logger.debug("Single file upload!");
		logger.info("waitForResult : "+waitForResult);

        if (provisioningFile.isEmpty() || passphraseKey.isEmpty()) {
        	resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_MSG_TYPE_PROVISIONING_FAILED);
        	resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
        	resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
        }
        else{
        	try {
        		
        		logger.info("uploading.............");
        		
        		uploadPath = config.getServletContext().getRealPath("ezioDemoFiles");
            	fileContent = new String(provisioningFile.getBytes());
            	System.out.println("fileContent : \n"+fileContent);
            	
            	fileName = provisioningFile.getOriginalFilename();
            	logger.info("fileName : "+fileName);
            	
            	tokenManagementOperations.saveUploadedFiles(provisioningFile, config);
            	logger.info("saveUploaded file is done......");
            	
            	//Setting up the fileName in session,
            	//To user it in getprovisioningstatus API 
            	httpSession.setAttribute("fileName", fileName);
                
    		    final byte[] bytes = fileContent.getBytes(ISO_ENCODING);
    	        if (tokenManagementOperations.isUTF8(bytes)) {
    	        	
    	        	logger.info("isUTF8(bytes) : "+tokenManagementOperations.isUTF8(bytes));
    	        	fileContent = tokenManagementOperations.printSkippedBomString(bytes);
    	        }
    	        
    		    logger.info("\n\n---------------------------------------------------\n\n");
    		    logger.info("\n BOM property removed.......");
    		    System.out.println(fileContent);
    		    logger.info("\n\n---------------------------------------------------\n\n");

				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    		    
    		    DocumentBuilder builder = factory.newDocumentBuilder();
    		    Document doc = builder.parse(new InputSource(uploadPath+"/"+fileName));
    	        doc.getDocumentElement().normalize();

    	        logger.info("\n\nRoot element :" + doc.getDocumentElement().getNodeName());

    	        NodeList nList2 = doc.getElementsByTagName("pskc:KeyPackage");
    	        logger.info("----------------------------");
    	        
    	        Node nNode = nList2.item(0);
    	        Element eElement = (Element) nNode;
    	        
            	String deviceID = eElement.getElementsByTagName("pskc:SerialNo").item(0).getTextContent();
            	logger.info("\nDeviceID : " + deviceID);
            	
            	String deviceType = deviceID.substring(0, 4);
            	logger.info("\n deviceType : " + deviceType);
            	
            	//Setting up the deviceID in session,
            	//To user it in getprovisioningstatus API 
            	httpSession.setAttribute("deviceID", deviceID);
            	
            	 //step 1
	            casWebHelper.authenticateCASever();
	            
	            //step 2 - upload file to CAS
	            //upload file in local folder
	            tokenManagementOperations.stringToDom(fileContent, uploadPath, fileName);
	            boolean isFileUploaded =  tokenWebHelper.uploadBatchProvisioningFile(fileName, uploadPath);
                // saveUploadedFiles(provisioningFile);
	            
	            String passphraseKeyValue = new String(passphraseKey.getBytes());
	           logger.info("passphraseKeyValue : "+passphraseKeyValue);
	            
	           if(isFileUploaded){
	        	 
	        	   //step 3 - launch provisioning
	        	   tokenWebHelper.startBatchProvisioning(passphraseKeyValue, deviceType);
	        	 
	        	    //step 4 - get status report
		        	provisioningStatusMap = tokenManagementOperations.getProvisioningStatus();
		   		    provisioningStatus = provisioningStatusMap.get("status");
		   		    logger.info("[Get status call] provisioningStatus : "+provisioningStatus);
		   		    
		   		    //check status until it's DONE
		   		    //And break the loop once it is DONE and send the response to
		   		    //Front-end application
		   		    int count = 0;
		   		    if(provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_RUNNING)){
		   		    	while (provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_RUNNING)) {
			   				provisioningStatusMap = tokenManagementOperations.getProvisioningStatus();
			   				provisioningStatus = provisioningStatusMap.get("status");
			   				logger.info("Count : "+ ++count);
			   				
			   				if(provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_DONE)){
			   					break;
			   				}
			   			    else if(provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_FAILED)){
			   			    	break;
			   				}
			   			    else if(provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_NOT_STARTED)){
			   			    	break;
			   				}
			   			}
		   			}
		   		    logger.info("---- provisioningStatus : "+provisioningStatus+" ----- count : "+count);
	   		    	resultStatus = tokenManagementOperations.getProvisioningResponseStatus(provisioningStatus, uploadPath, fileName);
	           }else{
	        	   resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_MSG_TYPE_FILE_COULD_NOT_UPLOAD);
	        	   resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_EXPECTATION_FAILED_417);
	        	   resultStatus.setStatusCode(HttpStatus.EXPECTATION_FAILED);
	           }
			    //Step 5- [1] - Link devices
			    //Step 5- [2] - Activate devices
                
            } catch (IOException e) {
            	e.printStackTrace();
            	resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_MSG_TYPE_IO_EXCEPTION);
            	resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
            	resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
            }catch(SAXException saxEx){
            	saxEx.printStackTrace();
                resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_MSG_TYPE_NOT_WELL_FORMATTED);
                resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
                resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
    		}
    		catch(NullPointerException nullEx){
    			nullEx.printStackTrace();
    			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_MSG_TYPE_NULL_EXCEPTION);
    			resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
    			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
    		}
    		catch(Exception e){
    			e.printStackTrace();
    			resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_MSG_TYPE_GENERAL_EXCEPTION);
    			resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
    			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
    		}
        	finally {
				resultStatus.setTemplateObject(provisioningStatusMap);
			}
        }
		return resultStatus;
	}
	
	
	/** REST API to activate token of user
	 * @param userId
	 * @param tokenSerialNumber
	 * @param otpValue
	 * @return ResultStatus object with Response Status and Message
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/activatetoken.user.action/{userId}/{tokenSerialNumber}/{otpValue}", method = RequestMethod.POST)
	public ResultStatus activateUserToken(@PathVariable("userId") String userId, 
			@PathVariable("tokenSerialNumber") String tokenSerialNumber, @PathVariable("otpValue") String otpValue)
			throws ControllerException {

		ResultStatus resultStatus = new ResultStatus();

		logger.info("[activatetoken] userId : " + userId);
		logger.info("[activatetoken] tokenSerialNumber : " + tokenSerialNumber);
		logger.info("[activatetoken]otpValue : " + otpValue);

		try {
			// Authenticate CAS and get JSESSIONID
			casWebHelper.authenticateCASever();

			if (tokenSerialNumber != null && userId != null && otpValue != null) {
				if (!tokenSerialNumber.equals("") && !userId.equals("") && !otpValue.equals("")) {
					logger.info("Activating token, based on the status..");
					resultStatus = tokenWebHelper.getDeviceResponseStatus(userId, otpValue, null, tokenSerialNumber,EzioMobileDemoConstant.OPERATION_TOKEN_ACTIVATION);
				}
			}
		} catch (Exception e) {
			logger.error("Exception : Couldn't Activate the token!");
			throw new ControllerException(e);
		}

		return resultStatus;
	}
	
	
	/**
	 * @param tokenResynchronizationDTO
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/tokenresync.user.action", consumes = {"application/json"}, method = RequestMethod.POST)
	public ResultStatus doTokenResynchronization(@RequestBody final TokenResynchronizationDTO tokenResynchronizationDTO)
			throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		
		String userId = "";
		String tokenSerialNumber = "";
		String otpValue1 = "";
		String otpValue2 = "";
		
		logger.info("Test : "+tokenResynchronizationDTO.toString());
		try {
			if(tokenResynchronizationDTO != null){
				userId = tokenResynchronizationDTO.getUserId();
				tokenSerialNumber = tokenResynchronizationDTO.getTokenSerialNumber();
				otpValue1 = tokenResynchronizationDTO.getOtpValue1();
				otpValue2 = tokenResynchronizationDTO.getOtpValue2();
				
				resultStatus = tokenWebHelper.getDeviceResponseStatus(userId, otpValue1, otpValue2, tokenSerialNumber, EzioMobileDemoConstant.OPERATION_TOKEN_RESYNCHRONIZATION);
				logger.info("[doTokenResynchronization] resultStatus : "+resultStatus.toString());
			}
		} catch (Exception e) {
			logger.error("Exception : Couldn't resynch the token!");
			throw new ControllerException(e);
		} 
		return resultStatus;
	}
	
	
	
	/** API to get list of tokens
	 * @param userId
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/getlistoftokens.user.action", method = RequestMethod.GET)
	public ResultStatus getListOfTokens(@RequestParam("userId") String userId) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		Map<String, Object> resMap = new HashMap<>();
		
		logger.info("[getListOfTokens] userId : "+userId);
		
		try {
			if(userId != null && !userId.equals("")){
				
				casWebHelper.authenticateCASever();
				
				JSONObject resJSON_Obj = tokenWebHelper.formatTokenList(userId);

				if(resJSON_Obj != null && resJSON_Obj.has("tokensList")){
					resMap = JsonToMapConvertUtil.jsonToMap(resJSON_Obj);
					logger.info("[getListOfTokens] resMap : "+resMap.toString());
					
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
					resultStatus.setStatusCode(HttpStatus.OK);
					resultStatus.setTemplateObject(resMap);
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_NO_CONTENT_204);
					resultStatus.setStatusCode(HttpStatus.NO_CONTENT);
					resultStatus.setTemplateObject(resMap);
				}
			}
		} catch (Exception e) {
			logger.error("Exception : Couldn't fetch list of tokens!");
			throw new ControllerException(e);
		}
		
		return resultStatus;
	}
		
	
	/**
	 * @param userId
	 * @param physicalTokenName
	 * @return ResultStatus object
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/releaseusertoken.action/{userId}/{physicalTokenName}", method = RequestMethod.POST)
	public ResultStatus releaseToken(@PathVariable("userId") String userId, @PathVariable("physicalTokenName") String physicalTokenName) throws ControllerException{
		
		ResultStatus resultStatus = new ResultStatus();
		
		try {
			if(userId != null && !userId.equals("") && physicalTokenName != null && !physicalTokenName.equals("")){
				
				casWebHelper.authenticateCASever();
				int isTokenReleased = tokenWebHelper.releaseToken(userId, physicalTokenName);
				
				logger.info("[releaseToken] isTokenReleased : "+isTokenReleased);
				logger.info("[releaseToken] ALL_OK : "+ CommonOperationsConstants.ALL_OK);
				
				if(isTokenReleased == CommonOperationsConstants.ALL_OK){
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
					resultStatus.setStatusCode(HttpStatus.OK);
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				}
			}
		} catch (Exception e) {
			logger.error("Exception : Couldn't release the token with tokenNumber : "+physicalTokenName);
			throw new ControllerException(e);
		}
		return resultStatus;
	}
		
	
	
	/**
	 * @return
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus tokenManagementErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
	
}


