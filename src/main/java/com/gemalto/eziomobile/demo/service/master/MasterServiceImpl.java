package com.gemalto.eziomobile.demo.service.master;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.accountmaster.AccountmasterDao;
import com.gemalto.eziomobile.demo.dao.atm.ATMAccessCodeDao;
import com.gemalto.eziomobile.demo.dao.atm.AtmQRCodeDao;
import com.gemalto.eziomobile.demo.dao.cardissuance.CardIssuanceMasterDao;
import com.gemalto.eziomobile.demo.dao.cardmanagementmaster.CardmanagementDao;
import com.gemalto.eziomobile.demo.dao.devicemaster.DevicemasterDao;
import com.gemalto.eziomobile.demo.dao.groupmaster.GroupmasterDao;
import com.gemalto.eziomobile.demo.dao.panmaster.PanMasterDao;
import com.gemalto.eziomobile.demo.dao.riskpreferencemaster.RiskpreferenceMasterDao;
import com.gemalto.eziomobile.demo.dao.signdatamaster.SigndatamasterDao;
import com.gemalto.eziomobile.demo.dao.transactionmaster.TransactionmasterDao;
import com.gemalto.eziomobile.demo.dao.usermaster.UsermasterDao;
import com.gemalto.eziomobile.demo.dao.userpreferencemaster.UserpreferenceMasterDao;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.model.CardManagementInfo;
import com.gemalto.eziomobile.demo.model.DeviceMasterInfo;
import com.gemalto.eziomobile.demo.model.GroupMasterInfo;
import com.gemalto.eziomobile.demo.model.PanMasterInfo;
import com.gemalto.eziomobile.demo.model.RiskPreferenceInfo;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;
import com.gemalto.eziomobile.demo.model.UserPreferenceInfo;

@Service
public class MasterServiceImpl implements MasterService {

    @Autowired
    UsermasterDao userDao;

    @Autowired
    DevicemasterDao deviceDao;

    @Autowired
    GroupmasterDao groupDao;

    @Autowired
    AccountmasterDao accountDao;

    @Autowired
    RiskpreferenceMasterDao riskPreferenceDao;

    @Autowired
    UserpreferenceMasterDao userPreferenceDao;

    @Autowired
    CardmanagementDao cardManagementDao;

    @Autowired
    PanMasterDao panMasterDao;

    @Autowired
    TransactionmasterDao transactionDao;

    @Autowired
    SigndatamasterDao signMasterDao;

    @Autowired
    AtmQRCodeDao atmQRCodeDao;

    @Autowired
    ATMAccessCodeDao atmAccessCodeDao;

    @Autowired
    CardIssuanceMasterDao cardIssuanceDao;

    private static final LoggerUtil logger = new LoggerUtil(MasterServiceImpl.class);

    @Override
    public UserMasterInfo getUserByUserId(String userId) throws ServiceException {
        UserMasterInfo userMasterInfo;
        try {
            userMasterInfo = userDao.findUserInfoByUserId(userId);
        } catch (Exception e) {
            logger.info("Unable to find userMasterInfo by userID : " + userId, e);
            throw new ServiceException(e);
        }
        return userMasterInfo;
    }

    @Override
    public UserMasterInfo getUserByUId(int uId) throws ServiceException {
        Optional<UserMasterInfo> userMasterInfo;
        try {
            userMasterInfo = userDao.findById(uId);
        } catch (Exception e) {
            logger.info("Unable to find userMasterInfo by uId : " + uId, e);
            throw new ServiceException(e);
        }
        return userMasterInfo.get();
    }

    @Override
    public List<AccountMasterInfo> findAccountByUserId(int userId) throws ServiceException {
        List<AccountMasterInfo> accountsList;
        try {
            accountsList = accountDao.getAccountByUserId(userId);
        } catch (Exception e) {
            logger.info("Unable to find Accounts List by userId : " + userId, e);
            throw new ServiceException(e);
        }
        return accountsList;
    }

    @Override
    public AccountMasterInfo getAccountMasterByAccountNo(String accountNo) throws ServiceException {
        AccountMasterInfo accountMasterInfo;
        try {
            accountMasterInfo = accountDao.findAccountByAccountNo(accountNo);
        } catch (Exception e) {
            logger.info("Unable to find AccountInfo by accountNo : " + accountNo, e);
            throw new ServiceException(e);
        }
        return accountMasterInfo;
    }

    @Override
    public DeviceMasterInfo getDeviceMasterByUserIdAndRegCode(int userId, String regCode) throws ServiceException {
        DeviceMasterInfo deviceMasterInfo;
        try {
            deviceMasterInfo = deviceDao.findDeviceByUserIdAndRegCode(userId, regCode);
        } catch (Exception e) {
            logger.info("Unable to find DeviceInfo by userID and RegCode : " + userId+", "+regCode, e);
            throw new ServiceException(e);
        }
        return deviceMasterInfo;
    }

    @Override
    public GroupMasterInfo getGroupByGroupId(int groupId) throws ServiceException {
        Optional<GroupMasterInfo> groupMasterInfo;
        try {
            groupMasterInfo = groupDao.findById(groupId);
        } catch (Exception e) {
            logger.info("Unable to find GroupInfo by groupId : " + groupId, e);
            throw new ServiceException(e);
        }
        return groupMasterInfo.get();
    }

    @Override
    public int createAccountMasterData(int uId) throws ServiceException {
        int updateCount = 0;
        try {
            int count = accountDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            logger.info("Account count : "+count);
            if(count == 0){
                logger.info("Creating accounts.....");
                AccountMasterInfo account1Data = new AccountMasterInfo();
                AccountMasterInfo account2Data = new AccountMasterInfo();
                AccountMasterInfo account3Data = new AccountMasterInfo();
                long accno1 = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                long accno2 = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
                long accno3 = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;

                account1Data.setUserId(uId);
                account1Data.setAccountBalance(5000);
                account1Data.setType(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0);
                account1Data.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
                account1Data.setAccountNo(String.valueOf(accno1));
                account1Data.setAccountName("Pay Credit Card");
                account1Data.setAccountRegistrationDate(new Date());

                account2Data.setUserId(uId);
                account2Data.setAccountBalance(5000);
                account2Data.setType(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0);
                account2Data.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
                account2Data.setAccountNo(String.valueOf(accno2));
                account2Data.setAccountName("Pay Credit Card");
                account2Data.setAccountRegistrationDate(new Date());

                account3Data.setUserId(uId);
                account3Data.setAccountBalance(5000);
                account3Data.setType(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0);
                account3Data.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
                account3Data.setAccountNo(String.valueOf(accno3));
                account3Data.setAccountName("Savings");
                account3Data.setAccountRegistrationDate(new Date());

                List<AccountMasterInfo> saveAccountDataList = new ArrayList<>();
                saveAccountDataList.add(account1Data);
                saveAccountDataList.add(account2Data);
                saveAccountDataList.add(account3Data);

                List<AccountMasterInfo> savedList = (List<AccountMasterInfo>) accountDao.saveAll(saveAccountDataList);
                logger.info("savedList of Accounts : "+savedList.toString());

                updateCount++;
            }
        } catch (Exception e) {
            logger.info("Unable to create accounts data  for userID: " + uId, e);
            throw new ServiceException(e);
        }
        return updateCount;
    }


    @Override
    public int createRiskPreferenceMasterData(int uId) throws ServiceException {
        int count;
        int updateCount = 0;
        try {
            count = riskPreferenceDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, String.valueOf(uId));
            logger.info("RiskPreference count : "+count);
            if(count == 0){
                RiskPreferenceInfo riskPreferenceInfo = new RiskPreferenceInfo();

                riskPreferenceInfo.setUserId(String.valueOf(uId));
                riskPreferenceInfo.setSecurityMode(2);
                riskPreferenceInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
                riskPreferenceInfo.setSecurityRiskManagement("01");
                riskPreferenceInfo.setAddBeneficiaryThreshold("50");
                riskPreferenceInfo.setOwnAccThreshold("50");
                riskPreferenceInfo.setOtherAccThreshold("50");
                riskPreferenceInfo.setUpdatedDate(new Date());

                riskPreferenceDao.save(riskPreferenceInfo);
                logger.info("RiskPreference data has been initialized!");

                updateCount++;
            }
        } catch (Exception e) {
            logger.info("Unable to create RiskPreference data  for userID: " + uId, e);
            throw new ServiceException(e);
        }
        return updateCount;

    }

    @Override
    public int createUserPreferenceMasterData(int uId) throws ServiceException {
        int updateCount = 0;
        int count;
        try {
            count = userPreferenceDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            logger.info("userPreference COUNT : "+count);
            if(count == 0){
                UserPreferenceInfo userPreferenceInfo = new UserPreferenceInfo();

                userPreferenceInfo.setUserId(uId);
                userPreferenceInfo.setSec_mode(1);
                userPreferenceInfo.setSec_login("00");
                userPreferenceInfo.setSec_txownacc("02");
                userPreferenceInfo.setSec_txother("02");
                userPreferenceInfo.setSec_addpayee("02");
                userPreferenceInfo.setSec_ecommerce3ds("02");
                userPreferenceInfo.setP2p_notification("02");
                userPreferenceInfo.setWeb_notification("01");
                userPreferenceInfo.setMobile_banking("02");
                userPreferenceInfo.setStatus(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1);
                userPreferenceInfo.setUpdated_date(new Date());
                userPreferenceDao.save(userPreferenceInfo);
                logger.info("UserPreference data has been initialized!");

                updateCount++;
            }
        } catch (Exception e) {
            logger.info("Unable to create UserPreference data  for userID: " + uId, e);
            throw new ServiceException(e);
        }
        return updateCount;

    }

    @Override
    public int createPanMasterData(int uId) throws ServiceException {
        int updateCount = 0;
        int count;
        String accountNo1;
        String accountNo2;
        try {
            List<String> accountList = accountDao.findAccountByUserIdNotEqualsToSavings(uId, EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_0, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);


            count = panMasterDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            logger.info("Pan COUNT : "+count);

            if(!accountList.isEmpty() && count == 0){

                PanMasterInfo panMasterInfo1 = new PanMasterInfo();
                PanMasterInfo panMasterInfo2 = new PanMasterInfo();

                List<PanMasterInfo> panInfoList = new ArrayList<>();

                long panno1 = (long) Math.floor(Math.random() * 900000000000000L) + 100000000000000L;
                long panno2 = (long) Math.floor(Math.random() * 900000000000000L) + 100000000000000L;

                String panno1Str = "4"+String.valueOf(panno1);
                String panno2Str = "5"+String.valueOf(panno2);

                String expDatePan1 = "07/30";
                String expDatePan2 = "07/30";

                String cvv1 = String.valueOf((int) Math.floor(Math.random() * 900) + 100);
                String cvv2 = String.valueOf((int) Math.floor(Math.random() * 900) + 100);

                logger.info("Creating Pan.......");
                logger.info("Creating Pan account data : "+accountList.toString());

                accountNo1 = accountList.get(0);
                accountNo2 = accountList.get(1);


                panMasterInfo1.setUserId(uId);
                panMasterInfo1.setAccountNo(accountNo1);
                panMasterInfo1.setPanNo(panno1Str);
                panMasterInfo1.setExpDate(expDatePan1);
                panMasterInfo1.setCardCVV(Integer.parseInt(cvv1));
                panMasterInfo1.setTokenId("DEPRECATED");
                panMasterInfo1.setIsDCV_Active(0);
                panMasterInfo1.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
                panMasterInfo1.setPanType(EzioMobileDemoConstant.PAN_TYPE_GENERIC_PAN);
                panMasterInfo1.setRegistrationDate(new Date());

                panMasterInfo2.setUserId(uId);
                panMasterInfo2.setAccountNo(accountNo2);
                panMasterInfo2.setPanNo(panno2Str);
                panMasterInfo2.setExpDate(expDatePan2);
                panMasterInfo2.setCardCVV(Integer.parseInt(cvv2));
                panMasterInfo2.setTokenId("DEPRECATED");
                panMasterInfo2.setIsDCV_Active(0);
                panMasterInfo2.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
                panMasterInfo2.setPanType(EzioMobileDemoConstant.PAN_TYPE_GENERIC_PAN);
                panMasterInfo2.setRegistrationDate(new Date());

                panInfoList.add(panMasterInfo1);
                panInfoList.add(panMasterInfo2);

                panMasterDao.saveAll(panInfoList);

                updateCount++;

            }
        } catch (Exception e) {
            logger.info("Unable to create PanMaster data  for userID: " + uId, e);
            throw new ServiceException(e);
        }
        return updateCount;
    }

    @Override
    public int createCardManagementMasterData(int uId) throws ServiceException {
        int updateCount = 0;
        try {
            List<PanMasterInfo> panList;

            int count = cardManagementDao.countByStatusAndUserId(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            logger.info("CardManagement COUNT : "+count);
            if(count == 0){
                panList = panMasterDao.findPanInfoByUserIdOrderByPanNoAsc(uId);

                logger.info("[CardManagmentMaster] panList size : "+panList.size());
                logger.info("[CardManagmentMaster] panList : "+panList.toString());

                if(!panList.isEmpty()){
                    CardManagementInfo cardManagementInfoForPanOne = new CardManagementInfo();
                    CardManagementInfo cardManagementInfoForPanTwo = new CardManagementInfo();

                    List<CardManagementInfo> cardManagementList = new ArrayList<>();

                    cardManagementInfoForPanOne.setUserId(uId);
                    cardManagementInfoForPanOne.setPanNo(panList.get(0).getPanNo());
                    cardManagementInfoForPanOne.setCardStatus("ON");
                    cardManagementInfoForPanOne.setInternationalTravel("ON");
                    cardManagementInfoForPanOne.setOnlineTransaction("ON");
                    cardManagementInfoForPanOne.setSpendLimitTransactionStatus("OFF");
                    cardManagementInfoForPanOne.setAmountLimitPerTransaction(0);
                    cardManagementInfoForPanOne.setSpendLimitMonthStatus("OFF");
                    cardManagementInfoForPanOne.setAmountLimitPerMonth(0);
                    cardManagementInfoForPanOne.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);

                    cardManagementInfoForPanTwo.setUserId(uId);
                    cardManagementInfoForPanTwo.setPanNo(panList.get(1).getPanNo());
                    cardManagementInfoForPanTwo.setCardStatus("ON");
                    cardManagementInfoForPanTwo.setInternationalTravel("ON");
                    cardManagementInfoForPanTwo.setOnlineTransaction("ON");
                    cardManagementInfoForPanTwo.setSpendLimitTransactionStatus("OFF");
                    cardManagementInfoForPanTwo.setAmountLimitPerTransaction(0);
                    cardManagementInfoForPanTwo.setSpendLimitMonthStatus("OFF");
                    cardManagementInfoForPanTwo.setAmountLimitPerMonth(0);
                    cardManagementInfoForPanTwo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);

                    cardManagementList.add(cardManagementInfoForPanOne);
                    cardManagementList.add(cardManagementInfoForPanTwo);

                    cardManagementDao.saveAll(cardManagementList);

                    updateCount++;
                }

            }

        } catch (Exception e) {
            logger.info("Unable to create CardManagementMaster data  for userID: " + uId, e);
            throw new ServiceException(e);
        }
        return updateCount;

    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteDeviceMasterData(int uId) throws ServiceException {
        try {
            logger.info("Device data deleting.....");
            deviceDao.deleteDeviceInfoByUserId(uId);
            logger.info("Device data has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete DeviceInfo data for userID: " + uId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void resetAccountBalanceByUid(int uId) throws ServiceException {
        try {
            logger.info("updating accountInfo with default data.....");
            accountDao.updateAccountWithDefaultData(uId);
            logger.info("Account data has been updated for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to update user accounts to default data! userID: " + uId, e);
            throw new ServiceException(e);
        }

    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteTransactionsDataByStatusAndUid(int status, int uId) throws ServiceException {
        try {
            logger.info("Trnasaction data deleting.....");
            transactionDao.deleteTransactionInfoByStatusAndUserId(status, uId);
            logger.info("Trnasaction data has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete TransactionsInfo for userID: " + uId, e);
            throw new ServiceException(e);
        }

    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteAccountsByTypeAndUid(int type, int uId) throws ServiceException {
        try {
            logger.info("Account with accountType = 1, deleting.....");
            accountDao.deleteAccountByTypeAndUserId(type, uId);
            logger.info("Account data has been deleted for accountType = 1 and Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete User Accounts by Type and UserId for userID: " + uId, e);
            throw new ServiceException(e);
        }

    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteSignDataByStatusAndUid(int status, int uId) throws ServiceException {
        try {
            logger.info("Signdata deleting.....");
            signMasterDao.deleteSigndataByStatusAndUserId(status, uId);
            logger.info("Signdata has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete SigndataInfo for userID: " + uId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteUserPreferenceByStatusAndUid(int status, int uId) throws ServiceException {
        try {
            logger.info("UserPreference settings deleting.....");
            userPreferenceDao.deleteUserPreferenceInfoByStatusAndUserId(status, uId);
            logger.info("UserPreference settings has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete UserPreferenceInfo for userID: " + uId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteRiskPreferenceByStatusAndUid(int status, String uId) throws ServiceException {
        try {
            logger.info("RiskPreference settings deleting.....");
            riskPreferenceDao.deleteRiskPreferenceInfoByStatusAndUserId(status, uId);
            logger.info("RiskPreference settings has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete RiskPreferenceInfo for userID: " + uId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deletePanMasterDataByStatusAndUid(int status, int uId) throws ServiceException {
        try {
            logger.info("PanInfo data deleting.....");
            panMasterDao.deletePanInfoByStatusAndUserId(status, uId);
            logger.info("PanInfo has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete PanInfo for userID: " + uId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteCardManagementDataByStatusAndUid(int status, int uId) throws ServiceException {
        try {
            logger.info("CardManagement data deleting.....");
            cardManagementDao.deleteCardManagementInfoByStatusAndUserId(status, uId);
            logger.info("CardManagement data has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete CardManagementInfo for userID: " + uId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteAtmAccesscodeInfoByUserId(int userId) throws ServiceException{
        try {
            logger.info("AtmAccessCode data deleting.....");
            atmAccessCodeDao.deleteAtmAccesscodeInfoByUserId(userId);
            logger.info("AtmAccessCode data has been deleted for Uid : "+userId);
        } catch (Exception e) {
            logger.info("Unable to delete AtmAccessCode for userID: " + userId, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteAtmQRCodeInfoByUserId(int uid) throws ServiceException{
        try {
            logger.info("AtmQRCode data deleting.....");
            atmQRCodeDao.deleteAtmQRCodeInfoByUserId(uid);
            logger.info("AtmQRCode data has been deleted for Uid : "+uid);
        } catch (Exception e) {
            logger.info("Unable to delete AtmQRCode for userID: " + uid, e);
            throw new ServiceException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void deleteCardIssuanceInfoByUserId(int uId) throws ServiceException {
        try {
            logger.info("CardIssuance data deleting.....");
            cardIssuanceDao.deleteCardIssuanceInfoByUserId(uId);
            logger.info("CardIssuance data has been deleted for Uid : "+uId);
        } catch (Exception e) {
            logger.info("Unable to delete CardIssuance data for userID: " + uId, e);
            throw new ServiceException(e);
        }
    }

}
