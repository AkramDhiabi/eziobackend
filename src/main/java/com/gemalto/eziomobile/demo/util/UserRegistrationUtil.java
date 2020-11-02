package com.gemalto.eziomobile.demo.util;

import java.util.Random;

public class UserRegistrationUtil {

	private static Random random = new Random();
	
	/**
	 * @param rootURL
	 * @param userId
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getActivationURL(String rootURL, String userId, String key) throws Exception {
		
		String url = rootURL+"activateaccount.user.action?userId="+userId+"&key="+key;
		return url;
	}

	/**
	 * @param frontURL
	 * @param emailAddress
	 * @return
	 * @throws Exception
	 */
	public static String getRecoverUrl(String frontURL, String emailAddress) throws Exception {

		String url = frontURL+"#/recoveraccount?emailAddress="+emailAddress+"&recoverToken=";
		return url;
	}


	/**
	 * @return
	 */
	public static String generateKey(){
		int length = 50;
		String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		int n = alphabet.length();

		String result = "";

		for (int i=0; i<length; i++)
		    result = result + alphabet.charAt(random.nextInt(n));

		return result;
	}
}
