package com.gemalto.eziomobile.demo.webhelper.onlinebankingtransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.BankingValidationTransactionParametersDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.webhelper.transactionmanagement.TransactionManagementWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.OPERATION_TYPE_MESSAGE;

@Component
public class OnlineBankingTransactionWebHelper {

	private static final LoggerUtil logger = new LoggerUtil(OnlineBankingTransactionWebHelper.class.getClass());

	@Autowired
	private UsermasterService usermasterService;

	@Autowired
	private TransactionManagementWebHelper transactionWebHelper;

	/**
	 * @param userId
	 * @param bankingValidationDTO
	 * @return
	 */
	public boolean updateDataBaseBasedOnOperationType(String userId, BankingValidationTransactionParametersDTO bankingValidationDTO){

		boolean isDBUpdated = false;
		String amount = "";
		
		logger.info("[updateDataBaseBasedOnOperationType] bankingValidationDTO : "+bankingValidationDTO.toString());
		
		try {
			
			String operationType = bankingValidationDTO.getTransactionType();
			
			int uId = usermasterService.findUidByUserId(userId);
			
			switch (bankingValidationDTO.getTransactionType()) {
			
			case EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY:
				
				logger.info(OPERATION_TYPE_MESSAGE +EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY);
				String payeeName = bankingValidationDTO.getBeneficiaryName();
				String payeeAccount = bankingValidationDTO.getBeneficiaryAccount();
				
				isDBUpdated = transactionWebHelper.updateDBForAddBeneficiaryOperations(uId, payeeName, payeeAccount, operationType);
				logger.info("isBeneficiaryAdded : "+isDBUpdated);
				logger.info("---------------------------------------------------");
				
				break;
				
			case  EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER:
				
				logger.info(OPERATION_TYPE_MESSAGE +EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER);
				String fromAccountNo = bankingValidationDTO.getFromAccountNo();
				String toAccountNo = bankingValidationDTO.getToAccountNo();
				amount  = bankingValidationDTO.getAmount();
				
				isDBUpdated = transactionWebHelper.updateDBForMoneyTransferOperations(uId, fromAccountNo, toAccountNo, Integer.parseInt(amount), operationType);
				logger.info("isMoneyTransferDone : "+isDBUpdated);
				logger.info("---------------------------------------------------");
				
				break;
				
			case EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS:
				
				logger.info(OPERATION_TYPE_MESSAGE +EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS);
				
				String panNo = bankingValidationDTO.getCardNumber();
				amount = bankingValidationDTO.getAmount();
				
				isDBUpdated = transactionWebHelper.updateDBForEcommereceOperations(uId, panNo, Integer.parseInt(amount), EzioMobileDemoConstant.OPERATION_TYPE_ECOMMERCE3DS);
				logger.info("isPaymentDone : "+isDBUpdated);
				
				break;

			default:
				break;
			}
		}
		catch (ServiceException e) {
			logger.error(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
			e.printStackTrace();
		}
		return isDBUpdated;
	}
}
