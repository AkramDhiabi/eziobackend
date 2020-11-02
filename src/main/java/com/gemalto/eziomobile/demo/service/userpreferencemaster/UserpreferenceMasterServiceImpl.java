package com.gemalto.eziomobile.demo.service.userpreferencemaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.dao.userpreferencemaster.UserpreferenceMasterDao;
import com.gemalto.eziomobile.demo.dto.UserpreferenceMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.UserPreferenceInfo;

@Service
public class UserpreferenceMasterServiceImpl implements UserpreferenceMasterService{
	
	private static final LoggerUtil logger = new LoggerUtil(UserpreferenceMasterServiceImpl.class.getClass());
	
	@Autowired
	private UserpreferenceMasterDao userPreferenceDao;

	@Override
	public String findSecLoginByStatusAndUserId(int status, int userId) throws ServiceException {
		String secLogin = "";
		try {
			secLogin = userPreferenceDao.findSecLoginByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[Login] Unable to find Login security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secLogin;
	}

	@Override
	public String findSecTxOtherByStatusAndUserId(int status, int userId) throws ServiceException {
		String secTxother = "";
		try {
			secTxother = userPreferenceDao.findSecTxOtherByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[TxOther] Unable to find security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secTxother;
	}

	@Override
	public String findSecTxOwnAccByStatusAndUserId(int status, int userId) throws ServiceException {
		String secTxOwnAcc = "";
		try {
			secTxOwnAcc = userPreferenceDao.findSecTxOwnAccByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[TxOwnAcc] Unable to find security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secTxOwnAcc;
	}

	@Override
	public String findSecAddPayeeByStatusAndUserId(int status, int userId) throws ServiceException {
		String secAddPayee = "";
		try {
			secAddPayee = userPreferenceDao.findSecAddPayeeByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[AddPayee] Unable to find security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secAddPayee;
	}

	@Override
	public String findSecEcommerce3dsByStatusAndUserId(int status, int userId) throws ServiceException {
		String secEcommerce3ds = "";
		try {
			secEcommerce3ds = userPreferenceDao.findSecEcommerce3dsByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[Ecommerce3ds] Unable to find security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secEcommerce3ds;
	}

	@Override
	public String findSecP2pNotificationByStatusAndUserId(int status, int userId) throws ServiceException {
		String secP2pNotification = "";
		try {
			secP2pNotification = userPreferenceDao.findSecP2pNotificationByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[P2pNotification] Unable to find security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secP2pNotification;
	}

	@Override
	public String findSecWebNotificationByStatusAndUserId(int status, int userId) throws ServiceException {
		
		String secWebNotification = "";
		try {
			secWebNotification = userPreferenceDao.findSecWebNotificationByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[WebNotification] Unable to find security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secWebNotification;
	}

	@Override
	public String findSecMobileBankingByStatusAndUserId(int status, int userId) throws ServiceException {
		
		String secMobileBanking = "";
		try {
			secMobileBanking = userPreferenceDao.findSecMobileBankingByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("[MobileBanking] Unable to find security mode for userID : "+userId, e);
			throw new ServiceException(e);
		}
		return secMobileBanking;
	}

	@Override
	public UserpreferenceMasterDTO findUserPreferenceInfoByUserIdAndStatus(int userId, int status) throws ServiceException {
		
		UserpreferenceMasterDTO userpreferenceMasterDTO = null;
		
		try {
			UserPreferenceInfo userPreferenceInfo = userPreferenceDao.findUserPreferenceInfoByUserIdAndStatus(userId, status);
			if(userPreferenceInfo != null){
				
				userpreferenceMasterDTO = new UserpreferenceMasterDTO();
				userpreferenceMasterDTO.setuId(userPreferenceInfo.getId());
				userpreferenceMasterDTO.setUserId(userPreferenceInfo.getUserId());
				userpreferenceMasterDTO.setStatus(userPreferenceInfo.getStatus());
				userpreferenceMasterDTO.setSec_txownacc(userPreferenceInfo.getSec_txownacc());
				userpreferenceMasterDTO.setSec_txother(userPreferenceInfo.getSec_txother());
				userpreferenceMasterDTO.setSec_mode(userPreferenceInfo.getSec_mode());
				userpreferenceMasterDTO.setSec_login(userPreferenceInfo.getSec_login());
				userpreferenceMasterDTO.setSec_ecommerce3ds(userPreferenceInfo.getSec_ecommerce3ds());
				userpreferenceMasterDTO.setSec_addpayee(userPreferenceInfo.getSec_addpayee());
				userpreferenceMasterDTO.setP2p_notification(userPreferenceInfo.getP2p_notification());
				userpreferenceMasterDTO.setMobile_banking(userPreferenceInfo.getMobile_banking());
				userpreferenceMasterDTO.setWeb_notification(userPreferenceInfo.getWeb_notification());
				userpreferenceMasterDTO.setUpdated_date(userPreferenceInfo.getUpdated_date());
				
			}
		} catch (Exception e) {
			logger.info("Exception : Unable to find CardManagement data for PanNo : "+ userId, e);
			throw new ServiceException(e);
		}
		return userpreferenceMasterDTO;
	}
	
	

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public boolean updateUserPreferenceInfo(UserpreferenceMasterDTO userpreferenceMasterDTO) throws ServiceException {
		boolean isCardSettingsUpdated = false;
		int updateCnt = 0;
		try {
			updateCnt = userPreferenceDao.updateUserPreferenceInfo(userpreferenceMasterDTO);
			
			if(updateCnt> 0) {
				isCardSettingsUpdated = true;
			}
			
		} catch (Exception e) {
			logger.info("Exception : Unable to update Card Settings!" ,e);
			throw new ServiceException(e);
		}
		return isCardSettingsUpdated;
}
}