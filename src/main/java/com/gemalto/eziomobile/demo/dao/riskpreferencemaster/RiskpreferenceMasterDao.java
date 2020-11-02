package com.gemalto.eziomobile.demo.dao.riskpreferencemaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.RiskPreferenceInfo;

@Repository
public interface RiskpreferenceMasterDao extends CrudRepository<RiskPreferenceInfo, Integer>, RiskpreferenceMasterOperations{
	
	int countByStatusAndUserId(int status, String userId);
	
	void deleteRiskPreferenceInfoByStatusAndUserId(int status, String userId);

}
