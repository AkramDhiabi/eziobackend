package com.gemalto.eziomobile.demo.service.oobsmessagemaster;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dao.oobsmessagemaster.OOBSMessagemasterDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.OOBSMessageMasterInfo;

@Service
public class OOBSMessagemasterServiceImpl implements OOBSMessagemasterService{
	
	@Autowired
	private OOBSMessagemasterDao oobsMessagemasterDao;
	
	private static final LoggerUtil logger = new LoggerUtil(OOBSMessagemasterServiceImpl.class.getClass());

	/* 
	 * 
	 */
	@Override
	public OOBSMessageMasterInfo findOOBSDataByMessageId(String msgId) throws ServiceException {
		OOBSMessageMasterInfo oobsMessageMasterInfo = new OOBSMessageMasterInfo();
		try {
			oobsMessageMasterInfo = oobsMessagemasterDao.findOOBSDataByMessageId(msgId);
		} catch (Exception e) {
			logger.info("Unable to find data with msgID : "+msgId, e);
			throw new ServiceException(e);
		}
		return oobsMessageMasterInfo;
	}

	/* 
	 * 
	 */
	@Override
	public void saveOOBSMessagemasterInfo(OOBSMessageMasterInfo oobsMessageMasterInfo) throws ServiceException {
		try {
			oobsMessagemasterDao.save(oobsMessageMasterInfo);
		} catch (Exception e) {
			logger.info("Unable save OOBS message master data!!", e);
			throw new ServiceException(e);
		}
		
	}

	/* 
	 * 
	 */
	@Override
	@Transactional
	public void deleteOOBSMessageDataByUserId(String userId) throws ServiceException {
		try {
			oobsMessagemasterDao.deleteOOBSMessageDataByUserId(userId);
		} catch (Exception e) {
			logger.info("Unable delete OOBS message master data!!", e);
			throw new ServiceException(e);
		}
	}

	
	
	@Override
	public String findMsgTypeByMessageId(String msgId) throws ServiceException {
		String msgType = null;
		try {
			msgType = oobsMessagemasterDao.findMsgTypeByMessageId(msgId);
		} catch (Exception e) {
			logger.info("Unable to find any Message type of MessageID : "+msgId, e);
			throw new ServiceException(e);
		}
		return msgType;
	}

	
	
	@Override
	public OOBSMessageMasterInfo findOOBSDataByUserId(String userId) throws ServiceException {
		OOBSMessageMasterInfo oobsMessageMasterInfo = new OOBSMessageMasterInfo();
		try {
			oobsMessageMasterInfo = oobsMessagemasterDao.findOOBSDataByUserId(userId);
		} catch (Exception e) {
			logger.info("Unable to find data with UserId : "+userId, e);
			throw new ServiceException(e);
		}
		return oobsMessageMasterInfo;
	}

	
	@Override
	public String findMsgIdByUserId(String userId) throws ServiceException {
		String msgId = "";
		try {
			msgId = oobsMessagemasterDao.findMsgIdByUserId(userId);
		} catch (Exception e) {
			logger.info("Unable to find MessageId with UserId : "+userId, e);
			throw new ServiceException(e);
		}
		return msgId;
	}

	
	@Override
	public String findUserIdByMessageId(String messageId) throws ServiceException {
		String userId = "";
		try {
			userId = oobsMessagemasterDao.findUserIdByMessageId(messageId);
		} catch (Exception e) {
			logger.info("Unable to find UID with messageId : "+messageId, e);
			throw new ServiceException(e);
		}
		return userId;
	}

	@Override
	public String checkNotificationStateFromOOBS(String messageId) {
		return oobsMessagemasterDao.checkNotificationStateFromOOBS(messageId);
	}

}
