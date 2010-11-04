package com.splicedmedia.mobile.meidconverter;
/**
 * @author Ghassan Idriss
 * @copyright 2010 Spliced Media L.L.C
 * @website http://www.splicedmedia.com
 * @package com.splicedmedia.mobile.meidconverter
 * @subpackage SqlHelper
 */
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
