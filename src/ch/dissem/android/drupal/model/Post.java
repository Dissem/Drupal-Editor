package ch.dissem.android.drupal.model;

import java.util.Date;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
	private String title;
	private String link;
	private Date dateCreated;
	private String permaLink;
	private String postid;
	private String description;

	public Post(Map<String, Object> struct) {
		title = (String) struct.get("title");
		link = (String) struct.get("link");
		dateCreated = (Date) struct.get("dateCreated");
		permaLink = (String) struct.get("permaLink");
		postid = (String) struct.get("postid");
		description = (String) struct.get("description");
	}

	/**
	 * mt_allow_comments=1, userid=christian, mt_convert_breaks=0,
	 * content=<title>Erster Blogeintrag</title>Dies ist nicht nur mein erster
	 * Blogeintrag, es ist auch der erste welcher mit dem Drupal Editor erstellt
	 * wurde. Ob es wohl funktioniert?, description=Dies ist nicht nur mein
	 * erster Blogeintrag, es ist auch der erste welcher mit dem Drupal Editor
	 * erstellt wurde. Ob es wohl funktioniert?
	 */

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public String getPermaLink() {
		return permaLink;
	}

	public String getPostid() {
		return postid;
	}

	public String getDescription() {
		return description;
	}

	// Parcelable implementation
	private Post(Parcel in) {
		title = in.readString();
		link = in.readString();
		dateCreated = new Date(in.readLong());
		permaLink = in.readString();
		postid = in.readString();
		description = in.readString();
	}

	public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
		public Post createFromParcel(Parcel in) {
			return new Post(in);
		}

		public Post[] newArray(int size) {
			return new Post[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(title);
		out.writeString(link);
		out.writeLong(dateCreated.getTime());
		out.writeString(permaLink);
		out.writeString(postid);
		out.writeString(description);
	}
}
