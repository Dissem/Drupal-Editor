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

	public Map<String, String> getCategoryMap() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("categoryId", getCategoryId());
		result.put("categoryName", getCategoryName());
		return result;
	}

	@Override
	public String toString() {
		return categoryName;
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
