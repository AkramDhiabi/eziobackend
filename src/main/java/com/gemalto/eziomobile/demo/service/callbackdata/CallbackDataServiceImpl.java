package com.gemalto.eziomobile.demo.service.callbackdata;

import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dao.callbackdata.CallbackDataDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.CallbackDataInfo;
import com.gemalto.eziomobile.demo.util.AsyncResponse;

@Service
public class CallbackDataServiceImpl implements CallbackDataService{
	
	private static final LoggerUtil logger = new LoggerUtil(CallbackDataServiceImpl.class.getClass());
	
	@Autowired
	private CallbackDataDao callbackDataDao;
	
	
	
	@Override
	public CallbackDataInfo findCallbackDataByMessageId(String messageId) throws ServiceException {
		CallbackDataInfo callBackResult = new CallbackDataInfo();
		try {
			callBackResult = callbackDataDao.findCallbackDataByMessageId(messageId);
		} catch (Exception e) {
			logger.info("Exception : Couldn't update the callback data! " +e);
			throw new ServiceException(e);
		}
		return callBackResult;
	}


	/*@Override
	public boolean findCallbackTypeByUserIdAndMessageId(String userId, String messageId) throws ServiceException {
		logger.info("> getting callback type based on userID and messageId..");

        String type = "";
        Boolean success = Boolean.FALSE;

        try {
            type = callbackDataDao.findCallbackTypeByUserIdAndMessageId(userId, messageId);
            if(type.equalsIgnoreCase("Validate")){
            	success = Boolean.TRUE;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        logger.info("< got!");
        return success;
	}*/

	@Async
	@Override
	public void getDataAsync(String userId, String messageId) {
		 logger.info("> sendAsync");

	        try {
	            //findCallbackTypeByUserIdAndMessageId(userId, messageId);
	        	findCallbackDataByMessageId(messageId);
	        } catch (Exception e) {
	            logger.warn("Exception caught sending asynchronous mail."+e);
	        }

	        logger.info("< sendAsync");
	}

	@Async
	@Override
	public Future<CallbackDataInfo> sendAsyncWithResult(String userId, String messageId) {
		 logger.info("> sendAsyncWithResult");

	        AsyncResponse<CallbackDataInfo> response = new AsyncResponse<CallbackDataInfo>();
	        String type = "";
	        CallbackDataInfo callbackDataInfo = new CallbackDataInfo();

	        try {
	        	//Boolean success = findCallbackTypeByUserIdAndMessageId(userId, messageId);
	        	callbackDataInfo = callbackDataDao.findCallbackDataByMessageId(messageId);
	        	
	            response.complete(callbackDataInfo);
	        } catch (Exception e) {
	            logger.warn("Exception caught sending asynchronous callback."+ e);
	            response.completeExceptionally(e);
	        }
			if(null != callbackDataInfo)
	        	logger.info("< callbackAsyncWithResult" + callbackDataInfo.toString());
	        return response;
	}


	@Override
	public void saveCallbackData(CallbackDataInfo callbackDataInfo) throws ServiceException {
		try {
			callbackDataDao.save(callbackDataInfo);
		} catch (Exception e) {
			logger.info("Exception : Couldn't save the callback data! " +e);
			throw new ServiceException(e);
		}
	}


	@Override
	public void updateCallBackData(String userId, String messageId, String callBackResult, int responseCode) throws ServiceException {
		try {
			logger.info("Callback data updating....");
			callbackDataDao.updateCallBackData(userId, messageId, callBackResult, responseCode);
		} catch (Exception e) {
			logger.info("Exception : Couldn't update the callback data! " +e);
			throw new ServiceException(e);
		}
	}


	@Override
	@Transactional
	public void deleteCallbackDataByUserId(String userId) throws ServiceException {
		try {
			logger.info("Callback data deleting....");
			callbackDataDao.deleteCallbackDataByUserId(userId);
		} catch (Exception e) {
			logger.info("Exception : Couldn't delete the callback data! " +e);
			throw new ServiceException(e);
		}
	}

}
