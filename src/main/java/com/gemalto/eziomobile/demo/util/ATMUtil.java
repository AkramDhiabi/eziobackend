package com.gemalto.eziomobile.demo.util;

import java.util.Random;

public class ATMUtil {

	private static Random random = new Random();

	public static int generateChallengeForAtm (){
		return (int) Math.floor(10000000 + Math.random() * 90000000);
	}

	public static int generateAtmIdForAtm (){
		return (int) Math.floor(10000000 + Math.random() * 90000000);
	}

	/**
	 * @return 6 digit random access code
	 */
	public static int generateAtmAccessCode (){
		int accessCode = random.nextInt(900000) + 100000;
		return accessCode;
	}
}
