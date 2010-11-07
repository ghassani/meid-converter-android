package com.splicedmedia.android.meidconverter;
/**
 * @author Ghassan Idriss
 * @copyright 2010 Spliced Media L.L.C
 * @website http://www.splicedmedia.com
 * @package com.splicedmedia.android.meidconv
 * @subpackage startup
 */

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


public class startup extends Activity{
	
	private Button calculateButton,historyButton;
	private EditText userInput;
	private TextView details;
	
	
	private SqlHelper DB;
	private MeidHelper MEID;
	
	private static final int SERIOUS_ERROR = 0;
	private static final int DIALOG_INPUT_ERROR = 1;
	
	/* Placeholders for Calculated Values*/
	public String 
	  userInputValue,
	  inputType,
	  meidHex,
	  meidDec,
	  esnHex,
	  esnDec,
	  msl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        /* Attempt a SQL Connection */
        try{
        	DB = new SqlHelper(this);
        	
        }catch(Exception e)
        {
        	showDialog(SERIOUS_ERROR);
        }
        
        /* Layout Items */
        calculateButton = (Button) findViewById(R.id.calculate);
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
            		
            		details.setText(
            	    	getText(R.string.inputTypeLabel).toString() + ": "
            	    	+ (MEID.isMEID ? "MEID" : "ESN") + (MEID.isDEC ? " (DEC)" : " (HEX)") 
            	        + "\n-----------------------"
            	        + "\nMEID (DEC): " + MEID.getMeidDec()
            	        + "\nMEID (HEX): " + MEID.getMeidHex()
            	        + "\n" + (MEID.isMEID ? "p" :null) + "ESN (DEC): " + MEID.getEsnDec()
            	        + "\n" + (MEID.isMEID ? "p" :null) + "ESN (HEX): " + MEID.getEsnHex()
            	        + "\n-----------------------"
            	        + "\nMetroPCS SPC: "+MEID.getMetroSpc()
            	     );
            		
            		Cursor existing = DB.findExisting(MEID.inputString, (MEID.isMEID ? "meid_" : "esn_") + (MEID.isDEC ? "dec" : "hex"));
            		
            		if(existing.getCount() == 0){
            			DB.insertHistory(MEID.getMeidDec(), MEID.getMeidHex(), MEID.getEsnDec(), MEID.getEsnHex(), MEID.getMetroSpc());
            			displayToastNotification("Added to history for future reference", Toast.LENGTH_SHORT);
            		}
            		
            	} catch(Exception e){
            		showDialog(DIALOG_INPUT_ERROR);
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
        			userInput.setText(null);
        			
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
    
    protected AlertDialog onCreateDialog(int id) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	
    	switch(id){
    	case SERIOUS_ERROR:
    		dialog.setMessage("")
    		  .setCancelable(false)
    		  .setTitle("")
    		  .setPositiveButton("", new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int id) {
    				  startup.this.finish();
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