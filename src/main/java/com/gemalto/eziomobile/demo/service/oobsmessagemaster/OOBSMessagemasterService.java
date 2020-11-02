package com.gemalto.eziomobile.demo.service.oobsmessagemaster;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.OOBSMessageMasterInfo;

@Service
public interface OOBSMessagemasterService {
	
	OOBSMessageMasterInfo findOOBSDataByMessageId(String msgId) throws ServiceException;
	
	OOBSMessageMasterInfo findOOBSDataByUserId(String userId) throws ServiceException;
	
	void saveOOBSMessagemasterInfo(OOBSMessageMasterInfo oobsMessageMasterInfo) throws ServiceException;
	
	//void  deleteOOBSMessagemasterInfo(OOBSMessageMasterInfo oobsMessageMasterInfo) throws ServiceException;
	void deleteOOBSMessageDataByUserId(String userId) throws ServiceException;

	String findMsgTypeByMessageId(String msgId) throws ServiceException;
	
	String findMsgIdByUserId(String userId) throws ServiceException;
	
	String findUserIdByMessageId(String messageId) throws ServiceException;
	
	public String checkNotificationStateFromOOBS(String messageId);
}
