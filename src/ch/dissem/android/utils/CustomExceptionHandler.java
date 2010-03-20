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
package ch.dissem.android.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import ch.dissem.android.drupal.R;

/**
 * {@link UncaughtExceptionHandler} that asks the user to send an e-mail with
 * some debug information to the developer.
 * 
 * @author christian
 */
public class CustomExceptionHandler implements UncaughtExceptionHandler {
	private static final String RECIPIENT = "chrigu.meyer@gmail.com";

	private Thread.UncaughtExceptionHandler previousHandler;
	private Context context;

	public CustomExceptionHandler(Context ctx, UncaughtExceptionHandler previous) {
		previousHandler = previous;
		context = ctx;
	}

	private StatFs getStatFs() {
		File path = Environment.getDataDirectory();
		return new StatFs(path.getPath());
	}

	private long getAvailableInternalMemorySize(StatFs stat) {
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	private long getTotalInternalMemorySize(StatFs stat) {
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	private void addInformation(StringBuilder message) {
		message.append("Locale: ").append(Locale.getDefault()).append('\n');
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			message.append("Version: ").append(pi.versionName).append('\n');
			message.append("Package: ").append(pi.packageName).append('\n');
		} catch (Exception e) {
			Log.e("CustomExceptionHandler", "Error", e);
			message.append("Could not get Version information for ").append(
					context.getPackageName());
		}
		message.append("Phone Model: ").append(android.os.Build.MODEL).append(
				'\n');
		message.append("Android Version: ").append(
				android.os.Build.VERSION.RELEASE).append('\n');
		message.append("Board: ").append(android.os.Build.BOARD).append('\n');
		message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
		message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
		message.append("Host: ").append(android.os.Build.HOST).append('\n');
		message.append("ID: ").append(android.os.Build.ID).append('\n');
		message.append("Model: ").append(android.os.Build.MODEL).append('\n');
		message.append("Product: ").append(android.os.Build.PRODUCT).append(
				'\n');
		message.append("Type: ").append(android.os.Build.TYPE).append('\n');
		StatFs stat = getStatFs();
		message.append("Total Internal memory: ").append(
				getTotalInternalMemorySize(stat)).append('\n');
		message.append("Available Internal memory: ").append(
				getAvailableInternalMemorySize(stat)).append('\n');
	}

	public void uncaughtException(Thread t, Throwable e) {
		try {
			StringBuilder report = new StringBuilder();
			Date curDate = new Date();
			report.append("Error Report collected on : ").append(
					curDate.toString()).append('\n').append('\n');

			report.append("Informations :").append('\n');
			report.append("==============").append('\n').append('\n');

			addInformation(report);

			report.append('\n').append('\n');
			report.append("Stack:\n");
			report.append("======\n");
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			report.append(result.toString());
			printWriter.close();
			report.append('\n');
			report.append("****  End of current Report ***");
			sendErrorMail(report);
		} catch (Throwable ignore) {
			Log.e(CustomExceptionHandler.class.getName(),
					"Error while sending error e-mail", ignore);
		}
		previousHandler.uncaughtException(t, e);
	}

	private void sendErrorMail(StringBuilder errorContent) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		String subject = context.getResources().getString(R.string.app_name)
				+ " crashed! Fix it!";
		StringBuilder body = new StringBuilder(context.getResources()
				.getString(R.string.app_name));
		body.append('\n').append('\n');
		body.append(errorContent).append('\n').append('\n');
		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { RECIPIENT });
		sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sendIntent.setType("message/rfc822");
		context.startActivity(Intent.createChooser(sendIntent, "Error Report"));
	}
}
