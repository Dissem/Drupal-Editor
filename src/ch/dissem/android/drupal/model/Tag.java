package ch.dissem.android.drupal.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable {
	private Integer id;
	private String startTag;
	private String defaultText;
	private String endTag;

	public Tag() {
		// Default constructor
	}

	public Tag(String startTag, String defaultText, String endTag) {
		this.startTag = startTag;
		this.defaultText = defaultText;
		this.endTag = endTag;
	}

	public String getStartTag() {
		return startTag;
	}

	public void setStartTag(String startTag) {
		this.startTag = startTag;
	}

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

	public String getEndTag() {
		return endTag;
	}

	public void setEndTag(String endTag) {
		this.endTag = endTag;
	}

	Integer getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return startTag;
	}

	// Parcelable implementation
	private Tag(Parcel in) {
		if (in.readByte() == 1)
			id = in.readInt();

		startTag = in.readString();
		if (in.readByte() == 1)
			defaultText = in.readString();
		endTag = in.readString();
	}

	public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
		public Tag createFromParcel(Parcel in) {
			return new Tag(in);
		}

		public Tag[] newArray(int size) {
			return new Tag[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		if (id != null) {
			out.writeByte((byte) 1);
			out.writeInt(id);
		} else {
			out.writeByte((byte) 0);
		}
		out.writeString(startTag);
		if (id != null) {
			out.writeByte((byte) 1);
			out.writeString(defaultText);
		} else {
			out.writeByte((byte) 0);
		}
		out.writeString(endTag);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + startTag.hashCode();
		result = prime * result + defaultText.hashCode();
		result = prime * result + endTag.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tag))
			return false;

		Tag other = (Tag) obj;

		if (!stringEqual(startTag, other.startTag))
			return false;

		if (!stringEqual(defaultText, other.defaultText))
			return false;

		if (!stringEqual(endTag, other.endTag))
			return false;

		return true;
	}

	private static boolean stringEqual(String a, String b) {
		if (a == null)
			return b == null;
		return a.equals(b);
	}
}
