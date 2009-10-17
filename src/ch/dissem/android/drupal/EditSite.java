package ch.dissem.android.drupal;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import ch.dissem.android.drupal.model.NamedObject;
import ch.dissem.android.drupal.model.Site;
import ch.dissem.android.drupal.model.SiteDAO;
import ch.dissem.android.drupal.model.Site.SignaturePosition;

public class EditSite extends Activity {
	public static final String KEY_SITE = "drupalsite";

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

		username = (EditText) findViewById(R.id.username);
		username.setText(drupal.getUsername());

		password = (EditText) findViewById(R.id.password);
		password.setTag(drupal.getPassword());

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
	@SuppressWarnings("unchecked")
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.site_save:
			drupal.setName(name.getText().toString());
			drupal.setUrl(url.getText().toString());
			drupal.setUsername(username.getText().toString());
			drupal.setPassword(password.getText().toString());
			drupal.setSignature(signature.getText().toString());
			drupal.setSignatureEnabled(useSignature.isChecked());
			drupal.setSignaturePosition(//
					((NamedObject<SignaturePosition>) signaturePos
							.getSelectedItem()).getValue());
			new SiteDAO(this).save(drupal);
			finish();
			return true;
		case R.id.site_cancel:
			finish();
			return true;
		default:
			return false;
		}
	}
}
