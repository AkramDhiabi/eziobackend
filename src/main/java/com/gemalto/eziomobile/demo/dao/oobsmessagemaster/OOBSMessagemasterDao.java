package com.gemalto.eziomobile.demo.dao.oobsmessagemaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.OOBSMessageMasterInfo;

@Repository
public interface OOBSMessagemasterDao extends CrudRepository<OOBSMessageMasterInfo, Integer>, OOBSMasterOperations{
	
	OOBSMessageMasterInfo findOOBSDataByMessageId(String messageId);
	
	OOBSMessageMasterInfo findOOBSDataByUserId(String userId);
	
	void deleteOOBSMessageDataByUserId(String userId);

}
