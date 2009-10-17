package ch.dissem.android.drupal;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	public static String getURL(Context context) {
		String result = PreferenceManager.getDefaultSharedPreferences(context)
				.getString("url", null);
		if (result == null)
			return null;
		if (!result.contains("://"))
			result = "http://" + result;
		if (!result.matches(".*[a-zA-Z0-9]/[a-zA-Z0-9].*\\.[a-zA-Z]+$")) {
			if (result.endsWith("/"))
				return result + "xmlrpc.php";
			else
				return result + "/xmlrpc.php";
		}
		return result;
	}

	public static String getUserName(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString("username", null);
	}

	public static String getPassword(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString("password", null);
	}

	public static int getHistorySize(Context context) {
		return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(
				context).getString("history_size", "10"));
	}
}
