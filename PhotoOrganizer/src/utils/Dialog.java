package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialog {
	public static DialogInterface.OnClickListener positive_listner = null;
	public static DialogInterface.OnClickListener negative_listner = null;

	public Dialog(Context c, String text, String title) {
		dialog(c, text, title, "Yes", "No");
	}

	public static AlertDialog.Builder dialog(Context c, String text,
			String title) {

		return dialog(c, text, title, "Yes", "No");

	}

	public static AlertDialog.Builder dialog(Context c, String text,
			String title, String positive, String negative) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(c);

		dialog.setTitle(title);
		dialog.setMessage(text);
		dialog.setCancelable(true);
		dialog.setPositiveButton(positive, positive_listner);
		dialog.setNegativeButton(negative, negative_listner);
		dialog.create();
		dialog.show();

		return dialog;

	}
}
