package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewEntryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.newentryactivity);
		
		Button but = (Button)findViewById(R.id.but_create);
		but.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createEntry();
			}
		});
		
		fillGroupList();
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
}
