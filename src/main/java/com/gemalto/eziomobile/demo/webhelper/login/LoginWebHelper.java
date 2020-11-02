package com.gemalto.eziomobile.demo.webhelper.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.UserMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.PanMasterInfo;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;
import com.gemalto.eziomobile.demo.service.groupmaster.GroupmasterService;
import com.gemalto.eziomobile.demo.service.master.MasterService;
import com.gemalto.eziomobile.demo.service.panmaster.PanmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.demo.webhelper.emv.EMVCardCreationWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.AUTHMODE_USERNAME_AND_OTP_EVENT_BASED;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.AUTHMODE_USERNAME_AND_OTP_TIME_BASED;

@Component
public class LoginWebHelper {
	
	private static final LoggerUtil logger = new LoggerUtil(LoginWebHelper.class.getClass());
	
	@Autowired
	private URLUtil urlUtil;
	
	@Autowired
	private MasterService masterService;
	
	@Autowired
	private EMVCardCreationWebHelper emvWebHelper;
	
	@Autowired
	private PanmasterService panService;
	
	@Autowired
	private UsermasterService usermasterService;

	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private GroupmasterService groupmasterService;
	
	@Autowired 
	private CommonWebHelper commonWebHelper;

	//Login methods
	public static final int AUTHMODE_USERNAME_AND_PASSWORD = 0x0;
	public static final int AUTHMODE_USERNAME_AND_OTP_TIME_BASED = 0x1;
	public static final int AUTHMODE_USERNAME_AND_OTP_EVENT_BASED = 0x2;

	/*
	// For now:
	// - Authent by Username and Password
	// - Authent by OTP (Standard time based OTP)
	// Later: (?)
	// - Authent by OTP (C/R)
	// - Authent by Username + Password + OTP (Time based)
	// - Authent by Username + Password + OTP (C/R)
	*/
	public boolean checkLogin(String userId, String PasswordOrOTP, int authenMode){
		if(userId==null || PasswordOrOTP==null || userId.isEmpty() || PasswordOrOTP.isEmpty()){
			return false;
		}
		try{
			switch(authenMode){
			case AUTHMODE_USERNAME_AND_OTP_TIME_BASED:
				return checkLoginUsernameAndOTP(userId, PasswordOrOTP);
			case AUTHMODE_USERNAME_AND_OTP_EVENT_BASED:
				return checkLoginUsernameAndOTP_OATH(userId, PasswordOrOTP);
			default:
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * @param userID
	 * @param OTP
	 * @return
	 */
	public boolean checkLoginUsernameAndOTP(String userID, String OTP){
		if(userID==null || OTP==null || userID.isEmpty() || OTP.isEmpty()){
			return false;
		}
		
		String pushresponseData = "";
		String _pushurl = "";
		boolean isOTPVerfied = false;

		String pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<AuthenticationRequest><UserID>" + userID
				+ "</UserID><OTP>" + OTP + "</OTP></AuthenticationRequest>";
		try {
			logger.info("before server call....");
			logger.info("[checkLoginUsernameAndOTP, TIME BASED], pushXmlData : " + pushXmlData);


			logger.info("Validate OTP URL [TIME BASED] : "+urlUtil.validateOTPURL());
			_pushurl = urlUtil.validateOTPURL(); // P2PWebHelper.validateOTPFromCAServerURL(EzioMobileDemoConstant.EZIO_PROPERTIES_PATH, context);
			logger.info("[Validate OTP , TIME BASED] _pushurl : "+_pushurl);
			
			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty("User-Agent", "Mozilla/5.0");
			pushcon.setRequestProperty("Content-type", "text/xml");
			pushcon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);

			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {
			
				logger.info("[checkLoginUsernameAndOTP, TIME BASED], SUCCESS!");
				logger.info("[checkLoginUsernameAndOTP, TIME BASED], OTP validated!");
				isOTPVerfied = true;
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			
			} else {
		
				logger.info("[checkLoginUsernameAndOTP, TIME BASED], FAILED!");
				logger.info("[checkLoginUsernameAndOTP, TIME BASED], In-valid OTP!");
				isOTPVerfied = false;
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
			logger.info("[checkLoginUsernameAndOTP, TIME BASED], EXCEPTION!!");
			isOTPVerfied = false;
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return isOTPVerfied;
	}
	
	
	
	/**
	 * @param userID
	 * @param OTP
	 * @return
	 */
	public boolean checkLoginUsernameAndOTP_OATH(String userID, String OTP){
		if(userID==null || OTP==null || userID.isEmpty() || OTP.isEmpty()){
			return false;
		}
		
		String pushresponseData = "";
		String _pushurl = "";
		boolean isOTPVerfied = false;

		String pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<AuthenticationRequest><UserID>" + userID
				+ "</UserID><OTP>" + OTP + "</OTP></AuthenticationRequest>";
		try {
			
			logger.info("[checkLoginUsernameAndOTP_OATH, EVENT BASED] before server call....");
			logger.info("[checkLoginUsernameAndOTP_OATH, EVENT BASED], pushXmlData : " + pushXmlData);
			
			logger.info("[checkLoginUsernameAndOTP_OATH, EVENT BASED] validate OTP URL : "+urlUtil.validateOTPURL());
			_pushurl = urlUtil.validateOTPURL(); //P2PWebHelper.validateOTPFromCAServerURL(EzioMobileDemoConstant.EZIO_PROPERTIES_PATH, context);
			_pushurl = _pushurl.replace("otb", "oath");
			logger.info("[checkLoginUsernameAndOTP_OATH, EVENT BASED] validate OTP, Updated-URL : "+_pushurl);

			URL pushobj = new URL(_pushurl);
			HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

			pushcon.setRequestMethod("POST");
			pushcon.setRequestProperty("User-Agent", "Mozilla/5.0");
			pushcon.setRequestProperty("Content-type", "text/xml");
			pushcon.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			pushcon.setDoOutput(true);
			PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
			pushpw.write(pushXmlData);

			pushpw.flush();
			pushpw.close();
			// For POST only - END

			int pushresponseCode = pushcon.getResponseCode();
			pushresponseData = Integer.toString(pushresponseCode);

			if (pushresponseCode == HttpURLConnection.HTTP_OK) {
			
				logger.info("[checkLoginUsernameAndOTP_OATH, EVENT BASED] SUCCESS!");
				
				isOTPVerfied = true;
				BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

				String pushinputLine;
				StringBuffer _pushresponse = new StringBuffer();

				while ((pushinputLine = pushin.readLine()) != null) {
					_pushresponse.append(pushinputLine);
				}
				pushin.close();
				pushresponseData = _pushresponse.toString();
			
			} else {
		
				logger.info("[checkLoginUsernameAndOTP_OATH, EVENT BASED] FAILED!");
				isOTPVerfied = false;
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
			logger.info("[checkLoginUsernameAndOTP_OATH, EVENT BASED] EXCEPTION!");
			isOTPVerfied = false;
			e.printStackTrace();
			pushresponseData = e.toString();
		}
		return isOTPVerfied;
	}
	
	
	
	/**
	 * @param userId
	 * @return
	 */
	public ResultStatus setupUserAccountsAndDB(String userId){
		ResultStatus resultStatus = new ResultStatus();
		boolean isCreated = false;
		boolean isLinked = false;
		boolean isActivated = false;
		int updateCount = 0;
		
		if(userId != null){
			int uId = 0;
			try {
				uId = usermasterService.findUidByUserId(userId);
				
				logger.info("Initialy updateCount  = "+updateCount);
				
				masterService.createAccountMasterData(uId);
				//logger.info("1 updateCount  = "+updateCount);
				
				masterService.createRiskPreferenceMasterData(uId);
				//logger.info("2 updateCount  = "+updateCount);
				
				masterService.createUserPreferenceMasterData(uId);
				//logger.info("3 updateCount  = "+updateCount);
				
				updateCount += masterService.createPanMasterData(uId);
				logger.info("1 updateCount  = "+updateCount);
				
				masterService.createCardManagementMasterData(uId);
				//logger.info("5 updateCount  = "+updateCount);
				
				List<PanMasterInfo> panList = panService.findPanInfoByUserId(uId);
				logger.info("panList : "+panList.toString());
				
				if(updateCount==1 && !panList.isEmpty() && panList.size()>0){
					
					casWebHelper.authenticateCASever();
					for (PanMasterInfo panMasterInfo : panList) {
						
						logger.info("EMV cards operations...");
						logger.info("For : "+panMasterInfo.getPanNo());
						
						isCreated = emvWebHelper.createEMVCards(panMasterInfo.getPanNo(), userId);
						isLinked = emvWebHelper.linkEMVCards(userId, panMasterInfo.getPanNo());
						isActivated = emvWebHelper.activeEMVCards(panMasterInfo.getPanNo());
						
						//Get the list of devices, is been created
					}
					if(isCreated && isLinked && isActivated){
						resultStatus.setMessage(EzioMobileDemoConstant.EMV_CARD_CREATE_LINK_ACTIVATE);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
						resultStatus.setStatusCode(HttpStatus.CREATED);
					}
					else{
						resultStatus.setMessage(EzioMobileDemoConstant.EMV_CARD_ERROR);
						resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
						resultStatus.setStatusCode(HttpStatus.EXPECTATION_FAILED);
					}
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.EMV_CARD_PAN_NOT_FOUND_ERROR_OR_ACCOUNT_ALREADY_SETUP);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.EXPECTATION_FAILED);
				}
			} catch (ServiceException e) {
				logger.error(EzioMobileDemoConstant.DEFAULT_ERROR_MSG, e);
				e.printStackTrace();
			}
		}
		return resultStatus;
	}
	
	
	
	public int checkEMVForNonCASUser(String userId){
		ResultStatus resultStatus = new ResultStatus();
		boolean isJobDone = false;
		boolean isCreated = false;
		boolean isLinked = false;
		boolean isActivated = false;
		int updateCount = 0;
		
		if(userId != null){
			int uId = 0;
			try {
				uId = usermasterService.findUidByUserId(userId);
				
				List<PanMasterInfo> panList = panService.findPanInfoByUserId(uId);
				logger.info("panList : "+panList.toString());
				
				if(!(!panList.isEmpty() && panList.size()>0)){
					return 0;
				}

				casWebHelper.authenticateCASever();
				for (PanMasterInfo panMasterInfo : panList) {

					logger.info("EMV cards operations...");
					logger.info("For : "+panMasterInfo.getPanNo());


					if(!emvWebHelper.createEMVCards(panMasterInfo.getPanNo(), userId)) {
						return -1;
					}

					emvWebHelper.linkEMVCards(userId, panMasterInfo.getPanNo());
					emvWebHelper.activeEMVCards(panMasterInfo.getPanNo());
					isJobDone = true;
				}

			} catch (ServiceException e) {
				logger.error(EzioMobileDemoConstant.DEFAULT_ERROR_MSG, e);
				e.printStackTrace();
			}
		}
		 if(isJobDone) {
			 return 0;
			 
		 }else {
			 return -1;
		 }
	}

	/**
	 * Setting values from UserMasterInfo and permissionMap to UserMasterDTO
	 * @param userMasterInfo
	 * @return userMasterDTO
	 */
	public UserMasterDTO createUserMasterDTO(UserMasterInfo userMasterInfo) {
		
		String userRole = userMasterInfo.getUserRole();
		logger.info("userRole in createUserMAsterDTO: "+userRole);
		int groupId = userMasterInfo.getGroupId();
		logger.info("groupId in createUserMAsterDTO: "+groupId);
		String groupName = null;
		String userId = userMasterInfo.getUserId();
		logger.info("userId in createUserMAsterDTO: "+userId);
		int updateCount = 0;
		int uId = 0;
		UserMasterDTO userMasterDTO = new UserMasterDTO();
		
		try{
			uId = usermasterService.findUidByUserId(userId);
			logger.info("uId in createUserMAsterDTO: "+uId);
			if(userRole == null){
				groupName = groupmasterService.findGroupNameByGroupId(groupId);
				logger.info("groupName in createUserMAsterDTO: "+groupName);

				
				if(groupName.equals(EzioMobileDemoConstant.GROUP_NAME_BLACK_LIST))
					//all permisssions except p2p and token provisioning
					//updateCount = usermasterService.updateUserRoleByUid(uId, EzioMobileDemoConstant.USER_ROLE_000);
					  updateCount = usermasterService.updateUserRoleByUid(uId, EzioMobileDemoConstant.USER_ROLE_BLACK_LIST);
				else 
					//all permisssions except token provisioning
					//updateCount = usermasterService.updateUserRoleByUid(uId, EzioMobileDemoConstant.USER_ROLE_001);
					updateCount = usermasterService.updateUserRoleByUid(uId, EzioMobileDemoConstant.USER_ROLE_NON_BLACK_LIST);
					
				if(updateCount>0)
					userMasterInfo = usermasterService.findUserInfoByUid(uId);
				
				logger.info("after update: "+userMasterInfo.toString());
			}
			logger.info("userRole before inserting in permission map: "+userRole);
			logger.info("userRole going in  prepareUserPermission: "+userMasterInfo.getUserRole());
			Map<String, Boolean> permissionMap = commonWebHelper.prepareUserPermissions(userMasterInfo.getUserRole());
			
			userMasterDTO.setPermission(userMasterInfo.getUserRole());
			userMasterDTO.setEmailAddress(userMasterInfo.getEmailAddress());
			userMasterDTO.setuId(userMasterInfo.getuId());
			userMasterDTO.setUserId(userMasterInfo.getUserId());
			userMasterDTO.setGroupId(userMasterInfo.getGroupId());
			userMasterDTO.setUserRole(permissionMap);
			userMasterDTO.setLastLoginTime(userMasterInfo.getLastLoginTime());
			
		}catch(Exception e){
			logger.error("Exception occurred in LoginWebHelper - createUserMasterDTO");
		}

		return userMasterDTO;
	}

}
