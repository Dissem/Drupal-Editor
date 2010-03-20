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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
	private String title;
	private String link;
	private Date dateCreated;
	private String permaLink;
	private String postid;
	private String description;
	private Set<CategoryInfo> categories = new HashSet<CategoryInfo>();

	private boolean categoriesSet;

	public Post() {
		// empty constructor
	}

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

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<CategoryInfo> getCategories() {
		return categories;
	}

	public void addCategory(CategoryInfo category) {
		categories.add(category);
		categoriesSet = true;
	}

	@SuppressWarnings("unchecked")
	public void setCategories(Object[] categories) {
		if (categories == null)
			return;
		this.categories = new HashSet<CategoryInfo>();
		for (Object c : categories)
			this.categories.add(new CategoryInfo((Map<String, Object>) c));
		categoriesSet = true;
	}

	public Map<String, Object> getMap() {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put("title", title);
		struct.put("link", link == null ? "" : link);
		struct.put("description", description);

		return struct;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object>[] getCategoriesAsMap() {
		Set<CategoryInfo> categories = getCategories();
		Map<String, Object>[] result = new Map[categories.size()];
		int i = 0;
		for (CategoryInfo c : categories)
			result[i++] = c.getMap();
		return result;
	}

	// Parcelable implementation
	private Post(Parcel in) {
		title = in.readString();
		link = in.readString();
		dateCreated = new Date(in.readLong());
		permaLink = in.readString();
		postid = in.readString();
		description = in.readString();
		categoriesSet = in.readByte() == 1;
		Object[] cats = in.readParcelableArray(CategoryInfo.class
				.getClassLoader());
		if (cats != null)
			for (Object o : cats)
				categories.add((CategoryInfo) o);
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
		out.writeByte(categoriesSet ? (byte) 1 : (byte) 0);
		out.writeParcelableArray(categories.toArray(new CategoryInfo[categories
				.size()]), 0);
	}

	public void setCategoriesSet(boolean categoriesSet) {
		this.categoriesSet = categoriesSet;
	}

	public boolean isCategoriesSet() {
		return categoriesSet;
	}
}
