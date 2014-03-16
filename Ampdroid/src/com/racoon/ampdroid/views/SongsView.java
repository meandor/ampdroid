/**
 * 
 */
package com.racoon.ampdroid.views;

import java.util.ArrayList;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.racoon.ampache.Song;
import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.R;
//import com.racoon.ampdroid.ServerConnector;
import com.racoon.ampdroid.StableArrayAdapter;

/**
 * @author Daniel Schruhl
 * 
 */
public class SongsView extends Fragment {

	// private String urlString;
	private Controller controller;

	/**
	 * 
	 */
	public SongsView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		SongsView p = new SongsView();
		return p;
	}

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
			for (Song s : controller.getSongs()) {
				list.add(s.toString());
			}
			StableArrayAdapter adapter = new StableArrayAdapter(getActivity().getApplicationContext(),
					R.layout.content_list_item, list);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					Log.d("Play now added:", controller.getSongs().get(position).toString());
					controller.getPlayNow().add(controller.getSongs().get(position));
					Context context = view.getContext();
					CharSequence text = "Zur Wiedergabe hinzugefügt";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}

			});
		}
		return root;
	}
}
