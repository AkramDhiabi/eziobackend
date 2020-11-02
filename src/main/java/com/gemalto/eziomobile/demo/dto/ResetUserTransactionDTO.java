package com.gemalto.eziomobile.demo.dto;

public class ResetUserTransactionDTO {
	
	boolean clearUserTransaction;
	boolean resetUserAccountBalance;
	boolean clearBeneficiaryAccount;
	
	
	public boolean isClearUserTransaction() {
		return clearUserTransaction;
	}
	public void setClearUserTransaction(boolean clearUserTransaction) {
		this.clearUserTransaction = clearUserTransaction;
	}
	public boolean isResetUserAccountBalance() {
		return resetUserAccountBalance;
	}
	public void setResetUserAccountBalance(boolean resetUserAccountBalance) {
		this.resetUserAccountBalance = resetUserAccountBalance;
	}
	public boolean isClearBeneficiaryAccount() {
		return clearBeneficiaryAccount;
	}
	public void setClearBeneficiaryAccount(boolean clearBeneficiaryAccount) {
		this.clearBeneficiaryAccount = clearBeneficiaryAccount;
	}
	@Override
	public String toString() {
		return "ResetUserAccountDTO [clearUserTransaction=" + clearUserTransaction + ", resetUserAccountBalance="
				+ resetUserAccountBalance + ", clearBeneficiaryAccount=" + clearBeneficiaryAccount + "]";
	}

}
