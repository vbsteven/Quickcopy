package be.vbsteven.qccommon;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import be.vbsteven.quickcopyfull.DBHelper;
import be.vbsteven.quickcopyfull.Global;
import be.vbsteven.quickcopyfull.R;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class EntryListActivity extends SherlockFragmentActivity {


    public static final int REQUEST_EDIT_ENTRY = 2;
    public static final int REQUEST_NEW_ENTRY = 0;

    private ArrayList<EntryListFragment> mFragments;

    private ViewPager mViewPager;
    private PageIndicator mPageIndicator;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Quickcopy");
		
		if (Global.getPrefs(this).getBoolean("integration.shownotification", false)) {
			startService(new Intent(this, NotificationService.class));
		}
		
		setContentView(R.layout.entrylistactivity);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPageIndicator = (PageIndicator) findViewById(R.id.titles);

		init();

		if (Global.isFreeVersion()) {
			initAds();
		}

		showWelcomeMessage();
	}

    private void focusDefaultGroupFragment() {
        String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "General");
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i).getGroup().name.equals(defaultGroup)) {
                mViewPager.setCurrentItem(i, false);
                mPageIndicator.setCurrentItem(i);
                break;
            }
        }
    }

    private void init() {

		refreshGroups();


	}

    private void refreshGroups() {
        DBHelper db = DBHelper.get(this);


        ArrayList<Group> groups = db.getGroups();

        mFragments = new ArrayList<EntryListFragment>();
        for (Group g: groups) {
            EntryListFragment frag = new EntryListFragment(g);
            mFragments.add(frag);
        }

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager())  {

            @Override
            public Fragment getItem(int i) {
                return mFragments.get(i);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragments.get(position).getGroup().name;
            }


        };

        mViewPager.setAdapter(adapter);
        mPageIndicator.setViewPager(mViewPager);


    }
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
		
	}

	private void initAds() {
//		AdView adview;
//		adview = (AdView) findViewById(R.id.ad);
//		adview.setVisibility(AdView.VISIBLE);
//		if (Global.isLightTheme(this)) {
//			adview.setBackgroundColor(Color.WHITE);
//			adview.setTextColor(Color.BLACK);
//		} else {
//			adview.setBackgroundColor(Color.BLACK);
//			adview.setTextColor(Color.WHITE);
//		}
	}



    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.add(0, 0, 0, "New entry").setIcon(R.drawable.ic_menu_add);
        menu.add(0, 1, 0, "New group").setIcon(R.drawable.ic_menu_add);
        menu.add(0, 4, 0, "Group management").setIcon(R.drawable.ic_menu_archive);
        menu.add(0, 2, 1, "Preferences")
                .setIcon(R.drawable.ic_menu_preferences);
        menu.add(0, 3, 1, "Help").setIcon(R.drawable.ic_menu_info_details);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

		switch (item.getItemId()) {
		case 0:
			if (mFragments.size() > 0) {
				Intent i = new Intent(this, NewEntryActivity.class);
                Group group = mFragments.get(mViewPager.getCurrentItem()).getGroup();
				i.putExtra(Global.QUICKCOPY_GROUP, group.name);
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


        // TODO refresh all fragment lists
	}

	private void refreshList() {
		for (EntryListFragment frag: mFragments) {
            frag.refreshList();
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
						refreshGroups();
					}
				}).setNegativeButton("Cancel", null).show();
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

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        focusDefaultGroupFragment();
    }
}
