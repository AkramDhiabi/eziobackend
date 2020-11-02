package com.gemalto.eziomobile.demo.dao.qrtokenmaster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.gemalto.eziomobile.demo.model.QRTokenMasterInfo;

@Repository
public interface QRTokenmasterDao extends CrudRepository<QRTokenMasterInfo, Integer>,QRTokenmasterOperations{

	int countByUserId(String userId);

	void deleteQRTokenMasterInfoByUserId(String userId);
	
}
