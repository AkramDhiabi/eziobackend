package com.gemalto.eziomobile.demo.webhelper.transactionmanagement;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.AccountMasterDTO;
import com.gemalto.eziomobile.demo.dto.CardIssuanceDTO;
import com.gemalto.eziomobile.demo.dto.TransactionInfoDTO;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.AccountMasterInfo;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.cardissuance.CardIssuanceMasterService;
import com.gemalto.eziomobile.demo.service.panmaster.PanmasterService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;

@Component
public class TransactionManagementWebHelper {

	private static final LoggerUtil logger = new LoggerUtil(TransactionManagementWebHelper.class.getClass());

	@Autowired
	private PanmasterService panMasterService;
	
	@Autowired
	private TransactionmasterService transactionService;
	
	@Autowired
	private AccountmasterService accountMasterService;
	
	@Autowired
	private CardIssuanceMasterService cardIssuanceService;
	
	/** Method to prepare transactionInfo object
	 * to update transactionMaster table
	 * @param userId
	 * @param fromAccountNo
	 * @param toAccountNo
	 * @param debitAmount
	 * @param creditAmount
	 * @param description
	 * Status will always be 1
	 * @return
	 */
	public TransactionInfoDTO prepareTransactionDataObject(int userId, String fromAccountNo, String toAccountNo,
			int debitAmount, int creditAmount, String description){
		TransactionInfoDTO transactionInfo = new TransactionInfoDTO();
		
		transactionInfo.setUserId(userId);
		transactionInfo.setFromAccountNo(fromAccountNo);
		transactionInfo.setToAccountNo(toAccountNo);
		transactionInfo.setDebit(debitAmount);
		transactionInfo.setCredit(creditAmount);
		transactionInfo.setDescription(description);
		transactionInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
		
		return transactionInfo;
	}
	
	/** Method to prepare transactionInfo object
	 * to update transactionMaster table
	 * @param userId
	 * @param fromAccountNo
	 * @param toAccountNo
	 * @param debitAmount
	 * @param creditAmount
	 * @param description
	 * Status will always be 1
	 * and panNo
	 * @return
	 */
	public TransactionInfoDTO prepareTransactionDataObject(int userId, String fromAccountNo, String toAccountNo,
			int debitAmount, int creditAmount, String description, String panNo){
		TransactionInfoDTO transactionInfo = new TransactionInfoDTO();
		
		transactionInfo.setUserId(userId);
		transactionInfo.setFromAccountNo(fromAccountNo);
		transactionInfo.setToAccountNo(toAccountNo);
		transactionInfo.setDebit(debitAmount);
		transactionInfo.setCredit(creditAmount);
		transactionInfo.setDescription(description);
		transactionInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
		transactionInfo.setPanNo(panNo);
		logger.info("print here panNo again :"+ transactionInfo.getPanNo() );
		return transactionInfo;
	}
	
	/** Update DB for E-commerce operations
	 * @param userId
	 * @param panNo
	 * @param amount
	 * @return true/false
	 */
	public boolean updateDBForEcommereceOperations(int userId, String panNo, int amount, String operationType){
		
		boolean isDBUpdated = false;
		TransactionInfoDTO transactionInfo = new TransactionInfoDTO();
		
		logger.info("[E-commerece] operationType : "+operationType);
		try {
			String accountNo = panMasterService.findAccountNoByPanNoAndUserId(panNo, userId);
			logger.info("[E-commerece Transaction] accountNo : "+accountNo +" for PAN NO : "+panNo);
			
			logger.info("print panNo :"+ panNo);
			transactionInfo = prepareTransactionDataObject(userId, accountNo, EzioMobileDemoConstant.EZIO_SHOP, amount, 0, EzioMobileDemoConstant.EZIO_SHOP, panNo);
			logger.info(" --------- "+operationType+" --------- ");
			logger.info(EzioMobileDemoConstant.PREPARED_TRANSACTION_OBJECT_LABEL +transactionInfo.toString());
			logger.info("--------------------------------------");
			
			transactionService.saveTransaction(transactionInfo);
			int updateCount = accountMasterService.updateSenderAccount(amount, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, accountNo, userId);
			
			isDBUpdated = true;
			
			logger.info("------ Ezio Shop Payment is Done! --------");
			logger.info(CommonOperationsConstants.UPDATE_COUNT_LABEL + updateCount);
			logger.info("------------------------------------------");
		} catch (Exception e) {
			logger.error("Exception : E-Commerce - "+operationType+" , unable to update transaction data!");
			e.printStackTrace();
		}
		return isDBUpdated;
	}
	
	
	/** Update DB for Money Transfer operations
	 * @param uId
	 * @param fromAccountNo
	 * @param toAccountNo
	 * @param amount
	 * @param operationType
	 * @return
	 */
	public boolean updateDBForMoneyTransferOperations(int uId, String fromAccountNo, String toAccountNo, int amount, String operationType){
		
		boolean isDBUpdated = false;
		String beneficiaryAccName = "";
		String senderAccName = "";
		int updateCount = 0;
		
		TransactionInfoDTO transactionInfo = new TransactionInfoDTO();
		AccountMasterDTO accountMasterInfo = new AccountMasterDTO();
		
		logger.info("[Money Transfer] operationType : "+operationType);
		
		try {
			//Get Receiver account information
			accountMasterInfo = accountMasterService.findAccountByAccountNoAndUserId(toAccountNo, uId);
			beneficiaryAccName = accountMasterInfo.getAccountName();
			String description = "To "+beneficiaryAccName+" (...."+toAccountNo.substring(toAccountNo.length()-4)+")";
			
			//update transationMaster and accountMaster for sender
			//update transaction master with debit value for sender
			/*transactionInfo.setUserId(uId);
			transactionInfo.setFromAccountNo(fromAccountNo);
			transactionInfo.setToAccountNo(toAccountNo);
			transactionInfo.setDebit(amount);
			transactionInfo.setCredit(0);
			transactionInfo.setDescription(description);
			transactionInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);*/
			
			transactionInfo = prepareTransactionDataObject(uId, fromAccountNo, toAccountNo, amount, 0, description);
			logger.info("----------[For Sender] "+operationType+" ---------");
			logger.info(EzioMobileDemoConstant.PREPARED_TRANSACTION_OBJECT_LABEL +transactionInfo.toString());
			logger.info("--------------------------------------");
			
			transactionService.saveTransaction(transactionInfo);
			updateCount = accountMasterService.updateSenderAccount(amount, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, fromAccountNo, uId);
			
			logger.info("------ Sender's account has been updated --------");
			logger.info(CommonOperationsConstants.UPDATE_COUNT_LABEL +updateCount);
			logger.info("-------------------------------------------------");
			
			//Get Sender account information
			//update transaction master with credit value for receiver
			if(accountMasterInfo.getType() == 0){
				accountMasterInfo = accountMasterService.findAccountByAccountNoAndUserId(fromAccountNo, uId);
				senderAccName = accountMasterInfo.getAccountName();
				description = "From "+senderAccName+" (...."+fromAccountNo.substring(fromAccountNo.length()-4)+")";
				
				//update transationMaster and accountMaster for receiver
				//update transaction master with credit value for receiver
				/*transactionInfo.setUserId(uId);
				transactionInfo.setFromAccountNo(toAccountNo);
				transactionInfo.setToAccountNo(fromAccountNo);
				transactionInfo.setDebit(0);
				transactionInfo.setCredit(amount);
				transactionInfo.setDescription(description);
				transactionInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);*/
				
				transactionInfo = prepareTransactionDataObject(uId, toAccountNo, fromAccountNo, 0, amount, description);
				logger.info("----------[For Receiver] "+operationType+" ---------");
				logger.info(EzioMobileDemoConstant.PREPARED_TRANSACTION_OBJECT_LABEL +transactionInfo.toString());
				logger.info("--------------------------------------");
				
				transactionService.saveTransaction(transactionInfo);
			}
			
			updateCount = accountMasterService.updateReceiverAccount(amount, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, toAccountNo, uId);
			
			
			logger.info("------ Receiver's account has been updated --------");
			logger.info(CommonOperationsConstants.UPDATE_COUNT_LABEL +updateCount);
			logger.info("---------------------------------------------------");
			
			isDBUpdated = true;
			
		} catch (Exception e) {
			logger.error("Exception : MoneyTransfer - "+operationType+" , unable to update transaction data!");
			e.printStackTrace();
		}
		return isDBUpdated;
	}
	
	
	/** Update DB for Add Beneficiary operations
	 * @param uId
	 * @param payeeName
	 * @param payeeAccountNo
	 * @param operationType
	 * @return
	 */
	public boolean updateDBForAddBeneficiaryOperations(int uId, String payeeName, String payeeAccountNo, String operationType){
		
		boolean isDBUpdated = false;
		AccountMasterInfo accountMasterInfo = new AccountMasterInfo();
		
		logger.info("[Add Beneficiary] operationType : "+operationType);
		
		try {
			accountMasterInfo.setUserId(uId);
			accountMasterInfo.setAccountName(payeeName);
			accountMasterInfo.setAccountNo(payeeAccountNo);
			accountMasterInfo.setAccountBalance(0);
			accountMasterInfo.setType(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1);
			accountMasterInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
			accountMasterInfo.setAccountRegistrationDate(new Date());
			accountMasterService.saveUserAccountInfo(accountMasterInfo);
			
			isDBUpdated = true;
			
			logger.info("New beneficiary has been added!");
		} catch (Exception e) {
			logger.error("Exception : AddBeneficiary - "+operationType+" , unable to update AccountMaster data!");
			e.printStackTrace();
		}
		return isDBUpdated;
	}
	
	
/** Update DB for new card request operations, CardIssuanceMaster table
 * @param uId
 * @param panNo
 * @param cardCVV
 * @param expDate
 * @param operationType
 * @return
 */
public boolean updateDBForCardIssuanceOperations(int uId, String panNo, String cardCVV, String expDate, String operationType){
		
		boolean isDBUpdated = false;
		CardIssuanceDTO cardIssuanceDTO = new CardIssuanceDTO();
		
		logger.info("[Card Issuance] operationType : "+operationType);
		
		try {
			cardIssuanceDTO.setUserId(uId);
			cardIssuanceDTO.setPanNo(panNo);
			cardIssuanceDTO.setCardCVV(Integer.parseInt(cardCVV));
			cardIssuanceDTO.setExpDate(expDate);
			cardIssuanceDTO.setIsDCV_Active(EzioMobileDemoConstant.EZIO_STATUS_VALUE_0);
			cardIssuanceDTO.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);
			cardIssuanceDTO.setPanType(EzioMobileDemoConstant.PAN_TYPE_NEW_PAN);
			
			cardIssuanceService.saveNewCardRequest(cardIssuanceDTO);
			
			isDBUpdated = true;
			
			logger.info("New card request has been added!");
		} catch (Exception e) {
			logger.error("Exception : Card Issuance - "+operationType+" , unable to update CardIssuanceInfo data!");
			e.printStackTrace();
		}
		return isDBUpdated;
	}
	
	
	
	/** Update DB for P2P Money transfer operations
	 * @param uId
	 * @param userId
	 * @param beneficiaryUId
	 * @param beneficiaryUserId
	 * @param amount
	 * @param fromAccount
	 * @return
	 */
	public Map<String, Object> updateDBForP2PTransactionOperation(int uId, String userId,  int beneficiaryUId, String beneficiaryUserId, int amount, String fromAccount){
		
		String beneficiaryAccountNo = "";
		String description = "";
		
		boolean isDBUpdated = false;
		int updateCount = 0;
		
		Map<String, Object> p2pResultMap = new HashMap<>(); 
		TransactionInfoDTO transactionInfo = new TransactionInfoDTO();
		try {
			
				if (userId.equals(beneficiaryUserId)) {
					// Search beneficiary AccountNo other than from AccountNo
					// for stand alone case
					beneficiaryAccountNo = accountMasterService.findAccountNoByUserIdAndAccountNoNotEqualToFromAccountNo(uId, fromAccount);

				} else {
					// for normal case, transfer money to external payee
					beneficiaryAccountNo = accountMasterService.findAccountNoByUserIdNotEqualsToSavings(beneficiaryUId); 
				}

				//Update sender's DB
				description = "To " + beneficiaryUserId + " (...." + (beneficiaryAccountNo.substring(beneficiaryAccountNo.length() - 4)) + ")";
				
				/*transactionInfo.setUserId(uId);
				transactionInfo.setFromAccountNo(fromAccount);
				transactionInfo.setToAccountNo(description);
				transactionInfo.setDebit(amount);
				transactionInfo.setCredit(0);
				transactionInfo.setDescription(description);
				transactionInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);*/
				
				transactionInfo = prepareTransactionDataObject(uId, fromAccount, description, amount, 0, description);
				logger.info("----------[For Sender] - P2P Transfer ---------");
				logger.info(EzioMobileDemoConstant.PREPARED_TRANSACTION_OBJECT_LABEL +transactionInfo.toString());
				logger.info("--------------------------------------");
				
				transactionService.saveTransaction(transactionInfo);
				
				updateCount = accountMasterService.updateSenderAccount(amount, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, fromAccount, uId);
				logger.info("\n------ Sender's account has been updated --------");
				logger.info(CommonOperationsConstants.UPDATE_COUNT_LABEL +updateCount);
				logger.info("-------------------------------------------------\n");
				

				//Update receiver's DB
				description = "From " + userId + " (...." + (fromAccount.substring(fromAccount.length() - 4)) + ")";
				
				/*transactionInfo.setUserId(uId);
				transactionInfo.setFromAccountNo(beneficiaryAccountNo);
				transactionInfo.setToAccountNo(description);
				transactionInfo.setDebit(0);
				transactionInfo.setCredit(amount);
				transactionInfo.setDescription(description);
				transactionInfo.setStatus(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);*/
				
				transactionInfo = prepareTransactionDataObject(uId, beneficiaryAccountNo, description, 0, amount, description);
				logger.info("----------[For Receiver] - P2P Transfer ---------");
				logger.info(EzioMobileDemoConstant.PREPARED_TRANSACTION_OBJECT_LABEL +transactionInfo.toString());
				logger.info("--------------------------------------");
				
				transactionService.saveTransaction(transactionInfo);
				
				updateCount = accountMasterService.updateReceiverAccount(amount, EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, beneficiaryAccountNo, beneficiaryUId);
				logger.info("\n------ Receiver's account has been updated --------");
				logger.info(CommonOperationsConstants.UPDATE_COUNT_LABEL +updateCount);
				logger.info("-------------------------------------------------\n");
				
				isDBUpdated = true;
				
				p2pResultMap.put("isDBUpdated", isDBUpdated);
				p2pResultMap.put("beneficiaryAccountNo", beneficiaryAccountNo);
			
		} catch (Exception e) {
			logger.error("Exception : P2P Money transfer - lunable to update AccountMaster and TransactionMaster data!");
			e.printStackTrace();
		}
		return p2pResultMap;
	}
}
