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
