package com.gemalto.eziomobile.demo.service.callbackdata;

import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.CallbackDataInfo;

@Service
public interface CallbackDataService {
	
	CallbackDataInfo findCallbackDataByMessageId(String messageId) throws ServiceException;
	
	void saveCallbackData(CallbackDataInfo callbackDataInfo) throws ServiceException;
	
	void updateCallBackData(String userId, String messageId, String callBackResult, int responseCode) throws ServiceException;
	
	//void deleteCallbackDataByMessageId(String messageId) throws ServiceException;
	
	void deleteCallbackDataByUserId(String userId) throws ServiceException;

	/**
     * Send a Greeting via email asynchronously.
     * @param greeting A Greeting to send.
     */
    void getDataAsync(String userId, String messageId);

    /**
     * Send a Greeting via email asynchronously. Returns a Future&lt;Boolean&gt;
     * response allowing the client to obtain the status of the operation once
     * it is completed.
     * @param greeting A Greeting to send.
     * @return A Future&lt;Boolean&gt; whose value is TRUE if sent successfully;
     *         otherwise, FALSE.
     */
    Future<CallbackDataInfo> sendAsyncWithResult(String userId, String messageId);
}
