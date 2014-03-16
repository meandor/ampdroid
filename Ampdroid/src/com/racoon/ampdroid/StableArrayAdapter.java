/**
 * 
 */
package com.racoon.ampdroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

/**
 * @author Daniel Schruhl
 * 
 */
public class StableArrayAdapter extends ArrayAdapter<String> implements SectionIndexer {

	HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	private String[] sections;
	private String[] sectionsChar;

	public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		for (int i = 0; i < objects.size(); ++i) {
			mIdMap.put(objects.get(i), i);
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
		return mIdMap.get(sections[section - 1]);
	}

	public int getSectionForPosition(int position) {
		return 1;
	}

	public Object[] getSections() {
		return sectionsChar;
	}

}
