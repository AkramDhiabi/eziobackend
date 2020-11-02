package com.gemalto.eziomobile.demo.dao.panmaster;

public interface PanMasterOperation {
	
	String findAccountNoByUserIdAndPanNo(int userId, String panNo);

}
