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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ch.dissem.android.drupal.model.Post;

public class PostAdapter extends BaseAdapter {
	private Post[] posts;
	private Context ctx;

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

	public View getView(int position, View convertView, ViewGroup parent) {
		PostView layout;
		if (convertView == null) {
			layout = new PostView(ctx);
		} else {
			layout = (PostView) convertView;
		}
		layout.setDate(posts[position].getDateCreated());
		layout.setTitle(String.valueOf(posts[position].getTitle()));
		return layout;
	}
}
