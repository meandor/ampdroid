/**
 * 
 */
package com.racoon.ampdroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.racoon.ampache.Song;

/**
 * @author Daniel Schruhl
 * 
 */
public class SongArrayAdapter extends ArrayAdapter<String> implements SectionIndexer {
	private final Context context;
	private final ArrayList<String> textValues;
	private ArrayList<Song> objectValues;
	HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	private String[] sections;
	private String[] sectionsChar;

	public SongArrayAdapter(Context context, ArrayList<String> list, ArrayList<Song> objects) {
		super(context, R.layout.song_list_item, list);
		this.context = context;
		this.textValues = list;
		this.objectValues = objects;

		for (int i = 0; i < objects.size(); ++i) {
			mIdMap.put(list.get(i), i);
		}

		Set<String> sectionLetters = mIdMap.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionsChar = new String[sectionList.size()];
		for (int i = 0; i < sectionList.size(); i++) {
			sections[i] = sectionList.get(i);
			sectionsChar[i] = sectionList.get(i).substring(0, 2);
		}
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.song_list_item, parent, false);
		TextView songTitle = (TextView) rowView.findViewById(R.id.songTitle);
		TextView songArtist = (TextView) rowView.findViewById(R.id.songArtist);

		songTitle.setText(textValues.get(position));
		songArtist.setText(objectValues.get(position).getArtist());
		return rowView;
	}

	@Override
	public long getItemId(int position) {
		String item = getItem(position);
		return mIdMap.get(item);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	public int getPositionForSection(int section) {
		if ((sections.length == section) && section == 1) {
			return mIdMap.get(sections[section - 1]);
		}
		return mIdMap.get(sections[section]);
	}

	public int getSectionForPosition(int position) {
		return 1;
	}

	public Object[] getSections() {
		return sectionsChar;
	}

}
