package com.gemalto.eziomobile.demo.dao.atm;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.AtmAccessCodeInfo;

@Repository
public interface ATMAccessCodeDao extends CrudRepository<AtmAccessCodeInfo, Integer>, AtmAccessCodeOperations{
	
	int countByUserId(int userId);
	
	public void deleteAtmAccesscodeInfoByUserId(int userId);
	
	public AtmAccessCodeInfo findAtmAccessCodeInfoByUserIdAndAccesscode(int userId, String accesscode);
}
