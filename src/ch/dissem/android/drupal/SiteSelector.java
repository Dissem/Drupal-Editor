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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
 */
public abstract class SiteSelector extends Activity implements
		OnItemSelectedListener {
	public static final String KEY_CONTENT_TYPE_LIST = "contentTypeList";

	protected ArrayList<UsersBlog> contentTypeList;
	private List<Site> drupalSiteList;

	private Spinner drupalSites;
	protected Spinner contentTypes;
	private ProgressBar progressBar;

	private int selectedSite;
	private int selectedType;

	private DAO dao;
	private WDAO wdao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wdao = new WDAO(this);

		drupalSites = (Spinner) findViewById(R.id.drupals);
		dao = new DAO(this);
		contentTypes = (Spinner) findViewById(R.id.sites);
		contentTypes.setEnabled(false);
		progressBar = (ProgressBar) findViewById(R.id.sites_loader_progress);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(KEY_CONTENT_TYPE_LIST, contentTypeList);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		contentTypeList = savedInstanceState
				.getParcelableArrayList(KEY_CONTENT_TYPE_LIST);
		drupalSiteList = new DAO(this).getSites();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		editor.putInt("selectedSite", drupalSites.getSelectedItemPosition());
		editor.putInt("selectedType", contentTypes.getSelectedItemPosition());
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		selectedSite = preferences.getInt("selectedSite", 0);
		selectedType = preferences.getInt("selectedType", 0);
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
			contentTypeList = null;
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
		drupalSiteList = dao.getSites();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (drupalSiteList.isEmpty()) {
			String url = preferences.getString("url", null);
			if (url != null) {
				Site imported = new Site(this);
				imported.setName("Default");
				imported.setUrl(url);
				imported.setUsername(preferences.getString(DAO.USERNAME, null));
				imported.setPassword(preferences.getString(DAO.PASSWORD, null));
				dao.save(imported);
				drupalSiteList.add(imported);
			} else {
				startActivity(new Intent(this, Settings.class));
				return;
			}
		}
		ArrayAdapter<Site> adapter = new ArrayAdapter<Site>(this,
				android.R.layout.simple_spinner_item, dao.getSites());
		adapter.setDropDownViewResource(//
				android.R.layout.simple_spinner_dropdown_item);
		drupalSites.setAdapter(adapter);
		if (selectedSite >= 0 && selectedSite < drupalSites.getCount())
			drupalSites.setSelection(selectedSite);
		drupalSites.setClickable(true);

		drupalSites.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> av, View view, int position,
			long arg3) {
		if (position != selectedSite) {
			selectedSite = position;
			selectedType = 0;
		}
		if (Settings.setSite((Site) av.getSelectedItem())
				|| contentTypeList == null) {
			contentTypeList = null;
			fillSiteSpinner();
		} else {
			if (!contentTypeList.isEmpty()) {
				updateContentTypeSpinner();
			} else {
				contentTypes.setClickable(false);
				contentTypes.setEnabled(false);
			}
		}
	}

	protected void fillSiteSpinner() {
		contentTypes.setEnabled(false);
		for (Button btn : getButtons())
			btn.setEnabled(false);
		progressBar.setVisibility(View.VISIBLE);

		final Handler handler = new Handler();
		new Thread() {
			public void run() {
				if (contentTypeList == null) {
					if (Settings.getURI() == null) {
						if (drupalSites.getAdapter().isEmpty())
							startActivity(new Intent(SiteSelector.this,
									Settings.class));
						else {
							Intent intentEdit = new Intent(SiteSelector.this,
									EditSite.class);
							intentEdit.putExtra(EditSite.KEY_SITE,
									(Site) drupalSites.getSelectedItem());
							intentEdit.putExtra(EditSite.KEY_URI_ERROR, true);
							startActivity(intentEdit);
						}
						return;
					}
					contentTypeList = wdao.getUsersBlogs();
					for (UsersBlog blog : contentTypeList)
						wdao.initCategories(blog.getBlogid());
				}
				if (!contentTypeList.isEmpty())
					handler.post(new Runnable() {
						public void run() {
							updateContentTypeSpinner();
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

	private void updateContentTypeSpinner() {
		try {
			ArrayAdapter<UsersBlog> adapter = new ArrayAdapter<UsersBlog>(
					SiteSelector.this, android.R.layout.simple_spinner_item,
					contentTypeList);
			adapter.setDropDownViewResource(//
					android.R.layout.simple_spinner_dropdown_item);
			contentTypes.setAdapter(adapter);

			if (selectedType >= 0 && selectedType < contentTypes.getCount())
				contentTypes.setSelection(selectedType);

			contentTypes.setClickable(true);
			contentTypes.setEnabled(true);
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
