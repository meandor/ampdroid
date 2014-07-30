/**
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

import com.racoon.ampache.Album;
import com.racoon.ampache.Artist;
import com.racoon.ampache.Song;
import com.racoon.ampdroid.ArtistArrayAdapter;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.R;

//import com.racoon.ampdroid.ServerConnector;

/**
 * @author Daniel Schruhl
 * 
 */
public class ArtistsView extends Fragment {

	// private String urlString;
	private Controller controller;

	/**
	 * 
	 */
	public ArtistsView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		ArtistsView p = new ArtistsView();
		return p;
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
			for (Artist a : controller.getArtists()) {
				list.add(a.toString());
			}
			ArtistArrayAdapter adapter = new ArtistArrayAdapter(getActivity().getApplicationContext(), list,
					controller.getArtists());
			listview.setAdapter(adapter);
			registerForContextMenu(listview);

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					Artist artist = controller.getArtists().get(position);

					controller.getSelectedAlbums().clear();
					for (Album a : controller.findAlbums(artist)) {
						controller.getSelectedAlbums().add(a);
					}
					// Create new fragment and transaction
					SelectedAlbumsView newFragment = new SelectedAlbumsView();

					// controller.getSelectedSongs().clear();
					// for (Song s : controller.findSongs(a)) {
					// controller.getSelectedSongs().add(s);
					// }
					// // Create new fragment and transaction
					// SelectedSongsView newFragment = new SelectedSongsView();
					FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

					// Replace whatever is in the fragment_container view with this fragment,
					// and add the transaction to the back stack
					transaction.replace(R.id.content_frame, newFragment);
					transaction.addToBackStack(null);
					// ((MainActivity) getActivity()).setActiveFragment(6);
					// Commit the transaction
					transaction.commit();
				}

			});
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
		Artist a = controller.getArtists().get((int) info.id);
		switch (item.getItemId()) {
		case R.id.contextMenuAdd:
			for (Song s : controller.findSongs(a)) {
				controller.getPlayNow().add(s);
			}
			Context context = getView().getContext();
			CharSequence text = getResources().getString(R.string.artistsViewArtistSongsAdded);
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return true;
		case R.id.contextMenuOpen:
			controller.getSelectedSongs().clear();
			for (Song s : controller.findSongs(a)) {
				controller.getSelectedSongs().add(s);
			}
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
