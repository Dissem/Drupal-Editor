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

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LocationDialog extends Activity implements LocationListener,
		OnClickListener {
	public static final String LONGITUDE = "long";
	public static final String LATITUDE = "lat";
	public static final int REQUEST_CODE = 0x10CA710;
	private LocationManager mgr;
	private String best;
	private Criteria criteria;
	private TextView info;
	private Location lastLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_dialog);
		setTitle(R.string.location_dialog_title);

		mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		criteria = new Criteria();

		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		best = mgr.getBestProvider(criteria, true);
		Log.d("location", best);
		Location location = mgr.getLastKnownLocation(best);
		Log.d("location", String.valueOf(location));

		info = (TextView) findViewById(R.id.location_dialog_info);
		setInfo(location);

		Button btn = (Button) findViewById(R.id.location_insert);
		btn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mgr.requestLocationUpdates(best, 2000, 0, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mgr.removeUpdates(this);
	}

	protected void setInfo(Location location) {
		String time;
		String accuracy;
		if (location == null) {
			time = getResources().getString(R.string.unknown);
			accuracy = getResources().getString(R.string.unknown);
		} else {
			time = new Date(location.getTime()).toLocaleString();
			float acc = location.getAccuracy();
			accuracy = Math.round(acc)
					+ getResources().getString(R.string.accuracy_unit);
		}
		info.setText(getResources().getString(R.string.location_dialog_info,
				accuracy, time));

		lastLocation = location;
	}

	public void onLocationChanged(Location location) {
		setInfo(location);
	}

	public void onProviderDisabled(String provider) {
		best = mgr.getBestProvider(criteria, true);
	}

	public void onProviderEnabled(String provider) {
		best = mgr.getBestProvider(criteria, true);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	public void onClick(View v) {
		if (lastLocation == null) {
			setResult(RESULT_CANCELED);
		} else {
			Intent intent = getIntent();
			intent.putExtra(LATITUDE, lastLocation.getLatitude());
			intent.putExtra(LONGITUDE, lastLocation.getLongitude());
			setResult(RESULT_OK, intent);
		}
		finish();
	}
}
