package ch.dissem.android.drupal.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import ch.dissem.android.drupal.R;

public class Site implements Parcelable {
	private Integer id;
	private String name;
	private String url;
	private String username;
	private String password;
	private String signature;
	private boolean signatureEnabled = false;
	private SignaturePosition signaturePosition;

	public Site() {
		// Is needed because of the private constructor for Parcelable
		// implementation.
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public boolean isSignatureEnabled() {
		return signatureEnabled;
	}

	public void setSignatureEnabled(boolean signatureEnabled) {
		this.signatureEnabled = signatureEnabled;
	}

	public SignaturePosition getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(SignaturePosition signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	public static enum SignaturePosition {
		START(R.string.start), END(R.string.end);
		int resId;

		private SignaturePosition(int resId) {
			this.resId = resId;
		}

		public String toString(Context ctx) {
			return ctx.getString(resId);
		}

		public static SignaturePosition parse(String s) {
			for (SignaturePosition v : values()) {
				if (v.toString().equals(s))
					return v;
			}
			return null;
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}

	// Parcelable implementation
	private Site(Parcel in) {
		if (in.readByte() == 1)
			id = in.readInt();
		name = in.readString();
		url = in.readString();
		username = in.readString();
		password = in.readString();
		signature = in.readString();
		signatureEnabled = (in.readByte() == 1);
		signaturePosition = SignaturePosition.parse(in.readString());
	}

	public static final Parcelable.Creator<Site> CREATOR = new Parcelable.Creator<Site>() {
		public Site createFromParcel(Parcel in) {
			return new Site(in);
		}

		public Site[] newArray(int size) {
			return new Site[size];
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
		out.writeString(name);
		out.writeString(url);
		out.writeString(username);
		out.writeString(password);
		out.writeString(signature);
		out.writeByte(signatureEnabled ? (byte) 1 : (byte) 0);
		out.writeString(signaturePosition == null ? null : signaturePosition
				.toString());
	}
}
