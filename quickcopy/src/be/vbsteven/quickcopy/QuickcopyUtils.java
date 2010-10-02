package be.vbsteven.quickcopy;
import android.app.AlertDialog;
import android.content.Context;
import android.text.ClipboardManager;


public class QuickcopyUtils {
	
	public static void showUserDialog(Context context, String title, String message) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("OK", null).show();
	}
	
	public static void copyToClipBoard(Context context, String value) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		manager.setText(value);
	}
	
}


