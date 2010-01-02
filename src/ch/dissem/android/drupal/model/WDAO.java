package ch.dissem.android.drupal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			XMLRPCClient client = new XMLRPCClient(Settings.getURL());
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
		if (post == null || post.getPostid() == null)
			return;

		XMLRPCClient client = new XMLRPCClient(Settings.getURL());
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
	}

	public void save(Post post, String blogid, boolean publish) {
		try {
			XMLRPCClient client = new XMLRPCClient(Settings.getURL());
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
		List<CategoryInfo> availableCategories = categoryInfo.get(blogid);
		if (availableCategories == null) {
			return loadCategory(blogid);
		}
		return availableCategories;
	}

	@SuppressWarnings("unchecked")
	protected ArrayList<CategoryInfo> loadCategory(String blogid) {
		ArrayList<CategoryInfo> availableCategories;
		try {
			XMLRPCClient client = new XMLRPCClient(Settings.getURL());
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

			synchronized (categoryInfo) {
				categoryInfo.put(blogid, availableCategories);
			}
		} catch (XMLRPCException e) {
			handleException(e, "Could not get category list");
			availableCategories = new ArrayList<CategoryInfo>();
		}
		return availableCategories;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<UsersBlog> getUsersBlogs() {
		try {
			ArrayList<UsersBlog> usersBlogs;
			XMLRPCClient client = new XMLRPCClient(Settings.getURL());
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
		XMLRPCClient client = new XMLRPCClient(Settings.getURL());
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
}
