package com.gemalto.eziomobile.demo.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;

@Component
public class PinGenerationUtil {

	private static Random random = new Random();

	private static final LoggerUtil logger = new LoggerUtil(PinGenerationUtil.class.getClass());
	
	/**
	 * @return 4 digit number, which is randomly generated
	 */
	public int generate4DigitRandomNumber() {
		
		int firstDigit = 0, secondDigit = 0, thirdDigit = 0, forthDigit = 0;
		int genPIN = 0;
		
		while (true) {
			genPIN = random.nextInt(9000) + 1000;
		    firstDigit = genPIN % 10;
		    secondDigit = (genPIN / 10) % 10;
		    thirdDigit = (genPIN / 100) % 10;
		    forthDigit = genPIN / 1000;
		    if (firstDigit == secondDigit || firstDigit == thirdDigit || firstDigit == forthDigit 
		    		|| secondDigit == thirdDigit || secondDigit == forthDigit || thirdDigit == forthDigit)
		        continue;
		    else
		        break;
		}
		return genPIN;
	}
	
	
	/**
	 * @param number
	 * @return int[] array which will be passed in isNonincreasingOrNondecreasing(int[] number)
	 * to check sequence of generated number
	 */
	public int[] convertIntToIntArray(int number) {
		
		String temp = Integer.toString(number);
		int[] numArray = new int[temp.length()];
		for (int i = 0; i < temp.length(); i++)
		{
			numArray[i] = temp.charAt(i) - '0';
		}
		return numArray;
	}
	
	
	/**
	 * @param number
	 * @return true/false, if given number is in increasing or decreasing order
	 */
	public boolean isNonincreasingOrNondecreasing(int[] number) {
	    // clear out equal entries from the beginning
	    int i = 1;
	    while (i < number.length && number[i - 1] == number[i]) {
	        i++;
	    }
	    // if there are no entries left, then it is nonincreasing and nondecreasing
	    if (i >= number.length) {
	        return true;
	    }
	    int previous = number[i - 1];
	    if (i < number.length && previous > number[i]) {
	        for (; i < number.length; i++) {
	            if (previous < number[i]) {
	                return false;
	            }
	            previous = number[i];
	        }
	        return true;
	    }
	    for (; i < number.length; i++) {
	        if (previous > number[i]) {
	            return false;
	        }
	        previous = number[i];
	    }
	    return true;
	}
	
	
	/**
	 * @return generate PIN of 4 digits
	 */
	public int generatePIN() {
		int[] pinArray;
		int pin = 0;
		
		pin = generate4DigitRandomNumber();
		
		Pattern pattern = Pattern.compile("^([0-9])\\1*$");
	    Matcher matcher = pattern.matcher(String.valueOf(pin));
	      
	      if (!matcher.matches()) {
	    	  pinArray = convertIntToIntArray(pin);
	    	  if(isNonincreasingOrNondecreasing(pinArray))
	    		  pin = generate4DigitRandomNumber();
	    	  
	    	  logger.info(pin+"  Generated pin is valid!");
	      }
		return pin;
	}
	
	
	
}
