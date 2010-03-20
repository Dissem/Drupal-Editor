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

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import ch.dissem.android.drupal.model.DAO;
import ch.dissem.android.drupal.model.Tag;

public class TagList extends ListActivity implements OnClickListener {
	public static final int REQUEST_CODE = 0x10CA711;
	public static final String TAG = "tag";
	private List<Tag> tagList;
	private DAO dao;

	private EditText startTag;
	private EditText defaultText;
	private EditText endTag;

	private ListView list;

	private Dialog editTagDialog;
	private Tag editTag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_list);

		dao = new DAO(this);

		list = getListView();
		initList();

		registerForContextMenu(getListView());
		findViewById(R.id.add_tag).setOnClickListener(this);
	}

	private void initList() {
		tagList = dao.getTags();
		list.setAdapter(new ArrayAdapter<Tag>(this,
				android.R.layout.simple_list_item_1, tagList
						.toArray(new Tag[tagList.size()])));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_tag:
			editTag = new Tag();
			openDialog();
			break;
		case R.id.save_tag:
			if (startTag.getText().length() > 0) {
				editTag.setStartTag(startTag.getText().toString());
				editTag.setDefaultText(defaultText.getText().toString());
				editTag.setEndTag(endTag.getText().toString());
				dao.save(editTag);
				editTag = new Tag();
				startTag.setText("");
				endTag.setText("");
				getDialog().hide();
				initList();
			}
			break;
		}
	}

	private void openDialog() {
		Dialog dlg = getDialog();
		startTag.setText(editTag.getStartTag());
		defaultText.setText(editTag.getDefaultText());
		endTag.setText(editTag.getEndTag());
		dlg.show();
	}

	private Dialog getDialog() {
		if (editTagDialog == null) {
			editTagDialog = new Dialog(this);
			editTagDialog.setContentView(R.layout.edit_tag);
			editTagDialog.setTitle(R.string.edit_tag);

			startTag = (EditText) editTagDialog.findViewById(R.id.start_tag);
			defaultText = (EditText) editTagDialog
					.findViewById(R.id.default_text);
			endTag = (EditText) editTagDialog.findViewById(R.id.end_tag);

			editTagDialog.findViewById(R.id.save_tag).setOnClickListener(this);
		}
		return editTagDialog;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Tag tag = (Tag) getListView().getItemAtPosition(position);
		Intent intent = getIntent();
		intent.putExtra(TAG, tag);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, R.string.edit, 0, R.string.edit);
		menu.add(Menu.NONE, R.string.delete, 1, R.string.delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info;
		try {
			info = (AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e("ctxMenu", "bad menuInfo", e);
			return false;
		}
		final Tag tag = (Tag) getListView().getItemAtPosition(info.position);

		switch (item.getItemId()) {
		case R.string.edit:
			editTag = tag;
			openDialog();
			break;
		case R.string.delete:
			dao.delete(tag);
			initList();
			break;
		default:
			return false;
		}
		return true;
	}
}
