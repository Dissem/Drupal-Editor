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
