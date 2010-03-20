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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A dialog that allows the user to select multiple entries.
 * 
 * @author christian
 * @param <T>
 *            Type for this dialog. Should have some useful toString()
 *            implementation.
 */
public class MultiChoice<T> extends Dialog {
	private ListView listView;

	private Map<T, Boolean> optionsWithSelection;
	private Collection<T> options;
	private Collection<T> selection;

	public MultiChoice(Context context, Map<T, Boolean> options) {
		super(context);
		this.optionsWithSelection = options;
	}

	public MultiChoice(Context context, Collection<T> options,
			Collection<T> selection) {
		super(context);
		this.options = options;
		this.selection = selection;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context ctx = getContext();
		listView = new ListView(ctx);
		ListAdapter adapter;
		if (optionsWithSelection != null)
			adapter = new MultiChoiceListAdapter<T>(ctx, optionsWithSelection);
		else
			adapter = new MultiChoiceListAdapter<T>(ctx, options, selection);
		listView.setAdapter(adapter);
		setContentView(listView);
	}

	public Map<T, Boolean> getOptionsMap() {
		return optionsWithSelection;
	}

	public Set<T> getSelection() {
		Set<T> result = new LinkedHashSet<T>();
		for (Entry<T, Boolean> e : optionsWithSelection.entrySet())
			if (Boolean.TRUE.equals(e.getValue()))
				result.add(e.getKey());
		return result;
	}
}
