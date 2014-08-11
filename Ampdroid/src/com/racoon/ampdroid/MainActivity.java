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
package com.racoon.ampdroid;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.racoon.ampache.Album;
import com.racoon.ampache.Artist;
import com.racoon.ampache.Playlist;
import com.racoon.ampache.ServerConnection;
import com.racoon.ampache.Song;
import com.racoon.ampdroid.views.CurrentPlaylistView;
import com.racoon.ampdroid.views.SelectedAlbumsView;
import com.racoon.ampdroid.views.SelectedArtistsView;
import com.racoon.ampdroid.views.SelectedPlaylistsView;
import com.racoon.ampdroid.views.SelectedSongsView;

public class MainActivity extends FragmentActivity {

	private String[] mNavItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private Controller controller;
	private int mProgressStatus = 0;
	private ProgressBar mProgress;
	private TextView loadingText;
	private LinearLayout progressLinearLayout;
	private boolean syncAlbums;
	private boolean syncArtists;
	private boolean syncPlaylists;
	private boolean syncSongs;
	private int syncFilesCount;
	private String syncText;
	private FrameLayout contentFrame;
	private boolean serviceConnected;
	private Mp3PlayerService service = null;
	private Intent Mp3PlayerIntent = null;
	private int activeFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		controller = Controller.getInstance(getResources().getStringArray(R.array.menu_array));
		mNavItems = getResources().getStringArray(R.array.menu_array);
		mDrawerTitle = getResources().getString(R.string.app_name);
		mTitle = controller.getFragmentsNames()[0];

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(mTitle);

		mProgress = (ProgressBar) findViewById(R.id.load_progressbar);
		mProgress.setVisibility(ProgressBar.GONE);
		loadingText = (TextView) findViewById(R.id.load_progressbar_text);
		loadingText.setVisibility(TextView.GONE);
		progressLinearLayout = (LinearLayout) findViewById(R.id.progressbar_layout);
		progressLinearLayout.setVisibility(LinearLayout.GONE);
		contentFrame = (FrameLayout) findViewById(R.id.content_frame);

		/** Media Control **/
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		/** Connection established **/
		if (controller.getServerConfig(this) != null
				&& controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {
			Log.d("bug", controller.getServer().getAmpacheConnection().getAuth());
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[0]),
					controller.getFragmentsNames()[0]);
			activeFragment = 0;
			// tx.addToBackStack(null);
			clearBackStack();
			tx.commit();
			showToast(getResources().getString(R.string.toastServerConnected), Toast.LENGTH_LONG);
			controller.loadCachedFiles();
			/** Sync Files **/
			synchronize(false);

		} else if (controller.getServerConfig(this) != null
				&& !controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[5]),
					controller.getFragmentsNames()[5]);
			activeFragment = 5;
			// tx.addToBackStack(null);
			clearBackStack();
			tx.commit();
			mTitle = controller.getFragmentsNames()[5];
			getActionBar().setTitle(mTitle);
			showToast(getResources().getString(R.string.toastServerConnectionRefused), Toast.LENGTH_LONG);
		} else {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[5]),
					controller.getFragmentsNames()[5]);
			activeFragment = 5;
			// tx.addToBackStack(null);
			clearBackStack();
			tx.commit();
			mTitle = controller.getFragmentsNames()[5];
			getActionBar().setTitle(mTitle);
			showToast(getResources().getString(R.string.toastSettingsNotSet), Toast.LENGTH_LONG);
		}

		// just styling option add shadow the right edge of the drawer
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavItems));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
				FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
				tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[pos]),
						controller.getFragmentsNames()[pos]);
				activeFragment = pos;
				// tx.addToBackStack(null);
				clearBackStack();
				tx.commit();
				mTitle = controller.getFragmentsNames()[pos];
				getActionBar().setTitle(controller.getFragmentsNames()[pos]);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		});
		handleIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...
		if (item.toString().equals(getResources().getString(R.string.action_settings))) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[5]),
					controller.getFragmentsNames()[5]);
			activeFragment = 5;
			// tx.addToBackStack(null);
			clearBackStack();
			tx.commit();
			getActionBar().setTitle(R.string.action_settings);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, Mp3PlayerService.class), connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (serviceConnected) {
			unbindService(connection);
			serviceConnected = false;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	public void saveSettings(View view) {
		String server = ((EditText) findViewById(R.id.settingsServer)).getText().toString();
		String user = ((EditText) findViewById(R.id.settingsUser)).getText().toString();
		String password = ((EditText) findViewById(R.id.settingsPassword)).getText().toString();
		if (controller.getServer() != null && !controller.getServer().getPassword().equals(password)) {
			password = controller.generateShaHash(password);
		}
		controller.saveSettings(password, user, server);
		if (!controller.saveServer(getApplicationContext())) {
			showToast(getResources().getString(R.string.toastSettingsCouldntSave), Toast.LENGTH_LONG);
		} else {
			showToast(getResources().getString(R.string.toastSettingsSaved), Toast.LENGTH_SHORT);
			if (!this.controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {
				showToast(getResources().getString(R.string.toastConnectionRefused), Toast.LENGTH_SHORT);
			} else {
				showToast(getResources().getString(R.string.toastConnected), Toast.LENGTH_SHORT);
				FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
				tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[0]),
						controller.getFragmentsNames()[0]);
				activeFragment = 0;
				// tx.addToBackStack(null);
				clearBackStack();
				tx.commit();
				getActionBar().setTitle(controller.getFragmentsNames()[0]);

				/** Sync Files **/
				synchronize(true);
			}
		}
	}

	public void togglePlay(View view) {
		if (!controller.getPlayNow().isEmpty()) {
			if (serviceConnected) {
				CurrentPlaylistView instanceFragment = (CurrentPlaylistView) getSupportFragmentManager()
						.findFragmentById(R.id.content_frame);
				if (getService().getCurrentSong() != null) {
					service.pause();
				} else {
					service.stop();
					Mp3PlayerIntent = new Intent(this, Mp3PlayerService.class);
					Mp3PlayerIntent.putExtra("com.racoon.ampdroid.NowPlaying", this.controller.getPlayNow());
					Mp3PlayerIntent.putExtra("ACTION", "play");
					startService(Mp3PlayerIntent);
				}
				instanceFragment.updateSongData();
			}

		}
	}

	public void stop(View view) {
		service.stop();
		CurrentPlaylistView instanceFragment = (CurrentPlaylistView) getSupportFragmentManager().findFragmentById(
				R.id.content_frame);
		instanceFragment.cleanView();
	}

	public void play(int pos) {
		Log.d("service", "artist name: " + service.getArtist());
		service.stop();
		Mp3PlayerIntent = new Intent(this, Mp3PlayerService.class);
		Mp3PlayerIntent.putExtra("com.racoon.ampdroid.NowPlaying", this.controller.getPlayNow());
		Mp3PlayerIntent.putExtra("CURSOR", pos);
		Mp3PlayerIntent.putExtra("SESSION", this.controller.getServer().getAmpacheConnection().getAuth());
		Mp3PlayerIntent.putExtra("ACTION", "play");
		startService(Mp3PlayerIntent);
	}

	public void next(View view) {
		if (service != null) {
			service.next();
			CurrentPlaylistView instanceFragment = (CurrentPlaylistView) getSupportFragmentManager().findFragmentById(
					R.id.content_frame);
			instanceFragment.updateSongData();
		}
	}

	public void previous(View view) {
		if (service != null) {
			service.previous();
			CurrentPlaylistView instanceFragment = (CurrentPlaylistView) getSupportFragmentManager().findFragmentById(
					R.id.content_frame);
			instanceFragment.updateSongData();
		}
	}

	/**
	 * Synchronizes the files if the corresponding boolean is true and starts an AsyncTask.
	 * 
	 * @param force boolean True if a synchronization is forced to synchronize everything
	 */
	public void synchronize(boolean force) {
		syncFilesCount = 0;
		ServerConnection ampache = controller.getServer().getAmpacheConnection();
		Log.d("bug songs anzahl", controller.getServer().getCachedData().getSongs().size() + ", "
				+ controller.getServer().getAmpacheConnection().getSongs());
		Log.d("bug albums anzahl", controller.getServer().getCachedData().getAlbums().size() + ", "
				+ controller.getServer().getAmpacheConnection().getAlbums());
		Log.d("bug artists anzahl", controller.getServer().getCachedData().getArtists().size() + ", "
				+ controller.getServer().getAmpacheConnection().getArtists());
		Log.d("bug playlists anzahl", controller.getServer().getCachedData().getPlaylists().size() + ", "
				+ controller.getServer().getAmpacheConnection().getPlaylists());
		if (controller.getServer().getCachedData().getAlbums().size() != controller.getServer().getAmpacheConnection()
				.getAlbums()
				|| force) {
			syncAlbums = true;
			syncFilesCount += ampache.getAlbums();
		}
		if (controller.getServer().getCachedData().getArtists().size() != controller.getServer().getAmpacheConnection()
				.getArtists()
				|| force) {
			syncArtists = true;
			syncFilesCount += ampache.getArtists();
		}
		if (controller.getServer().getCachedData().getPlaylists().size() != controller.getServer()
				.getAmpacheConnection().getPlaylists()
				|| force) {
			syncPlaylists = true;
			syncFilesCount += ampache.getPlaylists();
		}
		if (controller.getServer().getCachedData().getSongs().size() != controller.getServer().getAmpacheConnection()
				.getSongs()
				|| force) {
			syncSongs = true;
			syncFilesCount += ampache.getSongs();
		}

		if (syncAlbums || syncArtists || syncPlaylists || syncSongs || force) {
			new DownloadFilesTask().execute();
		}
	}

	private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			progressLinearLayout.setVisibility(LinearLayout.VISIBLE);
			mProgress.setVisibility(ProgressBar.VISIBLE);
			loadingText.setVisibility(TextView.VISIBLE);
			float scale = getResources().getDisplayMetrics().density;
			int dpAsPixels = (int) (48 * scale + 0.5f);
			int dpAsPixelsDim = (int) (16 * scale + 0.5f);
			contentFrame.setPadding(dpAsPixelsDim, dpAsPixels, dpAsPixelsDim, dpAsPixelsDim);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			String urlString = "";
			if (syncSongs) {
				syncText = "Lade Songs";
				urlString = controller.getServer().getHost() + "/server/xml.server.php?action=songs&auth="
						+ controller.getServer().getAuthKey();
				controller.parseSongs(urlString);
				controller.getServer().getCachedData().setSongs(controller.getSongs());
				publishProgress();
			}

			if (syncAlbums) {
				syncText = "Lade Alben";
				urlString = controller.getServer().getHost() + "/server/xml.server.php?action=albums&auth="
						+ controller.getServer().getAuthKey();
				controller.parseAlbums(urlString);
				controller.getServer().getCachedData().setAlbums(controller.getAlbums());
				publishProgress();
			}

			if (syncPlaylists) {
				syncText = "Lade Playlists";
				urlString = controller.getServer().getHost() + "/server/xml.server.php?action=playlists&auth="
						+ controller.getServer().getAuthKey();
				controller.parsePlaylists(urlString);
				controller.getServer().getCachedData().setPlaylists(controller.getPlaylists());
				publishProgress();
			}

			if (syncArtists) {
				syncText = "Lade Interpreten";
				urlString = controller.getServer().getHost() + "/server/xml.server.php?action=artists&auth="
						+ controller.getServer().getAuthKey();
				controller.parseArtists(urlString);
				controller.getServer().getCachedData().setArtists(controller.getArtists());
				publishProgress();
			}
			if (controller.saveServer(getApplicationContext())) {
				Log.d("bug", "sync erfolgreich gespeichert.");
				Log.d("bug", "songs hat jetzt " + controller.getServer().getCachedData().getSongs().size() + " Dateien");
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... params) {
			loadingText.setText(syncText);
			mProgressStatus = ((int) ((((double) (controller.getProgress()) / ((double) (syncFilesCount))) * 100)));
			Log.d("progress", String.valueOf(controller.getProgress()));
			Log.d("progress prozent", String.valueOf(mProgressStatus));
			mProgress.setProgress(mProgressStatus);
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgress.setVisibility(ProgressBar.GONE);
			loadingText.setVisibility(TextView.GONE);
			progressLinearLayout.setVisibility(LinearLayout.GONE);
			Log.d("sync", "done");
			Log.d("bug songs anzahl", controller.getServer().getCachedData().getSongs().size() + ", "
					+ controller.getServer().getAmpacheConnection().getSongs());
			Log.d("bug albums anzahl", controller.getServer().getCachedData().getAlbums().size() + ", "
					+ controller.getServer().getAmpacheConnection().getAlbums());
			Log.d("bug artists anzahl", controller.getServer().getCachedData().getArtists().size() + ", "
					+ controller.getServer().getAmpacheConnection().getArtists());
			Log.d("bug playlists anzahl", controller.getServer().getCachedData().getPlaylists().size() + ", "
					+ controller.getServer().getAmpacheConnection().getPlaylists());
			controller.setProgress(0);
			float scale = getResources().getDisplayMetrics().density;
			int dpAsPixels = (int) (16 * scale + 0.5f);
			contentFrame.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
		}

	}

	/**
	 * Reconnects the server connection and gets new session or prolongs it.
	 * 
	 * @param view View
	 */
	public void reconnect(View view) {
		try {
			if (controller.isOnline(getApplicationContext())) {
				if (this.controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {
					showToast(getResources().getString(R.string.toastConnected), Toast.LENGTH_SHORT);
					/** Sync Files **/
					synchronize(false);
				} else {
					showToast(getResources().getString(R.string.toastConnectionRefused), Toast.LENGTH_SHORT);
				}
			} else {
				showToast(getResources().getString(R.string.toastInternetConnectionLost), Toast.LENGTH_SHORT);
			}
		} catch (NullPointerException e) {
			Log.d("bug", "Server Verbindung nicht vorhanden");
		}
	}

	public void showToast(String message, int duration) {
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}

	/**
	 * Forces a sync of all files.
	 * 
	 * @param view View
	 */
	public void forceSync(View view) {
		try {
			if (controller.isOnline(getApplicationContext())) {
				if (this.controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {
					showToast(getResources().getString(R.string.toastConnected), Toast.LENGTH_SHORT);
					/** Sync Files **/
					synchronize(true);
				} else {
					showToast(getResources().getString(R.string.toastConnectionRefused), Toast.LENGTH_SHORT);
				}
			} else {
				showToast(getResources().getString(R.string.toastInternetConnectionLost), Toast.LENGTH_SHORT);
			}
		} catch (NullPointerException e) {
			Log.d("bug", "Server Verbindung nicht vorhanden");
		}
	}

	@SuppressLint("DefaultLocale")
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			ArrayList<Song> searchableSongs = new ArrayList<Song>();
			Log.d("bugs", "active fragment " + String.valueOf(activeFragment));
			/** find song **/
			if (activeFragment < 2 || activeFragment == 6) {
				ArrayList<Song> result = new ArrayList<Song>();
				if (activeFragment == 0) {
					searchableSongs = controller.getPlayNow();
				} else if (activeFragment == 1) {
					searchableSongs = controller.getSongs();
				} else if (activeFragment == 6) {
					searchableSongs = controller.getSelectedSongs();
				}
				for (int i = 0; i < searchableSongs.size(); i++) {
					if (searchableSongs.get(i).getTitle().toLowerCase().contains(query.toLowerCase())) {
						result.add(searchableSongs.get(i));
					}
				}
				controller.setSelectedSongs(result);
				// Create new fragment and transaction
				SelectedSongsView newFragment = new SelectedSongsView();
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.replace(R.id.content_frame, newFragment);
				transaction.addToBackStack(null);
				activeFragment = 6;
				// Commit the transaction
				transaction.commit();
			}
			/** find artist **/
			else if (activeFragment == 2) {
				ArrayList<Artist> result = new ArrayList<Artist>();
				for (int i = 0; i < controller.getArtists().size(); i++) {
					if (controller.getArtists().get(i).getName().toLowerCase().contains(query.toLowerCase())) {
						result.add(controller.getArtists().get(i));
					}
				}
				controller.setSelectedArtists(result);
				// Create new fragment and transaction
				SelectedArtistsView newFragment = new SelectedArtistsView();
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.replace(R.id.content_frame, newFragment);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
			}
			/** find playlist **/
			else if (activeFragment == 4) {
				ArrayList<Playlist> result = new ArrayList<Playlist>();
				for (int i = 0; i < controller.getPlaylists().size(); i++) {
					if (controller.getPlaylists().get(i).getName().toLowerCase().contains(query.toLowerCase())) {
						result.add(controller.getPlaylists().get(i));
					}
				}
				controller.setSelectedPlaylists(result);
				// Create new fragment and transaction
				SelectedPlaylistsView newFragment = new SelectedPlaylistsView();
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.replace(R.id.content_frame, newFragment);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
			}
			/** find album **/
			else if (activeFragment == 3) {
				ArrayList<Album> result = new ArrayList<Album>();
				for (int i = 0; i < controller.getAlbums().size(); i++) {
					if (controller.getAlbums().get(i).getName().toLowerCase().contains(query.toLowerCase())) {
						result.add(controller.getAlbums().get(i));
					}
				}
				controller.setSelectedAlbums(result);
				// Create new fragment and transaction
				SelectedAlbumsView newFragment = new SelectedAlbumsView();
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.replace(R.id.content_frame, newFragment);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
			}
			Log.d("search", "searchquery " + query);
		}
	}

	private void clearBackStack() {
		for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
			getSupportFragmentManager().popBackStack();
		}
	}

	/**
	 * @return the mProgress
	 */
	public ProgressBar getmProgress() {
		return mProgress;
	}

	/**
	 * @param mProgress the mProgress to set
	 */
	public void setmProgress(ProgressBar mProgress) {
		this.mProgress = mProgress;
	}

	/**
	 * @return the loadingText
	 */
	public TextView getLoadingText() {
		return loadingText;
	}

	/**
	 * @param loadingText the loadingText to set
	 */
	public void setLoadingText(TextView loadingText) {
		this.loadingText = loadingText;
	}

	/**
	 * @return the service
	 */
	public Mp3PlayerService getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(Mp3PlayerService service) {
		this.service = service;
	}

	/**
	 * @param activeFragment the activeFragment to set
	 */
	public void setActiveFragment(int activeFragment) {
		this.activeFragment = activeFragment;
	}

	/**
	 * Create a connection to the Mp3PlayerService
	 */
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		// Called when connection is made
		public void onServiceConnected(ComponentName cName, IBinder binder) {
			service = ((Mp3PlayerService.Mp3Binder) binder).getService();
			serviceConnected = true;
		}

		@Override
		//
		public void onServiceDisconnected(ComponentName cName) {
			service = null;
			serviceConnected = false;
		}
	};

}
