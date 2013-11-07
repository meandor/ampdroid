/**
 * 
 */
package com.racoon.ampdroid.views;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.racoon.ampache.Playlist;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.R;
import com.racoon.ampdroid.StableArrayAdapter;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.ampache_playlists, null);
		ListView listview = (ListView) root.findViewById(R.id.playlists_listview);
		if (controller.getServer() != null) {
			// ServerConnector server = controller.getServer();
			// urlString = server.getServer() + "/server/xml.server.php?action=playlists&auth=" + server.getAuthKey();
			// if (controller.getPlaylists().size() != controller.getServer().getAmpacheConnection().getPlaylists()) {
			// controller.parsePlaylists(urlString);
			// }
			ArrayList<String> list = new ArrayList<String>();
			for (Playlist p : controller.getPlaylists()) {
				list.add(p.toString());
			}
			StableArrayAdapter adapter = new StableArrayAdapter(getActivity().getApplicationContext(),
					R.layout.content_list_item, list);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					Playlist selected = controller.getPlaylists().get(position);

					String urlString = controller.getServer().getHost()
							+ "/server/xml.server.php?action=playlist_songs&auth="
							+ controller.getServer().getAuthKey() + "&filter=" + String.valueOf(selected.getId());
					Log.d("url", urlString);
					controller.parsePlaylistSongs(urlString);

					Context context = view.getContext();
					CharSequence text = "Playlist zur Wiedergabe hinzugefügt";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}

			});
		} else {
			Log.d("bugs", "server null");
		}
		return root;
	}
}
