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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SqlHelper {

	private static final String DB_NAME = "meidconverter.db";
	private static final int DB_VER = 1;
	
	private Context context;
	private SQLiteDatabase db;
	
	public SqlHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}
	  
	public Cursor findExisting(String search, String field)
	{
		return this.db.query("history", new String[] { "id", "created_at", "meid_dec", "meid_hex", "esn_dec", "esn_hex", "metropcs_spc" }, field+" = '"+search+"'", null, null, null, "created_at asc LIMIT 1");
	}
	
	public long insertHistory(String meid_dec, String meid_hex, String esn_dec, String esn_hex, String metropcs_spc) {
		SQLiteStatement statement = this.db.compileStatement("INSERT INTO history (created_at,meid_dec,meid_hex,esn_dec,esn_hex,metropcs_spc) VALUES(date('now'),'"+meid_dec+"','"+meid_hex+"','"+esn_dec+"','"+esn_hex+"','"+metropcs_spc+"');");
		return statement.executeInsert();
	}
	
	public Cursor getAllHistory() {
		return this.db.query("history", new String[] { "id", "created_at", "meid_dec", "esn_dec", "metropcs_spc" }, null, null, null, null, "created_at asc");
	}
	
	public long clearHistory() {
		SQLiteStatement statement = this.db.compileStatement("DELETE FROM history");
		return statement.executeInsert();
	}
	
	private static class OpenHelper extends SQLiteOpenHelper {
	      OpenHelper(Context context) {
	          super(context, DB_NAME, null, DB_VER);
	       }

	       @Override
	       public void onCreate(SQLiteDatabase db) {
	          db.execSQL("CREATE TABLE history (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `created_at` TEXT NULL, `meid_dec` TEXT NULL, `meid_hex` TEXT NULL, `esn_dec` TEXT NULL, `esn_hex` TEXT NULL, `metropcs_spc` TEXT NULL)");
	       }

	       @Override
	       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	          db.execSQL("DROP TABLE IF EXISTS history");
	          onCreate(db);
	       }
	}
}
