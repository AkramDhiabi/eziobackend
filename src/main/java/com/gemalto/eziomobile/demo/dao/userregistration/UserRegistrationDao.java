package com.gemalto.eziomobile.demo.dao.userregistration;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.UserRegistrationInfo;

@Repository
public interface UserRegistrationDao extends CrudRepository<UserRegistrationInfo, Integer>, UserRegistrationOperation{

	int countByUsername(String username);
	
	UserRegistrationInfo findUserRegistrationInfoByUsernameAndActivationkey(String username, String activationKey);
	
	UserRegistrationInfo findUserRegistrationInfoByUsername(String username);

	long deleteByUsername(String username);
}
