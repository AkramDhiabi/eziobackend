package com.gemalto.eziomobile.demo.webhelper.tokenmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;

@Component
public class TokenManagementOperations {
	
	@Autowired
	private TokenManagementWebHelper tokenWebHelper;
	
	private static final LoggerUtil logger = new LoggerUtil(TokenManagementOperations.class.getClass());
	
	private final static String ISO_ENCODING = "ISO-8859-1";
    private final static int UTF8_BOM_LENGTH = 3;

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;

	public TokenManagementOperations() {
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Cannot parse XML {}.", e);
		}
	}
    
	/** step 4 - get status report
	 * @return Map with Provisioning status data 
	 */
	public Map<String, String> getProvisioningStatus(){
		Map<String, String> provisioningStatusMap = new HashMap<String, String>();
		
		//casWebHelper.authenticateCASever();
		//step 4 - get status report
	    provisioningStatusMap = tokenWebHelper.getBatchProvisioningStatus();
	    String provisioningStatus = provisioningStatusMap.get("status");
	    System.out.println("[Get status call] provisioningStatus : "+provisioningStatus);
		
	    return provisioningStatusMap;
	}
	

	/**
	 * @param provisioningStatus
	 * @param uploadPath
	 * @param fileName
	 * @return
	 */
	public ResultStatus getProvisioningResponseStatus(String provisioningStatus, String uploadPath, String fileName){
		
			ResultStatus resultStatus = new ResultStatus();
		
			if(provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_DONE)){
  			 
			   //Deleting uploaded file from the path
	           deleteUploadedFile(uploadPath, fileName);
			   
	           resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_DONE);
	           resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
	           resultStatus.setStatusCode(HttpStatus.OK);
			}
		    else if(provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_FAILED)){
			   
			   //Deleting uploaded file from the path
		       deleteUploadedFile(uploadPath, fileName);
		           
			   resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_FAILED);
	           resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
	           resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
			}
		    else if(provisioningStatus.equalsIgnoreCase(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_NOT_STARTED)){
				   
		    	//Deleting uploaded file from the path
			    deleteUploadedFile(uploadPath, fileName);
			           
				resultStatus.setMessage(EzioMobileDemoConstant.TOKEN_PROVISIONING_STATUS_NOT_STARTED);
		        resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		        resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
			}
		return resultStatus;
	}
	
	/** save file
	 * @param file
	 * @throws IOException
	 */
	public void saveUploadedFiles(MultipartFile file, ServletConfig config) throws IOException {
	        //for (MultipartFile file : files) {

			String uploadPath = config.getServletContext().getRealPath("ezioDemoFiles");
			logger.info("[saveUploadedFiles] uploadPath : "+uploadPath);
			
			File fileDir = new File(uploadPath);
	        if (!fileDir.exists()) {
	            if (fileDir.mkdir()) {
	                logger.info("Directory is created!");
	            } else {
	            	logger.info("Failed to create directory!");
	            }
	        }
	            byte[] bytes = file.getBytes();
	            Path path = Paths.get(uploadPath+"/"+file.getOriginalFilename());
	            Files.write(path, bytes);
	       // }
	    }
	
	/** Delete uploaded provisioning file
	 * @param uploadPath
	 * @param fileName
	 * @return
	 */
	public boolean deleteUploadedFile(String uploadPath, String fileName){
		
		File file = new File(uploadPath+"/"+fileName);
	        boolean fileDelete = file.delete();
	        if(fileDelete){
	        	logger.info("------------------------------------------");
	        	logger.info("File has been deleted from upload folder!");
	        	logger.info("------------------------------------------");
	        }
		return fileDelete;
	}
	
	/**
	 * @param xmlSource
	 * @param uploadPath
	 * @param fileName
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public void stringToDom(String xmlSource, String uploadPath, String fileName) throws SAXException, IOException, TransformerException{
	    
	    InputStream is = new FileInputStream(uploadPath+"/"+fileName);
	    Document doc = builder.parse(is , "UTF-8");
	    
	    // Use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();

	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(new File(uploadPath+"/"+fileName));
	    transformer.transform(source, result);
	}

	
	/**Skip BOM characters, if any
	 * @param bytes
	 * @throws UnsupportedEncodingException
	 */
	public String printSkippedBomString(final byte[] bytes) throws UnsupportedEncodingException {
		
		logger.info("printSkippedBomString......");
		
        int length = bytes.length - UTF8_BOM_LENGTH;
        byte[] barray = new byte[length];
        System.arraycopy(bytes, UTF8_BOM_LENGTH, barray, 0, barray.length);
        String str = new String(barray, ISO_ENCODING);
		return str;
    }
	
	
	/**Check UTF-8 format
	 * @param bytes
	 * @return
	 */
	public boolean isUTF8(byte[] bytes) {
		
		logger.info("checking isUTF8......");
        if ((bytes[0] & 0xFF) == 0xEF && 
            (bytes[1] & 0xFF) == 0xBB && 
            (bytes[2] & 0xFF) == 0xBF) {
            return true;
        }
        return false;
    }

}
