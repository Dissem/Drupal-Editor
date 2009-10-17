package ch.dissem.android.drupal.model;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;
import java.util.List;

import ch.dissem.android.drupal.model.Site.SignaturePosition;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SiteDAO extends SQLiteOpenHelper {
	public static String DATABASE_NAME = "drupaleditor.db";
	public static int DATABASE_VERSION = 1;
	public static String TABLE_NAME = "sites";

	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String SIGNATURE = "signature";
	public static final String SIGNATURE_ENABLED = "signature_on";
	public static final String SIGNATURE_POSITION = "signature_position";

	public SiteDAO(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, "
				+ URL + " TEXT, " + USERNAME + " TEXT, " + PASSWORD + " TEXT, "
				+ SIGNATURE + " TEXT, " + SIGNATURE_ENABLED + " TEXT, "
				+ SIGNATURE_POSITION + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// FIXME: Remember to change that if DB changes!
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// onCreate(db);
	}

	public void save(Site site) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(NAME, site.getName());
			values.put(URL, site.getUrl());
			values.put(USERNAME, site.getUsername());
			values.put(PASSWORD, site.getPassword());
			values.put(SIGNATURE, site.getSignature());
			values.put(SIGNATURE_ENABLED, String.valueOf(site
					.isSignatureEnabled()));
			Site.SignaturePosition pos = site.getSignaturePosition();
			values.put(SIGNATURE_POSITION, pos == null ? null : pos.toString());
			if (site.getId() != null) {
				// update
				db.update(TABLE_NAME, values, _ID + "=" + site.getId(), null);
			} else {
				// save
				db.insertOrThrow(TABLE_NAME, null, values);
			}
		} finally {
			db.close();
		}
	}

	public void delete(Site site) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(TABLE_NAME, _ID + "=" + site.getId(), null);
		} finally {
			db.close();
		}
	}

	public List<Site> getSites() {
		SQLiteDatabase db = getReadableDatabase();
		try {
			Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
			List<Site> list = new ArrayList<Site>(c.getColumnCount());
			while (c.moveToNext()) {
				Site s = new Site();
				s.setId(c.getInt(0));
				s.setName(c.getString(1));
				s.setUrl(c.getString(2));
				s.setUsername(c.getString(3));
				s.setPassword(c.getString(4));
				s.setSignature(c.getString(5));
				s.setSignatureEnabled(Boolean.parseBoolean(c.getString(6)));
				s.setSignaturePosition(SignaturePosition.parse(c.getString(7)));
				list.add(s);
			}
			c.close();
			return list;
		} finally {
			db.close();
		}
	}
}
