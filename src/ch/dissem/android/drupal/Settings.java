package ch.dissem.android.drupal;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import ch.dissem.android.drupal.model.Site;
import ch.dissem.android.drupal.model.SiteDAO;

public class Settings extends Activity implements OnClickListener {
	private static final String HISTORY_SIZE = "history_size";
	private static Site selected;
	private static Editor settingsEditor;

	public static void setSite(Site selected) {
		Settings.selected = selected;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_sites);

		SiteDAO dao = new SiteDAO(this);
		List<Site> drupals = dao.getSites();
		if (drupals.isEmpty()) {
			editSite(new Site());
			return;
		}

		settingsEditor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		final EditText hsize = (EditText) findViewById(R.id.history_size);
		hsize.setText(PreferenceManager.getDefaultSharedPreferences(this)
				.getString(HISTORY_SIZE, "10"));
		hsize.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				settingsEditor.putString(HISTORY_SIZE, hsize.getText()
						.toString());
				return false;
			}
		});

		ListView lv = (ListView) findViewById(R.id.site_list);
		lv.setAdapter(new ArrayAdapter<Site>(this,
				android.R.layout.simple_list_item_1, drupals));

		Button btn = (Button) findViewById(R.id.add_site);
		btn.setOnClickListener(this);
	}

	protected void editSite(Site drupal) {
		Intent intentEdit = new Intent(this, EditSite.class);
		intentEdit.putExtra(EditSite.KEY_SITE, drupal);
		startActivity(intentEdit);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (settingsEditor == null)
			settingsEditor = PreferenceManager
					.getDefaultSharedPreferences(this).edit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		settingsEditor.commit();
	}

	public static String getURL(Context context) {
		if (selected == null)
			return null;
		String result = selected.getUrl();
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
		if (selected == null)
			return null;
		return selected.getUsername();
	}

	public static String getPassword(Context context) {
		if (selected == null)
			return null;
		return selected.getPassword();
	}

	public static int getHistorySize(Context context) {
		return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(
				context).getString(HISTORY_SIZE, "10"));
	}

	@Override
	public void onClick(View v) {
		editSite(new Site());
	}
}
