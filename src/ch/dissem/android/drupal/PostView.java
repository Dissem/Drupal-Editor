package ch.dissem.android.drupal;

import java.util.Date;

import android.content.Context;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostView extends LinearLayout {
	TextView dateView;
	TextView titleView;

	public PostView(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		setPadding(0, 5, 0, 5);

		dateView = new TextView(context);
		dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		addView(dateView);

		titleView = new TextView(context);
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		addView(titleView);
	}

	public void setDate(Date date) {
		dateView.setText(date.toLocaleString());
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}
}
