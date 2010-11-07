package com.splicedmedia.android.meidconverter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

public class History extends Activity{
	
	private Button calculateButton,clearbutton;
	private SqlHelper db;
	private Cursor history;
	private TableLayout historyTable;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.history);
        
        calculateButton = (Button) findViewById(R.id.calculate);
        historyTable = (TableLayout) findViewById(R.id.table);
        
        try{
        	db = new SqlHelper(this);
        	
        	history = db.getAllHistory();

        	if(history.getCount() > 0 ) {
        		history.moveToFirst();
        		int i=0;
        		do {
        			TableRow tr = new TableRow(this);
        			tr.setLayoutParams(new LayoutParams(
                            LayoutParams.FILL_PARENT,
                            LayoutParams.WRAP_CONTENT)
            		);
        			
        			TextView td1 = new TextView(this);
        			td1.setPadding(4,4,4,4);
        			td1.setText(history.getString(history.getColumnIndex("created_at")));
        			
        			TextView td2 = new TextView(this);
        			td2.setPadding(4,4,4,4);
        			
        			if(history.getString(history.getColumnIndex("meid_dec")) == " -- "){
        				td2.setText(history.getString(history.getColumnIndex("esn_dec")));
        			} else{
        				td2.setText(history.getString(history.getColumnIndex("meid_dec")));
        			}
        			
        			TextView td3 = new TextView(this);
        			td3.setPadding(4,4,4,4);
        			td3.setText(history.getString(history.getColumnIndex("metropcs_spc")));
        			

        			tr.addView(td1);
        			tr.addView(td2);
        			tr.addView(td3);
        			
        			historyTable.addView(tr);
        			i++;
        			
        		}while(history.moveToNext());
        		
        		
        	}
        	
        }catch(Exception e)
        {
        	displayToastNotification(e.getMessage(),Toast.LENGTH_LONG);
        }
        


        calculateButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		 Intent startup = new Intent(v.getContext(), Calculate.class);
                 startActivityForResult(startup, 0);
        	}
        });
        
       
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
                History.this.finish();
           }
       });
    	AlertDialog alert = dialog.create();
    	alert.show();
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
}
