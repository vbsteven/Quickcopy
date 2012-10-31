package be.vbsteven.qccommon;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import be.vbsteven.quickcopyfull.DBHelper;
import be.vbsteven.quickcopyfull.Global;
import android.content.Context;
import android.util.Log;

public class QuickcopyExporter {
	public static boolean exportData(Context context, String filename) {
		try {
		File f = new File("/sdcard/quickcopybackup.xml");
		FileOutputStream out = new FileOutputStream(f);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		
		writer.write("<groups>\n");
		
		ArrayList<Group> groups = DBHelper.get(context).getGroups();
		for (Group g: groups) {
			writer.write("<group action=\"" + g.name + "\">\n");
			ArrayList<Entry> entries = DBHelper.get(context).getEntriesFromGroup(g);
			for (Entry e: entries) {
				writer.write("<entry><title>" + e.key + "</title><value>" + e.value + "</value></entry>\n"); // TODO: fix encoding!
			}
			writer.write("</group>\n");
		}
		writer.write("</groups>");
		writer.close();
		} catch (FileNotFoundException e) {
			Log.e(Global.TAG, "File not found while trying to backup");
		} catch (IOException e) {
			Log.e(Global.TAG, "IOException while trying to backup");
		}
		
		return true;
	}
}
