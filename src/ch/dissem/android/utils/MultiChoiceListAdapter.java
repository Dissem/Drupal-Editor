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
package ch.dissem.android.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * {@link BaseAdapter}-Implementation for the {@link MultiChoice} Dialog.
 * 
 * @author christian
 * @param <T>
 *            Type this Adapter contains. Should have some useful toString()
 *            implementation.
 */
public class MultiChoiceListAdapter<T> extends BaseAdapter {
	private Context ctx;

	private Collection<T> options;
	private Collection<T> selection;
	private List<T> filteredOptions;

	public MultiChoiceListAdapter(Context context, Collection<T> options,
			Collection<T> selection) {
		this.ctx = context;

		this.options = options;
		this.selection = selection;

		this.filteredOptions = new ArrayList<T>(options.size());
		setFilter(null);
	}

	@Override
	public int getCount() {
		return filteredOptions.size();
	}

	@Override
	public T getItem(int position) {
		return filteredOptions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
		ChoiceView view;
		T item = getItem(position);
		boolean selected = selection.contains(item);
		if (convertView == null) {
			view = new ChoiceView(ctx, item, selected);
		} else {
			view = (ChoiceView) convertView;
			view.setItem(item, selected);
		}
		return view;
	}

	public void setFilter(String filter) {
		if (filter != null)
			filter = filter.toLowerCase();

		filteredOptions.clear();
		for (T item : selection)
			filteredOptions.add(item);
		for (T item : options)
			if (!selection.contains(item)
					&& (filter == null || item.toString().toLowerCase()
							.contains(filter)))
				filteredOptions.add(item);
	}

	public class ChoiceView extends CheckBox implements OnCheckedChangeListener {
		private T object;

		public ChoiceView(Context context, T object, Boolean selected) {
			super(context);
			this.object = object;
			setOnCheckedChangeListener(this);
			setItem(object, selected);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (selection != null) {
				if (isChecked && !selection.contains(object))
					selection.add(object);
				else if (!isChecked)
					selection.remove(object);
			}
			notifyDataSetChanged();
		}

		public void setItem(T object, Boolean selected) {
			this.object = object;
			setChecked(selected);
			setText(object.toString());
		}
	}
}
