package com.gemalto.eziomobile.demo.dao.accountmaster;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gemalto.eziomobile.demo.model.AccountMasterInfo;

@Repository
public interface AccountmasterDao extends CrudRepository<AccountMasterInfo, Integer>, AccountmasterOperations{
	
	List<AccountMasterInfo> getAccountByUserId(int userId);
	
	AccountMasterInfo findAccountByAccountNo(String accountNo);
	
	AccountMasterInfo findAccountByAccountNoAndUserIdAndStatus(String accountNo, int userId, int status);
	
	AccountMasterInfo findAccountByAccountNoAndUserId(String accountNo, int userId);
	
	List<AccountMasterInfo> findAccountByTypeAndStatusAndUserIdOrderByAccountNameAsc(int type, int status, int userId);
	
	List<AccountMasterInfo> findAccountByStatusAndUserIdOrderByAccountNameAsc(int status, int userId);
	
	List<AccountMasterInfo> findAccountByUserIdAndAccountNameOrderByAccountNameAsc(int userId, String accountName);
	
	List<AccountMasterInfo> findAccountByUserIdAndTypeAndAccountNameOrderByAccountNameAsc(int userId, int type, String accountName);
	
	int countByStatusAndUserId(int status, int userId);
	
	AccountMasterInfo findAccountByAccountNoAndStatus(int status, String accountNo);
	
	void deleteAccountByTypeAndUserId(int type, int userId);
	
	int deleteUserAccountsByTypeAndUserId(int type, int userId);
	
	boolean existsByUserIdAndAccountNo(int userId, String accountNo);

	void deleteAccountByAccountNoAndUserId(String accountNo, int uid);

	AccountMasterInfo findAccountByAccountNoAndUserIdAndType(String accountNumber, int uid, int type);
	
}
