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
package ch.dissem.android.drupal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import ch.dissem.android.drupal.R;
import ch.dissem.android.drupal.Settings;

public class WDAO {
	public static final String BLOGGER_API_KEY = "0123456789ABCDEF";

	private Map<String, List<CategoryInfo>> categoryInfo;
	private Map<String, Lock> categoryInfoPreloadLocks = new HashMap<String, Lock>();

	private Context ctx;
	private Handler handler;

	public WDAO(Context context) {
		ctx = context;
		handler = new Handler();
		categoryInfo = new HashMap<String, List<CategoryInfo>>();
	}

	@SuppressWarnings("unchecked")
	public Post[] getPosts(String blogid) {
		try {
			XMLRPCClient client = new XMLRPCClient(Settings.getURI());
			Object[] results = (Object[]) client.call(
					"metaWeblog.getRecentPosts", blogid, //
					Settings.getUserName(), //
					Settings.getPassword(), //
					Settings.getHistorySize(ctx));
			final Post[] posts = new Post[results.length];
			for (int i = 0; i < results.length; i++) {
				posts[i] = new Post((Map) results[i]);
			}
			return posts;
		} catch (XMLRPCException e) {
			handleException(e, "Could not get post categories");
		}
		return new Post[0];
	}

	public void setCategories(Post post) {
		Log.d(getClass().getSimpleName(), "setCategories started");
		if (post == null || post.getPostid() == null)
			return;

		XMLRPCClient client = new XMLRPCClient(Settings.getURI());
		Object[] categories;
		try {
			categories = (Object[]) client.call("mt.getPostCategories", post
					.getPostid(), Settings.getUserName(), Settings
					.getPassword());
		} catch (XMLRPCException e) {
			handleException(e, "Could not load categories for post "
					+ post.getPostid());
			categories = null;
		}
		post.setCategories(categories);
		Log.d(getClass().getSimpleName(), "setCategories finished");
	}

	public void save(Post post, String blogid, boolean publish) {
		try {
			XMLRPCClient client = new XMLRPCClient(Settings.getURI());
			if (post.getPostid() == null)
				client.call("metaWeblog.newPost", blogid, Settings
						.getUserName(), Settings.getPassword(), post.getMap(),
						publish);
			else
				client.call("metaWeblog.editPost", post.getPostid(), //
						Settings.getUserName(), Settings.getPassword(), //
						post.getMap(), publish);
			if (post.isCategoriesSet())
				client.call("mt.setPostCategories", post.getPostid(), //
						Settings.getUserName(), Settings.getPassword(), //
						post.getCategoriesAsMap());
		} catch (XMLRPCException e) {
			handleException(e, "Could not send post");
		}
	}

	public void initCategories(String... blogids) {
		for (final String blogid : blogids)
			new Thread() {
				public void run() {
					loadCategory(blogid);
				};
			}.start();
	}

	public List<CategoryInfo> getCategories(String blogid) {
		Lock lock = getLock(blogid);
		lock.lock();

		List<CategoryInfo> availableCategories = categoryInfo.get(blogid);
		if (availableCategories == null) {
			return loadCategory(blogid);
		}

		lock.unlock();
		return availableCategories;
	}

	@SuppressWarnings("unchecked")
	protected ArrayList<CategoryInfo> loadCategory(String blogid) {
		Lock lock = getLock(blogid);
		lock.lock();
		ArrayList<CategoryInfo> availableCategories;
		try {
			XMLRPCClient client = new XMLRPCClient(Settings.getURI());
			Object[] res = (Object[]) client.call("mt.getCategoryList", blogid, //
					Settings.getUserName(), Settings.getPassword());
			Arrays.sort(res, new Comparator<Object>() {
				@Override
				public int compare(Object object1, Object object2) {
					return object1.toString().compareTo(object2.toString());
				}
			});
			availableCategories = new ArrayList<CategoryInfo>(res.length);
			for (Object c : res)
				availableCategories.add(new CategoryInfo(
						(Map<String, Object>) c));

			categoryInfo.put(blogid, availableCategories);
		} catch (XMLRPCException e) {
			handleException(e, "Could not get category list");
			availableCategories = new ArrayList<CategoryInfo>();
		}
		lock.unlock();
		return availableCategories;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<UsersBlog> getUsersBlogs() {
		try {
			ArrayList<UsersBlog> usersBlogs;
			XMLRPCClient client = new XMLRPCClient(Settings.getURI());
			Object[] result = (Object[]) client.call("blogger.getUsersBlogs",
					BLOGGER_API_KEY, Settings.getUserName(), Settings
							.getPassword());
			usersBlogs = new ArrayList<UsersBlog>(result.length);
			for (Object map : result) {
				usersBlogs.add(new UsersBlog((Map) map));
			}
			return usersBlogs;
		} catch (XMLRPCException e) {
			handleException(e, "Could not get users blogs");
			return new ArrayList<UsersBlog>();
		}
	}

	public boolean delete(Post post) {
		XMLRPCClient client = new XMLRPCClient(Settings.getURI());
		try {
			client.call("blogger.deletePost", BLOGGER_API_KEY, //
					post.getPostid(), //
					Settings.getUserName(), //
					Settings.getPassword(), false);
			return true;
		} catch (XMLRPCException e) {
			handleException(e, "Could not delete post " + post.getPostid());
			return false;
		}
	}

	protected void handleException(final Throwable e, String msg) {
		Log.e("WDAO", msg, e);
		handler.post(new Runnable() {
			@Override
			public void run() {
				Builder alertBuilder = new Builder(ctx);
				if (e instanceof XMLRPCFault
						&& ((XMLRPCFault) e).getFaultCode() == 1) {
					alertBuilder.setMessage(R.string.xmlrpc_fault_1);
				} else {
					alertBuilder.setMessage(R.string.wdao_fault);
				}
				alertBuilder.create().show();
			}
		});
	}

	private synchronized Lock getLock(String blogid) {
		Lock lock = categoryInfoPreloadLocks.get(blogid);
		if (lock == null) {
			lock = new ReentrantLock();
			categoryInfoPreloadLocks.put(blogid, lock);
		}
		return lock;

	}
}
