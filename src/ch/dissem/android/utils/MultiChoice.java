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
 * @author christian
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
