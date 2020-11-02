package com.gemalto.eziomobile.demo.service.accountmaster;

import java.util.ArrayList;
import java.util.List;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.accountmaster.AccountmasterDao;
import com.gemalto.eziomobile.demo.dto.AccountMasterDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;
import com.gemalto.eziomobile.demo.util.CurrencyUtil;

@Service
public class AccountmasterServiceImpl implements AccountmasterService{

	private static final LoggerUtil logger = new LoggerUtil(AccountmasterServiceImpl.class.getClass());

	@Autowired
	AccountmasterDao accountDao;

	@Override
	public void saveUserAccountInfo(AccountMasterInfo accountMasterInfo) throws ServiceException {
		try {
			accountDao.save(accountMasterInfo);
		} catch (Exception e) {
			logger.info("Unable save account information!"+ e);
			throw new ServiceException(e);
		}
	}

	@Override
	public AccountMasterInfo findAccountByAccountNoAndUserIdAndStatus(String accountNo, int userId, int status)
			throws ServiceException {
		AccountMasterInfo accountMasterInfo = new AccountMasterInfo();
		try {
			accountMasterInfo = accountDao.findAccountByAccountNoAndUserIdAndStatus(accountNo, userId, status);
		} catch (Exception e) {
			logger.info("Unable to fetch account details with UserId: "+userId+", accountNo : "+accountNo+ CommonOperationsConstants.STATUS_LABEL +status+" "+ e);
			throw new ServiceException(e);
		}
		return accountMasterInfo;
	}

	@Override
	public List<AccountMasterInfo> getAccountByUserId(int userId) throws ServiceException {
		List<AccountMasterInfo> accountList = new ArrayList<AccountMasterInfo>();
		try {
			accountList = accountDao.getAccountByUserId(userId);
		} catch (Exception e) {
			logger.info("Unable to get accountList with UserId: "+userId+" "+e);
			throw new ServiceException(e);
		}
		return accountList;
	}

	@Override
	public AccountMasterInfo findAccountByAccountNo(String accountNo) throws ServiceException {
		AccountMasterInfo accountMasterInfo = new AccountMasterInfo();
		try {
			logger.info("accountNo : "+accountNo);
			accountMasterInfo = accountDao.findAccountByAccountNo(accountNo);
		} catch (Exception e) {
			logger.info("Unable to fetch account details with accountNo: "+accountNo+" "+e);
			throw new ServiceException(e);
		}
		return accountMasterInfo;
	}

	@Override
	public List<AccountMasterInfo> findAccountByTypeAndStatusAndUserIdOrderByAccountNameAsc(int type, int status,
			int userId) throws ServiceException {
		List<AccountMasterInfo> accountList = new ArrayList<AccountMasterInfo>();
		try {
			accountList = accountDao.findAccountByTypeAndStatusAndUserIdOrderByAccountNameAsc(type, status, userId);
		} catch (Exception e) {
			logger.info("Unable to fetch list of accounts with UserId: "+userId+", account-type : "+type+ CommonOperationsConstants.STATUS_LABEL +status+" "+ e);
			throw new ServiceException(e);
		}
		return accountList;
	}

	/* 
	 * 
	 */
	@Override
	public List<AccountMasterInfo> findAccountByStatusAndUserIdOrderByAccountNameAsc(int status, int userId)
			throws ServiceException {
		List<AccountMasterInfo> accountList = new ArrayList<AccountMasterInfo>();
		try {
			accountList = accountDao.findAccountByStatusAndUserIdOrderByAccountNameAsc(status, userId);
		} catch (Exception e) {
			logger.info(CommonOperationsConstants.UNABLE_TO_FETCH_LIST_OF_ACCOUNT_DETAILS_WITH_USER_ID_MESSAGE +userId+ CommonOperationsConstants.STATUS_LABEL +status+" "+ e);
			throw new ServiceException(e);
		}
		return accountList;
	}

	/* 
	 * 
	 */
	@Override
	public int findTypeByStatusAndAccountNoAndUserId(int status, String accountNo, int userId) throws ServiceException {

		int accountType = 0;
		try {
			accountType = accountDao.findTypeByStatusAndAccountNoAndUserId(status, accountNo, userId);
		} catch (Exception e) {
			logger.info("Unable to find account-type with UserId: "+userId+ CommonOperationsConstants.STATUS_LABEL +status+", accountNo: "+accountNo+" "+ e);
			throw new ServiceException(e);
		}
		return accountType;
	}

	/* 
	 * 
	 */
	@Override
	public int countByStatusAndUserId(int status, int userId) throws ServiceException {
		int count = 0;
		try {
			count = accountDao.countByStatusAndUserId(status, userId);
		} catch (Exception e) {
			logger.info("Unable to find account counts with UserId: "+userId+ CommonOperationsConstants.STATUS_LABEL +status+" "+ e);
			throw new ServiceException(e);
		}
		return count;
	}

	/* 
	 * 
	 */
	@Override
	public AccountMasterInfo findAccountByAccountNoAndStatus(int status, String accountNo) throws ServiceException {
		AccountMasterInfo accountMasterInfo = new AccountMasterInfo();
		try {
			accountMasterInfo = accountDao.findAccountByAccountNoAndStatus(status, accountNo);
		} catch (Exception e) {
			logger.info("Unable to find account detais with accountNo: "+accountNo+ CommonOperationsConstants.STATUS_LABEL +status+" "+ e);
			throw new ServiceException(e);
		}
		return accountMasterInfo;
	}

	/* Reference from old website
	 * queryString = " from accountmaster where type='1' and user_uid=(select uid from usermaster where userid='"+userid+"')" ;
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteAccountByTypeAndUserId(int type, int userId) throws ServiceException {
		//AccountMasterInfo accountMasterInfo = new AccountMasterInfo();
		
		try {
			 //accountMasterInfo.setType(type);
			//accountMasterInfo.setUserId(userId);
			accountDao.deleteAccountByTypeAndUserId(type, userId);
		} catch (Exception e) {
			logger.info("Unable to delete account with account-type: "+type+ CommonOperationsConstants.USER_ID_LABEL2 +userId+" "+ e);
			throw new ServiceException(e);
		}
		
	}

	/* 
	 * 
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public int updateSenderAccount(int amount, int status, String fromAccountNo, int userId) throws ServiceException{
		int count = 0;
		try {
			count = accountDao.updateSenderAccount(amount, status, fromAccountNo, userId);
		} catch (Exception e) {
			logger.info("Unable to update sender's (user) account balance with: "+amount+", for userId : "+userId+", fromAccountNo: "+fromAccountNo+" "+ e);
			throw new ServiceException(e);
		}
		return count;
	}

	/* 
	 * 
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public int updateReceiverAccount(int amount, int status, String toAccountNo, int userId) throws ServiceException{
		int count = 0;
		try {
			count = accountDao.updateReceiverAccount(amount, status, toAccountNo, userId);
		} catch (Exception e) {
			logger.info(CommonOperationsConstants.UNABLE_TO_UPDATE_RECEIVER_S_USER_ACCOUNT_BALANCE_WITH_MESSAFE +amount+", for userId : "+userId+", toAccountNo: "+toAccountNo+" "+ e);
			throw new ServiceException(e);
		}
		return count;
	}

	/* 
	 * 
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public int updateSenderAccount(int amount, int status, String fromAccountNo) throws ServiceException{
		int count = 0;
		try {
			count = accountDao.updateSenderAccount(amount, status, fromAccountNo);
		} catch (Exception e) {
			logger.info(CommonOperationsConstants.UNABLE_TO_UPDATE_RECEIVER_S_USER_ACCOUNT_BALANCE_WITH_MESSAFE +amount+", for fromAccountNo: "+fromAccountNo+" "+ e);
			throw new ServiceException(e);
		}
		return count;
	}

	/* 
	 * 
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public int updateReceiverAccount(int amount, int status, String toAccountNo) throws ServiceException{
		int count = 0;
		try {
			count = accountDao.updateReceiverAccount(amount, status, toAccountNo);
		} catch (Exception e) {
			logger.info(CommonOperationsConstants.UNABLE_TO_UPDATE_RECEIVER_S_USER_ACCOUNT_BALANCE_WITH_MESSAFE +amount+", for toAccountNo: "+toAccountNo+" "+ e);
			throw new ServiceException(e);
		}
		return count;
	}

	/* 
	 * 
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public int updateAccountWithDefaultData(int userId) throws ServiceException{
		int count = 0;
		try {
			count = accountDao.updateAccountWithDefaultData(userId);
		} catch (Exception e) {
			logger.info("Unable to update (user) account with default values for userId: "+userId+" "+ e);
			throw new ServiceException(e);
		}
		return count;
	}

	/* 
	 * 
	 */
	@Override
	public List<AccountMasterInfo> findAccountByUserIdAndAccountNameOrderByAccountNameAsc(int userId,
			String accountName) throws ServiceException {
		List<AccountMasterInfo> accountList = new ArrayList<AccountMasterInfo>();
		try {
			accountList = accountDao.findAccountByUserIdAndAccountNameOrderByAccountNameAsc(userId, accountName);
		} catch (Exception e) {
			logger.info(CommonOperationsConstants.UNABLE_TO_FETCH_LIST_OF_ACCOUNT_DETAILS_WITH_USER_ID_MESSAGE +userId+" and accountName : "+accountName+" "+ e);
			throw new ServiceException(e);
		}
		return accountList;
	}


	@Override
	public List<String> findAccountByUserIdNotEqualsToSavings(int uId) throws ServiceException {
		List<String> accountList = new ArrayList<>();
		try {
			accountList = accountDao.findAccountByUserIdNotEqualsToSavings(uId, EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
		} catch (Exception e) {
			logger.info(CommonOperationsConstants.UNABLE_TO_FETCH_LIST_OF_ACCOUNT_DETAILS_WITH_USER_ID_MESSAGE +uId, e);
			throw new ServiceException(e);
		}
		return accountList;
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public boolean deleteAccountByUserIdAndType(int userId, int type) throws ServiceException {
		int update = 0;
		boolean isClearBeneficiaryAcc = false;
		try {
			logger.info("userId in deleteBeneficiaryAccount: "+userId);
			update = accountDao.deleteUserAccountsByTypeAndUserId(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1, userId);
			if(update!=0){
				isClearBeneficiaryAcc = true;
				logger.info("beneficiary account detail is deleted successfully...");
			}
		} catch (Exception e) {
			logger.error("Unable to delete beneficiary account for userId: "+userId+" "+ e);
			throw new ServiceException(e);
		}
		return isClearBeneficiaryAcc;
	}

	@Override
	public boolean isPayeeAvailableByUserIdAndAccountNo(int userId, String accountNo) throws ServiceException {
		boolean isAvailable = false;
		try {
			isAvailable = accountDao.existsByUserIdAndAccountNo(userId, accountNo);
		} catch (Exception e) {
			logger.error("Unable to find beneficiary details for userId: "+userId+ CommonOperationsConstants.AND_PAYEE_ACCOUNT_NO_LABEL +accountNo ,  e);
			throw new ServiceException(e);
		}
		return isAvailable;
	}
	

	@Override
	public AccountMasterDTO findAccountByAccountNoAndUserId(String accountNo, int userId) throws ServiceException {
		AccountMasterDTO accountMasterDTO = new AccountMasterDTO();
		try {
			AccountMasterInfo accountMasterInfo = accountDao.findAccountByAccountNoAndUserId(accountNo, userId);
			
			accountMasterDTO.setUserId(accountMasterInfo.getUserId());
			accountMasterDTO.setAccountName(accountMasterInfo.getAccountName());
			accountMasterDTO.setAccountNo(accountMasterInfo.getAccountNo());
			accountMasterDTO.setAccountBalance(accountMasterInfo.getAccountBalance());
			accountMasterDTO.setType(accountMasterInfo.getType());
			accountMasterDTO.setFormatAccBalance(CurrencyUtil.formatCurrency(accountMasterInfo.getAccountBalance()));
			accountMasterDTO.setAccountRegistrationDate(ConvertDateToStringDate.convertDateToString(accountMasterInfo.getAccountRegistrationDate()));
		} catch (Exception e) {
			logger.error("Unable to find account details for userId: "+userId+ CommonOperationsConstants.AND_PAYEE_ACCOUNT_NO_LABEL +accountNo ,  e);
			throw new ServiceException(e);
		}
		return accountMasterDTO;
	}

	
	
	@Override
	public String findAccountNoByUserIdAndAccountNoNotEqualToFromAccountNo(int userId, String fromAccountNo) throws ServiceException {
		String accountNum = "";
		try {
			accountDao.findAccountNoByUserIdAndAccountNoNotEqualToFromAccountNo(userId, fromAccountNo);
		} catch (Exception e) {
			logger.error("Unable to find account no for userId: "+userId+ CommonOperationsConstants.AND_PAYEE_ACCOUNT_NO_LABEL +fromAccountNo ,  e);
			throw new ServiceException(e);
		}
		return accountNum;
	}

	
	@Override
	public String findAccountNoByUserIdNotEqualsToSavings(int uId) throws ServiceException {
		String accountNum = "";
		try {
			List<String> accountList = accountDao.findAccountByUserIdNotEqualsToSavings(uId, EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
			if(!accountList.isEmpty() && accountList.size()>0){
				accountNum = accountList.get(0);
				logger.info("[findAccountNoByUserIdNotEqualsToSavings] accountNum: "+accountNum);
			}
		} catch (Exception e) {
			logger.error("Unable to find account no for userId: "+uId,  e);
			throw new ServiceException(e);
		}
		return accountNum;
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void deleteAccountByAccountNumberAndUserId(String accountNumber, int uid) throws ServiceException {
		
		try {
			accountDao.deleteAccountByAccountNoAndUserId(accountNumber, uid);
			logger.info("delete account with account-number: "+accountNumber+ CommonOperationsConstants.USER_ID_LABEL2 +uid+" ");
		} catch (IllegalArgumentException e) {
			logger.info("Unable to delete account with account-number: "+accountNumber+ CommonOperationsConstants.USER_ID_LABEL2 +uid+" "+ e);
			throw new ServiceException(e);
		}	catch (Exception e) {
			logger.info("Unable to delete account with account-number: "+accountNumber+ CommonOperationsConstants.USER_ID_LABEL2 +uid+" "+ e);
			throw new ServiceException(e);
		}	
	}

	@Override
	public AccountMasterInfo findAccountByAccountNoAndUserIdAndType(String accountNumber, int uid,
			int type) throws ServiceException {
		AccountMasterInfo accountMasterInfo = new AccountMasterInfo();
		try {
			accountMasterInfo = accountDao.findAccountByAccountNoAndUserIdAndType(accountNumber, uid, type);
		} catch (Exception e) {
			logger.info("Unable to fetch account details with UserId: "+uid+", accountNo : "+accountNumber+", type : "+type+" "+ e);
			throw new ServiceException(e);
		}
		return accountMasterInfo;
	}

	@Override
	public void deleteAccountByAccountNumberList(List<AccountMasterInfo> accountMasterInfoList) throws ServiceException {
		try {	 
			accountDao.deleteAll(accountMasterInfoList);
		} catch (Exception e) {
			logger.info("Unable to delete accountMasterInfoList: "+accountMasterInfoList);
			throw new ServiceException(e);
		}
	}
	/* 
	 * 
	 */
	@Override
	public List<AccountMasterInfo> findAccountByUserIdAntTypeAndAccountNameOrderByAccountNameAsc(int userId, int type,
			String accountName) throws ServiceException {
		List<AccountMasterInfo> accountList = new ArrayList<AccountMasterInfo>();
		try {
			accountList = accountDao.findAccountByUserIdAndTypeAndAccountNameOrderByAccountNameAsc(userId, type, accountName);
		} catch (Exception e) {
			logger.info(CommonOperationsConstants.UNABLE_TO_FETCH_LIST_OF_ACCOUNT_DETAILS_WITH_USER_ID_MESSAGE +userId+" and accountName : "+accountName+" And Type : "+type, e);
			throw new ServiceException(e);
		}
		return accountList;

	}	

}
