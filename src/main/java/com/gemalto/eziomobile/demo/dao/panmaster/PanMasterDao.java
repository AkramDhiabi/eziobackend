package com.gemalto.eziomobile.demo.dao.panmaster;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.PanMasterInfo;

@Repository
public interface PanMasterDao extends CrudRepository<PanMasterInfo, Integer>, PanMasterOperation{

	int countByStatusAndUserId(int status, int userId);
	
	List<PanMasterInfo> findPanInfoByUserIdOrderByPanNoAsc(int uId);
	
	void deletePanInfoByStatusAndUserId(int status, int userId);
	
	PanMasterInfo findPanInfoByUserIdAndPanNo(int userId, String panNo);
	
	void deletePanInfoByUserIdAndPanNo(int userId, String panNo);
	
	int countByStatusAndUserIdAndPanNoAndPanType(int status, int userId, String panNo, int panType); 
}
