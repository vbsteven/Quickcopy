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
	private static final String COLUMN_NAME_HIDDEN = "hidden";
	private static final String COLUMN_NAME_KEY = "key";
	private static final String CREATE_TABLE_ENTRIES = "create table " + TABLE_NAME_ENTRIES + " (_id integer primary key autoincrement, " + COLUMN_NAME_VALUE + " text not null);";
	private static final String CREATE_TABLE_GROUPS = "create table " + TABLE_NAME_GROUPS + "(_id integer primary key autoincrement, " + COLUMN_NAME_VALUE + " text not null);";
	private static final String ALTER_TABLE_V1 = "alter table " + TABLE_NAME_ENTRIES + " add " + COLUMN_NAME_GROUP + " integer;";
	private static final String ALTER_TABLE_V2a = "alter table " + TABLE_NAME_ENTRIES + " add " + COLUMN_NAME_HIDDEN + " integer;";
	private static final String ALTER_TABLE_V2b = "alter table " + TABLE_NAME_ENTRIES + " add " + COLUMN_NAME_KEY + " text";
	
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
        
        try {
        	db.execSQL(ALTER_TABLE_V2a);
        } catch (Exception e) {
        	// modification already done;
        }
        
        try {
        	db.execSQL(ALTER_TABLE_V2b);
        } catch (Exception e) {
        	// modification already done
        }

        addDefaultGroup();
	}
	
	private void addDefaultGroup() {
		Group g = getGroup("General");
		if (g == null) {
			// default group does not exist, create
			addGroup("General");
			moveOldEntriesWithoutGroup();
		}
	}
	
	/*
	 * again a very ugly way to do it but I really couldn't figure out
	 * how to do the general UPDATE statement with "WHERE group_ = ?"
	 * 
	 * I'm sorry for all the kittens that have died in the process of 
	 * creating this implementation
	 */
	private void moveOldEntriesWithoutGroup() {
		Group g = getGroup("General");
		
		ArrayList<Entry> entries = getEntries();
		for (Entry e: entries) {
			e.group = g.id;
			updateEntry(e);
		}
		
	}

	public Entry getEntry(int id) {
		String selection = "_id = ?";
		String[] selectionArgs = new String[] {Integer.valueOf(id).toString()};
		Cursor c = db.query(TABLE_NAME_ENTRIES, null, selection, selectionArgs, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId = c.getColumnIndex("_id");
		int hiddenId = c.getColumnIndex(COLUMN_NAME_HIDDEN);
		int keyId = c.getColumnIndex(COLUMN_NAME_KEY);
		int groupId = c.getColumnIndex(COLUMN_NAME_GROUP);
		
		if (c.moveToFirst()) {
			Entry result = new Entry(c.getInt(idId), c.getString(valueId), c.getInt(hiddenId) == 1, c.getString(keyId), c.getInt(groupId));
			c.close();
			return result;
		} else {
			c.close();
			return null;
		}
	}
	
	public Group getGroup(int id) {
		String selection = "_id = ?";
		String[] selectionArgs = new String[] {Integer.valueOf(id).toString()};
		Cursor c = db.query(TABLE_NAME_GROUPS, null, selection, selectionArgs, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId = c.getColumnIndex("_id");
		
		if (c.moveToFirst()) {
			Group g = new Group(c.getInt(idId), c.getString(valueId));
			c.close();
			return g;
		} else {
			c.close();
			return null;
		}
	}
	
	public ArrayList<Entry> getEntries() {
		ArrayList<Entry> result = new ArrayList<Entry>();
		
		Cursor c = db.query(TABLE_NAME_ENTRIES, null, null, null, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId = c.getColumnIndex("_id");
		int hiddenId = c.getColumnIndex(COLUMN_NAME_HIDDEN);
		int keyId = c.getColumnIndex(COLUMN_NAME_KEY);
		int groupId = c.getColumnIndex(COLUMN_NAME_GROUP);
		
		if (c.moveToFirst()) {
			do {
				result.add(new Entry(c.getInt(idId), c.getString(valueId), c.getInt(hiddenId) == 1, c.getString(keyId),  c.getInt(groupId)));
			} while(c.moveToNext());
		}
		c.close();
		
		Collections.sort(result);
		return result;
	}
	
	public ArrayList<Entry> getEntriesFromGroup(Group group) {
		ArrayList<Entry> result = new ArrayList<Entry>();
		
		Cursor c = db.query(TABLE_NAME_ENTRIES, null, COLUMN_NAME_GROUP + " = ?", new String[] {group.id + ""}, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId = c.getColumnIndex("_id");
		int hiddenId = c.getColumnIndex(COLUMN_NAME_HIDDEN);
		int keyId = c.getColumnIndex(COLUMN_NAME_KEY);
		int groupId = c.getColumnIndex(COLUMN_NAME_GROUP);
		
		if (c.moveToFirst()) {
			do {
				result.add(new Entry(c.getInt(idId), c.getString(valueId), c.getInt(hiddenId) == 1, c.getString(keyId),  c.getInt(groupId)));
			} while(c.moveToNext());
		}
		
		c.close();
		Collections.sort(result);
		return result;
	}
	
	public ArrayList<Group> getGroups() {
		ArrayList<Group> result = new ArrayList<Group>();
		Cursor c = db.query(TABLE_NAME_GROUPS, new String[] {COLUMN_NAME_VALUE, "_id"}, null, null, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId =  c.getColumnIndex("_id");
		if (c.moveToFirst()) {
			do {
				result.add(new Group(c.getInt(idId), c.getString(valueId)));
			} while(c.moveToNext());
		}
		
		Collections.sort(result);
		c.close();
		return result;
	}
	
	public void addEntry(String key, String value, boolean hidden, Group g) {
		SQLiteStatement statement = db.compileStatement("INSERT INTO " + TABLE_NAME_ENTRIES + " VALUES (?, ?, ?, ?, ?)");
		statement.bindNull(1); // id
		statement.bindString(2, value); // value
		statement.bindLong(3, g.id); // group
		statement.bindLong(4, hidden?1:0); // hidden
		statement.bindString(5, key); //key
		statement.execute();
	}
	
	public void updateEntry(Entry entry) {
		SQLiteStatement statement = db.compileStatement("UPDATE " + TABLE_NAME_ENTRIES + " SET value = ?, key = ?, _group = ?, hidden = ? WHERE _id = ?;");
		statement.bindString(1, entry.value);
		statement.bindString(2, entry.key);
		statement.bindLong(3, entry.group);
		statement.bindLong(4, entry.hidden?1:0);
		statement.bindLong(5, entry.id);
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
	
	public Group getGroup(String name) {
		String selection = COLUMN_NAME_VALUE + " = ?";
		String[] selectionArgs = new String[] { name };
		Group result = null;
		Cursor c = db.query(TABLE_NAME_GROUPS, new String[] {COLUMN_NAME_VALUE, "_id"}, selection, selectionArgs, null, null, null);
		int valueId = c.getColumnIndex(COLUMN_NAME_VALUE);
		int idId =  c.getColumnIndex("_id");
		if (c.moveToFirst()) {
				result = new Group(c.getInt(idId), c.getString(valueId));
		}
		
		c.close();
		return result;
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

	public int id;
	public String value;




}
