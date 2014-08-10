/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Daniel Schruhl
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

import com.racoon.ampache.Artist;

/**
 * @author Daniel Schruhl
 * 
 */
public class ArtistArrayAdapter extends ArrayAdapter<String> implements SectionIndexer {
	private final Context context;
	private final ArrayList<String> textValues;
	private ArrayList<Artist> objectValues;
	HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	private String[] sections;
	private String[] sectionsChar;

	public ArtistArrayAdapter(Context context, ArrayList<String> list, ArrayList<Artist> objects) {
		super(context, R.layout.album_list_item, list);
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
		View rowView = inflater.inflate(R.layout.album_list_item, parent, false);
		TextView songTitle = (TextView) rowView.findViewById(R.id.albumTitle);
		TextView songArtist = (TextView) rowView.findViewById(R.id.albumArtist);

		songTitle.setText(textValues.get(position));
		String songsText = " Song";
		if (objectValues.get(position).getSongs() > 1) {
			songsText = " Songs";
		}
		songArtist.setText(String.valueOf(objectValues.get(position).getSongs()) + songsText);
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
