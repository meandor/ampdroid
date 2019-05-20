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
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.racoon.ampache.Song;

/**
 * @author Daniel Schruhl
 * 
 */
public class CurrentlyPlayingPlaylistArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> textValues;
	private ArrayList<Song> objectValues;

	public CurrentlyPlayingPlaylistArrayAdapter(Context context, ArrayList<String> list, ArrayList<Song> objects) {
		super(context, R.layout.currently_playing_playlist_item, list);
		this.context = context;
		this.textValues = list;
		this.objectValues = objects;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.currently_playing_playlist_item, parent, false);
		TextView songTitle = (TextView) rowView.findViewById(R.id.playingNowSongTitle);
		TextView songArtist = (TextView) rowView.findViewById(R.id.playingNowSongArtist);
		TextView songDuration = (TextView) rowView.findViewById(R.id.playingNowSongDuration);

		songTitle.setText(textValues.get(position));
		songArtist.setText(objectValues.get(position).getArtist());
		int rawTime = objectValues.get(position).getTime();
		long minute = TimeUnit.SECONDS.toMinutes(rawTime) - (TimeUnit.SECONDS.toHours(rawTime) * 60);
		long second = TimeUnit.SECONDS.toSeconds(rawTime) - (TimeUnit.SECONDS.toMinutes(rawTime) * 60);
		String seconds = String.valueOf(second);
		if (second < 10) {
			seconds = "0" + String.valueOf(second);
		}
		songDuration.setText(String.valueOf(minute) + ":" + seconds);
		return rowView;
	}
}
