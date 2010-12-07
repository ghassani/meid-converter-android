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
 * 
 */

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
	
	private Button calculateButton,clearButton;
	private SqlHelper db;
	private Cursor history;
	private TableLayout historyTable;

	private static final int DIALOG_CLEAR_HISTORY = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.history);
      
        calculateButton = (Button) findViewById(R.id.calculate);
        clearButton = (Button) findViewById(R.id.clearDB);
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
        		 Intent calculate = new Intent(v.getContext(), Calculate.class);
                 startActivityForResult(calculate, 0);
        	}
        });
        
        
        
        clearButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		showDialog(DIALOG_CLEAR_HISTORY);
        	}
       });

    }
    
    /* @function onCreateDialog
     * @param int id
    */
    @Override
    protected AlertDialog onCreateDialog(int id) {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    	
    	switch(id){
    	case DIALOG_CLEAR_HISTORY:
    		dialog.setMessage(getText(R.string.clearHistoryMessage).toString())
    		  .setCancelable(false)
    		  .setTitle(getText(R.string.alertClearHistoryTitle).toString())
    		  .setPositiveButton(getText(R.string.alertButtonConfirm).toString(), new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int id) {
    				  db.clearHistory();
    			      Intent calculate = new Intent(getApplicationContext(), Calculate.class);
    			      startActivityForResult(calculate, 0);
    			  }
    		  }).setNegativeButton(getText(R.string.alertButtonCancel).toString(), new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int id) {
    				  return;
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
