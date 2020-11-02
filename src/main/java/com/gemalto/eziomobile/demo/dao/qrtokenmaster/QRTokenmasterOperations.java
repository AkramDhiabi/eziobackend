package com.gemalto.eziomobile.demo.dao.qrtokenmaster;

public interface QRTokenmasterOperations {
	
	String findtransactionHashByUserId(String userId);
	
	String findtransactionHashByUserIdAndTranscationType(String userId);

}
