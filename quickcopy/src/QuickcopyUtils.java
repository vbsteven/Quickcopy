import android.app.AlertDialog;
import android.content.Context;


public class QuickcopyUtils {
	public static void showUserDialog(Context context, String title, String message) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("OK", null).show();
	}
}
