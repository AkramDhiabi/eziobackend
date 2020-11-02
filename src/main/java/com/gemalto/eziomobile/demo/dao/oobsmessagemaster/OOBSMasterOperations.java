package com.gemalto.eziomobile.demo.dao.oobsmessagemaster;

public interface OOBSMasterOperations {
	
	String findMsgIdByUserId(String userId);
	
	String findMsgTypeByMessageId(String messageId);
	
	String findUserIdByMessageId(String messageId);
	
	String checkNotificationStateFromOOBS(String messageId);

}
