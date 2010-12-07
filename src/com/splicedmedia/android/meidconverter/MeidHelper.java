/*
 * Copyright (C) 2010 Gassan Idriss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.splicedmedia.android.meidconverter;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class MeidHelper {
	/* Serial Testing Patterns */
	private static final String meidDecTest = "[0-9]{18}";
	private static final String meidHexTest = "[a-fA-F0-9]{14}";
	private static final String esnDecTest = "[0-9]{11}";
	private static final String esnHexTest = "[a-fA-F0-9]{8}";
	private static final String NO_VALUE = " -- ";
	
	private CharSequence userInput;
	
	private MessageDigest sha1;
	
	public boolean isMEID = false;
	public boolean isESN = false;

	public boolean isHEX = false;
	public boolean isDEC = false;
	
	public String 
	  inputString,
	  meidDEC,
	  meidHEX,
	  esnHEX,
	  esnDEC,
	  metroSPC;
	
	/* Construct 
	 * @param CharSeqence input - The User Input
	 */
	public MeidHelper(CharSequence input) throws Exception {
		userInput = input;
		
		inputString = input.toString().toUpperCase();
		
		if(testInput(inputString,meidDecTest)){
			isMEID = true;
			isDEC = true;
		} else if(testInput(inputString,meidHexTest)){
			isMEID = true;
			isHEX = true;
		} else if(testInput(inputString,esnDecTest)){
			isESN = true;
			isDEC = true;
		} else if(testInput(inputString,esnHexTest)){
			isESN = true;
			isHEX = true;
		}
	}
		
    /* @function getMeidDec
     * @return String Converted String
     */
    protected String getMeidDec()
    {
    	if(meidDEC != null){ // already calculated
    		return inputString;
    	} else if(isESN){ // ESN/pESN Provided
    		return NO_VALUE;
    	} else if(isMEID && isDEC){// given in this format
    		meidDEC = inputString;
    	} else {// convert HEX to DEC
    		meidDEC = transformSerial(userInput, 16, 10, 8, 10, 8);
    	}
    	return meidDEC;
    }

    /* @function meidDecToHex
     * @param CharSequence User Input
     * @return String Converted String
     */
    protected String getMeidHex()
    {
    	if(meidHEX != null){// already calculated
    		return meidHEX;
    	} else if(isESN){ // ESN/pESN Provided
    		return NO_VALUE;
    	} else if(isMEID && isHEX){// given in this format
    		meidHEX = inputString;
    	} else { // convert DEC to HEX
    		meidHEX = transformSerial(userInput, 10, 16, 10, 8, 6);
    	}
    	return meidHEX;
    }
    
    /* @function getEsnHex
     * @return String Converted String
     */
    protected String getEsnHex()
    {    	
    	if(esnHEX != null){ // already calculated
    		return esnHEX;
    	} else if(isMEID){// calculate the PESN
    		esnHEX = calculatePESN(); 
    	} else if(isESN && isHEX){// given in this format
    		esnHEX = inputString;
    	} else { // convert from dec to hex
    		esnHEX = transformSerial(userInput, 10, 16, 3, 2, 6);
    	}
    	return esnHEX;
    }
    /* @function getEsnDec
     * @return String Converted String
     */
    protected String getEsnDec()
    {
    	if(esnDEC != null){ // already calculated
    		return esnDEC;
    	} else if(isMEID){ // get pESN in HEX to Convert to DEC
    		return transformSerial(calculatePESN(), 16, 10, 2, 3, 8);
    	} else if(isESN && isDEC){ // given in this format
    		esnDEC = inputString;
    	} else { // convert from hex to dec
    		esnDEC = transformSerial(userInput, 16, 10, 2, 3, 8);
    	}
    	return esnDEC;
    }
    
    /* @function getMetroSpc
     * @param String User Input
     * @return int 6-digit MSL/SPC Code
     * Returns the 6-digit SPC Code for MetroPCS
     */
    protected String getMetroSpc()
    {
    	if(metroSPC != null){
    		return metroSPC;
    	}
    	String input = getEsnDec();
    	String subSet = input.substring(8).toString();
		double SPC = (Math.pow(2, 5 + Character.getNumericValue(input.charAt(0)) + Character.getNumericValue(input.charAt(1)) + Character.getNumericValue(input.charAt(2))) - 1) * (Integer.parseInt(subSet, 10) + 199) * (23 + Character.getNumericValue(input.charAt(3)) + Character.getNumericValue(input.charAt(4)) + Character.getNumericValue(input.charAt(5)) + Character.getNumericValue(input.charAt(6)) + Character.getNumericValue(input.charAt(7)) + Character.getNumericValue(input.charAt(8)) + Character.getNumericValue(input.charAt(9)) + Character.getNumericValue(input.charAt(10)));
		long SPC2 = (long) SPC;
		String spc = Long.toString(SPC2);
    	return spc.substring(spc.length() - 6);
    }
    
    /* @function calculatePesn
     * @return String Converted String
    */
    protected String calculatePESN()
    {
    
	    if( isESN ){
	    	return NO_VALUE;
	    }
	    
	    String input = isDEC ? getMeidHex() : inputString;
	    
    	int[] p;
	    p = new int[7];   
	 
	    p[0] = Integer.parseInt(input.substring(0,2),16);
	    p[1] = Integer.parseInt(input.substring(2,4),16);
	    p[2] = Integer.parseInt(input.substring(4,6),16);
	    p[3] = Integer.parseInt(input.substring(6,8),16);
	    p[4] = Integer.parseInt(input.substring(8,10),16);
	    p[5] = Integer.parseInt(input.substring(10,12),16);
	    p[6] = Integer.parseInt(input.substring(12,14),16);
	    
	    String calc = new String(p, 0, 7);

    	try{
    	    
    	    sha1 = MessageDigest.getInstance("SHA-1");
    	    
    	    sha1.update(calc.getBytes("iso-8859-1"));
    	    
    	    byte[] sha1hash = sha1.digest();
    	        	   
    	    String hash = convertBytesToHex(sha1hash);
    	    
    	    hash = "80"+hash.substring((hash.length() -6));
    	    
    	    return hash.toUpperCase();

    	}catch(NoSuchAlgorithmException e)
    	{
    		// todo; handle error
    		return "ERROR";
    	} catch(UnsupportedEncodingException e)
    	{
    		// todo; handle error
    		return "ERROR";
    	}
    	
    }
    
    /* @function testInput
     * @param String input - User Input
     * @param String regex - Regex Expression
     * @return boolean
     * Test a String against a Regular Expression 
     */
    protected boolean testInput(String input, String regex)
    {
    	return Pattern.matches(regex, input);
    }

    /* @function transformSerial
     * @return String
     * Returns a converted ESN/MEID based on specified paramters
     * See function userInput.setOnClickListener
     */
    protected static String transformSerial(CharSequence n, int srcBase, int dstBase, int p1Width, int p1Padding, int p2Padding)
    {
    	String p1 = lPad(Long.toString(Long.parseLong(n.toString().substring(0,p1Width),srcBase),dstBase), p1Padding, "0");
    	String p2 = lPad(Long.toString(Long.parseLong(n.toString().substring(p1Width),srcBase),dstBase), p2Padding, "0");
    	
    	String c = p1+p2;
    	return c.toUpperCase();
    }
    /* @function lPad
     * @return String
     * Returns a left padded string for DEC/HEX Conversion 
     */
    protected static String lPad(String s, int len, String p) 
    { 
    	if(s.length() >= len){
    		return s;
    	}
    	return lPad(p + s, len, p);
    }
    
    /* @function convertBytesToHex
     * @return String
     * Returns a converted string from byte[] to hex
     */
    private static String convertBytesToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    }
    
}

