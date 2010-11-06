package be.vbsteven.qccommon;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import be.vbsteven.quickcopy.DBHelper;
import be.vbsteven.quickcopy.Global;
import be.vbsteven.quickcopy.R;

import com.admob.android.ads.AdView;

public class EntryListActivity extends Activity {

	private static final int REQUEST_NEW_ENTRY = 0;
	private static final int REQUEST_EDIT_ENTRY = 2;

	private static final int CONTEXT_MENU_EDIT = 3;
	private static final int CONTEXT_MENU_DELETE = 4;
	private static final int CONTEXT_MENU_SHARE = 5;

	private EntryListAdapter entryAdapter;
	private Spinner spinner;

	private Entry entryForContextMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		QuickcopyUtils.setTheme(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setTitle("Quickcopy - entry list");
		
		if (Global.getPrefs(this).getBoolean("integration.shownotification", false)) {
			startService(new Intent(this, NotificationService.class));
		}
		
		setContentView(R.layout.entrylistactivity);

		init();

		if (Global.isFreeVersion()) {
			initAds();
		}

		showWelcomeMessage();
	}

	private void init() {
		DBHelper db = DBHelper.get(this);
		
		String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "General");
		ArrayList<Entry> entries = db.getEntriesFromGroup(DBHelper.get(this).getGroup(defaultGroup));

		entryAdapter = new EntryListAdapter(this, entries);

		ListView lv = (ListView) findViewById(R.id.listview_entrylist);
		lv.setAdapter(entryAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Entry entry = (Entry) adapterView.getAdapter()
						.getItem(position);
				onEntryClicked(entry);
				return;
			}
		});

		registerForContextMenu(lv);

		fillGroupList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		init();
		
	}

	private void initAds() {
		AdView adview;
		adview = (AdView) findViewById(R.id.ad);
		adview.setVisibility(AdView.VISIBLE);
		if (Global.isLightTheme(this)) {
			adview.setBackgroundColor(Color.WHITE);
			adview.setTextColor(Color.BLACK);
		} else {
			adview.setBackgroundColor(Color.BLACK);
			adview.setTextColor(Color.WHITE);
		}
	}

	protected void onEntryClicked(Entry entry) {
		QuickcopyUtils.copyToClipBoard(this, entry.value);
		Toast.makeText(
				this,
				"The value of item \"" + entry.key
						+ "\" is copied to the clipboard", Toast.LENGTH_LONG)
				.show();
		finish();
	}

	protected void onEntryLongClicked(Entry entry) {

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		entryForContextMenu = (Entry) entryAdapter.getItem(info.position);
		menu.setHeaderTitle(entryForContextMenu.key);
		menu.add(0, CONTEXT_MENU_EDIT, 0, "Edit entry");
		menu.add(0, CONTEXT_MENU_SHARE, 0, "Share entry");
		menu.add(0, CONTEXT_MENU_DELETE, 0, "Delete entry");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXT_MENU_EDIT:
			Intent i = new Intent(this, NewEntryActivity.class);
			i.putExtra(Global.QUICKCOPY_ENTRY_ID, entryForContextMenu.id);
			startActivityForResult(i, REQUEST_EDIT_ENTRY);
			break;
		case CONTEXT_MENU_DELETE:
			DBHelper.get(this).deleteEntry(entryForContextMenu.id);
			refreshList();
			break;
		case CONTEXT_MENU_SHARE:
			Entry entry = DBHelper.get(this).getEntry(entryForContextMenu.id);
			if (entry != null) {
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("text/plain");
				share.putExtra(Intent.EXTRA_TEXT, entry.value);
				startActivity(share);
			}
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "New entry").setIcon(R.drawable.ic_menu_add);
		menu.add(0, 1, 0, "New group").setIcon(R.drawable.ic_menu_add);
		menu.add(0, 4, 0, "Group management").setIcon(R.drawable.ic_menu_archive);
		menu.add(0, 2, 1, "Preferences")
				.setIcon(R.drawable.ic_menu_preferences);
		menu.add(0, 3, 1, "Help").setIcon(R.drawable.ic_menu_info_details);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:
			if (spinner.getAdapter().getCount()  > 0) {
				Intent i = new Intent(this, NewEntryActivity.class);
				i.putExtra(Global.QUICKCOPY_GROUP,
						((Group) spinner.getSelectedItem()).name);
				startActivityForResult(i,		REQUEST_NEW_ENTRY);
			} else {
				// there is no group
				new AlertDialog.Builder(EntryListActivity.this)
					.setTitle("No groups available")
					.setMessage("Please add a group first before you add new entries")
					.setPositiveButton("OK", null)
					.create().show();
			}
			break;
		case 1:
			showAddGroupDialog();
			break;
		case 2:
			startActivity(new Intent(this, Preferences.class));
			break;
		case 3:
			startActivity(new Intent(this, HelpActivity.class));
			break;
		case 4:
			startActivity(new Intent(this, GroupManagementActivity.class));
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_NEW_ENTRY && resultCode == RESULT_OK) {
			refreshList();
		} else if (requestCode == REQUEST_EDIT_ENTRY && resultCode == RESULT_OK) {
			refreshList();
		}
	}

	private void refreshList() {
		ArrayList<Entry> entries = DBHelper.get(this).getEntriesFromGroup(
				(Group) spinner.getSelectedItem());
		entryAdapter.updateEntries(entries);
		updateNoEntriesTextView();
	}

	private void fillGroupList() {
		DBHelper db = DBHelper.get(this);
		ArrayList<Group> groups = db.getGroups();
		
		spinner = (Spinner)findViewById(R.id.spinner_category);
		ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, groups);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				changeGroup((Group)adapterView.getAdapter().getItem((int)id));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "");
		setSpinnerToGroup(defaultGroup);
	}

	private void changeGroup(Group group) {
		ArrayList<Entry> entries = DBHelper.get(this)
				.getEntriesFromGroup(group);
		entryAdapter.updateEntries(entries);
		updateNoEntriesTextView();
	}

	private void updateNoEntriesTextView() {
		TextView tv = (TextView)findViewById(R.id.tv_noentries);
		if (entryAdapter.isEmpty()) {
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	private void showAddGroupDialog() {
		final View v = View.inflate(this, R.layout.addgroupdialog, null);
		final EditText text = (EditText) v.findViewById(R.id.et_groupname);
		new AlertDialog.Builder(this).setTitle("Add group").setView(text)
				.setPositiveButton("Add", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = text.getText().toString();
						addGroup(name);
						fillGroupList();
						setSpinnerToGroup(name);
					}
				}).setNegativeButton("Cancel", null).show();
	}

	/*
	 * This is ugly I know
	 */
	protected void setSpinnerToGroup(String name) {
		Adapter a = spinner.getAdapter();
		for (int i = 0; i < a.getCount(); i++) {
			Group g = (Group) a.getItem(i);
			if (g.name.equals(name)) {
				spinner.setSelection(i);
				break;
			}
		}
	}

	protected void addGroup(String string) {
		DBHelper.get(this).addGroup(string);
	}
	
	protected void showWelcomeMessage() {
		if (!Global.getPrefs(this).getBoolean("hasDisplayedWelcomeMessage0.8.4", false)) {
			Global.getPrefs(this).edit().putBoolean("hasDisplayedWelcomeMessage0.8.4", true).commit();
			
			AlertDialog d = new AlertDialog.Builder(this).setTitle("Quickcopy v0.8.4")
				.setPositiveButton("OK", null).create();
				if (Global.isFreeVersion()) {
					d.setMessage(getResources().getString(R.string.welcome_message_free));
				} else {
					d.setMessage(getResources().getString(R.string.welcome_message_paid));
				}
				d.show();
		}
	}

}
