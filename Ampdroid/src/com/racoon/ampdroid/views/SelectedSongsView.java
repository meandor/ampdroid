/**
 * 
 */
package com.racoon.ampdroid.views;

import java.util.ArrayList;

import com.racoon.ampache.Song;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.R;
import com.racoon.ampdroid.SongArrayAdapter;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Daniel Schruhl
 * 
 */
public class SelectedSongsView extends Fragment {
	private Controller controller;

	/**
	 * 
	 */
	public SelectedSongsView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		SelectedSongsView f = new SelectedSongsView();
		return f;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.ampache_songs, null);
		ListView listview = (ListView) root.findViewById(R.id.songs_listview);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			listview.setFastScrollAlwaysVisible(true);
		}
		if (controller.getServer() != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (Song s : controller.getSelectedSongs()) {
				list.add(s.toString());
			}
			SongArrayAdapter adapter = new SongArrayAdapter(getActivity().getApplicationContext(), list,
					controller.getSelectedSongs());
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					Log.d("Play now added:", controller.getSongs().get(position).toString());
					controller.getPlayNow().add(controller.getSongs().get(position));
					Context context = view.getContext();
					CharSequence text = getResources().getString(R.string.songsViewSongAdded);
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}

			});
		}
		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.selected_songs, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit_add_all:
			controller.getPlayNow().addAll(controller.getSelectedSongs());
			Context context = getView().getContext();
			CharSequence text = getResources().getString(R.string.selectedSongsViewAdded);
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
