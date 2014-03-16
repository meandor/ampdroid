/**
 * 
 */
package com.racoon.ampdroid;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.racoon.ampache.Album;
import com.racoon.ampache.Artist;
import com.racoon.ampache.Playlist;
import com.racoon.ampache.Song;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Daniel Schruhl
 * 
 */

public class Controller {

	private static Controller controller = null;
	private ServerConnector server;
	private String[] fragments;
	private String[] fragmentsNames;
	private ArrayList<Playlist> playlists;
	private ArrayList<Song> songs;
	private ArrayList<Artist> artists;
	private ArrayList<Album> albums;
	private ArrayList<Song> playNow;
	private int playNowPosition;
	private Song playingNow;
	private MediaPlayer mediaPlayer;
	private int progress = 0;

	/**
	 * 
	 */
	public Controller() {
		init();
	}

	/**
	 * Method to initialize the controller.
	 */
	private void init() {
		fragments = new String[6];
		fragments[0] = "com.racoon.ampdroid.views.CurrentPlaylistView";
		fragments[1] = "com.racoon.ampdroid.views.SongsView";
		fragments[2] = "com.racoon.ampdroid.views.ArtistsView";
		fragments[3] = "com.racoon.ampdroid.views.AlbumsView";
		fragments[4] = "com.racoon.ampdroid.views.PlaylistsView";
		fragments[5] = "com.racoon.ampdroid.views.SettingsView";

		fragmentsNames = new String[6];
		fragmentsNames[0] = "Spielt gerade";
		fragmentsNames[1] = "Titel";
		fragmentsNames[2] = "Interpreten";
		fragmentsNames[3] = "Alben";
		fragmentsNames[4] = "Playlists";
		fragmentsNames[5] = "Einstellungen";

		this.playlists = new ArrayList<Playlist>();
		this.songs = new ArrayList<Song>();
		this.playNow = new ArrayList<Song>();
		this.artists = new ArrayList<Artist>();
		this.albums = new ArrayList<Album>();
		this.server = new ServerConnector("", "", "");

		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	public void saveSettings(String password, String user, String host) {
		Log.d("passwort-raw:", password);
		Log.d("passwort-raw:", generateShaHash(password));
		this.server.setPassword(password);
		this.server.setUser(user);
		this.server.setHost(host);
	}

	/**
	 * @return the fragmentsNames
	 */
	public String[] getFragmentsNames() {
		return fragmentsNames;
	}

	/**
	 * @param fragmentsNames the fragmentsNames to set
	 */
	public void setFragmentsNames(String[] fragmentsNames) {
		this.fragmentsNames = fragmentsNames;
	}

	/**
	 * @return the fragments
	 */
	public String[] getFragments() {
		return fragments;
	}

	/**
	 * @param fragments the fragments to set
	 */
	public void setFragments(String[] fragments) {
		this.fragments = fragments;
	}

	/**
	 * Allows to get the only Instance of the android controller singleton.
	 * 
	 * @return the instance of this controller class
	 */
	public static Controller getInstance() {
		if (controller == null) {
			controller = new Controller();
		}
		return controller;
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public String generateShaHash(String s) {
		String hash = "";
		hash = bin2hex(getHash(s)).toLowerCase();
		return hash;
	}

	/**
	 * 
	 * @param password
	 * @return
	 */
	public byte[] getHash(String password) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		digest.reset();
		return digest.digest(password.getBytes());
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	static String bin2hex(byte[] data) {
		return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
	}

	public boolean saveServer(Context context) {
		try {
			FileOutputStream fos = context.openFileOutput("config", Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.server);
			// Log.d("ser", server.getAmpacheConnection().getAuth());
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("bug", "Speichern nicht erfolgreich");
			return false;
		}
		return true;
	}

	/**
	 * Returns if online connectivity is given or not.
	 * 
	 * @param context ApplicationContext
	 * @return {@code true} if connected to Internet, {@code false} otherwise
	 */
	public boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public void loadCachedFiles() {
		this.setSongs(this.server.getCachedData().getSongs());
		this.setAlbums(this.server.getCachedData().getAlbums());
		this.setArtists(this.server.getCachedData().getArtists());
		this.setPlaylists(this.server.getCachedData().getPlaylists());
	}

	public ServerConnector getServerConfig(Context context) {
		try {
			FileInputStream fis = context.openFileInput("config");
			ObjectInputStream is = new ObjectInputStream(fis);
			Object readObject = is.readObject();
			is.close();

			if (readObject != null && readObject instanceof ServerConnector) {
				setServer((ServerConnector) readObject);
				// Log.d("ser", ((ServerConnector) readObject).getAmpacheConnection().getAuth());
				return (ServerConnector) readObject;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<Song> findSongs(Artist artist) {
		ArrayList<Song> result = new ArrayList<Song>();
		for (Song s : songs) {
			if (s.getArtist().equals(artist.getName())) {
				result.add(s);
				this.progress++;
			}
		}
		return result;
	}

	public ArrayList<Song> findSongs(Album album) {
		ArrayList<Song> result = new ArrayList<Song>();
		for (Song s : songs) {
			if (s.getAlbum().equals(album.getName())) {
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * @return the server
	 */
	public ServerConnector getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(ServerConnector server) {
		this.server = server;
	}

	public void parseSongs(String urlString) {
		Log.d("songs", urlString);
		Log.d("songs anzahl", String.valueOf(server.getAmpacheConnection().getSongs()));
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			XmlPullParserFactory pullParserFactory;
			try {
				pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				InputStream in_s = con.getInputStream();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in_s, null);
				parseSongsXML(parser);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.d("error", "keine Verbindung möglich");
			e.printStackTrace();
		}
	}

	public void parsePlaylistSongs(String urlString) {
		Log.d("playlist Songs", urlString);
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			XmlPullParserFactory pullParserFactory;
			try {
				pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				InputStream in_s = con.getInputStream();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in_s, null);
				parsePlaylistSongsXML(parser);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.d("error", "keine Verbindung möglich");
			e.printStackTrace();
		}
	}

	private void parsePlaylistSongsXML(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		Song currentSong = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				Log.d("playlist songs titles:", name);
				if (name.equals("song")) {
					currentSong = new Song();
					currentSong.setId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
				} else if (currentSong != null) {
					if (name.equals("title")) {
						currentSong.setTitle(parser.nextText());
					} else if (name.equals("artist")) {
						currentSong.setArtist(parser.nextText());
						// currentSong.setArtistId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
					} else if (name.equals("album")) {
						currentSong.setAlbum(parser.nextText());
						// currentSong.setAlbumId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
					} else if (name.equals("track")) {
						currentSong.setTrackNumber(Integer.parseInt(parser.nextText()));
					} else if (name.equals("time")) {
						currentSong.setTime(Integer.parseInt(parser.nextText()));
					} else if (name.equals("year")) {
						currentSong.setYear(Integer.parseInt(parser.nextText()));
					} else if (name.equals("bitrate")) {
						currentSong.setBitrate(Integer.parseInt(parser.nextText()));
					} else if (name.equals("mode")) {
						currentSong.setMode(parser.nextText());
					} else if (name.equals("mime")) {
						currentSong.setMime(parser.nextText());
					} else if (name.equals("url")) {
						currentSong.setUrl(parser.nextText());
					} else if (name.equals("size")) {
						currentSong.setSize(Integer.parseInt(parser.nextText()));
					} else if (name.equals("art")) {
						currentSong.setArt(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("song") && currentSong != null) {
					this.playNow.add(currentSong);
				}
			}
			eventType = parser.next();
		}
		Log.d("songs:", this.playNow.toString());
	}

	public void parsePlaylists(String urlString) {
		Log.d("playlists", urlString);
		Log.d("playlists anzahl", String.valueOf(server.getAmpacheConnection().getPlaylists()));
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			XmlPullParserFactory pullParserFactory;
			try {
				pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				InputStream in_s = con.getInputStream();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in_s, null);
				parsePlaylistsXML(parser);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.d("error", "keine Verbindung möglich");
			e.printStackTrace();
		}
	}

	private void parsePlaylistsXML(XmlPullParser parser) throws XmlPullParserException, IOException {
		this.playlists = new ArrayList<Playlist>();
		int eventType = parser.getEventType();
		Playlist currentPlaylist = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equals("playlist")) {
					currentPlaylist = new Playlist();
					currentPlaylist.setId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
				} else if (currentPlaylist != null) {
					if (name.equals("name")) {
						currentPlaylist.setName(parser.nextText());
					} else if (name.equals("owner")) {
						currentPlaylist.setOwner(parser.nextText());
					} else if (name.equals("items")) {
						currentPlaylist.setItems(parser.nextText());
					} else if (name.equals("type")) {
						currentPlaylist.setType(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("playlist") && currentPlaylist != null) {
					this.playlists.add(currentPlaylist);
					this.progress++;
				}
			}
			eventType = parser.next();
		}
		Log.d("playlists:", this.playlists.toString());
	}

	private void parseSongsXML(XmlPullParser parser) throws XmlPullParserException, IOException {
		this.songs = new ArrayList<Song>();
		int eventType = parser.getEventType();
		Song currentSong = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equals("song")) {
					currentSong = new Song();
					currentSong.setId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
				} else if (currentSong != null) {
					if (name.equals("title")) {
						currentSong.setTitle(parser.nextText());
					} else if (name.equals("artist")) {
						currentSong.setArtist(parser.nextText());
						// currentSong.setArtistId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
					} else if (name.equals("album")) {
						currentSong.setAlbum(parser.nextText());
						// currentSong.setAlbumId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
					} else if (name.equals("track")) {
						currentSong.setTrackNumber(Integer.parseInt(parser.nextText()));
					} else if (name.equals("time")) {
						currentSong.setTime(Integer.parseInt(parser.nextText()));
					} else if (name.equals("year")) {
						currentSong.setYear(Integer.parseInt(parser.nextText()));
					} else if (name.equals("bitrate")) {
						currentSong.setBitrate(Integer.parseInt(parser.nextText()));
					} else if (name.equals("mode")) {
						currentSong.setMode(parser.nextText());
					} else if (name.equals("mime")) {
						currentSong.setMime(parser.nextText());
					} else if (name.equals("url")) {
						currentSong.setUrl(parser.nextText());
					} else if (name.equals("size")) {
						currentSong.setSize(Integer.parseInt(parser.nextText()));
					} else if (name.equals("art")) {
						currentSong.setArt(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("song") && currentSong != null) {
					this.songs.add(currentSong);
					this.progress++;
				}
			}
			eventType = parser.next();
		}
		Log.d("songs:", this.songs.toString());
	}

	public void parseAlbums(String urlString) {
		Log.d("albums", urlString);
		Log.d("albums anzahl", String.valueOf(server.getAmpacheConnection().getAlbums()));
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			XmlPullParserFactory pullParserFactory;
			try {
				pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				InputStream in_s = con.getInputStream();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in_s, null);
				parseAlbumsXML(parser);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.d("error", "keine Verbindung möglich");
			e.printStackTrace();
		}
	}

	private void parseAlbumsXML(XmlPullParser parser) throws XmlPullParserException, IOException {
		this.albums = new ArrayList<Album>();
		int eventType = parser.getEventType();
		Album currentAlbum = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equals("album")) {
					currentAlbum = new Album();
					currentAlbum.setId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
				} else if (currentAlbum != null) {
					if (name.equals("name")) {
						currentAlbum.setName(parser.nextText());
					} else if (name.equals("artist")) {
						// currentAlbum.setArtistId(Integer.parseInt(parser.getAttributeValue(null, "id")));
						currentAlbum.setArtist(parser.nextText());
					} else if (name.equals("tracks")) {
						currentAlbum.setTracks(Integer.parseInt(parser.nextText()));
					} else if (name.equals("art")) {
						currentAlbum.setArt(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("album") && currentAlbum != null) {
					this.albums.add(currentAlbum);
					this.progress++;
				}
			}
			eventType = parser.next();
		}
		Log.d("albums:", this.albums.toString());
	}

	public void parseArtists(String urlString) {
		Log.d("artists", urlString);
		Log.d("artists anzahl", String.valueOf(server.getAmpacheConnection().getArtists()));
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			XmlPullParserFactory pullParserFactory;
			try {
				pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				InputStream in_s = con.getInputStream();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in_s, null);
				parseArtistsXML(parser);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.d("error", "keine Verbindung möglich");
			e.printStackTrace();
		}
	}

	private void parseArtistsXML(XmlPullParser parser) throws XmlPullParserException, IOException {
		this.artists = new ArrayList<Artist>();
		int eventType = parser.getEventType();
		Artist currentArtist = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equals("artist")) {
					currentArtist = new Artist();
					currentArtist.setId(Integer.parseInt((parser.getAttributeValue(null, "id"))));
				} else if (currentArtist != null) {
					if (name.equals("name")) {
						currentArtist.setName(parser.nextText());
					} else if (name.equals("albums")) {
						currentArtist.setAlbums(Integer.parseInt(parser.nextText()));
					} else if (name.equals("songs")) {
						currentArtist.setSongs(Integer.parseInt(parser.nextText()));
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("artist") && currentArtist != null) {
					this.artists.add(currentArtist);
					this.progress++;
				}
			}
			eventType = parser.next();
		}
		Log.d("artists:", this.artists.toString());
	}

	/**
	 * @return the playlists
	 */
	public ArrayList<Playlist> getPlaylists() {
		return playlists;
	}

	/**
	 * @param playlists the playlists to set
	 */
	public void setPlaylists(ArrayList<Playlist> playlists) {
		this.playlists = playlists;
	}

	/**
	 * @return the songs
	 */
	public ArrayList<Song> getSongs() {
		return songs;
	}

	/**
	 * @param songs the songs to set
	 */
	public void setSongs(ArrayList<Song> songs) {
		this.songs = songs;
	}

	/**
	 * @return the artists
	 */
	public ArrayList<Artist> getArtists() {
		return artists;
	}

	/**
	 * @param artists the artists to set
	 */
	public void setArtists(ArrayList<Artist> artists) {
		this.artists = artists;
	}

	/**
	 * @return the playNow
	 */
	public ArrayList<Song> getPlayNow() {
		return playNow;
	}

	/**
	 * @param playNow the playNow to set
	 */
	public void setPlayNow(ArrayList<Song> playNow) {
		this.playNow = playNow;
	}

	/**
	 * @return the playingNow
	 */
	public Song getPlayingNow() {
		return playingNow;
	}

	/**
	 * @param playingNow the playingNow to set
	 */
	public void setPlayingNow(Song playingNow) {
		this.playingNow = playingNow;
	}

	/**
	 * @return the albums
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}

	/**
	 * @param albums the albums to set
	 */
	public void setAlbums(ArrayList<Album> albums) {
		this.albums = albums;
	}

	/**
	 * @return the mediaPlayer
	 */
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	/**
	 * @param mediaPlayer the mediaPlayer to set
	 */
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the playNowPosition
	 */
	public int getPlayNowPosition() {
		return playNowPosition;
	}

	/**
	 * @param playNowPosition the playNowPosition to set
	 */
	public void setPlayNowPosition(int playNowPosition) {
		this.playNowPosition = playNowPosition;
	}

}
