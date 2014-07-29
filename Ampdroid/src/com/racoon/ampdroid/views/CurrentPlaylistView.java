/**
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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.racoon.ampache.Song;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.MainActivity;
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
	private ListView playlist;

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
		duration = (TextView) root.findViewById(R.id.playNow_duration);
		currentDuration = (TextView) root.findViewById(R.id.playNow_duration_current);
		ArrayList<String> list = new ArrayList<String>();
		for (Song s : controller.getPlayNow()) {
			list.add(s.toString());
		}
		Log.d("songs:", list.toString());
		StableArrayAdapter adapter = new StableArrayAdapter(getActivity().getApplicationContext(),
				R.layout.content_list_item, list);
		playlist.setAdapter(adapter);
		playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				controller.setPlayingNow(controller.getPlayNow().get(position));
				try {
					controller.setPlayNowPosition(position);
					String title = controller.getPlayingNow().toString();
					if (controller.getPlayingNow().getArtist() != null) {
						title += " - " + controller.getPlayingNow().getArtist().toString();
					}
					songTitle.setText(title);
					MainActivity main = (MainActivity) getActivity();
					main.play(position);
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
				Log.d("Playing now:", controller.getPlayingNow().toString());
			}

		});
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
			StableArrayAdapter adapter = new StableArrayAdapter(getActivity().getApplicationContext(),
					R.layout.content_list_item, list);
			playlist.setAdapter(adapter);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
