/**
 * 
 */
package com.racoon.ampdroid.views;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.racoon.ampache.Album;
import com.racoon.ampache.Song;
import com.racoon.ampdroid.AlbumArrayAdapter;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.MainActivity;
import com.racoon.ampdroid.R;

/**
 * @author Daniel Schruhl
 * 
 */
public class SelectedAlbumsView extends Fragment {

	private Controller controller;

	/**
	 * 
	 */
	public SelectedAlbumsView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		SelectedAlbumsView p = new SelectedAlbumsView();
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
			for (Album a : controller.getSelectedAlbums()) {
				list.add(a.toString());
			}
			AlbumArrayAdapter adapter = new AlbumArrayAdapter(getActivity().getApplicationContext(), list,
					controller.getSelectedAlbums());
			listview.setAdapter(adapter);
			registerForContextMenu(listview);

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					Album a = controller.getSelectedAlbums().get(position);
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
					((MainActivity) getActivity()).setActiveFragment(6);
					// Commit the transaction
					transaction.commit();
				}

			});
		}
		setHasOptionsMenu(true);
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
		Album a = controller.getSelectedAlbums().get((int) info.id);
		switch (item.getItemId()) {
		case R.id.contextMenuAdd:
			for (Song s : controller.findSongs(a)) {
				controller.getPlayNow().add(s);
			}
			Context context = getView().getContext();
			CharSequence text = getResources().getString(R.string.albumsViewAlbumsAdded);
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
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.selected_songs, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit_add_all:
			for (Album a : controller.getSelectedAlbums()) {
				for (Song s : controller.findSongs(a)) {
					controller.getPlayNow().add(s);
				}
			}
			Context context = getView().getContext();
			CharSequence text = getResources().getString(R.string.albumsViewAlbumsAdded);
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
