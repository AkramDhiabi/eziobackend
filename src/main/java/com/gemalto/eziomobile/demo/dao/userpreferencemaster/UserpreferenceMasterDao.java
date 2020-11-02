package com.gemalto.eziomobile.demo.dao.userpreferencemaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import com.gemalto.eziomobile.demo.model.UserPreferenceInfo;

@Repository
public interface UserpreferenceMasterDao extends CrudRepository<UserPreferenceInfo, Integer>, UserpreferenceMasterOperation{

	int countByStatusAndUserId(int status, int userId);
	
	void deleteUserPreferenceInfoByStatusAndUserId(int status, int userId);
	
	UserPreferenceInfo findUserPreferenceInfoByUserIdAndStatus(int userId, int status);
	
}
