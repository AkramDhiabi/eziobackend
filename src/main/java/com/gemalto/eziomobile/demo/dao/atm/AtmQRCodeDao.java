package com.gemalto.eziomobile.demo.dao.atm;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.AtmQRCodeInfo;

@Repository
public interface AtmQRCodeDao extends CrudRepository<AtmQRCodeInfo, Integer>, AtmQRCodeOperations{

	AtmQRCodeInfo findAtmQRCodeStatusByUserIdAndAtmId(int uid, String atmId);

	void deleteAtmQRCodeInfoByUserId(int uid);

	int countByUserId(int userId);
	
	AtmQRCodeInfo findAtmQRCodeInfoByUserIdAndAtmIdAndChallenge(int userId, String atmId, String Challenge);

}
