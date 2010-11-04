package com.splicedmedia.mobile.meidconverter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.database.Cursor;

public class history extends Activity{
	
	private Button calculateButton;
	private SqlHelper db;
	private Cursor history;
	private ListView historyList;
	private String historyListItems[];
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.history);
        
        calculateButton = (Button) findViewById(R.id.calculate);
        historyList = (ListView) findViewById(R.id.historyList);
        
        try{
        	db = new SqlHelper(this);
        	
        	history = db.getAllHistory();

        	if(history.getCount() > 0 && history.moveToFirst()) {
        		int i=0;
        		do {
        			
        			historyListItems[i] = "1";
        			i++;
        			
        		}while(history.moveToNext());
        		
        		//historyList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , historyListItems));
        	}
        	
        }catch(Exception e)
        {
        	displayAlert(e.getMessage(),"","");
        }
        


        calculateButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		 Intent startup = new Intent(v.getContext(), startup.class);
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
                history.this.finish();
           }
       });
    	AlertDialog alert = dialog.create();
    	alert.show();
    }
}
