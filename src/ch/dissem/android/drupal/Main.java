/**
 * Copyright (C) 2010 Christian Meyer
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

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import ch.dissem.android.drupal.model.UsersBlog;

public class Main extends SiteSelector implements OnClickListener {

	private AdView adView;

	private Button btnNew;
	private Button btnRecent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);

		btnNew = (Button) findViewById(R.id.new_button);
		btnNew.setOnClickListener(this);
		btnRecent = (Button) findViewById(R.id.recent_button);
		btnRecent.setOnClickListener(this);

		if (Settings.isShowAds(this))
			setUpAds();
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
			intentRecent.putExtra(EditPost.KEY_BLOG_ID,
					((UsersBlog) contentTypes.getSelectedItem()).getBlogid());
			intentRecent.putExtra(KEY_CONTENT_TYPE_LIST, contentTypeList);
			startActivity(intentRecent);
			break;
		}
	}

	@Override
	protected Button[] getButtons() {
		return new Button[] { btnNew, btnRecent };
	}

	private void setUpAds() {
		try {
			adView = new AdView(this, AdSize.SMART_BANNER, getString(R.string.admob));
			AdRequest adRequest = new AdRequest();
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR); // Emulator
			adRequest.addTestDevice("D8DAABC966F7DF36A94AF57F1F809AE7"); // Transformer
			adRequest.addTestDevice("82CC01D4D1D7EFD209DB33A58DD10EF1"); // GalaxyNexus
			adRequest.addTestDevice("5827EEF4F3AEF72339B86D1A2A3193AC"); // Note 10.1

			adView.loadAd(adRequest);

			LinearLayout main = (LinearLayout) findViewById(R.id.ad_space);
			main.addView(adView);
		} catch (Exception e) {
			// Ignore all exceptions, just don't display any ads...
			Log.d("ads", e.getMessage());
		}
	}
}