package com.gemalto.eziomobile.demo.service.transactionmaster;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dao.transactionmaster.TransactionmasterDao;
import com.gemalto.eziomobile.demo.dto.TransactionInfoDTO;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.TransactionInfo;
import com.gemalto.eziomobile.demo.util.ConvertDateToStringDate;
import com.gemalto.eziomobile.demo.util.CurrencyUtil;

@Service
public class TransactionmasterServiceImpl implements TransactionmasterService {

	private static final LoggerUtil logger = new LoggerUtil(TransactionmasterServiceImpl.class.getClass());

	@Autowired
	TransactionmasterDao transactionDao;

	@Override
	public List<TransactionInfoDTO> findTop20TransactionsByUserIdAndFromAccountNoAndStatus(int userId, String accountNo,
			int status) throws ServiceException {
		List<TransactionInfo> transactionList = new ArrayList<TransactionInfo>();
		List<TransactionInfoDTO> transactions = new ArrayList<TransactionInfoDTO>();
		TransactionInfoDTO transactionDTO = null;

		try {
			transactionList = transactionDao.findTop20TransactionsByUserIdAndFromAccountNoAndStatus(userId, accountNo,
					status);
			for (TransactionInfo transactionInfo : transactionList) {
				transactionDTO = new TransactionInfoDTO();

				transactionDTO.setUserId(transactionInfo.getUserId());
				transactionDTO.setFromAccountNo(transactionInfo.getFromAccountNo());
				transactionDTO.setToAccountNo(transactionInfo.getToAccountNo());
				transactionDTO.setCredit(transactionInfo.getCredit());
				transactionDTO.setDebit(transactionInfo.getDebit());
				transactionDTO.setCashIn(CurrencyUtil.formatCurrency(transactionInfo.getCredit()));
				transactionDTO.setCashOut(CurrencyUtil.formatCurrency(transactionInfo.getDebit()));
				transactionDTO.setDescription(transactionInfo.getDescription());
				transactionDTO.setStatus(transactionInfo.getStatus());
				transactionDTO.setTransactionDate(
						ConvertDateToStringDate.convertDateToString(transactionInfo.getTransactionDate()));

				transactions.add(transactionDTO);
			}

		} catch (Exception e) {
			logger.info("Unable to fecth account summary for account no : " + accountNo + " and userId : " + userId, e);
			throw new ServiceException(e);
		}
		return transactions;
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public void saveTransaction(TransactionInfoDTO transactionDTO) throws ServiceException {
		try {
			TransactionInfo transactionData = new TransactionInfo();

			transactionData.setFromAccountNo(transactionDTO.getFromAccountNo());
			transactionData.setToAccountNo(transactionDTO.getToAccountNo());
			transactionData.setUserId(transactionDTO.getUserId());
			transactionData.setCredit(transactionDTO.getCredit());
			transactionData.setDebit(transactionDTO.getDebit());
			transactionData.setDescription(transactionDTO.getDescription());
			transactionData.setStatus(transactionDTO.getStatus());
			transactionData.setPanNo(transactionDTO.getPanNo());
			transactionData.setTransactionDate(new Date());

			transactionDao.save(transactionData);
		} catch (Exception e) {
			logger.info("Something went wrong! Unable to process the transaction!", e);
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public boolean deleteUserTransactions(int userId) throws ServiceException {
		int reset = 0;
		boolean isClearUserTransaction = false;
		try {
			logger.info("userId in deleteUserTransactions: " + userId);
			reset = transactionDao.deleteUserTransactionByUserIdAndStatus(userId,
					EzioMobileDemoConstant.EZIO_STATUS_VALUE_1);

			if (reset != 0) {
				isClearUserTransaction = true;
				logger.info("transaction details are deleted successfully.....");
			}
		} catch (Exception e) {
			logger.error("unable to reset transaction list....");
			throw new ServiceException(e);
		}
		return isClearUserTransaction;
	}

	@Override
	public List<TransactionInfoDTO> findTopNTransactionsForCard(int userId, String accountNo, int transactionsToDisplay, String panNo)
			throws ServiceException {

		List<TransactionInfoDTO> transactionsList = new ArrayList<TransactionInfoDTO>();
		TransactionInfoDTO transactionDTO = null;
		try {
			List<TransactionInfo> transactions = transactionDao
					.findTopNTransactionsByUserIdAndAccountNoAndDescriptionEqualsToEzioShop(userId, accountNo,
							transactionsToDisplay, panNo);

			for (TransactionInfo transactionInfo : transactions) {
				transactionDTO = new TransactionInfoDTO();

				transactionDTO.setUserId(transactionInfo.getUserId());
				transactionDTO.setFromAccountNo(transactionInfo.getFromAccountNo());
				transactionDTO.setToAccountNo(transactionInfo.getToAccountNo());
				transactionDTO.setCredit(transactionInfo.getCredit());
				transactionDTO.setDebit(transactionInfo.getDebit());
				transactionDTO.setCashIn(CurrencyUtil.formatCurrency(transactionInfo.getCredit()));
				transactionDTO.setCashOut(CurrencyUtil.formatCurrency(transactionInfo.getDebit()));
				transactionDTO.setDescription(transactionInfo.getDescription());
				transactionDTO.setStatus(transactionInfo.getStatus());
				transactionDTO.setTransactionDate(
						ConvertDateToStringDate.convertDateToString(transactionInfo.getTransactionDate()));

				transactionsList.add(transactionDTO);
			}

		} catch (Exception e) {
			logger.error("unable to find 20 transaction list....");
			throw new ServiceException(e);
		}
		return transactionsList;
	}

}
