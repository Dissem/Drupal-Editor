package ch.dissem.android.drupal.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.util.Log;
import ch.dissem.android.drupal.R;
import ch.dissem.android.drupal.Settings;

public class WDAO {
	private List<CategoryInfo> availableCategories;

	private Context ctx;

	public WDAO(Context context) {
		ctx = context;
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
				Object[] categories = (Object[]) client.call(
						"mt.getPostCategories", posts[i].getPostid(), Settings
								.getUserName(), Settings.getPassword());
				posts[i].setCategories(categories);
			}
			return posts;
		} catch (XMLRPCException e) {
			if (e instanceof XMLRPCFault
					&& ((XMLRPCFault) e).getFaultCode() == 1) {
				Builder alertBuilder = new Builder(ctx);
				alertBuilder.setMessage(R.string.xmlrpc_fault_1);
				alertBuilder.create().show();
			} else
				Log.e("sendPost", "Could not send post", e);
		}
		return new Post[0];
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
			if (post.getCategories() != null)
				client.call("mt.setPostCategories", post.getPostid(), //
						Settings.getUserName(), Settings.getPassword(), //
						post.getCategoriesAsMap());
		} catch (XMLRPCException e) {
			if (e instanceof XMLRPCFault
					&& ((XMLRPCFault) e).getFaultCode() == 1) {
				Builder alertBuilder = new Builder(ctx);
				alertBuilder.setMessage(R.string.xmlrpc_fault_1);
				alertBuilder.create().show();
			} else
				Log.e("sendPost", "Could not send post", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CategoryInfo> getCategories(String blogid) {
		if (availableCategories == null) {
			availableCategories = new LinkedList<CategoryInfo>();
			try {
				XMLRPCClient client = new XMLRPCClient(Settings.getURL());
				Object[] res = (Object[]) client.call("mt.getCategoryList",
						blogid, //
						Settings.getUserName(), Settings.getPassword());
				for (Object c : res)
					availableCategories.add(new CategoryInfo(
							(Map<String, Object>) c));
			} catch (XMLRPCException e) {
				if (e instanceof XMLRPCFault
						&& ((XMLRPCFault) e).getFaultCode() == 1) {
					Builder alertBuilder = new Builder(ctx);
					alertBuilder.setMessage(R.string.xmlrpc_fault_1);
					alertBuilder.create().show();
				} else
					Log.e("getCategoryList", "Could not send post", e);
			}
		}
		return availableCategories;
	}
}
