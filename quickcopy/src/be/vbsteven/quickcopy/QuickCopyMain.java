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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.admob.android.ads.AdView;

public class QuickCopyMain extends Activity {
	
	private static final int ID_NOTIFICATION = 0;
	
	private static final int ACTIVITY_MAIN = 1;
	private static final int ACTIVITY_PREFS = 2;
	
	private AdView adview;
	private ViewFlipper flipper;
	private GestureDetector gestureDetector;
	private NotificationManager notificationManager;
	
	private Animation leftInAnimation;
	private Animation leftOutAnimation;
	private Animation rightInAnimation;
	private Animation rightOutAnimation;
	
	private int idLastSelectedGroup = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		
		setTitle("Quickcopy");
		
		flipper = (ViewFlipper)findViewById(R.id.flipper);
	    
		gestureDetector = new GestureDetector(new MyGestureListener());
		
		leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
		
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		checkAdAndNotificationSettings();
		
		refresh();
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		} 
		
		return super.onTouchEvent(event);
	}
	

	private void initAd() {
		Log.d(Global.TAG, "Showing ad");
		adview = (AdView)findViewById(R.id.ad);
		adview.setVisibility(AdView.VISIBLE);
	}
	
	private void hideAd() {
		Log.d(Global.TAG, "Hiding ad");
		adview = (AdView) findViewById(R.id.ad);
		adview.setVisibility(AdView.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkAdAndNotificationSettings();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_PREFS) {   
			
		}
	}


	private void checkAdAndNotificationSettings() {
		SharedPreferences prefs = Global.getPrefs(this);
		if (prefs.getBoolean("integration.shownotification", true)) {
			showNotification();
		} else {
			hideNotification();
		}
		
		if (prefs.getBoolean("integration.showads", true)) {
			initAd();
		} else {
			hideAd();
		}
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

	// setup options menu
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuAdd:
			addEntry();
			break;
		case R.id.menuHelp:
			showHelp();
			break;
		case R.id.menuAddGroup:
			addGroup();
			break;
		case R.id.menuPreferences:
			showPreferences();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showPreferences() {
		startActivityForResult(new Intent(this, Preferences.class), ACTIVITY_PREFS);
	}


	private void addGroup() {
		final EditText edit = new EditText(this);
		edit.setEms(20);
		edit.setLines(1);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Add a new group").setView(edit).setCancelable(true)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String value = edit.getText().toString();
								if (!value.equals("")) {
									DBHelper.get(QuickCopyMain.this).addGroup(
											value);
									refresh();
								}
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showHelp() {
		TextView tv = new TextView(this);
		tv
				.setText("1. Use the menu button to add new entries to the list"
						+ "\n\n2. Tap an existing entry to copy that entry onto the clipboard"
						+ "\n\n3. Long press on an existing entry to edit or delete that entry"
						+ "\n\nThis application uses Google Analytics to gather anonymous usage stats.");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Instructions").setView(tv).setCancelable(true)
				.setNeutralButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void addEntry() {
		DBHelper db = DBHelper.get(this);
		final View v = View.inflate(this, R.layout.addentrydialog, null);
		
		ArrayList<Group> groups = db.getGroups();
		groups.add(0, db.getDummyGroup());
		ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, groups);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner spinner = (Spinner)v.findViewById(R.id.dialog_spinner);
		spinner.setAdapter(adapter);
		if (idLastSelectedGroup < 0) {
			spinner.setSelection(0);
		} else {
			spinner.setSelection(idLastSelectedGroup);
		}
		
		final EditText edit = (EditText)v.findViewById(R.id.dialog_edit);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Add a new entry").setView(v).setCancelable(true)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							// TODO in onClick, determine if a group was selected
							public void onClick(DialogInterface dialog, int id) {
								String value = edit.getText().toString();
								if (!value.equals("")) {
									Entry group = (Entry)spinner.getSelectedItem();
									idLastSelectedGroup = (int)spinner.getSelectedItemId();
									if (group.id < 0) {
										// group is empty
//										DBHelper.get(QuickCopyMain.this).addEntry(
//												value);
										refresh();
									} else {
//										DBHelper.get(QuickCopyMain.this).addEntryWithGroup(value, group.id);
//										refresh();
									}
								}
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void editEntry(final Entry entry) {

		final EditText edit = new EditText(this);
		edit.setText(entry.value);
		edit.setEms(20);
		edit.setLines(5);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Edit entry").setView(edit).setCancelable(true)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String value = edit.getText().toString();
								if (!value.equals("")) {
//									DBHelper.get(QuickCopyMain.this).updateEntry(entry.id, value);
									refresh();
								}
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).setNeutralButton("Delete", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String value = edit.getText().toString();
								if (!value.equals("")) {
									DBHelper.get(QuickCopyMain.this).deleteEntry(entry.id);
									refresh();
								}
							}
						});

		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void refresh() {

		DBHelper db = DBHelper.get(this);
		
		ArrayList<Entry> entries = db.getEntries();
		ArrayList<Group> groups = db.getGroups();
		if (entries.size() == 0) {
			TextView tv = (TextView)findViewById(R.id.textEntries);
			tv.setText("Your list of entries is still empty\n\nUse the menu button on your device to add new entries to your Quickcopy list" 
					+  "\n\nItems will be sorted alphabetically");
		} else {
			TextView tv = (TextView)findViewById(R.id.textEntries);
			tv.setText("Tap to copy / Long press to edit");
			
			// clear the flipper
			flipper.removeAllViews();
			
			// add all items
			flipper.addView(createCategoryView("All entries", entries));
			
			// add all groups
			for (Group group: groups) {
				flipper.addView(createCategoryView(group.name, db.getEntriesFromGroup(group)));
			}
			

			
		}
	}

	private void copyToClipBoard(String value) {
		ClipboardManager manager = (ClipboardManager) QuickCopyMain.this
		.getSystemService(CLIPBOARD_SERVICE);
		manager.setText(value);
		Toast t = Toast.makeText(QuickCopyMain.this, "copied \"" + value
				+ "\"" + " to the clipboard", 6000);
		t.show();
		finish();
	}
	
	private class MyGestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				
				flipper.setInAnimation(leftInAnimation);
				flipper.setOutAnimation(leftOutAnimation);
				flipper.showNext();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				flipper.setInAnimation(rightInAnimation);
				flipper.setOutAnimation(rightOutAnimation);
				flipper.showPrevious();
			}
			
			
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
	
	private class EntriesAdapter extends BaseAdapter { 
		
		private ArrayList<Entry> entries;
		
		public EntriesAdapter(ArrayList<Entry> entries) {
			this.entries = entries;
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Entry li = (Entry)entries.get(arg0);
			if (arg1 == null) {
				arg1 = View.inflate(QuickCopyMain.this, R.layout.simple_list_item_1, null);
			}
			((TextView)arg1).setText(li.value);
			return arg1;
		}
		
		@Override
		public long getItemId(int arg0) {
			return entries.get(arg0).id;
		}
		
		@Override
		public Object getItem(int arg0) {
			return entries.get(arg0);
		}
		
		@Override
		public int getCount() {
			return entries.size();
		}
	};
	
	private View createCategoryView(String name, ArrayList<Entry> entries) {
		// init view
		View v = View.inflate(this, R.layout.valuelist, null);
		
		// init textview
		((TextView)v.findViewById(R.id.valuelist_tv)).setText(name);
		
		// init listview
		ListView lv = (ListView) v.findViewById(R.id.valuesList);
		lv.setOnTouchListener(touchListener);
		EntriesAdapter adapter = new EntriesAdapter(entries);
		Log.d("quickcopy", "adapter size: " + adapter.getCount());
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Entry item = (Entry)arg0.getAdapter().getItem(arg2);
				copyToClipBoard(item.value);
			}


		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Entry item = (Entry)arg0.getAdapter().getItem(arg2);
				editEntry(item);
				return false;
			}

		});
		
		
		return v;
	}
	
	public void showNotification() {
		Log.d(Global.TAG, "Showing notification");
		PendingIntent pi = PendingIntent.getActivity(this, ACTIVITY_MAIN, new Intent(this, QuickCopyMain.class), Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification notification = new Notification(R.drawable.icon, "Quickcopy", System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(this, "Quickcopy", "Click to view your text snippets", pi);
		notificationManager.notify(ID_NOTIFICATION, notification);
	
	}
	
	public void hideNotification() {
		Log.d(Global.TAG, "Hide notification");
		notificationManager.cancelAll();
	}
}

