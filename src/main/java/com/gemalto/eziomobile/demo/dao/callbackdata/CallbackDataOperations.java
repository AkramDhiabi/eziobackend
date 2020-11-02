package com.gemalto.eziomobile.demo.dao.callbackdata;

public interface CallbackDataOperations {

	String findCallbackTypeByUserIdAndMessageId(String userId, String messageId);

	String findCallbackTypeByUserId(String userId);
	
	void updateCallBackData(String userId, String messageId, String callBackResult, int responseCode);
}
