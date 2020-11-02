package com.gemalto.eziomobile.demo.dao.cardmanagementmaster;

public interface CardmanagementOperation {
	
	boolean updateCardStatusByUserIdAndPanNo(int userId, String panNo, String cardStatusValue);
	
	int findIdByUserIdAndPanNo(int userId, String panNo);
	
	String findCardStatusByUserIdAndPanNo(int userId, String panNo);

}
