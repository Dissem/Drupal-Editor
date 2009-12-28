package ch.dissem.android.drupal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.dissem.android.drupal.model.CategoryInfo;
import ch.dissem.android.drupal.model.Post;
import ch.dissem.android.drupal.model.Tag;
import ch.dissem.android.drupal.model.WDAO;
import ch.dissem.android.utils.MultiChoice;

public class EditPost extends Activity implements OnClickListener {
	private boolean showTagWarning = true;

	public static final String KEY_BLOG_ID = "blogid";
	public static final String KEY_POST = "post";
	private String blogid;
	private Post post;
	private EditText content;

	private WDAO wdao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_post);

		View saveButton = findViewById(R.id.save_post);
		saveButton.setOnClickListener(this);

		post = getIntent().getParcelableExtra(KEY_POST);
		blogid = getIntent().getStringExtra(KEY_BLOG_ID);
		content = (EditText) findViewById(R.id.Text);

		if (post != null) {
			EditText title = (EditText) findViewById(R.id.Title);
			title.setText(post.getTitle());
			String text = replaceLinks(post.getDescription());
			text = removeSignature(text);
			content.setText(text);
		}
		wdao = new WDAO(this);
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
		case R.id.taxonomy:
			MultiChoice<CategoryInfo> dlg = new MultiChoice<CategoryInfo>(this, wdao.getCategories(blogid), post.getCategories());
			dlg.show();
			// TODO: not yet implemented
			Toast.makeText(this, R.string.todo, Toast.LENGTH_LONG);
			return false;
		case R.id.tag_em:
			insertTag("<em>", null, "</em>");
			return true;
		case R.id.tag_strong:
			insertTag("<strong>", null, "</strong>");
			return true;
		case R.id.tag_menu:
			startActivityForResult(new Intent(this, TagList.class),
					TagList.REQUEST_CODE);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED)
			return;

		if (requestCode == LocationDialog.REQUEST_CODE) {
			double lat = data.getDoubleExtra(LocationDialog.LATITUDE, 0);
			double lng = data.getDoubleExtra(LocationDialog.LONGITUDE, 0);
			insertTag(new StringBuilder("[").append(lat).append(",")
					.append(lng).append("|"), //
					getResources().getString(R.string.location_link_text), "]");
		} else if (requestCode == TagList.REQUEST_CODE) {
			Tag tag = data.getParcelableExtra(TagList.TAG);
			insertTag(tag.getStartTag(), tag.getDefaultText(), tag.getEndTag());
		}
	}

	public void onClick(View v) {
		if (post == null)
			post = new Post();
		post.setTitle(String.valueOf(((TextView) findViewById(R.id.Title))
				.getText()));
		post.setDescription(addSignature(replaceShorts(String
				.valueOf(((TextView) findViewById(R.id.Text)).getText()))));

		wdao.save(post, blogid, ((CheckBox) findViewById(R.id.publish))
				.isChecked());
		finish();
	}

	/**
	 * Replace links with shortcuts in text
	 * 
	 * @param text
	 * @return
	 */
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

	/**
	 * @param link
	 * @return shortcut string for link
	 */
	private String getShorts(String link) {
		String[] p = getLinkPattern();
		link = link.replace(p[0], "[");
		link = link.replace(p[1], ",");
		link = link.replace(p[2], "|");
		link = link.replace(p[3], "]");
		return link;
	}

	/**
	 * Replace shortcuts with real links
	 * 
	 * @param text
	 * @return
	 */
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

	/**
	 * Remove signature to save space on screen.
	 * 
	 * @param text
	 * @return
	 */
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

	/**
	 * Add the signature
	 * 
	 * @param text
	 * @return
	 */
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

	private void insertTag(CharSequence startTag, CharSequence text,
			CharSequence endTag) {
		int endTagPos = content.getSelectionEnd();
		int startTagPos = content.getSelectionStart();
		int selectionLength = endTagPos - startTagPos;
		content.getText().insert(endTagPos, endTag);
		if (endTagPos == startTagPos && text != null)
			content.getText().insert(startTagPos, text);
		content.getText().insert(startTagPos, startTag);

		int dq = getDoubleQuote(startTag);
		if (dq > 0)
			content.setSelection(startTagPos + dq);
		else {
			startTagPos = content.getSelectionStart();
			content.setSelection(startTagPos, startTagPos + selectionLength);
		}

		if (showTagWarning && startTag.length() > 0
				&& startTag.charAt(0) == '<') {
			Toast toast = Toast.makeText(this, R.string.tag_warning,
					Toast.LENGTH_LONG);
			toast.show();
			showTagWarning = false;
		}
	}

	/**
	 * @param tag
	 * @return the first position in a "", or -1 if there is no occurence
	 */
	private int getDoubleQuote(CharSequence tag) {
		for (int i = 0; i < tag.length() - 1; i++) {
			if (tag.charAt(i) == '"' && tag.charAt(i + 1) == '"')
				return i + 1;
		}
		return -1;
	}
}
