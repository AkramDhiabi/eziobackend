package com.gemalto.eziomobile.demo.service.master;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.model.DeviceMasterInfo;
import com.gemalto.eziomobile.demo.model.GroupMasterInfo;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;

@Service
public interface MasterService {
	
	UserMasterInfo getUserByUserId(String userId) throws ServiceException;
	
	UserMasterInfo getUserByUId(int uId) throws ServiceException;
	
	List<AccountMasterInfo> findAccountByUserId(int userId) throws ServiceException;
	
	AccountMasterInfo getAccountMasterByAccountNo(String accountNo) throws ServiceException;
	
	DeviceMasterInfo getDeviceMasterByUserIdAndRegCode(int userId, String regCode) throws ServiceException;
	
	GroupMasterInfo getGroupByGroupId(int groupId) throws ServiceException;
	
	int createAccountMasterData(int uId) throws ServiceException;
	
	int createRiskPreferenceMasterData(int uId) throws ServiceException;
	
	int createUserPreferenceMasterData(int uId) throws ServiceException;
	
	int createPanMasterData(int uId) throws ServiceException;
	
	int createCardManagementMasterData(int uId) throws ServiceException;
	
	//Reset account
	void deleteDeviceMasterData(int uId) throws ServiceException;
	
	void resetAccountBalanceByUid(int uId) throws ServiceException;
	
	void deleteTransactionsDataByStatusAndUid(int status, int uId) throws ServiceException;
	
	void deleteAccountsByTypeAndUid(int type, int uId) throws ServiceException;
	
	void deleteSignDataByStatusAndUid(int status, int uId) throws ServiceException;
	
	void deleteUserPreferenceByStatusAndUid(int status, int uId) throws ServiceException;
	
	void deleteRiskPreferenceByStatusAndUid(int status, String uId) throws ServiceException;
	
	void deletePanMasterDataByStatusAndUid(int status, int uId) throws ServiceException;
	
	void deleteCardManagementDataByStatusAndUid(int status, int uId) throws ServiceException;
	
	void deleteAtmAccesscodeInfoByUserId(int userId) throws ServiceException;
	
	void deleteAtmQRCodeInfoByUserId(int uid) throws ServiceException;
	
	void deleteCardIssuanceInfoByUserId(int uId) throws ServiceException;
}
