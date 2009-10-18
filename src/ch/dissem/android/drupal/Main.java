package ch.dissem.android.drupal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import ch.dissem.android.drupal.model.Site;
import ch.dissem.android.drupal.model.SiteDAO;
import ch.dissem.android.drupal.model.UsersBlog;

public class Main extends Activity implements OnClickListener,
		OnItemSelectedListener {
	public static final String BLOGGER_API_KEY = "0123456789ABCDEF";
	public static final String KEY_SITE_LIST = "siteList";
	public static final String KEY_SITE_LIST_SELECTION = "siteListSelection";
	public static final String KEY_DRUPAL_LIST_SELECTION = "drupalListSelection";
	private ArrayList<UsersBlog> siteList;
	private List<Site> drupalList;
	private int siteListSelection;
	private int drupalListSelection;

	private Button btnNew;
	private Button btnRecent;
	private Spinner blogs;
	private ProgressBar progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnNew = (Button) findViewById(R.id.new_button);
		btnNew.setOnClickListener(this);
		btnRecent = (Button) findViewById(R.id.recent_button);
		btnRecent.setOnClickListener(this);

		blogs = (Spinner) findViewById(R.id.sites);
		progressBar = (ProgressBar) findViewById(R.id.sites_loader_progress);
	}

	protected void fillDrupalsSpinner() {
		Spinner drupals = (Spinner) findViewById(R.id.drupals);
		SiteDAO dao = new SiteDAO(this);
		drupalList = dao.getSites();
		if (drupalList.isEmpty()) {
			if (Settings.getURL(this) != null) {
				Site imported = new Site();
				imported.setName("Default");
				imported.setUrl(Settings.getURL(this));
				imported.setUsername(Settings.getUserName(this));
				imported.setPassword(Settings.getPassword(this));
				dao.save(imported);
				drupalList.add(imported);
			} else {
				startActivity(new Intent(this, Settings.class));
				return;
			}
		}
		ArrayAdapter<Site> adapter = new ArrayAdapter<Site>(this,
				android.R.layout.simple_spinner_item, dao.getSites());
		adapter.setDropDownViewResource(//
				android.R.layout.simple_spinner_dropdown_item);
		drupals.setAdapter(adapter);
		drupals.setClickable(true);
		if (!drupalList.isEmpty())
			drupals.setSelection(drupalListSelection);

		drupals.setOnItemSelectedListener(this);
	}

	protected void fillSiteSpinner() {
		blogs.setEnabled(false);
		btnNew.setEnabled(false);
		btnRecent.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);

		final Handler handler = new Handler();
		new Thread() {
			@SuppressWarnings("unchecked")
			public void run() {
				try {
					if (siteList == null) {
						String url = Settings.getURL(Main.this);
						if (url == null) {
							startActivity(new Intent(Main.this, Settings.class));
							return;
						}
						XMLRPCClient client = new XMLRPCClient(url);
						Object[] result = (Object[]) client.call(
								"blogger.getUsersBlogs", BLOGGER_API_KEY,
								Settings.getUserName(Main.this), Settings
										.getPassword(Main.this));
						siteList = new ArrayList<UsersBlog>();
						for (Object map : result) {
							siteList.add(new UsersBlog((Map) map));
						}
					}
					handler.post(new Runnable() {
						public void run() {
							ArrayAdapter<UsersBlog> adapter = new ArrayAdapter<UsersBlog>(
									Main.this,
									android.R.layout.simple_spinner_item,
									siteList);
							adapter.setDropDownViewResource(//
									android.R.layout.simple_spinner_dropdown_item);
							blogs.setAdapter(adapter);
							blogs.setClickable(true);
							blogs.setSelection(siteListSelection);
							blogs.setEnabled(true);
							btnNew.setEnabled(true);
							btnRecent.setEnabled(true);
							progressBar.setVisibility(View.INVISIBLE);
						}
					});
				} catch (XMLRPCException e) {
					Log.e("xmlrpc", "XMLRPC fehlgeschlagen", e);
					handler.post(new Runnable() {
						public void run() {
							progressBar.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}.start();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(KEY_SITE_LIST, siteList);
		outState.putInt(KEY_SITE_LIST_SELECTION,
				((Spinner) findViewById(R.id.sites)).getSelectedItemPosition());
		outState.putInt(KEY_DRUPAL_LIST_SELECTION,
				((Spinner) findViewById(R.id.drupals))
						.getSelectedItemPosition());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		siteList = savedInstanceState.getParcelableArrayList(KEY_SITE_LIST);
		siteListSelection = savedInstanceState.getInt(KEY_SITE_LIST_SELECTION);
		drupalList = new SiteDAO(this).getSites();
		drupalListSelection = savedInstanceState
				.getInt(KEY_DRUPAL_LIST_SELECTION);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillDrupalsSpinner();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.reload_sites:
			siteList = null;
			siteListSelection = 0;
			fillSiteSpinner();
			return true;
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		default:
			return false;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_button:
			Intent intentEdit = new Intent(this, EditPost.class);
			intentEdit.putExtra(EditPost.KEY_BLOG_ID,
					((UsersBlog) ((Spinner) findViewById(R.id.sites))
							.getSelectedItem()).getBlogid());
			startActivity(intentEdit);
			break;
		case R.id.recent_button:
			Intent intentRecent = new Intent(this, RecentEntries.class);
			Spinner sites = (Spinner) findViewById(R.id.sites);
			intentRecent.putExtra(EditPost.KEY_BLOG_ID, ((UsersBlog) sites
					.getSelectedItem()).getBlogid());
			intentRecent.putExtra(KEY_SITE_LIST, siteList);
			intentRecent.putExtra(KEY_SITE_LIST_SELECTION, sites
					.getSelectedItemPosition());
			startActivity(intentRecent);
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> av, View view, int position,
			long arg3) {
		if (Settings.setSite((Site) av.getSelectedItem())) {
			siteList = null;
			siteListSelection = 0;
			fillSiteSpinner();
		} else {
			ArrayAdapter<UsersBlog> adapter = new ArrayAdapter<UsersBlog>(
					Main.this, android.R.layout.simple_spinner_item, siteList);
			adapter.setDropDownViewResource(//
					android.R.layout.simple_spinner_dropdown_item);
			blogs.setAdapter(adapter);
			blogs.setSelection(siteListSelection);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Nothing to do, I presume
	}
}