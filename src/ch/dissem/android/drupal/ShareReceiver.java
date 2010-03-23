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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import ch.dissem.android.drupal.model.Post;
import ch.dissem.android.drupal.model.UsersBlog;

/**
 * Activity to handle "shared" texts.
 * 
 * @author christian
 */
public class ShareReceiver extends SiteSelector implements OnClickListener {
	private Button btnNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.share_receiver);
		super.onCreate(savedInstanceState);

		btnNew = (Button) findViewById(R.id.new_button);
		btnNew.setOnClickListener(this);
	}

	@Override
	protected Button[] getButtons() {
		return new Button[] { btnNew };
	}

	@Override
	public void onClick(View v) {
		Intent intentEdit = new Intent(this, EditPost.class);

		String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
		String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		Post post = new Post();
		post.setTitle(title);
		post.setDescription(text);
		intentEdit.putExtra(EditPost.KEY_BLOG_ID,
				((UsersBlog) ((Spinner) findViewById(R.id.sites))
						.getSelectedItem()).getBlogid());
		intentEdit.putExtra(EditPost.KEY_POST, post);
		startActivity(intentEdit);
	}
}
