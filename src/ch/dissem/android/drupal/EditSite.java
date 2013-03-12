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

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import ch.dissem.android.drupal.model.DAO;
import ch.dissem.android.drupal.model.NamedObject;
import ch.dissem.android.drupal.model.Site;
import ch.dissem.android.drupal.model.Site.SignaturePosition;

public class EditSite extends Activity {
	public static final String KEY_SITE = "drupalsite";
	public static final String KEY_URI_ERROR = "uriError";

	private Site drupal;

	private EditText name;
	private EditText url;
	private EditText username;
	private EditText password;
	private EditText signature;
	private CheckBox useSignature;
	private Spinner signaturePos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_site);

		drupal = getIntent().getParcelableExtra(KEY_SITE);

		name = (EditText) findViewById(R.id.site_name);
		name.setText(drupal.getName());

		url = (EditText) findViewById(R.id.site_url);
		url.setText(drupal.getUrl());
		if (getIntent().getBooleanExtra(KEY_URI_ERROR, false)) {
			url.requestFocus();
			url.selectAll();
		}

		username = (EditText) findViewById(R.id.username);
		username.setText(drupal.getUsername());

		password = (EditText) findViewById(R.id.password);
		password.setText(drupal.getPassword());

		signature = (EditText) findViewById(R.id.signature);
		signature.setText(drupal.getSignature());

		useSignature = (CheckBox) findViewById(R.id.show_signature);
		useSignature.setChecked(drupal.isSignatureEnabled());

		signaturePos = (Spinner) findViewById(R.id.signature_position);
		ArrayList<NamedObject<SignaturePosition>> data = new ArrayList<NamedObject<SignaturePosition>>(
				2);
		for (SignaturePosition sp : SignaturePosition.values()) {
			data.add(new NamedObject<SignaturePosition>(sp.toString(this), sp));
		}
		ArrayAdapter<NamedObject<SignaturePosition>> adapter = new ArrayAdapter<NamedObject<SignaturePosition>>(
				this, android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(//
		android.R.layout.simple_spinner_dropdown_item);
		signaturePos.setAdapter(adapter);

		SignaturePosition sp = drupal.getSignaturePosition();
		for (int i = 0; i < data.size(); i++)
			if (data.get(i).equals(sp))
				signaturePos.setSelection(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_site, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.site_save:
			save();
			finish();
			return true;
		case R.id.site_cancel:
			finish();
			return true;
		default:
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private void save() {
		drupal.setName(name.getText().toString());
		drupal.setUrl(url.getText().toString());
		drupal.setUsername(username.getText().toString());
		drupal.setPassword(password.getText().toString());
		drupal.setSignature(signature.getText().toString());
		drupal.setSignatureEnabled(useSignature.isChecked());
		drupal.setSignaturePosition(//
		((NamedObject<SignaturePosition>) signaturePos.getSelectedItem())
				.getValue());
		new DAO(this).save(drupal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
