package com.gemalto.eziomobile.demo.dao.usermaster;

public interface UsermasterOperations {
	
	int findUidByUserId(String userId);

	String findUserIdByUid(int uId);
	
	//UserMasterInfo findUserInfoByUserIdAndPassword(String userId, String password);
	
	int updateUserRoleByUserId(int uId, String userRole);
	
	public boolean updatePasswordByUsername(int uId, String newPassword);

	public boolean updatePasswordByEmailAndRecoverToken(String emailAddress, String recoverToken, String newPassword);

	public boolean updateRecoverTokenByEmailAndRecoverToken(String emailAddress, String recoverToken);

	public boolean updateEmailByUsername(int uId, String emailAddr);
	
	public String findPasswordByUId(int uId);

	public boolean updateRecoverTokenByUsername(String recoverToken, int uId);

	public boolean isRecoverTokenValid(String emailAddress, String recoverToken);
}
