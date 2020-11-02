package com.gemalto.eziomobile.demo.dao.cardmanagementmaster;



import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.PanDCVListInfo;

@Repository
public interface DCVActivationDao extends CrudRepository<PanDCVListInfo, Integer>{
	
	PanDCVListInfo findPanDCVListInfoByPanNo(String panNo);
	
}
