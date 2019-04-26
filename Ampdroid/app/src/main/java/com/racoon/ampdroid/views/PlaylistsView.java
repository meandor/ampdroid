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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.racoon.ampache.Playlist;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.MainActivity;
import com.racoon.ampdroid.PlaylistArrayAdapter;
import com.racoon.ampdroid.R;

//import com.racoon.ampdroid.ServerConnector;

/**
 * @author Daniel Schruhl
 * 
 */
public class PlaylistsView extends Fragment {

	// private String urlString;
	private Controller controller;

	/**
	 * 
	 */
	public PlaylistsView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		PlaylistsView p = new PlaylistsView();
		return p;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.ampache_playlists, null);
		ListView listview = (ListView) root.findViewById(R.id.playlists_listview);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			listview.setFastScrollAlwaysVisible(true);
		}
		if (controller.getServer() != null) {
			ArrayList<String> list = new ArrayList<String>();
			for (Playlist p : controller.getPlaylists()) {
				list.add(p.toString());
			}
			PlaylistArrayAdapter adapter = new PlaylistArrayAdapter(getActivity().getApplicationContext(), list,
					controller.getPlaylists());
			listview.setAdapter(adapter);
			registerForContextMenu(listview);

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					Playlist selected = controller.getPlaylists().get(position);

					String urlString = controller.getServer().getHost()
							+ "/server/xml.server.php?action=playlist_songs&auth="
							+ controller.getServer().getAuthKey() + "&filter=" + String.valueOf(selected.getId());
					Log.d("url", urlString);
					controller.getSelectedSongs().clear();
					controller.parsePlaylistSongs(urlString, controller.getSelectedSongs());
					SelectedSongsView newFragment = new SelectedSongsView();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

					// Replace whatever is in the fragment_container view with this fragment,
					// and add the transaction to the back stack
					transaction.replace(R.id.content_frame, newFragment);
					transaction.addToBackStack(null);
					((MainActivity) getActivity()).setActiveFragment(6);
					// Commit the transaction
					transaction.commit();
				}

			});
		} else {
			Log.d("bugs", "server null");
		}
		return root;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Playlist selected = controller.getPlaylists().get((int) info.id);
		String urlString = controller.getServer().getHost() + "/server/xml.server.php?action=playlist_songs&auth="
				+ controller.getServer().getAuthKey() + "&filter=" + String.valueOf(selected.getId());
		Log.d("url", urlString);
		switch (item.getItemId()) {
		case R.id.contextMenuAdd:
			controller.parsePlaylistSongs(urlString, controller.getPlayNow());
			Context context = getView().getContext();
			CharSequence text = getResources().getString(R.string.playlistsViewPlaylistAdded);
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return true;
		case R.id.contextMenuOpen:
			controller.getSelectedSongs().clear();
			controller.parsePlaylistSongs(urlString, controller.getSelectedSongs());
			// Create new fragment and transaction
			SelectedSongsView newFragment = new SelectedSongsView();
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			transaction.replace(R.id.content_frame, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

}
