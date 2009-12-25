package ch.dissem.android.drupal;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import ch.dissem.android.drupal.model.DAO;
import ch.dissem.android.drupal.model.Tag;

public class TagList extends ListActivity implements OnClickListener {
	public static final int REQUEST_CODE = 0x10CA711;
	public static final String TAG = "tag";
	private List<Tag> tagList;
	private DAO dao;

	private EditText startTag;
	private EditText defaultText;
	private EditText endTag;

	private ListView list;

	private Tag editTag = new Tag();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_list);

		dao = new DAO(this);

		tagList = dao.getTags();

		list = getListView();
		initList();

		registerForContextMenu(getListView());
		findViewById(R.id.add_tag).setOnClickListener(this);

		startTag = (EditText) findViewById(R.id.start_tag);
		defaultText = (EditText) findViewById(R.id.default_text);
		endTag = (EditText) findViewById(R.id.end_tag);
	}

	private void initList() {
		list.setAdapter(new ArrayAdapter<Tag>(this,
				android.R.layout.simple_list_item_1, tagList
						.toArray(new Tag[tagList.size()])));
	}

	@Override
	public void onClick(View v) {
		if (startTag.getText().length() > 0) {
			editTag.setStartTag(startTag.getText().toString());
			editTag.setDefaultText(defaultText.getText().toString());
			editTag.setEndTag(endTag.getText().toString());
			dao.save(editTag);
			editTag = new Tag();
			startTag.setText("");
			endTag.setText("");
			tagList = dao.getTags();
			initList();
		}
	}
}
