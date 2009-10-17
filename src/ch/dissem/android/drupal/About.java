package ch.dissem.android.drupal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class About extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		// Set up click listeners
		View cancelButton = findViewById(R.id.about_close);
		cancelButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		finish();
	}
}
