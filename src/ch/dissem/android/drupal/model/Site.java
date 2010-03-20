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
	}

	public Site(Context ctx) {
		signature = ctx.getString(R.string.default_signature);
		signaturePosition = SignaturePosition.END;
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

	Integer getId() {
		return id;
	}

	void setId(int id) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((signature == null) ? 0 : signature.hashCode());
		result = prime * result + (signatureEnabled ? 1231 : 1237);
		result = prime
				* result
				+ ((signaturePosition == null) ? 0 : signaturePosition
						.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Site other = (Site) obj;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;

		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;

		if (signatureEnabled != other.signatureEnabled)
			return false;

		if (signaturePosition == null) {
			if (other.signaturePosition != null)
				return false;
		} else if (!signaturePosition.equals(other.signaturePosition))
			return false;

		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;

		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;

		return true;
	}

	public boolean isSame(Site other) {
		if (id != null)
			return id.equals(other.id);

		return false;
	}
}
