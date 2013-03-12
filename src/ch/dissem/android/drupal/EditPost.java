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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.dissem.android.drupal.model.CategoryInfo;
import ch.dissem.android.drupal.model.Post;
import ch.dissem.android.drupal.model.Tag;
import ch.dissem.android.drupal.model.WDAO;
import ch.dissem.android.utils.MultiChoice;
import ch.dissem.android.utils.ThreadingUtils;

public class EditPost extends Activity implements OnClickListener {
	private boolean showTagWarning = true;

	public static final String KEY_BLOG_ID = "blogid";
	public static final String KEY_POST = "post";
	private String blogid;
	private Post post;
	private EditText content;

	private WDAO wdao;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		handler = new Handler();
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_post);

		View saveButton = findViewById(R.id.save_post);
		saveButton.setOnClickListener(this);

		wdao = new WDAO(this);

		post = getIntent().getParcelableExtra(KEY_POST);
		blogid = getIntent().getStringExtra(KEY_BLOG_ID);
		content = (EditText) findViewById(R.id.Text);

		if (post != null) {
			EditText title = (EditText) findViewById(R.id.Title);
			title.setText(post.getTitle());
			String description = post.getDescription();
			if (description != null && description.length() > 0) {
				setText(description);
			} else {
				setProgressBarIndeterminateVisibility(true);
				new Thread() {
					public void run() {
						wdao.updateContent(post);
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (content.getText().length() == 0)
									setText(post.getDescription());
								EditPost.this
										.setProgressBarIndeterminateVisibility(false);
							}
						});
					};
				}.start();
			}
			if (!post.isCategoriesSet()) {
				new Thread() {
					public void run() {
						wdao.setCategories(post);
					};
				}.start();
			}
		} else
			post = new Post();
	}

	private void setText(String description) {
		String text = removeSignature(description);
		text = replaceLinks(text);
		content.setText(text);
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
			if (post.getPostid() != null && !post.isCategoriesSet()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(android.R.string.dialog_alert_title);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setMessage(R.string.taxonomy_warning);
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showTaxonomyDialog();
							}
						});
				builder.setCancelable(true);
				Dialog warning = builder.create();
				warning.show();
			} else {
				showTaxonomyDialog();
			}
			return true;
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

	private void showTaxonomyDialog() {
		EditPost.this.setProgressBarIndeterminate(true);
		final Handler handler = new Handler();
		new Thread() {
			@Override
			public void run() {
				final List<CategoryInfo> categories = wdao
						.getCategories(blogid);
				handler.post(new Runnable() {
					@Override
					public void run() {
						MultiChoice<CategoryInfo> dlg = new MultiChoice<CategoryInfo>(
								EditPost.this, categories, post.getCategories());
						dlg.setTitle(R.string.taxonomy);
						post.setCategoriesSet(true);
						dlg.show();
						EditPost.this.setProgressBarIndeterminate(false);
					}
				});
			}
		}.start();
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
		final String title = String.valueOf(//
				((TextView) findViewById(R.id.Title)).getText());

		final ProgressDialog progress = ProgressDialog.show(EditPost.this,
				EditPost.this.getString(R.string.saving_post), title, false);

		new Thread() {
			@Override
			public void run() {
				if (post == null)
					post = new Post();
				post.setTitle(title);

				String text = String.valueOf(//
						((TextView) findViewById(R.id.Text)).getText());
				post.setDescription(addSignature(replaceShorts(text)));

				if (wdao.save(post, blogid,
						((CheckBox) findViewById(R.id.publish)).isChecked())) {
					ThreadingUtils.showToast(handler, EditPost.this,
							R.string.post_saved, Toast.LENGTH_LONG);
					finish();
				}
				progress.dismiss();
			}
		}.start();
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
				+ link.replace("\\", "\\\\").replace(".", "\\.")
						.replace("?", "\\?").replace("+", "\\+")
						.replace("-", "\\-").replace("&", "\\&")
						.replace("%s", "\\d*\\.?\\d*") + "\">.*?</a>)");
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
	 * @return the first position in a "", or -1 if there is no occurrence
	 */
	private int getDoubleQuote(CharSequence tag) {
		for (int i = 0; i < tag.length() - 1; i++) {
			if (tag.charAt(i) == '"' && tag.charAt(i + 1) == '"')
				return i + 1;
		}
		return -1;
	}
}
