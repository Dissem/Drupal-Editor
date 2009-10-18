package ch.dissem.android.drupal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import ch.dissem.android.drupal.model.Post;

public class EditPost extends Activity implements OnClickListener {
	private XMLRPCClient client;

	public static final String KEY_BLOG_ID = "blogid";
	public static final String KEY_POST = "post";
	private String blogid;
	private Post post;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_post);

		client = new XMLRPCClient(Settings.getURL());

		View saveButton = findViewById(R.id.save_post);
		saveButton.setOnClickListener(this);

		post = getIntent().getParcelableExtra(KEY_POST);
		blogid = getIntent().getStringExtra(KEY_BLOG_ID);

		if (post != null) {
			EditText title = (EditText) findViewById(R.id.Title);
			title.setText(post.getTitle());
			EditText content = (EditText) findViewById(R.id.Text);
			String text = replaceLinks(post.getDescription());
			text = removeSignature(text);
			content.setText(text);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_post, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert_location:
			startActivityForResult(new Intent(this, LocationDialog.class),
					LocationDialog.REQUEST_CODE);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != LocationDialog.REQUEST_CODE
				|| resultCode == RESULT_CANCELED)
			return;

		double lat = data.getDoubleExtra(LocationDialog.LATITUDE, 0);
		double lng = data.getDoubleExtra(LocationDialog.LONGITUDE, 0);
		EditText et = (EditText) findViewById(R.id.Text);
		StringBuilder b = new StringBuilder();
		b.append("[").append(lat).append(",").append(lng).append("|");
		b.append(getResources().getString(R.string.location_link_text));
		b.append("]");

		et.append(b.toString());
	}

	public void onClick(View v) {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put("title", String
				.valueOf(((TextView) findViewById(R.id.Title)).getText()));
		struct.put("link", "");
		struct.put("description", addSignature(replaceShorts(String
				.valueOf(((TextView) findViewById(R.id.Text)).getText()))));
		try {
			if (post == null)
				client.call("metaWeblog.newPost", blogid, Settings
						.getUserName(), Settings.getPassword(), struct,
						((CheckBox) findViewById(R.id.publish)).isChecked());
			else
				client.call("metaWeblog.editPost", post.getPostid(), Settings
						.getUserName(), Settings.getPassword(), struct,
						((CheckBox) findViewById(R.id.publish)).isChecked());
			finish();
		} catch (XMLRPCException e) {
			if (e instanceof XMLRPCFault
					&& ((XMLRPCFault) e).getFaultCode() == 1) {
				Builder alertBuilder = new Builder(this);
				alertBuilder.setMessage(R.string.xmlrpc_fault_1);
				alertBuilder.create().show();
			} else
				Log.e("sendPost", "Could not send post", e);
		}
	}

	// Replacement of location links

	public String replaceLinks(String text) {
		StringBuilder result = new StringBuilder(text);
		String link = getResources().getText(R.string.location_link).toString();
		Pattern p = Pattern.compile("(<a href=\""
				+ link.replace("\\", "\\\\").replace(".", "\\.").replace("?",
						"\\?").replace("+", "\\+").replace("-", "\\-").replace(
						"&", "\\&").replace("%s", "\\d*\\.?\\d*")
				+ "\">.*?</a>)");
		Matcher m = p.matcher(text);
		while (m.find()) {
			result.replace(m.start(), m.end(), getShorts(m.group()));
		}
		return result.toString();
	}

	private String getShorts(String link) {
		String[] p = getLinkPattern();
		link = link.replace(p[0], "[");
		link = link.replace(p[1], ",");
		link = link.replace(p[2], "|");
		link = link.replace(p[3], "]");
		return link;
	}

	public String replaceShorts(String text) {
		StringBuilder result = new StringBuilder(text);
		Pattern p = Pattern.compile("(\\[\\d*\\.?\\d*,\\d*\\.?\\d*\\|.*?\\])");
		Matcher m = p.matcher(text);
		while (m.find()) {
			result.replace(m.start(), m.end(), getLink(m.group()));
		}
		return result.toString();
	}

	private String getLink(String shortLink) {
		String[] p = getLinkPattern();
		shortLink = shortLink.replace("[", p[0]);
		shortLink = shortLink.replace(",", p[1]);
		shortLink = shortLink.replace("|", p[2]);
		shortLink = shortLink.replace("]", p[3]);
		return shortLink;
	}

	private String[] linkPattern = null;

	private String[] getLinkPattern() {
		if (linkPattern == null) {
			String linkString = "<a href=\""
					+ getResources().getText(R.string.location_link)
					+ "\">%s</a>";
			int i0 = 0;
			int i1 = linkString.indexOf("%s", 0);
			linkPattern = new String[4];
			for (int c = 0; c < 3; c++) {
				linkPattern[c] = linkString.substring(i0, i1);
				i0 = i1 + 2;
				i1 = c < 2 ? linkString.indexOf("%s", i0) : linkString.length();
			}
			linkPattern[3] = linkString.substring(i0, i1);
		}
		return linkPattern;
	}

	private String removeSignature(String text) {
		if (Settings.isSignatureEnabled()) {
			String signature = Settings.getSignature();
			switch (Settings.getSignaturePosition()) {
			case START:
				if (text.startsWith(signature)) {
					text = text.substring(signature.length());
				}
				return text;
			case END:
				if (text.endsWith(signature)) {
					text = text
							.substring(0, text.length() - signature.length());
				}
				return text;
			}
		}
		return text;
	}

	private String addSignature(String text) {
		if (Settings.isSignatureEnabled()) {
			String signature = Settings.getSignature();
			switch (Settings.getSignaturePosition()) {
			case START:
				return signature + text;
			case END:
				return text + signature;
			}
		}
		return text;
	}
}
