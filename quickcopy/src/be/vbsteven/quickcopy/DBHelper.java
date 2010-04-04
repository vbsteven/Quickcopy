/*
        Quickcopy: Android app for managing frequently used text snippets
        Copyright (C) 2009 Steven Van Bael

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package be.vbsteven.quickcopy;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


public class DBHelper {
	private static DBHelper instance;
	
	private static final int	DATABASE_VERSION = 1; // database version stored in sharedpreferences
	private static final String DATABASE_NAME = "/data/data/be.vbsteven.quickcopy/databases/quickcopy.db";
	private static final String TABLE_NAME_ENTRIES = "entries";
	private static final String TABLE_NAME_GROUPS = "groups";
	private static final String COLUMN_NAME_VALUE = "value";
	private static final String COLUMN_NAME_GROUP = "_group";
	private static final String CREATE_TABLE_ENTRIES = "create table " + TABLE_NAME_ENTRIES + " (_id integer primary key autoincrement, " + COLUMN_NAME_VALUE + " text not null);";
	private static final String CREATE_TABLE_GROUPS = "create table " + TABLE_NAME_GROUPS + "(_id integer primary key autoincrement, " + COLUMN_NAME_VALUE + " text not null);";
	private static final String ALTER_TABLE_V1 = "alter table " + TABLE_NAME_ENTRIES + " add " + COLUMN_NAME_GROUP + " integer;";
	
	private SQLiteDatabase db;
	
	public static DBHelper get(Context context) {
		
		if (instance == null) { 
			instance = new DBHelper(context);
		} 
		return instance;
	}
	
	private DBHelper(Context context) {
        db = context.openOrCreateDatabase("quickcopy", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        try {
        	db.execSQL(CREATE_TABLE_ENTRIES);
        } catch (Exception e) {
        	// table already exists
        }
        
        try {
        	db.execSQL(CREATE_TABLE_GROUPS);
        } catch (Exception e) {
        	// table already exists
        }
        
        try {
        	db.execSQL(ALTER_TABLE_V1);
        } catch (Exception e) {
        	// modification already done;
        }
	}
	

//	public ArrayList<String> getEntries() {
//		Cursor c = db.query(TABLE_NAME_ENTRIES, new String[] {COLUMN_NAME_VALUE}, null, null, null, null, null);
//        ArrayList<String> values = new ArrayList<String>();
//        int columnId = c.getColumnIndex(COLUMN_NAME_VALUE);
//        if (c.moveToFirst()) {
//        	do {
//        		values.add(c.getString(columnId));
//        	} while (c.moveToNext());
//        }
//        
//        Collections.sort(values);
//        return values;
//	}
	
	public ArrayList<ListItem> getEntries() {
		ArrayList<ListItem> result = new ArrayList<ListItem>();
		
		Cursor c = db.query(TABLE_NAME_ENTRIES, new String[] {COLUMN_NAME_VALUE, "_id"}, null, null, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId = c.getColumnIndex("_id");
		if (c.moveToFirst()) {
			do {
				result.add(new ListItem(c.getInt(idId), c.getString(valueId), ListItem.ITEM));
			} while(c.moveToNext());
		}
		
		return result;
	}
	
	public ArrayList<ListItem> getEntriesFromGroup(ListItem group) {
ArrayList<ListItem> result = new ArrayList<ListItem>();
		
		Cursor c = db.query(TABLE_NAME_ENTRIES, new String[] {COLUMN_NAME_VALUE, "_id"}, COLUMN_NAME_GROUP + " = ?", new String[] {group.id + ""}, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId = c.getColumnIndex("_id");
		if (c.moveToFirst()) {
			do {
				result.add(new ListItem(c.getInt(idId), c.getString(valueId), ListItem.ITEM));
			} while(c.moveToNext());
		}
		
		return result;
	}
	
	public ArrayList<ListItem> getGroups() {
		ArrayList<ListItem> result = new ArrayList<ListItem>();
		Cursor c = db.query(TABLE_NAME_GROUPS, new String[] {COLUMN_NAME_VALUE, "_id"}, null, null, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId =  c.getColumnIndex("_id");
		if (c.moveToFirst()) {
			do {
				result.add(new ListItem(c.getInt(idId), c.getString(valueId), ListItem.FOLDER));
			} while(c.moveToNext());
		}
		
		Collections.sort(result);
		return result;
		
	}
	
	public ListItem getDummyGroup() {
		ListItem group = new ListItem(-1, "No group", ListItem.FOLDER);
		return group;
	}

	public void addEntry(String entry) {
		SQLiteStatement statement = db.compileStatement("INSERT INTO " + TABLE_NAME_ENTRIES + " VALUES (?, ?, ?)");
		statement.bindNull(1);
		statement.bindString(2, entry);
		statement.bindNull(3);
		statement.execute();
	}
	
	public void addEntryWithGroup(String entry, int groupId) {
		SQLiteStatement statement = db.compileStatement("INSERT INTO " + TABLE_NAME_ENTRIES + " VALUES (?, ?, ?)");
		statement.bindNull(1);
		statement.bindString(2, entry);
		statement.bindString(3, groupId + "");
		statement.execute();
	}
	
	public void updateEntry(int id, String newValue) {
		SQLiteStatement statement = db.compileStatement("UPDATE " + TABLE_NAME_ENTRIES + " SET value = ? WHERE _id = ?;");
		statement.bindString(1, newValue);
		statement.bindLong(2, id);
		statement.execute();
	}

	public void deleteEntry(int id) {
		SQLiteStatement statement = db.compileStatement("DELETE FROM " + TABLE_NAME_ENTRIES + " WHERE _id = ?;");
		statement.bindLong(1, id);
		statement.execute();
	}

	public void addGroup(String value) {
		SQLiteStatement statement = db.compileStatement("INSERT INTO " + TABLE_NAME_GROUPS + " VALUES (?, ?)");
		statement.bindNull(1);
		statement.bindString(2, value);
		statement.execute();
	}
	
	public void deleteGroup(int id) {
		SQLiteStatement statement = db.compileStatement("DELETE FROM " + TABLE_NAME_GROUPS + " WHERE _id = ?;");
		statement.bindLong(1, id);
		statement.execute();
	}
	
	public void updateGroup(int id, String newValue) {
		SQLiteStatement statement = db.compileStatement("UPDATE " + TABLE_NAME_GROUPS + " SET value = ? WHERE _id = ?;");
		statement.bindString(1, newValue);
		statement.bindLong(2, id);
		statement.execute();
	}
}
