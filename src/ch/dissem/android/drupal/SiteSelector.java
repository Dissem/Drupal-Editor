/**
 * Copyright (C) 2010 christian
 * This file is part of Drupal Editor.
 *
 * Drupal Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Drupal Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drupal Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dissem.android.drupal;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import ch.dissem.android.drupal.model.DAO;
import ch.dissem.android.drupal.model.Site;
import ch.dissem.android.drupal.model.UsersBlog;
import ch.dissem.android.drupal.model.WDAO;

/**
 * @author christian
 * 
 */
public abstract class SiteSelector extends Activity implements
		OnItemSelectedListener {
	public static final String KEY_SITE_LIST = "siteList";
	public static final String KEY_SITE_LIST_SELECTION = "siteListSelection";
	public static final String KEY_DRUPAL_LIST_SELECTION = "drupalListSelection";

	protected ArrayList<UsersBlog> siteList;
	private List<Site> drupalList;
	private int siteListSelection;
	private int drupalListSelection;

	private Spinner drupalInstallations;
	private Spinner blogs;
	private ProgressBar progressBar;

	private WDAO wdao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wdao = new WDAO(this);

		blogs = (Spinner) findViewById(R.id.sites);
		progressBar = (ProgressBar) findViewById(R.id.sites_loader_progress);
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
		drupalList = new DAO(this).getSites();
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

	protected void fillDrupalsSpinner() {
		drupalInstallations = (Spinner) findViewById(R.id.drupals);
		DAO dao = new DAO(this);
		drupalList = dao.getSites();
		if (drupalList.isEmpty()) {
			String url = PreferenceManager.getDefaultSharedPreferences(this)
					.getString("url", null);
			if (url != null) {
				Site imported = new Site(this);
				imported.setName("Default");
				imported.setUrl(url);
				imported.setUsername(PreferenceManager
						.getDefaultSharedPreferences(this).getString(
								"username", null));
				imported.setPassword(PreferenceManager
						.getDefaultSharedPreferences(this).getString(
								"password", null));
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
		drupalInstallations.setAdapter(adapter);
		drupalInstallations.setClickable(true);
		if (!drupalList.isEmpty())
			drupalInstallations.setSelection(drupalListSelection);

		drupalInstallations.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> av, View view, int position,
			long arg3) {
		if (Settings.setSite((Site) av.getSelectedItem()) || siteList == null) {
			siteList = null;
			siteListSelection = 0;
			fillSiteSpinner();
		} else {
			if (!siteList.isEmpty()) {
				updateBlogsSpinner();
			} else {
				blogs.setClickable(false);
				blogs.setEnabled(false);
			}
		}
	}

	protected void fillSiteSpinner() {
		blogs.setEnabled(false);
		for (Button btn : getButtons())
			btn.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);

		final Handler handler = new Handler();
		new Thread() {
			public void run() {
				if (siteList == null) {
					if (Settings.getURI() == null) {
						if (drupalInstallations.getAdapter().isEmpty())
							startActivity(new Intent(SiteSelector.this,
									Settings.class));
						else {
							Intent intentEdit = new Intent(SiteSelector.this,
									EditSite.class);
							intentEdit.putExtra(EditSite.KEY_SITE,
									(Site) drupalInstallations
											.getSelectedItem());
							intentEdit.putExtra(EditSite.KEY_URI_ERROR, true);
							startActivity(intentEdit);
						}
						return;
					}
					siteList = wdao.getUsersBlogs();
					for (UsersBlog blog : siteList)
						wdao.initCategories(blog.getBlogid());
				}
				if (!siteList.isEmpty())
					handler.post(new Runnable() {
						public void run() {
							updateBlogsSpinner();
							for (Button btn : getButtons())
								btn.setEnabled(true);
							progressBar.setVisibility(View.INVISIBLE);
						}
					});
				else
					handler.post(new Runnable() {
						public void run() {
							progressBar.setVisibility(View.INVISIBLE);
						}
					});
			}
		}.start();
	}

	private void updateBlogsSpinner() {
		try {
			ArrayAdapter<UsersBlog> adapter = new ArrayAdapter<UsersBlog>(
					SiteSelector.this, android.R.layout.simple_spinner_item,
					siteList);
			adapter.setDropDownViewResource(//
					android.R.layout.simple_spinner_dropdown_item);
			blogs.setAdapter(adapter);
			blogs.setClickable(true);
			blogs.setSelection(siteListSelection);
			blogs.setEnabled(true);
		} catch (NullPointerException ignore) {
			// If the user selects another site while loading, there can be a
			// NPE - just ignore it.
		}
	}

	/**
	 * Buttons that ought to stay inactive until the sites are loaded.
	 * 
	 * @return
	 */
	protected abstract Button[] getButtons();

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Nothing to do, I presume
	}
}
