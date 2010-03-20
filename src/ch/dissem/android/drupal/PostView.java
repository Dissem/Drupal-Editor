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
