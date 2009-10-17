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
