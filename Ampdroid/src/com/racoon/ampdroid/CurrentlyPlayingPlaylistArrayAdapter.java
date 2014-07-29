/**
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
		songDuration.setText(String.valueOf(minute) + ":" + String.valueOf(second));
		return rowView;
	}
}
