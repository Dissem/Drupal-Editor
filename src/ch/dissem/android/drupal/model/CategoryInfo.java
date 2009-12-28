package ch.dissem.android.drupal.model;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CategoryInfo implements Parcelable {
	String categoryName;
	String categoryId;

	public CategoryInfo(Map<String, Object> struct) {
		Log.d("CategoryInfo", struct.toString());
		categoryName = (String) struct.get("categoryName");
		categoryId = (String) struct.get("categoryId");
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public Map<String, Object> getMap() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("categoryId", categoryId);
		result.put("categoryName", categoryName);
		return result;
	}

	@Override
	public String toString() {
		return categoryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
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
		CategoryInfo other = (CategoryInfo) obj;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}

	// Parcelable
	public CategoryInfo(Parcel in) {
		categoryId = in.readString();
		categoryName = in.readString();
	}

	public static final Parcelable.Creator<CategoryInfo> CREATOR = new Parcelable.Creator<CategoryInfo>() {
		public CategoryInfo createFromParcel(Parcel in) {
			return new CategoryInfo(in);
		}

		public CategoryInfo[] newArray(int size) {
			return new CategoryInfo[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(categoryId);
		dest.writeString(categoryName);
	}
}
