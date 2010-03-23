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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import ch.dissem.android.drupal.model.UsersBlog;
import ch.dissem.android.utils.CustomExceptionHandler;

public class Main extends SiteSelector implements OnClickListener {

	private Button btnNew;
	private Button btnRecent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
				this, Thread.getDefaultUncaughtExceptionHandler()));
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);

		btnNew = (Button) findViewById(R.id.new_button);
		btnNew.setOnClickListener(this);
		btnRecent = (Button) findViewById(R.id.recent_button);
		btnRecent.setOnClickListener(this);
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
	protected Button[] getButtons() {
		return new Button[] { btnNew, btnRecent };
	}
}