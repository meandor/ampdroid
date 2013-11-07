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

import com.racoon.ampache.Album;
import com.racoon.ampache.Song;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.R;
//import com.racoon.ampdroid.ServerConnector;
import com.racoon.ampdroid.StableArrayAdapter;

/**
 * @author Daniel Schruhl
 * 
 */
public class AlbumsView extends Fragment {

//	private String urlString;
	private Controller controller;

	/**
	 * 
	 */
	public AlbumsView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		AlbumsView p = new AlbumsView();
		return p;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.ampache_songs, null);
		ListView listview = (ListView) root.findViewById(R.id.songs_listview);
		if (controller.getServer() != null) {
//			ServerConnector server = controller.getServer();
//			urlString = server.getServer() + "/server/xml.server.php?action=albums&auth=" + server.getAuthKey();
			// if (controller.getAlbums().size() != controller.getServer().getAmpacheConnection().getAlbums()) {
			// controller.parseAlbums(urlString);
			// }
			ArrayList<String> list = new ArrayList<String>();
			for (Album a : controller.getAlbums()) {
				list.add(a.toString());
			}
			StableArrayAdapter adapter = new StableArrayAdapter(getActivity().getApplicationContext(),
					R.layout.content_list_item, list);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					Album a = controller.getAlbums().get(position);
					Log.d("gesuchte Songs", controller.findSongs(a).toString());
					for (Song s : controller.findSongs(a)) {
						controller.getPlayNow().add(s);
					}
					Context context = view.getContext();
					CharSequence text = "Album zur Wiedergabe hinzugefügt";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}

			});
		}
		return root;
	}
}
