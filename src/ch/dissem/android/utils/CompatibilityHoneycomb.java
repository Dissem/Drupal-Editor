/**
 * Copyright (C) 2013 chris
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
package ch.dissem.android.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

/**
 * @author chris
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CompatibilityHoneycomb {
	public static void displayHomeAdUp(Activity activity) {
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
