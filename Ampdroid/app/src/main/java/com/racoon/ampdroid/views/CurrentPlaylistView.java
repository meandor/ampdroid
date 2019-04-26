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
package com.racoon.ampdroid.views;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.racoon.ampache.Song;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.CurrentlyPlayingPlaylistArrayAdapter;
import com.racoon.ampdroid.MainActivity;
import com.racoon.ampdroid.R;

/**
 * @author Daniel Schruhl
 * 
 */
public class CurrentPlaylistView extends Fragment {

	private Controller controller;
	private SeekBar seekBar;
	private Handler mHandler;
	private Runnable mRunnable;
	private TextView duration;
	private TextView currentDuration;
	private TextView songTitle;
	private TextView songArtist;
	private ListView playlist;
	private ImageButton togglePlayButton;
	private int updateAttempts;

	/**
	 * 
	 */
	public CurrentPlaylistView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		CurrentPlaylistView f = new CurrentPlaylistView();
		return f;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.current_playlist, null);
		playlist = (ListView) root.findViewById(R.id.playNow_listview);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			playlist.setFastScrollAlwaysVisible(true);
		}
		seekBar = (SeekBar) root.findViewById(R.id.playNow_seekbar);
		songTitle = (TextView) root.findViewById(R.id.playNow_song);
		songArtist = (TextView) root.findViewById(R.id.playNow_artist);
		duration = (TextView) root.findViewById(R.id.playNow_duration);
		currentDuration = (TextView) root.findViewById(R.id.playNow_duration_current);
		togglePlayButton = (ImageButton) root.findViewById(R.id.playlist_play_pause);

		ArrayList<String> list = new ArrayList<String>();
		for (Song s : controller.getPlayNow()) {
			list.add(s.toString());
		}
		Log.d("songs:", list.toString());
		CurrentlyPlayingPlaylistArrayAdapter adapter = new CurrentlyPlayingPlaylistArrayAdapter(getActivity()
				.getApplicationContext(), list, controller.getPlayNow());
		playlist.setAdapter(adapter);
		playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				try {
					MainActivity main = (MainActivity) getActivity();
					main.play(position);
					updateSongData();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		updateSongData();
		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.currently_playing_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit_discard:
			controller.getPlayNow().clear();
			ArrayList<String> list = new ArrayList<String>();
			for (Song s : controller.getPlayNow()) {
				list.add(s.toString());
			}
			CurrentlyPlayingPlaylistArrayAdapter adapter = new CurrentlyPlayingPlaylistArrayAdapter(getActivity()
					.getApplicationContext(), list, controller.getPlayNow());
			playlist.setAdapter(adapter);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void cleanView() {
		songTitle.setText("");
		songArtist.setText("");
		duration.setText("");
		currentDuration.setText("");
		seekBar.setProgress(0);
		try {
			togglePlayButton.setBackground(getResources().getDrawable(R.drawable.ic_action_play));
		} catch (IllegalStateException e) {
			Log.d("error", e.getStackTrace().toString());
		}
		updateAttempts = 0;
	}

	public void pauseView() {
		togglePlayButton.setBackground(getResources().getDrawable(R.drawable.ic_action_play));
		updateAttempts = 0;
	}

	public void updateSongData() {
		mHandler = new Handler();
		mRunnable = new Runnable() {
			@Override
			public void run() {
				Log.d("bugs", "Thread läuft noch");
				final MainActivity main = (MainActivity) getActivity();
				if (main != null && main.getService() != null) {
					if (main.getService().isPlaying()) {
						controller.setPlayingNow(main.getService().getCurrentSong());
						songTitle.setText(main.getService().getCurrentSong().toString());
						String artist = "Unknown";
						if (controller.getPlayingNow().getArtist() != null) {
							artist = controller.getPlayingNow().getArtist().toString();
						}
						songArtist.setText(artist);
						togglePlayButton.setBackground(getResources().getDrawable(R.drawable.ic_action_pause));
						int songDuration = main.getService().getMediaPlayer().getDuration() / 1000;
						duration.setText(String.format("%02d:%02d", (songDuration % 3600) / 60, (songDuration % 60)));
						songDuration = main.getService().getMediaPlayer().getCurrentPosition() / 1000;
						currentDuration.setText(String.format("%02d:%02d", (songDuration % 3600) / 60,
								(songDuration % 60)));
						int mCurrentPosition = main.getService().getMediaPlayer().getCurrentPosition();
						seekBar.setMax(main.getService().getMediaPlayer().getDuration());
						seekBar.setProgress(mCurrentPosition);
						seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
								main.getService().getMediaPlayer().seekTo(seekBar.getProgress());
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {

							}

							@Override
							public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

							}
						});
						mHandler.postDelayed(this, 1000);
					} else {
						updateAttempts++;
						Log.d("bugs", "attempt " + String.valueOf(updateAttempts));
						if (updateAttempts >= 3) {
							Log.d("bugs", "thread pause");
							pauseView();
							mHandler.removeCallbacks(mRunnable);
						}
					}
				} else {
					Log.d("bugs", "service ist nicht vorhanden");
					cleanView();
					mHandler.removeCallbacks(mRunnable);
				}

			}
		};
		mHandler.post(mRunnable);
	}

}
