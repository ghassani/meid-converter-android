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


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;


public class Calculate extends Activity{
	
	private Button calculateButton,historyButton, aboutButton;
	private EditText userInput;
	private TextView details;
	
	
	private SqlHelper DB;
	private MeidHelper MEID;
	
	private static final int DIALOG_SERIOUS_ERROR = 0;
	private static final int DIALOG_INPUT_ERROR = 1;
	private static final int DIALOG_ABOUT = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        /* Attempt a SQL Connection */
        try{
        	DB = new SqlHelper(this);
        	
        }catch(Exception e){
        	showDialog(DIALOG_SERIOUS_ERROR);
        }
        
        /* Layout Items */
        calculateButton = (Button) findViewById(R.id.calculate);
        aboutButton = (Button) findViewById(R.id.about);
        historyButton = (Button) findViewById(R.id.history);
        userInput = (EditText) findViewById(R.id.meid);
        details = (TextView) findViewById(R.id.details);
        
        /*
         * Calculate Button Actions
         */
        calculateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	
            	try{
            		
            		MEID = new MeidHelper(userInput.getText());
            		
            		userInput.setText("");            	
            		
            		String meidDec = MEID.getMeidDec();
            		String meidHex = MEID.getMeidHex();
            		String esnDec = MEID.getEsnDec();
            		String esnHex = MEID.getEsnHex();
            		String metroSPC = MEID.getMetroSpc();
            		
            		// DISPLAY RESULTS
            		details.setGravity(3); //LEFT
            		details.setText(
            	    	getText(R.string.inputTypeLabel).toString() + ": "
            	    	+ (MEID.isMEID ? "MEID" : "ESN") + (MEID.isDEC ? " (DEC)" : " (HEX)") 
            	        + "\n-----------------------"
            	        + "\nMEID (DEC): " + meidDec
            	        + "\nMEID (HEX): " + meidHex
            	        + "\n" + (MEID.isMEID ? "p":"") + "ESN (DEC): " + esnDec
            	        + "\n" + (MEID.isMEID ? "p":"") + "ESN (HEX): " + esnHex
            	        + "\n-----------------------"
            	        + "\nMetroPCS SPC: "+metroSPC
            	     );
            		
            		Cursor existing = DB.findExisting(MEID.inputString, (MEID.isMEID ? "meid_" : "esn_") + (MEID.isDEC ? "dec" : "hex"));
            		
            		if(existing.getCount() == 0){
            			DB.insertHistory(meidDec, meidHex, esnDec, esnHex, metroSPC);
            			displayToastNotification("Added to history for future reference", Toast.LENGTH_SHORT);
            		}
            		
            	} catch(Exception e){
            		showDialog(DIALOG_INPUT_ERROR);
            	}
            }
        });
        
        
        /*
         * View History onClick Listener
         */
        historyButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		 Intent history = new Intent(v.getContext(), History.class);
                 startActivityForResult(history, 0);
        	}
        });
        /*
         * View About onClick Listener
         */
        aboutButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		showDialog(DIALOG_ABOUT);
        	}
        });
    }
    
    /* @function displayToastNotification
     * @param String message The message to display
     * @param int length The length of the message. Feed it a Valid Toast Length
     * @return VOID
     * Displays a Toast notification for a certain length
    */
    protected void displayToastNotification(String message, int length)
    {
    	Toast.makeText(getApplicationContext(), message, length).show();
    } 
    
    /* @function onCreateDialog
     * @param int id
    */
    @Override
    protected AlertDialog onCreateDialog(int id) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	
    	switch(id){
    	case DIALOG_SERIOUS_ERROR:
    		dialog.setMessage(getText(R.string.seriousErrorMessage).toString())
    		  .setCancelable(false)
    		  .setTitle(getText(R.string.alertInputErrorTitle).toString())
    		  .setPositiveButton(getText(R.string.alertButtonConfirm).toString(), new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int id) {
    				  Calculate.this.finish();
    			  }
    		  });
    		break;
    	case DIALOG_ABOUT:
    		dialog.setMessage(getText(R.string.aboutMessage).toString())
	  		  .setCancelable(false)
	  		  .setTitle(getText(R.string.aboutMessageTitle).toString())
	  		  .setPositiveButton(getText(R.string.aboutReturnButton).toString(), new DialogInterface.OnClickListener() {
	  			  public void onClick(DialogInterface dialog, int id) {
	  				  
	  			  }
	  		  });
    		break;
    	case DIALOG_INPUT_ERROR:
    		dialog.setMessage(getText(R.string.inputError).toString())
    		  .setCancelable(false)
    		  .setTitle(getText(R.string.alertInputErrorTitle).toString())
    		  .setPositiveButton(getText(R.string.alertButtonConfirm).toString(), new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int id) {
    				  
    			  }
    		  });
    		break;
    	default:
    		break;
    	}
    	
    	AlertDialog alert = dialog.create();
    	alert.show();
    	return alert;
    }
}