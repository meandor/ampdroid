/**
 * 
 */
package com.racoon.ampdroid.views;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.racoon.ampache.Song;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.R;
import com.racoon.ampdroid.StableArrayAdapter;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.current_playlist, null);
		ListView listview = (ListView) root.findViewById(R.id.playNow_listview);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			listview.setFastScrollAlwaysVisible(true);
		}
		seekBar = (SeekBar) root.findViewById(R.id.playNow_seekbar);
		songTitle = (TextView) root.findViewById(R.id.playNow_song);
		duration = (TextView) root.findViewById(R.id.playNow_duration);
		currentDuration = (TextView) root.findViewById(R.id.playNow_duration_current);
		ArrayList<String> list = new ArrayList<String>();
		for (Song s : controller.getPlayNow()) {
			list.add(s.toString());
		}
		Log.d("songs:", list.toString());
		StableArrayAdapter adapter = new StableArrayAdapter(getActivity().getApplicationContext(),
				R.layout.content_list_item, list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				controller.setPlayingNow(controller.getPlayNow().get(position));
				try {
					controller.setPlayNowPosition(position);
					controller.getMediaPlayer().reset();
					controller.getMediaPlayer().setDataSource(controller.getPlayingNow().getUrl());
					controller.getMediaPlayer().prepare(); // might take long! (for buffering, etc)
					controller.getMediaPlayer().start();
					String title = controller.getPlayingNow().toString();
					if (controller.getPlayingNow().getArtist() != null) {
						title += " - " + controller.getPlayingNow().getArtist().toString();
					}
					songTitle.setText(title);
					int songDuration = controller.getMediaPlayer().getDuration() / 1000;
					duration.setText(String.format("%02d:%02d", (songDuration % 3600) / 60, (songDuration % 60)));
					seekBar.setMax(controller.getMediaPlayer().getDuration());
					seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {

						}

						@Override
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
							if (controller.getMediaPlayer() != null && fromUser) {
								controller.getMediaPlayer().seekTo(progress);
							}
						}
					});

					mHandler = new Handler();
					mRunnable = new Runnable() {
						@Override
						public void run() {
							if (controller.getMediaPlayer() != null) {
								int mCurrentPosition = controller.getMediaPlayer().getCurrentPosition();
								seekBar.setProgress(mCurrentPosition);
								if (seekBar.getProgress() == seekBar.getMax() && ((controller.getPlayNowPosition() + 1) < controller.getPlayNow().size())) {
									/** start next song **/
									controller.setPlayNowPosition(controller.getPlayNowPosition() + 1);
									controller.setPlayingNow(controller.getPlayNow().get(
											controller.getPlayNowPosition()));
									controller.getMediaPlayer().reset();
									try {
										controller.getMediaPlayer().setDataSource(controller.getPlayingNow().getUrl());
										controller.getMediaPlayer().prepare();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalStateException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									seekBar.setMax(controller.getMediaPlayer().getDuration());
									seekBar.setProgress(0);
									controller.getMediaPlayer().start();
									String title = controller.getPlayingNow().toString();
									if (controller.getPlayingNow().getArtist() != null) {
										title += " - " + controller.getPlayingNow().getArtist().toString();
									}
									songTitle.setText(title);
									int songDuration = controller.getMediaPlayer().getDuration() / 1000;
									duration.setText(String.format("%02d:%02d", (songDuration % 3600) / 60,
											(songDuration % 60)));
								}
								int songDuration = controller.getMediaPlayer().getCurrentPosition() / 1000;
								currentDuration.setText(String.format("%02d:%02d", (songDuration % 3600) / 60,
										(songDuration % 60)));
								Log.d("seekbar", String.valueOf(seekBar.getProgress()));
								Log.d("duration", String.valueOf(seekBar.getMax()));
							}
							mHandler.postDelayed(this, 1000);
						}
					};
					Log.d("seekbar duration", String.valueOf(controller.getMediaPlayer().getDuration()));
					mHandler.post(mRunnable);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Playing now:", controller.getPlayingNow().toString());
			}

		});
		return root;
	}

}
