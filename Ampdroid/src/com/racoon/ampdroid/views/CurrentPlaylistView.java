/**
 * 
 */
package com.racoon.ampdroid.views;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.racoon.ampache.ServerConnection;
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
	private ProgressBar mProgress;
	private int mProgressStatus = 0;
	private TextView loadingText;

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
		mProgress = (ProgressBar) root.findViewById(R.id.load_progressbar);
		mProgress.setVisibility(ProgressBar.GONE);
		loadingText = (TextView) root.findViewById(R.id.load_progressbar_text);
		loadingText.setVisibility(TextView.GONE);
		if (controller.getSongs().size() == 0 && controller.isOnline(getActivity())) {
			/** Sync Files **/
			new DownloadFilesTask().execute();
		}
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
					controller.getMediaPlayer().reset();
					controller.getMediaPlayer().setDataSource(controller.getPlayingNow().getUrl());
					controller.getMediaPlayer().prepare(); // might take long! (for buffering, etc)
					controller.getMediaPlayer().start();
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

	private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mProgress.setVisibility(ProgressBar.VISIBLE);
			loadingText.setVisibility(TextView.VISIBLE);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			String urlString = controller.getServer().getServer() + "/server/xml.server.php?action=albums&auth="
					+ controller.getServer().getAuthKey();
			controller.parseAlbums(urlString);
			publishProgress();
			urlString = controller.getServer().getServer() + "/server/xml.server.php?action=songs&auth="
					+ controller.getServer().getAuthKey();
			controller.parseSongs(urlString);
			publishProgress();
			urlString = controller.getServer().getServer() + "/server/xml.server.php?action=playlists&auth="
					+ controller.getServer().getAuthKey();
			controller.parsePlaylists(urlString);
			publishProgress();
			urlString = controller.getServer().getServer() + "/server/xml.server.php?action=artists&auth="
					+ controller.getServer().getAuthKey();
			controller.parseArtists(urlString);
			publishProgress();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... params) {
			ServerConnection ampache = controller.getServer().getAmpacheConnection();
			int count = ampache.getAlbums() + ampache.getSongs() + ampache.getPlaylists() + ampache.getArtists();
			mProgressStatus = ((int) ((((double) (controller.getProgress()) / ((double) (count))) * 100)));
			Log.d("progress", String.valueOf(controller.getProgress()));
			Log.d("progress prozent", String.valueOf(mProgressStatus));
			mProgress.setProgress(mProgressStatus);
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgress.setVisibility(ProgressBar.GONE);
			loadingText.setVisibility(TextView.GONE);
			Log.d("sync", "done");
		}

	}
}
