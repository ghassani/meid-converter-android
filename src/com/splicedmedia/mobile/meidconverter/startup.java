package com.splicedmedia.mobile.meidconverter;
/**
 * @author Ghassan Idriss
 * @copyright 2010 Spliced Media L.L.C
 * @website http://www.splicedmedia.com
 * @package com.splicedmedia.mobile.meidconverter
 * @subpackage startup
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;
import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
import android.database.Cursor;


public class startup extends Activity{
	
	private Button calculateButton,historyButton;
	private EditText userInput;
	private TextView details;
	
	/* Serial Testing Patterns */
	private static final String meidDecTest = "[0-9]{18}";
	private static final String meidHexTest = "[a-fA-F0-9]{14}";
	private static final String esnDecTest = "[0-9]{11}";
	private static final String esnHexTest = "[a-fA-F0-9]{8}";
	
	private boolean calcStatus = true;
	
	private MessageDigest hcalc;
	private SqlHelper db;
	/* Placeholders for Calculated Values*/
	public String 
	  userInputValue,
	  inputType,
	  meidHex,
	  meidDec,
	  esnHex,
	  esnDec,
	  msl;
	
    /** Entry Point */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        try{
        	db = new SqlHelper(this);
        }catch(Exception e)
        {
        	displayAlert(e.getMessage(),getText(R.string.alertExceptionTitle).toString(),getText(R.string.alertButtonConfirm).toString());
        }
        calculateButton = (Button) findViewById(R.id.calculate);
        historyButton = (Button) findViewById(R.id.history);
        userInput = (EditText) findViewById(R.id.meid);
        details = (TextView) findViewById(R.id.details);
        /*
         * Calculate Button Actions
         */
        calculateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	CharSequence input = userInput.getText();
            	
            	try{
            		/* INPUT IS MEID DEC FORMAT */
	            	if( testInput(input.toString(),meidDecTest))
	            	{
	            		inputType = "MEID (DEC)";
	            		Cursor existing = db.findExisting(input.toString(), "meid_dec");
	            		if(existing.getCount() > 0){
	            			existing.moveToFirst();
		            		meidDec = existing.getString(existing.getColumnIndex("meid_dec"));
		            		meidHex = existing.getString(existing.getColumnIndex("meid_hex"));
		            		esnHex = existing.getString(existing.getColumnIndex("esn_hex"));
		            		esnDec = existing.getString(existing.getColumnIndex("esn_dec"));
		            		msl = existing.getString(existing.getColumnIndex("metropcs_spc"));
	            			displayToastNotification("Found in History. Loading from database.",Toast.LENGTH_SHORT);
	            			outputToUser();
	            			return;
	            		} else{
	            			displayToastNotification(getText(R.string.notificationAcceptedMEIDHex).toString(),Toast.LENGTH_SHORT);
	            		}
	            		meidDec = input.toString();
	            		meidHex = meidDecToHex(input);
	            		esnHex = meidToPesn(meidHex);
	            		esnDec = esnHexToDec(esnHex);
	            		msl = Integer.toString(calculateMetroPCS_SPC(esnDec));
	            		
	            		outputToUser();

	            	/* INPUT IS MEID HEX FORMAT */
	            	} else if(testInput(input.toString(),meidHexTest))
	            	{
	            		inputType = "MEID (HEX)";
	            		Cursor existing = db.findExisting(input.toString(), "meid_hex");
	            		if(existing.getCount() > 0){
	            			existing.moveToFirst();
		            		meidDec = existing.getString(existing.getColumnIndex("meid_dec"));
		            		meidHex = existing.getString(existing.getColumnIndex("meid_hex"));
		            		esnHex = existing.getString(existing.getColumnIndex("esn_hex"));
		            		esnDec = existing.getString(existing.getColumnIndex("esn_dec"));
		            		msl = existing.getString(existing.getColumnIndex("metropcs_spc"));
	            			displayToastNotification("Found in History. Loading from database.",Toast.LENGTH_SHORT);
	            			outputToUser();
	            			return;
	            		} else{
	            			displayToastNotification(getText(R.string.notificationAcceptedMEIDHex).toString(),Toast.LENGTH_SHORT);
	            		}

	            		meidDec = meidHexToDec(input);
	            		meidHex = input.toString();
	            		esnHex = meidToPesn(input.toString());
	            		esnDec = esnHexToDec(esnHex);
	            		msl = Integer.toString(calculateMetroPCS_SPC(esnDec));
	            		
	            		outputToUser();
	            		
	            	/* INPUT IS ESN DEC FORMAT */
	            	}else if(testInput(input.toString(),esnDecTest))
	            	{

	            		inputType = "ESN (DEC)";
	            		Cursor existing = db.findExisting(input.toString(), "esn_dec");
	            		if(existing.getCount() > 0){
	            			existing.moveToFirst();
		            		meidDec = existing.getString(existing.getColumnIndex("meid_dec"));
		            		meidHex = existing.getString(existing.getColumnIndex("meid_hex"));
		            		esnHex = existing.getString(existing.getColumnIndex("esn_hex"));
		            		esnDec = existing.getString(existing.getColumnIndex("esn_dec"));
		            		msl = existing.getString(existing.getColumnIndex("metropcs_spc"));
	            			displayToastNotification("Found in History. Loading from database.",Toast.LENGTH_SHORT);
	            			outputToUser();
	            			return;
	            		} else{
	            			displayToastNotification(getText(R.string.notificationAcceptedMEIDHex).toString(),Toast.LENGTH_SHORT);
	            		}
	            		
	            		msl = Integer.toString(calculateMetroPCS_SPC(input.toString()));
	            		esnDec = input.toString();
	            		esnHex = esnDecToHex(input);
	            		meidDec = " -- ";
	            		meidHex = " -- ";
	            		outputToUser();
	            		
	            	/* INPUT IS ESN HEX FORMAT */
	            	}else if(testInput(input.toString(),esnHexTest))
	            	{
	            		inputType = "ESN (Hex)";
	            		Cursor existing = db.findExisting(input.toString(), "esn_dec");
	            		if(existing.getCount() > 0){
	            			existing.moveToFirst();
		            		meidDec = existing.getString(existing.getColumnIndex("meid_dec"));
		            		meidHex = existing.getString(existing.getColumnIndex("meid_hex"));
		            		esnHex = existing.getString(existing.getColumnIndex("esn_hex"));
		            		esnDec = existing.getString(existing.getColumnIndex("esn_dec"));
		            		msl = existing.getString(existing.getColumnIndex("metropcs_spc"));
	            			displayToastNotification("Found in History. Loading from database.",Toast.LENGTH_SHORT);
	            			outputToUser();
	            			return;
	            		} else{
	            			displayToastNotification(getText(R.string.notificationAcceptedMEIDHex).toString(),Toast.LENGTH_SHORT);
	            		}
	            		
	            		esnDec = esnHexToDec(input);
	            		esnHex = input.toString();
	            		meidDec = " -- ";
	            		meidHex = " -- ";
	            		msl = Integer.toString(calculateMetroPCS_SPC(esnDec));
	            		
	            		
	            		outputToUser();
	            		
	            	}else
	            	{
	            		displayAlert(getText(R.string.inputError).toString()+input.length(),getText(R.string.alertInputErrorTitle).toString(),getText(R.string.alertButtonConfirm).toString());
	            		calcStatus = false;
	            	}
            	} 
            	catch(Exception e)
            	{
            		displayAlert(e.getMessage(),getText(R.string.alertExceptionTitle).toString(),getText(R.string.alertButtonConfirm).toString());
            		calcStatus = false;
            	}
            	
            	// we had success?
            	if(calcStatus == true )
            	{
            		
            		try{
            			db.insertHistory(meidDec, meidHex, esnDec, esnHex, msl);
            		}catch(Exception e)
                    {
                    	displayAlert(e.getMessage(),getText(R.string.alertExceptionTitle).toString(),getText(R.string.alertButtonConfirm).toString());
                    }
            		/*MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.success);
            		mp.start();*/
            	} else
            	{
            		/*MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error);
            		mp.start();*/
            	}
            	
            }
        });
        
        /*
         * Clear Text on Click of User Input
         */
        userInput.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {

        		if( getText(R.string.initialInputText).toString() ==  userInput.getText().toString() )
        		{
        			userInput.setText("");
        			
        		}
        	}
        });
        
        /*
         * View History onClick Listener
         */
        historyButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		 Intent history = new Intent(v.getContext(), history.class);
                 startActivityForResult(history, 0);
        	}
        });

    }
    

    /* @function displayToastNotification
     * @param String message The message to display
     * @param int length The length of the message. Feed it a Valid Toast Length Constant
     * @return VOID
     * Displays a Toast notification for a certain length
    */
    protected void displayToastNotification(String message, int length)
    {
    	Toast.makeText(getApplicationContext(), message, length).show();
    }
    /* @function displayAlert
     * @param String message The Message to display
     * @param String title The Title of the Alert Box to display
     * @param String btnlabel The text to display on the button
     * @return VOID
     * Displays a simple one button alert message
    */
    protected void displayAlert(String message, String title, String btnlabel)
    {    	
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	dialog.setMessage(message)
    	.setCancelable(false)   
    	.setTitle(title)
    	.setPositiveButton(btnlabel, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
                startup.this.finish();
           }
       });
    	AlertDialog alert = dialog.create();
    	alert.show();
    }
    /* @function meidToPesn
     * @param String User Input
     * @return String Converted String
     * Return a converted MEID in HEX format to pESN
    */
    protected String meidToPesn(String input)
    {
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
    	    
    	    hcalc = MessageDigest.getInstance("SHA-1");
    	    
    	    hcalc.update(calc.getBytes("iso-8859-1"));
    	    
    	    byte[] sha1hash = hcalc.digest();
    	        	   
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
    /* @function outputToUser
     * @return Void
     * Output results to user
     */
    protected void outputToUser()
    {
		details.setText(
    		getText(R.string.inputTypeLabel).toString()+": "+inputType
        	+"\n-----------------------"
        	+"\nMEID (DEC): "+meidDec
        	+"\nMEID (HEX): "+meidHex
        	+"\n(p)ESN (DEC): "+esnDec
        	+"\n(p)ESN (HEX): "+esnHex
        	+"\n-----------------------"
        	+"\nMetroPCS SPC: "+msl
       );
    }
    /* @function meidHexToDec
     * @param CharSequence User Input
     * @return String Converted String
     * Proxy Method to Return a converted MEID in DEC Format from HEX
     * See function transformSN
     */
    protected String meidHexToDec(CharSequence input)
    {
    	return convertSerial(input, 16, 10, 8, 10, 8);
    }
    /* @function meidDecToHex
     * @param CharSequence User Input
     * @return String Converted String
     * Proxy Method to Return a converted MEID in HEX Format from DEC
     * See function transformSN
     */
    protected String meidDecToHex(CharSequence input)
    {
    	return convertSerial(input, 10, 16, 10, 8, 6);
    }
    /* @function esnDecToHex
     * @param CharSequence User Input
     * @return String Converted String
     * Proxy Method to Return a converted ESN in DEC Format from HEX
     * See function transformSN
     */
    protected String esnDecToHex(CharSequence input)
    {
    	return convertSerial(input, 10, 16, 3, 2, 6);
    }
    /* @function esnHexToDec
     * @param CharSequence User Input
     * @return String Converted String
     * Proxy Method to Return a converted ESN in HEX Format from DEC
     * See function transformSN
     */
    protected String esnHexToDec(CharSequence input)
    {
    	return convertSerial(input, 16, 10, 2, 3, 8);
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
    /* @function transformSN
     * @return String
     * Returns a converted ESN/MEID based on specified paramters
     * See function userInput.setOnClickListener
     */
    protected static String convertSerial(CharSequence n, int srcBase, int dstBase, int p1Width, int p1Padding, int p2Padding)
    {
    	String p1 = lPad(Long.toString(Long.parseLong(n.toString().substring(0,p1Width),srcBase),dstBase), p1Padding, "0");
    	String p2 = lPad(Long.toString(Long.parseLong(n.toString().substring(p1Width),srcBase),dstBase), p2Padding, "0");
    	
    	String c = p1+p2;
    	return c.toUpperCase();
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
    
    /* @function calculateMetroPCS_SPC
     * @param String User Input
     * @return int 6-digit MSL/SPC Code
     * Returns the 6-digit SPC Code for MetroPCS
     */
    protected int calculateMetroPCS_SPC(String input)
    {
    	String subSet = input.substring(8).toString();
		double v = (Math.pow(2, 5 + Character.getNumericValue(input.charAt(0)) + Character.getNumericValue(input.charAt(1)) + Character.getNumericValue(input.charAt(2))) - 1);
		double v2 = (Integer.parseInt(subSet, 10) + 199) * (23 + Character.getNumericValue(input.charAt(3)) + Character.getNumericValue(input.charAt(4)) + Character.getNumericValue(input.charAt(5)) + Character.getNumericValue(input.charAt(6)) + Character.getNumericValue(input.charAt(7)) + Character.getNumericValue(input.charAt(8)) + Character.getNumericValue(input.charAt(9)) + Character.getNumericValue(input.charAt(10)));
		double SPC1 = v * v2;
		long SPC2 = (long) SPC1;
		String spc = Long.toString(SPC2);
    	return Integer.parseInt(spc.substring(spc.length() - 6));
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