package com.gemalto.eziomobile.demo.dao.cardmanagementmaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.CardManagementInfo;

@Repository
public interface CardmanagementDao extends CrudRepository<CardManagementInfo, Integer>, CardmanagementOperation{
	
	int countByStatusAndUserId(int status, int userId);
	
	void deleteCardManagementInfoByStatusAndUserId(int status, int userId);
	
	CardManagementInfo findCardManagementInfoByUserIdAndPanNo(int userId, String panNo);
	
	void deleteCardManagementInfoByUserIdAndPanNo(int userId, String panNo);
	
	int countByUserIdAndPanNo(int userId, String panNo);
	
}
