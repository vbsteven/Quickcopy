package be.vbsteven.qccommon;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import be.vbsteven.quickcopyfull.DBHelper;
import be.vbsteven.quickcopyfull.Global;
import com.actionbarsherlock.app.SherlockListFragment;

import java.util.ArrayList;

public class EntryListFragment extends SherlockListFragment {


    private static final int CONTEXT_MENU_EDIT = 3;
    private static final int CONTEXT_MENU_DELETE = 4;
    private static final int CONTEXT_MENU_SHARE = 5;

    private DBHelper mDbHelper;
    private Group mGroup;
    private Entry mEntryForContextMenu;



    public EntryListFragment(Group group) {
        this.mGroup = group;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mDbHelper = DBHelper.get(getActivity());

        refreshList();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(getListView());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        setEmptyText("No entries");


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Entry entry = (Entry) l.getAdapter().getItem(position);
        onEntryClicked(entry);
    }

    protected void onEntryClicked(Entry entry) {
        QuickcopyUtils.copyToClipBoard(getActivity(), entry.value);
        Toast.makeText(
                getActivity(),
                "The value of item \"" + entry.key
                        + "\" is copied to the clipboard", Toast.LENGTH_LONG)
                .show();
        getActivity().finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        mEntryForContextMenu = (Entry) getListAdapter().getItem(info.position);
        menu.setHeaderTitle(mEntryForContextMenu.key);
        menu.add(0, CONTEXT_MENU_EDIT, 0, "Edit entry");
        menu.add(0, CONTEXT_MENU_SHARE, 0, "Share entry");
        menu.add(0, CONTEXT_MENU_DELETE, 0, "Delete entry");
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                Intent i = new Intent(getActivity(), NewEntryActivity.class);
                i.putExtra(Global.QUICKCOPY_ENTRY_ID, mEntryForContextMenu.id);
                startActivityForResult(i, EntryListActivity.REQUEST_EDIT_ENTRY);
                return true;
            case CONTEXT_MENU_DELETE:
                DBHelper.get(getActivity()).deleteEntry(mEntryForContextMenu.id);
                refreshList();
                return true;
            case CONTEXT_MENU_SHARE:
                Entry entry = DBHelper.get(getActivity()).getEntry(mEntryForContextMenu.id);
                if (entry != null) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, entry.value);
                    startActivity(share);
                }
                return true;
        }

        return super.onContextItemSelected(item);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        refreshList();
    }

    public void refreshList() {
        if (mDbHelper != null) {
            ArrayList<Entry> entries = mDbHelper.getEntriesFromGroup(mGroup);
            EntryListAdapter adapter = new EntryListAdapter(getActivity(), entries);
            setListAdapter(adapter);
        }
    }

    public Group getGroup() {
        return mGroup;
    }
}
