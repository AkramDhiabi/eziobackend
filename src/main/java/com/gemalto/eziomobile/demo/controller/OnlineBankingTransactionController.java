package com.gemalto.eziomobile.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.BankingValidationTransactionParametersDTO;
import com.gemalto.eziomobile.demo.dto.ResetUserTransactionDTO;
import com.gemalto.eziomobile.demo.exception.ControllerException;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.ResultStatus;
import com.gemalto.eziomobile.demo.service.accountmaster.AccountmasterService;
import com.gemalto.eziomobile.demo.service.qrtokenmaster.QRTokenmasterService;
import com.gemalto.eziomobile.demo.service.transactionmaster.TransactionmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.common.CommonWebHelper;
import com.gemalto.eziomobile.demo.webhelper.onlinebankingtransaction.OnlineBankingTransactionWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.B_USE_CHALLENGE_LABEL;
import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.TOKEN_TYPE_GATZ_OR_GATB_S_DATA_TO_SIGN_HEXA_LABEL;

@RestController
public class OnlineBankingTransactionController {

	private static final LoggerUtil logger = new LoggerUtil(OnlineBankingTransactionController.class.getClass());

	@Autowired
	private CommonWebHelper commonWebHelper;
	
	@Autowired
	private CASWebHelper casWebHelper;
	
	@Autowired
	private AccountmasterService accountmasterService;
	
	@Autowired
	private UsermasterService usermasterService;
	
	@Autowired
	private TransactionmasterService transactionmasterService;
	
	@Autowired
	private OnlineBankingTransactionWebHelper onlineBankingWebHelper;
	
	@Autowired
	QRTokenmasterService qrTokenmasterService;
	
	@Autowired
	private URLUtil urlUtil;
	
	
	/**
	 * This API is used for validating banking transaction based on operation type, token
	 * type, userId, and bank details
	 * 
	 * @param bankingValidationDTO
	 * @return ResultStatus
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/validatebankingtransaction.action", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus validateBankingTransactions(@RequestBody final BankingValidationTransactionParametersDTO bankingValidationDTO)
			throws ControllerException {
		
		String userId =  bankingValidationDTO.getUserId();
		String otpValue = bankingValidationDTO.getOtpValue();
		ResultStatus resultStatus = new ResultStatus();
		
		String sDataToSignHexa = "";
		boolean otpVerified = false;
		
		try {
			casWebHelper.authenticateCASever();

			switch (bankingValidationDTO.getTokenType()) {
				case EzioMobileDemoConstant.GATB:
				case EzioMobileDemoConstant.GATZ:
					// EZIO FLEX & EZIO SIGNER SUPPORT
					// Note 1: We use the same procedure to validate OTP with FLEX and SIGNER
					// Note 2: At this point we don't know if OTP is generated with DS or explicit signature
					// So we check DS and if it fails then explicit signature

					// We try to validate Dynamic signature with template first
					otpVerified = GATZisOtpVerified(bankingValidationDTO, userId, otpValue, otpVerified);
					break;
				case EzioMobileDemoConstant.GADB:
				case EzioMobileDemoConstant.GADF:
					otpVerified = GADFisOtpVerified(bankingValidationDTO, userId, otpValue, otpVerified);
					break;
				case EzioMobileDemoConstant.GAQT:
					String qrtoken_seedOTP = urlUtil.getQrtoken_seedOTP();
					logger.info("qrtoken_seedOTP : " + qrtoken_seedOTP);

					otpVerified = GAQTisOtpVerified(bankingValidationDTO, userId, otpValue, qrtoken_seedOTP);
					break;
				default:
					sDataToSignHexa = getTransactionData(bankingValidationDTO, false);
					logger.info("sDataToSignHexa: "+sDataToSignHexa);

					if(sDataToSignHexa != null && !sDataToSignHexa.equals("")){
						logger.info("SHA1 value: "+commonWebHelper.getSHA1(getTransactionData(bankingValidationDTO, false)));
						if(commonWebHelper.validateOTP(userId, otpValue, commonWebHelper.getSHA1(getTransactionData(bankingValidationDTO, false)))){
							logger.info("OTP verified .........");
							otpVerified = true;
						}
					}
					break;

			}
		} catch (Exception e) {
			logger.error("Exception occurred.........");
			e.printStackTrace();
			throw new ControllerException(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		}finally {
			if(otpVerified){
				boolean isDBUpdated = onlineBankingWebHelper.updateDataBaseBasedOnOperationType(userId, bankingValidationDTO);
				if(isDBUpdated){
					resultStatus.setMessage(EzioMobileDemoConstant.OTP_VALIDATION_SUCCESS);
					resultStatus.setStatusCode(HttpStatus.OK);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				}else{
					resultStatus.setMessage(EzioMobileDemoConstant.OTP_VALIDATION_FAILED);
					resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
					resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
				}
			}else{
				resultStatus.setMessage(EzioMobileDemoConstant.OTP_VALIDATION_FAILED);
				resultStatus.setStatusCode(HttpStatus.UNAUTHORIZED);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
			}
		}
		return resultStatus;
	}

	/**
	 *
	 * @param bankingValidationDTO
	 * @param userId
	 * @param otpValue
	 * @param qrtoken_seedOTP
	 * @return
	 */
	private boolean GAQTisOtpVerified(@RequestBody BankingValidationTransactionParametersDTO bankingValidationDTO, String userId, String otpValue, String qrtoken_seedOTP) {
		Boolean otpVerified = false;
		if (qrtoken_seedOTP.equals("1")) {
			if (commonWebHelper.validateOTP(userId, otpValue, getTransactionData(bankingValidationDTO, false))) {
				logger.info("GAQT-> OTP verified time.........1");
				otpVerified = true;
			}

		} else if (qrtoken_seedOTP.equals("2")) {
			if (commonWebHelper.validateOTP_OATH(userId, otpValue,
					getTransactionData(bankingValidationDTO, false))) {
				logger.info("GAQT-> OTP verified event.........2");
				otpVerified = true;
			}

		} else {
			otpVerified = false;
		}
		return otpVerified;
	}

	/**
	 *
	 * @param bankingValidationDTO
	 * @param userId
	 * @param otpValue
	 * @param otpVerified
	 * @return
	 */
	private boolean GADFisOtpVerified(@RequestBody BankingValidationTransactionParametersDTO bankingValidationDTO, String userId, String otpValue, boolean otpVerified) {
		if(commonWebHelper.validateOTP(userId, otpValue, commonWebHelper.getSHA1(getTransactionData(bankingValidationDTO, false)))){
			logger.info("GADB or GADF-> OTP verified .........1");
			otpVerified = true;
		}else if(commonWebHelper.validateOTP_OATH(userId, otpValue, commonWebHelper.getSHA1(getTransactionData(bankingValidationDTO, false)))){
			logger.info("GADB or GADF-> OTP verified .........2");
			otpVerified = true;
		}
		return otpVerified;
	}

	/**
	 *
	 * @param bankingValidationDTO
	 * @param userId
	 * @param otpValue
	 * @param otpVerified
	 * @return
	 */
	private boolean GATZisOtpVerified(@RequestBody BankingValidationTransactionParametersDTO bankingValidationDTO, String userId, String otpValue, boolean otpVerified) {
		if(commonWebHelper.validateOTP(userId, otpValue, commonWebHelper.getSHA1(getTransactionData(bankingValidationDTO, true)))){
			logger.info("GATB or GATZ-> OTP verified ........1");
			otpVerified = true;
		}else if(commonWebHelper.validateOTP(userId, otpValue, commonWebHelper.getSHA1(getTransactionData(bankingValidationDTO, false)))){
			logger.info("GATB or GATZ-> OTP verified .........2");
			otpVerified = true;
		}
		return otpVerified;
	}

	/**
	 * This method is used for getting transaction data based on operation type
	 * and token type
	 *  
	 * @param bankingValidationDTO
	 * @param bUseChallenge
	 * @return sDataToSignHexa bases on different devices and tx type
	 */
	public String getTransactionData(BankingValidationTransactionParametersDTO bankingValidationDTO, boolean bUseChallenge){
		
		logger.info("getting transaction data....");
		logger.info("Transaction type : "+bankingValidationDTO.getTransactionType());
		
		String sDataToSignHexa = "";
		if(bankingValidationDTO.getTransactionType().equals(EzioMobileDemoConstant.OPERATION_TYPE_MONEY_TRANSFER)){
			
			logger.info("Token type : "+bankingValidationDTO.getTokenType());
			
			switch (bankingValidationDTO.getTokenType()) {
			case EzioMobileDemoConstant.GAOC:
				sDataToSignHexa="30000003";
				sDataToSignHexa+=bankingValidationDTO.getFromAccountNo()+bankingValidationDTO.getToAccountNo()+bankingValidationDTO.getAmount();
				logger.info("TokenType : GAOC -- sDataToSignHexa : "+sDataToSignHexa );
				
				sDataToSignHexa = commonWebHelper.asciiToHex(sDataToSignHexa);
				
				logger.info("TokenType : GAOC -- sDataToSignHexa (updated) : "+sDataToSignHexa );
				
				break;
			case EzioMobileDemoConstant.GAQT:
				try {
					logger.info("Entering the GAQT loop");
					String transactionHash = qrTokenmasterService.findtransactionHashByUserId(bankingValidationDTO.getUserId());
					return transactionHash;
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				break;
			case EzioMobileDemoConstant.GATZ:
			case EzioMobileDemoConstant.GATB:
				sDataToSignHexa = GATBTypeMoneyTransferGeneratesDataToSignHexa(bankingValidationDTO, bUseChallenge);
				break;
			case EzioMobileDemoConstant.GATP:
				sDataToSignHexa = commonWebHelper.asciiToHex(bankingValidationDTO.getToAccountNo() + "~" + bankingValidationDTO.getAmount() + "~");
				logger.info("TokenType : GATP -- sDataToSignHexa : "+sDataToSignHexa);
				break;
			case EzioMobileDemoConstant.GADF:
			case EzioMobileDemoConstant.GADB:
				sDataToSignHexa = commonWebHelper.asciiToHex(bankingValidationDTO.getAmount());
				logger.info("TokenType : GADF or GADB -- sDataToSignHexa : "+sDataToSignHexa);
				break;
			default:
				return null;
			}
		}else if(bankingValidationDTO.getTransactionType().equals(EzioMobileDemoConstant.OPERATION_TYPE_NEW_BENEFICIARY)){
			switch (bankingValidationDTO.getTokenType()) {
			case EzioMobileDemoConstant.GAOC:
				sDataToSignHexa="30000003";
				sDataToSignHexa+= bankingValidationDTO.getBeneficiaryName()+bankingValidationDTO.getBeneficiaryAccount();
				logger.info("TokenType : GAOC -- sDataToSignHexa : "+sDataToSignHexa );
				
				sDataToSignHexa = commonWebHelper.asciiToHex(sDataToSignHexa);
				
				logger.info("TokenType : GAOC -- sDataToSignHexa (updated) : "+sDataToSignHexa );
				
				break;
			case EzioMobileDemoConstant.GAQT:
				try {
					logger.info("Entering the GAQT loop");
					String transactionHash = qrTokenmasterService.findtransactionHashByUserId(bankingValidationDTO.getUserId());
					return transactionHash;
				} catch (ServiceException e) {
					logger.error("Cannot find transaction for user " + bankingValidationDTO.getUserId() + ", stack: {}", e);
				}			
				break;
			case EzioMobileDemoConstant.GATZ:
			case EzioMobileDemoConstant.GATB:
				sDataToSignHexa = GATBTypeNotMoneyTransferGeneratesDataToSignHexa(bankingValidationDTO, bUseChallenge);

				break;
			case EzioMobileDemoConstant.GATP:
				sDataToSignHexa = commonWebHelper.asciiToHex(bankingValidationDTO.getBeneficiaryAccount() + "~~");
				logger.info("TokenType : GATP -- sDataToSignHexa : "+sDataToSignHexa);
				
				break;
			case EzioMobileDemoConstant.GADF:
			case EzioMobileDemoConstant.GADB:
				sDataToSignHexa = commonWebHelper.asciiToHex(bankingValidationDTO.getBeneficiaryAccount().substring(2));
				logger.info("TokenType : GADF or GADB -- sDataToSignHexa : "+sDataToSignHexa);
				
				break;
			default:
				return null;
			}
		}else{
			return null;
		}
		
		return sDataToSignHexa;
		
	}

	/**
	 *
	 * @param bankingValidationDTO
	 * @param bUseChallenge
	 * @return
	 */
	private String GATBTypeNotMoneyTransferGeneratesDataToSignHexa(BankingValidationTransactionParametersDTO bankingValidationDTO, boolean bUseChallenge) {
		String sDataToSignHexa;
		if(bUseChallenge){
			logger.info("TokenType : GATZ or GATB ---bUseChallenge : "+bUseChallenge);
			sDataToSignHexa = "E3075349474E205631" + "9F3706" + commonWebHelper.asciiToHex(bankingValidationDTO.getsHardTokenChallenge()) + "DF030A" + commonWebHelper.asciiToHex(bankingValidationDTO.getBeneficiaryAccount());
			logger.info(TOKEN_TYPE_GATZ_OR_GATB_S_DATA_TO_SIGN_HEXA_LABEL + sDataToSignHexa + B_USE_CHALLENGE_LABEL + bUseChallenge);
		}else{
			sDataToSignHexa = commonWebHelper.asciiToHex("03~" + bankingValidationDTO.getBeneficiaryAccount());
			logger.info(TOKEN_TYPE_GATZ_OR_GATB_S_DATA_TO_SIGN_HEXA_LABEL + sDataToSignHexa + B_USE_CHALLENGE_LABEL + bUseChallenge);
		}
		return sDataToSignHexa;
	}

	/**
	 *
	 * @param bankingValidationDTO
	 * @param bUseChallenge
	 * @return
	 */
	private String GATBTypeMoneyTransferGeneratesDataToSignHexa(BankingValidationTransactionParametersDTO bankingValidationDTO, boolean bUseChallenge) {
		String sDataToSignHexa;
		if(bUseChallenge){
			String sPad = "000000000000";
			String sAmount = sPad.substring(0, 12 - bankingValidationDTO.getAmount().length() - 2) +  bankingValidationDTO.getAmount() + "00";

			logger.info("TokenType : GATZ or GATB -- sAmount : "+sAmount+ B_USE_CHALLENGE_LABEL +bUseChallenge);

			logger.info("getsHardTokenChallenge: "+ bankingValidationDTO.getsHardTokenChallenge());
			logger.info("getToAccountNo : "+bankingValidationDTO.getToAccountNo());


			sDataToSignHexa = "E3075349474E205631" + "9F3706" + commonWebHelper.asciiToHex(bankingValidationDTO.getsHardTokenChallenge()) + "DF040A" + commonWebHelper.asciiToHex(bankingValidationDTO.getToAccountNo());
			sDataToSignHexa = sDataToSignHexa + "9F030C" + commonWebHelper.asciiToHex(sAmount);

			logger.info("asciiToHex(bankingValidationDTO.getsHardTokenChallenge()): "+commonWebHelper.asciiToHex(bankingValidationDTO.getsHardTokenChallenge()));
			logger.info("asciiToHex(bankingValidationDTO.getToAccountNo()): "+commonWebHelper.asciiToHex(bankingValidationDTO.getToAccountNo()));

			logger.info(TOKEN_TYPE_GATZ_OR_GATB_S_DATA_TO_SIGN_HEXA_LABEL + sDataToSignHexa + B_USE_CHALLENGE_LABEL + bUseChallenge);
		}else{
			sDataToSignHexa = commonWebHelper.asciiToHex("02~" + bankingValidationDTO.getAmount());
			logger.info(TOKEN_TYPE_GATZ_OR_GATB_S_DATA_TO_SIGN_HEXA_LABEL + sDataToSignHexa + B_USE_CHALLENGE_LABEL + bUseChallenge);
		}
		return sDataToSignHexa;
	}

	/**
	 * This API is used for resetting own account balances to 5000 dollar
	 * and beneficiary account balance to 0 dollar along with resetting transaction list 
	 * based on userId
	 * 
	 * @param userId
	 * @return resultStatus
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/resetbankingtransaction.user.action/{userId}", method = RequestMethod.POST, consumes = "application/json")
	public ResultStatus resetBankingTransactions(@PathVariable("userId") String userId, 
			@RequestBody ResetUserTransactionDTO resetOperation) throws ControllerException {
			
		
		logger.info("Entered into OnlineBankingTransactionController - resetBankingTransaction "
				+ "with userId: "+userId);
		
		logger.info("Entered into OnlineBankingTransactionController   resetOperation "+ resetOperation.toString());
		
		int uId = 0;
		boolean status = false;		
		ResultStatus resultStatus = new ResultStatus();		
		try{
			setResultSet(userId, resetOperation, status, resultStatus);
		}catch(Exception e){
			logger.error("Exception occurred in OnlineBankingTransactionController - resetBankingTransaction: "
					+ "unable to reset account balances and transactionlist");
			throw new ControllerException(e.getMessage());
		}		
		return resultStatus;	
	}

	/**
	 *
	 * @param userId
	 * @param resetOperation
	 * @param status
	 * @param resultStatus
	 * @throws Exception
	 */
	private void setResultSet(@PathVariable("userId") String userId, @RequestBody ResetUserTransactionDTO resetOperation, boolean status, ResultStatus resultStatus) throws Exception {
		int uId;
		if(userId!=null && !userId.equals("")){
			uId = usermasterService.findUidByUserId(userId);

			status = setStatus(resetOperation, uId, status);
			if(status){
				resultStatus.setMessage(EzioMobileDemoConstant.RESPONSE_SUCCESS);
				resultStatus.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_200);
				resultStatus.setStatusCode(HttpStatus.OK);
			}
		}
	}

	/**
	 *
	 * @param resetOperation
	 * @param uId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	private boolean setStatus(@RequestBody ResetUserTransactionDTO resetOperation, int uId, boolean status) throws Exception {
		boolean isClearUserTransaction;
		int isAccountBalanceUpdated;
		boolean isClearBeneficiaryAcc;
		if(resetOperation.isClearUserTransaction()){
			isClearUserTransaction = transactionmasterService.deleteUserTransactions(uId);
			if(!isClearUserTransaction)
				throw new Exception("Exception occurred : Couldn't update transaction data!");
			else
				status = true;
		}
		if(resetOperation.isResetUserAccountBalance()){
			isAccountBalanceUpdated = accountmasterService.updateAccountWithDefaultData(uId);
			if(isAccountBalanceUpdated == 0)
				throw new Exception("Exception occurred : Couldn't update Account data!");
			else
				status = true;
		}
		if(resetOperation.isClearBeneficiaryAccount()){
			isClearBeneficiaryAcc = accountmasterService.deleteAccountByUserIdAndType(uId, EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1);
			if(!isClearBeneficiaryAcc)
				throw new Exception("Exception occurred : Couldn't delete Beneficiary Accounts!");
			else
				status = true;
		}
		return status;
	}

	/**
	 * This method is used for handling onlineBankingTransaction exception
	 * 
	 * Exception handler
	 * @return ResultStatus object with response code 
	 */
	@ExceptionHandler(ControllerException.class)
	public ResultStatus onlineBankingTransactionErrorHandler() {
		ResultStatus status = new ResultStatus();
		status.setMessage(EzioMobileDemoConstant.DEFAULT_ERROR_MSG);
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		status.setResponseCode(EzioMobileDemoConstant.RESPONSE_CODE_401);
		return status;
	}
}
