package com.gemalto.eziomobile.demo.dao.atm;

public interface AtmQRCodeOperations {
	
	boolean updateAtmQRCodeStatusByUserIdAndAtmId(int status, String atmId, int uId);
	
	public String findAccesscodeByUserId(int userId);

}
