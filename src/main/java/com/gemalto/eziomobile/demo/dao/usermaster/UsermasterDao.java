package com.gemalto.eziomobile.demo.dao.usermaster;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.UserMasterInfo;

@Repository
public interface UsermasterDao extends CrudRepository<UserMasterInfo, Integer>, UsermasterOperations{
	
	UserMasterInfo findUserInfoByUserId(String username);
	
	//@Query("SELECT u FROM UserMasterInfo u WHERE u.userId=:userId AND u.password=:password")
	//UserMasterInfo findUserByUserIdAndPassword(@Param("userId") String userId, @Param("password") String password);
	UserMasterInfo findUserByUserIdAndPassword(String userId, String password);

	UserMasterInfo findPasswordByUserId(String userId);

	List<UserMasterInfo> getUsersListByGroupId(int groupId);
	
	UserMasterInfo findUserRoleByUId(int uid);
	
	int countByStatusAndUId(int status, int uId);
	
	int countByUserId(String userId);
	
	String findEmailAddressByUId(int uId);
	
	UserMasterInfo findUserInfoByUId(int uId);
	
	List<UserMasterInfo> findUserInfoByEmailAddress(String emailAddress);
	
	boolean existsByEmailAddress(String emailAddress);

	String findPasswordByUId(int uId);
}
