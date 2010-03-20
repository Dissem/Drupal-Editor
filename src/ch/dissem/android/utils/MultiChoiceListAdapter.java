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
import java.util.Map;
import java.util.Map.Entry;

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

	private ArrayList<ChoiceView> views;
	private Map<T, Boolean> optionsWithSelection;
	private Collection<T> selection;

	public MultiChoiceListAdapter(Context context, Map<T, Boolean> options) {
		views = new ArrayList<ChoiceView>();
		this.optionsWithSelection = options;

		for (Entry<T, Boolean> e : options.entrySet())
			views.add(new ChoiceView(context, e.getKey(), e.getValue()));
	}

	public MultiChoiceListAdapter(Context context, Collection<T> options,
			Collection<T> selection) {
		views = new ArrayList<ChoiceView>();
		this.selection = selection;

		for (T o : options)
			views.add(new ChoiceView(context, o, selection.contains(o)));
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object getItem(int position) {
		return views.get(position).object;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return views.get(position);
	}

	public class ChoiceView extends CheckBox implements OnCheckedChangeListener {
		private T object;

		public ChoiceView(Context context, T object, Boolean selected) {
			super(context);
			this.object = object;
			setChecked(selected);
			setOnCheckedChangeListener(this);
			setText(object.toString());
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (optionsWithSelection != null)
				optionsWithSelection.put(object, Boolean.valueOf(isChecked));
			if (selection != null) {
				if (isChecked && !selection.contains(object))
					selection.add(object);
				else if (!isChecked)
					selection.remove(object);
			}
			notifyDataSetChanged();
		}
	}
}
