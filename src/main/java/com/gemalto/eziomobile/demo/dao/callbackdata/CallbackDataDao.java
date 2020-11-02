package com.gemalto.eziomobile.demo.dao.callbackdata;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.CallbackDataInfo;

@Repository
public interface CallbackDataDao extends CrudRepository<CallbackDataInfo, Integer>, CallbackDataOperations{
	
	CallbackDataInfo findCallbackDataByMessageId(String messageId);
	
	//void deleteCallbackDataByMessageId(String messageId);
	
	void deleteCallbackDataByUserId(String userId);
}
