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

public class DAO extends SQLiteOpenHelper {
	public static String DATABASE_NAME = "drupaleditor.db";
	public static int DATABASE_VERSION = 2;

	// Sites table
	public static String SITES_TABLE_NAME = "sites";
	public static final String NAME = "name";
	public static final String URL = "url";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String SIGNATURE = "signature";
	public static final String SIGNATURE_ENABLED = "signature_on";
	public static final String SIGNATURE_POSITION = "signature_position";

	// Tags table
	public static String TAGS_TABLE_NAME = "tags";
	public static final String START_TAG = "startTag";
	public static final String DEFAULT_TEXT = "defaultText";
	public static final String END_TAG = "endTag";

	public DAO(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create sites table
		db.execSQL("CREATE TABLE " + SITES_TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, "
				+ URL + " TEXT, " + USERNAME + " TEXT, " + PASSWORD + " TEXT, "
				+ SIGNATURE + " TEXT, " + SIGNATURE_ENABLED + " TEXT, "
				+ SIGNATURE_POSITION + " TEXT);");
		// create tags table
		db.execSQL("CREATE TABLE " + TAGS_TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + START_TAG
				+ " TEXT, " + DEFAULT_TEXT + " TEXT, " + END_TAG + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			db.execSQL("CREATE TABLE " + TAGS_TABLE_NAME + " (" + _ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + START_TAG
					+ " TEXT, " + DEFAULT_TEXT + " TEXT, " + END_TAG
					+ " TEXT);");
		}
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
				db.update(SITES_TABLE_NAME, values, _ID + "=" + site.getId(),
						null);
			} else {
				// save
				db.insertOrThrow(SITES_TABLE_NAME, null, values);
			}
		} finally {
			db.close();
		}
	}

	public void save(Tag tag) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(START_TAG, tag.getStartTag());
			values.put(DEFAULT_TEXT, tag.getDefaultText());
			values.put(END_TAG, tag.getEndTag());
			if (tag.getId() != null) {
				// update
				db.update(TAGS_TABLE_NAME, values, _ID + "=" + tag.getId(),
						null);
			} else {
				// save
				db.insertOrThrow(TAGS_TABLE_NAME, null, values);
			}
		} finally {
			db.close();
		}
	}

	public void delete(Site site) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(SITES_TABLE_NAME, _ID + "=" + site.getId(), null);
		} finally {
			db.close();
		}
	}

	public void delete(Tag tag) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(TAGS_TABLE_NAME, _ID + "=" + tag.getId(), null);
		} finally {
			db.close();
		}
	}

	public List<Site> getSites() {
		SQLiteDatabase db = getReadableDatabase();
		try {
			Cursor c = db.query(SITES_TABLE_NAME, null, null, null, null, null,
					null);
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

	public List<Tag> getTags() {
		SQLiteDatabase db = getReadableDatabase();
		try {
			Cursor c = db.query(TAGS_TABLE_NAME, null, null, null, null, null,
					START_TAG);
			List<Tag> list = new ArrayList<Tag>(c.getColumnCount());
			while (c.moveToNext()) {
				Tag t = new Tag();
				t.setId(c.getInt(0));
				t.setStartTag(c.getString(1));
				t.setDefaultText(c.getString(2));
				t.setEndTag(c.getString(3));
				list.add(t);
			}
			c.close();
			return list;
		} finally {
			db.close();
		}
	}
}
