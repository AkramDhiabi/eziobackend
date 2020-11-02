package com.gemalto.eziomobile.demo.dao.userregistration;

public interface UserRegistrationOperation {
	
	public boolean updateStatusByUsernameAndActivationKey(String userid, String activationKey);
	
	public boolean isActivationKeyValid(String username, String activationKey);

}
