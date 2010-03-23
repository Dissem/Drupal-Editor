/**
 * Copyright (C) 2010 christian
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

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * @author christian
 * 
 */
public class ThreadingUtils {
	public static void showToast(Handler handler, final Context context,
			final int resId, final int duration) {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(context, resId, duration).show();
			}
		});
	}
}
