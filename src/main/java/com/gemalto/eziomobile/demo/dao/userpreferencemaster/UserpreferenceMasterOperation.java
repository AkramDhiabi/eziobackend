package com.gemalto.eziomobile.demo.dao.userpreferencemaster;

import com.gemalto.eziomobile.demo.dto.UserpreferenceMasterDTO;

public interface UserpreferenceMasterOperation {
	
	String findSecLoginByStatusAndUserId(int status, int userId);
	
	String findSecTxOtherByStatusAndUserId(int status, int userId);
	
	String findSecTxOwnAccByStatusAndUserId(int status, int userId);
	
	String findSecAddPayeeByStatusAndUserId(int status, int userId);
	
	String findSecEcommerce3dsByStatusAndUserId(int status, int userId);
	
	String findSecP2pNotificationByStatusAndUserId(int status, int userId);
	
	String findSecWebNotificationByStatusAndUserId(int status, int userId);
	
	String findSecMobileBankingByStatusAndUserId(int status, int userId);
	
	int updateUserPreferenceInfo(UserpreferenceMasterDTO userpreferenceMasterDTO);

}
