package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewEntryActivity extends Activity {

	private Entry entry; // entry for the case we are called to edit an existing entry
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.newentryactivity);
		fillGroupList();

		
		if (getIntent().hasExtra(Global.QUICKCOPY_ENTRY_ID)) {
			// we are called with an id, this is an edit
			Button but2 = (Button)findViewById(R.id.but_save);
			but2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					saveEntry();
				}
			});
			but2.setVisibility(View.VISIBLE);
			
			entry = DBHelper.get(this).getEntry(getIntent().getIntExtra(Global.QUICKCOPY_ENTRY_ID, 0));
			
			EditText valueText = (EditText)findViewById(R.id.et_value);
			EditText titleText = (EditText)findViewById(R.id.et_title);
			CheckBox passwordBox = (CheckBox)findViewById(R.id.checkbox_password);
			
			valueText.setText(entry.value);
			titleText.setText(entry.key);
			passwordBox.setChecked(entry.hidden);
			Group g = DBHelper.get(this).getGroup(entry.group);
			if (g != null) {
				setSpinnerToGroup(g.name);
			}
		} else {
			Button but = (Button)findViewById(R.id.but_create);
			but.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					createEntry();
				}
			});
			but.setVisibility(View.VISIBLE);
		}
		
		
		
	}
	
	
	protected void saveEntry() {
		EditText titleText = (EditText)findViewById(R.id.et_title);
		EditText valueText = (EditText)findViewById(R.id.et_value);
		
		if (titleText.getText().toString().equals("") || valueText.getText().toString().equals("")) {
			QuickcopyUtils.showUserDialog(this, "Error creating entry", "Some of the fields are empty.");
			return;
		}
		
		CheckBox passwordBox = (CheckBox)findViewById(R.id.checkbox_password);
		
		Spinner s = (Spinner)findViewById(R.id.spinner_category);
		Group g = (Group)s.getSelectedItem();
		
		Log.e("quickcopy", "id of entry: " + entry.id);
		entry.key = titleText.getText().toString();
		entry.value = valueText.getText().toString();
		entry.hidden = passwordBox.isChecked();
		entry.group = g.id;
		DBHelper.get(this).updateEntry(entry); 
		
		Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
		
		setResult(RESULT_OK);
		finish();
		
		
		return;
	}


	protected void createEntry() {
		EditText titleText = (EditText)findViewById(R.id.et_title);
		EditText valueText = (EditText)findViewById(R.id.et_value);
		
		if (titleText.getText().toString().equals("") || valueText.getText().toString().equals("")) {
			QuickcopyUtils.showUserDialog(this, "Error creating entry", "Some of the fields are empty.");
			return;
		}
		
		CheckBox passwordBox = (CheckBox)findViewById(R.id.checkbox_password);
		
		Spinner s = (Spinner)findViewById(R.id.spinner_category);
		Group g = (Group)s.getSelectedItem();
		
		DBHelper.get(this).addEntry(titleText.getText().toString(), valueText.getText().toString(), passwordBox.isChecked(), g);
		
		Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
		
		setResult(RESULT_OK);
		finish();
		
		
		return;
	}

	private void fillGroupList() {
		DBHelper db = DBHelper.get(this);
		ArrayList<Group> groups = db.getGroups();
		groups.add(0, db.getDummyGroup());
		
		Spinner s = (Spinner)findViewById(R.id.spinner_category);
		ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, groups);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
	}
	
	/*
	 * This is ugly I know
	 */
	protected void setSpinnerToGroup(String name) {
		Spinner spinner = (Spinner)findViewById(R.id.spinner_category);
		Adapter a = spinner.getAdapter();
		for (int i = 0; i < a.getCount(); i++) {
			Group g = (Group)a.getItem(i);
			if (g.name.equals(name)) {
				spinner.setSelection(i);
				break;
			}
		}
	}
}
