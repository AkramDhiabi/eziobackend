package com.gemalto.eziomobile.demo.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

	/**
	 * @param amount
	 * @return
	 */
	public static String formatCurrency(int amount){  
		 //Locale locale = Locale.US;
		 NumberFormat formatter=NumberFormat.getCurrencyInstance(new Locale("en", "US"));  
		 String currency=formatter.format(amount);
		 currency = currency.replaceAll("\\.00", "");
		
		 return currency;  
	} 
	
}
