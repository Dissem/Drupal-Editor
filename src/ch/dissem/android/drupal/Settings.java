package ch.dissem.android.drupal;

import java.net.URI;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import ch.dissem.android.drupal.model.Site;
import ch.dissem.android.drupal.model.DAO;
import ch.dissem.android.drupal.model.Site.SignaturePosition;

public class Settings extends Activity implements OnClickListener {
	private static final String HISTORY_SIZE = "history_size";
	private static Site selected;
	private static Editor settingsEditor;

	private ListView list;
	private DAO dao;

	/**
	 * @param selected
	 * @return <code>true</code> if settings actually changed
	 */
	public static boolean setSite(Site selected) {
		try {
			return selected == null ? Settings.selected != null : !selected
					.equals(Settings.selected);
		} finally {
			Settings.selected = selected;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_sites);

		dao = new DAO(this);
		List<Site> drupals = dao.getSites();
		if (drupals.isEmpty()) {
			editSite(new Site(this));
			return;
		}

		settingsEditor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		final EditText hsize = (EditText) findViewById(R.id.history_size);
		hsize.setText(String.valueOf(getHistorySize(this)));
		hsize.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				settingsEditor.putString(HISTORY_SIZE, hsize.getText()
						.toString());
				return false;
			}
		});

		list = (ListView) findViewById(R.id.site_list);

		Button btn = (Button) findViewById(R.id.add_site);
		btn.setOnClickListener(this);
		registerForContextMenu(list);
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
		if (list == null)
			list = (ListView) findViewById(R.id.site_list);
		list.setAdapter(new ArrayAdapter<Site>(this,
				android.R.layout.simple_list_item_1, dao.getSites()));
	}

	@Override
	protected void onPause() {
		super.onPause();
		settingsEditor.commit();
	}

	public static URI getURI() {
		try {
			if (selected == null)
				return null;
			String result = selected.getUrl();
			if (result == null)
				return null;
			if (!result.contains("://"))
				result = "http://" + result;
			if (!result.matches(".*[a-zA-Z0-9]/[a-zA-Z0-9].*\\.[a-zA-Z]+$")) {
				if (result.endsWith("/"))
					return URI.create(result + "xmlrpc.php");
				else
					return URI.create(result + "/xmlrpc.php");
			}
			return URI.create(result);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static String getUserName() {
		if (selected == null)
			return null;
		return selected.getUsername();
	}

	public static String getPassword() {
		if (selected == null)
			return null;
		return selected.getPassword();
	}

	public static int getHistorySize(Context context) {
		try {
			return Integer.valueOf(PreferenceManager
					.getDefaultSharedPreferences(context).getString(
							HISTORY_SIZE, "10"));
		} catch (NumberFormatException e) {
			return 10;
		}
	}

	public static boolean isSignatureEnabled() {
		if (selected == null)
			return false;
		return selected.isSignatureEnabled();
	}

	public static SignaturePosition getSignaturePosition() {
		if (selected == null)
			return null;
		return selected.getSignaturePosition();
	}

	public static String getSignature() {
		if (selected == null)
			return null;
		return selected.getSignature();
	}

	@Override
	public void onClick(View v) {
		editSite(new Site(this));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, R.string.edit, 0, R.string.edit);
		menu.add(Menu.NONE, R.string.delete, 1, R.string.delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		try {
			info = (AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e("ctxMenu", "bad menuInfo", e);
			return false;
		}
		final Site site = (Site) list.getAdapter().getItem(info.position);

		switch (item.getItemId()) {
		case R.string.edit:
			Intent intentEdit = new Intent(this, EditSite.class);
			intentEdit.putExtra(EditSite.KEY_SITE, site);
			startActivity(intentEdit);
			return true;
		case R.string.delete:
			dao.delete(site);
			if (site.isSame(selected))
				selected = null;
			list.setAdapter(new ArrayAdapter<Site>(this,
					android.R.layout.simple_list_item_1, dao.getSites()));
			return true;
		default:
			return false;
		}
	}
}
