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

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.dissem.android.drupal.model.Post;

public class PostAdapter extends BaseAdapter {
	private Post[] posts;
	private Context ctx;
	private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
			Locale.getDefault());

	public PostAdapter(Context ctx, Post[] posts) {
		this.ctx = ctx;
		this.posts = posts;
	}

	public int getCount() {
		return posts.length;
	}

	public Object getItem(int position) {
		return posts[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View v, ViewGroup parent) {
		if (v == null) {
			v = View.inflate(ctx, R.layout.recent_list_item, null);
		}
		Date date = posts[position].getDateCreated();
		String title = posts[position].getTitle();
		((TextView) v.findViewById(R.id.date)).setText(df.format(date));
		((TextView) v.findViewById(R.id.title)).setText(title);
		return v;
	}
}
