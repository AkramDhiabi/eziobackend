package com.gemalto.eziomobile.demo.controller.ecommerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.BankingValidationTransactionParametersDTO;
import com.gemalto.eziomobile.demo.dto.CardManagementDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.cardmanagement.CardManagementService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.webhelper.ecommerce.EcommerceWebHelper;
import com.gemalto.eziomobile.demo.webhelper.transactionmanagement.TransactionManagementWebHelper;

@RestController
public class EcommerceController {
	
	private static final LoggerUtil logger = new LoggerUtil(EcommerceController.class.getClass());
	
	@Autowired
	private CardManagementService cardManagementService;
	
	@Autowired
	private UsermasterService userMasterService;
	
	@Autowired
	private EcommerceWebHelper eCommerceWebHelper;
	
	@Autowired
	private TransactionManagementWebHelper transactionWebHelper;
	
	
	/** Dedicated to Purchase goods with DCV
	 * @param bankingTransactionDTO
	 * @return
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/validatetransaction.ecommerce.action", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus valdiateEcommerceTransaction(@RequestBody BankingValidationTransactionParametersDTO bankingTransactionDTO) throws ControllerException{
		
		String operationType = "";
		boolean isDCValidated = false;
		
		logger.info("Ecommerece request body : "+bankingTransactionDTO.toString());
		
		ResultStatus resultStatus = new ResultStatus();
		CardManagementDTO cardManagementDTO = new CardManagementDTO();
		
		try {
			if(!(bankingTransactionDTO.getUserId()!= null && !bankingTransactionDTO.getUserId().equals("") && bankingTransactionDTO.getCardNumber()!= null)){
				return resultStatus;
			}

			operationType = bankingTransactionDTO.getTransactionType();

			int uId = 0;
			uId = userMasterService.findUidByUserId(bankingTransactionDTO.getUserId());

			String panNo =  bankingTransactionDTO.getCardNumber();
			logger.info("[valdiateEcommerceTransaction] pan number : " +panNo);

			int amount = Integer.parseInt(bankingTransactionDTO.getAmount());

			cardManagementDTO = cardManagementService.findCardManagementInfoByUserIdAndPanNo(uId, panNo);
			logger.info("\n [DCV] cardManagementDTO : "+cardManagementDTO.toString());

			resultStatus = setResultSet(bankingTransactionDTO, operationType, cardManagementDTO, uId, panNo, amount);

		} catch (Exception e) {
			logger.error("Exception : DCV payment validation failed!");
			throw new ControllerException(e);
		}
		return resultStatus;
	}

	/**
	 *
	 * @param bankingTransactionDTO
	 * @param operationType
	 * @param cardManagementDTO
	 * @param uId
	 * @param panNo
	 * @param amount
	 * @return
	 */
	private ResultStatus setResultSet(@RequestBody BankingValidationTransactionParametersDTO bankingTransactionDTO, String operationType, CardManagementDTO cardManagementDTO, int uId, String panNo, int amount) {
		boolean isDCValidated;
		ResultStatus resultStatus = new ResultStatus();
		if (cardManagementDTO.getPanNo() != null && ((cardManagementDTO.getCardStatus().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_ON))
				&& cardManagementDTO.getOnlineTransaction().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_ON)))
		{
			logger.info("Stage one............1");
			if ( ((cardManagementDTO.getSpendLimitTransactionStatus().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_OFF))||
					((cardManagementDTO.getSpendLimitTransactionStatus().equalsIgnoreCase(EzioMobileDemoConstant.CARD_MANAGEMENT_ON)
							&& cardManagementDTO.getAmountLimitPerTransaction() >= amount))) ) {

				logger.info("Stage two............2");

				isDCValidated = eCommerceWebHelper.validateDCVTransaction(bankingTransactionDTO.getUserId(), panNo, bankingTransactionDTO.getCvv());
				logger.info("isDCValidated : "+isDCValidated);

				if(isDCValidated){
					boolean flag = transactionWebHelper.updateDBForEcommereceOperations(uId, panNo, amount, operationType);
					logger.info("isPaymentDone : "+flag);

					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
					resultStatus.setStatusCode(HttpStatus.OK);

				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_FAILURE);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
					resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				}
			}else{
				logger.info("Stage two validation failed............2");
				resultStatus.setMessage("Stage two validation failed");
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
				resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
				resultStatus.setTemplateObject(cardManagementDTO);
				resultStatus.setTempObject(bankingTransactionDTO);
			}
		}else{
			logger.info("Stage one validation failed............1");
			resultStatus.setMessage("Stage one validation failed");
			resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_BAD_REQUEST_400);
			resultStatus.setStatusCode(HttpStatus.BAD_REQUEST);
			resultStatus.setTemplateObject(cardManagementDTO);
			resultStatus.setTempObject(bankingTransactionDTO);
		}
		return resultStatus;
	}


	/**
	 * @return 
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus ecommerceErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
	
	
}
