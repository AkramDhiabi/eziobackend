package com.gemalto.eziomobile.demo.util;

public class SubStringUtil {

	/**
	 * Method to get the substring of length n from the end of a string.
	 * 
	 * @param inputString
	 *            - String from which substring to be extracted.
	 * @param subStringLength-
	 *            int Desired length of the substring.
	 * @return lastCharacters- String
	 * @author Zaheer Paracha
	 * 
	 */
	public static String getLastnCharacters(String inputString, int subStringLength) {
		
		int length = inputString.length();
		if (length <= subStringLength) {
			return inputString;
		}
		
		//OR use (_ac1[1].substring(_ac1[1].length() - 4))
		int startIndex = length - subStringLength;
		return inputString.substring(startIndex);
	}
	
	/** Method to format PanNO with space
	 * @param panNo
	 * @return PanNO with spaced added in between
	 */
	public static String addSpaceInPanNo(String panNo) {
		String formatPanNo = "";
		return formatPanNo = panNo.substring(0, 4)+" "+panNo.substring(4, 8)+" "+panNo.substring(8, 12)+" "+panNo.substring(12, 16);
		
	}

}
