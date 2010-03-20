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
package ch.dissem.android.drupal.model;

import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class UsersBlog implements Parcelable {
	private String blogName;
	private String blogid;
	private String url;

	public UsersBlog(Map<String, String> map) {
		blogName = map.get("blogName");
		blogid = map.get("blogid");
		url = map.get("url");
	}

	public String getBlogName() {
		return blogName;
	}

	public String getBlogid() {
		return blogid;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return blogName;
	}

	// Parcelable implementation
	private UsersBlog(Parcel in) {
		blogName = in.readString();
		blogid = in.readString();
		url = in.readString();
	}

	public static final Parcelable.Creator<UsersBlog> CREATOR = new Parcelable.Creator<UsersBlog>() {
		public UsersBlog createFromParcel(Parcel in) {
			return new UsersBlog(in);
		}

		public UsersBlog[] newArray(int size) {
			return new UsersBlog[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(blogName);
		out.writeString(blogid);
		out.writeString(url);
	}
}
