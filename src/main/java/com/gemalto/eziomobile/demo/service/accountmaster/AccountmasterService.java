package com.gemalto.eziomobile.demo.service.accountmaster;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.dto.AccountMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;

@Service
public interface AccountmasterService {
	
	void saveUserAccountInfo(AccountMasterInfo accountMasterInfo) throws ServiceException;
	
	AccountMasterInfo findAccountByAccountNoAndUserIdAndStatus(String accountNo, int userId, int status) throws ServiceException;

	AccountMasterDTO findAccountByAccountNoAndUserId(String accountNo, int userId) throws ServiceException;
	
	List<AccountMasterInfo> getAccountByUserId(int userId) throws ServiceException;
		
	AccountMasterInfo findAccountByAccountNo(String accountNo) throws ServiceException;
	
	List<AccountMasterInfo> findAccountByTypeAndStatusAndUserIdOrderByAccountNameAsc(int type, int status, int userId) throws ServiceException;
	
	List<AccountMasterInfo> findAccountByStatusAndUserIdOrderByAccountNameAsc(int status, int userId) throws ServiceException;
	
	List<AccountMasterInfo> findAccountByUserIdAndAccountNameOrderByAccountNameAsc(int userId, String accountName) throws ServiceException;
	
	int findTypeByStatusAndAccountNoAndUserId(int status, String accountNo, int userId) throws ServiceException;
	
	int countByStatusAndUserId(int status, int userId) throws ServiceException;
	
	AccountMasterInfo findAccountByAccountNoAndStatus(int status, String accountNo) throws ServiceException;
	
	void deleteAccountByTypeAndUserId(int type, int userId) throws ServiceException;
	
	int updateSenderAccount(int amount, int status, String fromAccountNo, int userId) throws ServiceException;
	
	int updateReceiverAccount(int amount, int status, String toAccountNo, int userId) throws ServiceException;
	
	int updateSenderAccount(int amount, int status, String fromAccountNo) throws ServiceException;
	
	int updateReceiverAccount(int amount, int status, String toAccountNo) throws ServiceException;
	
	int updateAccountWithDefaultData(int userId) throws ServiceException;
	
	List<String> findAccountByUserIdNotEqualsToSavings(int uId) throws ServiceException;
	
	boolean deleteAccountByUserIdAndType(int userId, int type) throws ServiceException;
	
	boolean isPayeeAvailableByUserIdAndAccountNo(int userId, String accountNo) throws ServiceException;
	
	public String findAccountNoByUserIdAndAccountNoNotEqualToFromAccountNo(int userId, String fromAccountNo) throws ServiceException;
	
	String findAccountNoByUserIdNotEqualsToSavings(int uId) throws ServiceException;

	void deleteAccountByAccountNumberAndUserId(String accountNumber, int uid) throws ServiceException;

	AccountMasterInfo findAccountByAccountNoAndUserIdAndType(String accountNumber, int uid,
			int type) throws ServiceException;

	void deleteAccountByAccountNumberList(List<AccountMasterInfo> accountMasterInfoList) throws ServiceException;

	
	List<AccountMasterInfo> findAccountByUserIdAntTypeAndAccountNameOrderByAccountNameAsc(int userId, int type, String accountName) throws ServiceException;

}
