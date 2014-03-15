/**
 * 
 */
package com.racoon.ampache;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Daniel Schruhl
 * 
 */
public class ServerConnection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String auth;
	private String api;
	private Calendar sessionExpire;
	private String update;
	private String add;
	private String clean;
	private int songs;
	private int albums;
	private int artists;
	private int playlists;
	private int videos;
	private int catalogs;

	public ServerConnection() {
		super();
	}

	public ServerConnection(String auth, String api, Calendar sessionExpire, String update, String add, String clean,
			int songs, int albums, int artists, int playlists, int videos, int catalogs) {
		super();
		this.auth = auth;
		this.api = api;
		this.sessionExpire = sessionExpire;
		this.update = update;
		this.add = add;
		this.clean = clean;
		this.songs = songs;
		this.albums = albums;
		this.artists = artists;
		this.playlists = playlists;
		this.videos = videos;
		this.catalogs = catalogs;
	}

	/**
	 * @return the auth
	 */
	public String getAuth() {
		return auth;
	}

	/**
	 * @param auth the auth to set
	 */
	public void setAuth(String auth) {
		this.auth = auth;
	}

	/**
	 * @return the api
	 */
	public String getApi() {
		return api;
	}

	/**
	 * @param api the api to set
	 */
	public void setApi(String api) {
		this.api = api;
	}

	/**
	 * @return the session_expire
	 */
	public Calendar getSessionExpire() {
		return sessionExpire;
	}

	/**
	 * @return the session_expire String
	 */
	public String getSessionExpireString() {
		return String.valueOf(sessionExpire.get(Calendar.DAY_OF_MONTH)) + "."
				+ String.valueOf(sessionExpire.get(Calendar.MONTH) + "."
				+ String.valueOf(sessionExpire.get(Calendar.YEAR)));
	}

	/**
	 * @param session_expire the session_expire to set
	 */
	public void setSessionExpire(Calendar sessionExpire) {
		this.sessionExpire = sessionExpire;
	}

	/**
	 * @return the update
	 */
	public String getUpdate() {
		return update;
	}

	/**
	 * @param update the update to set
	 */
	public void setUpdate(String update) {
		this.update = update;
	}

	/**
	 * @return the add
	 */
	public String getAdd() {
		return add;
	}

	/**
	 * @param add the add to set
	 */
	public void setAdd(String add) {
		this.add = add;
	}

	/**
	 * @return the clean
	 */
	public String getClean() {
		return clean;
	}

	/**
	 * @param clean the clean to set
	 */
	public void setClean(String clean) {
		this.clean = clean;
	}

	/**
	 * @return the songs
	 */
	public int getSongs() {
		return songs;
	}

	/**
	 * @param songs the songs to set
	 */
	public void setSongs(int songs) {
		this.songs = songs;
	}

	/**
	 * @return the albums
	 */
	public int getAlbums() {
		return albums;
	}

	/**
	 * @param albums the albums to set
	 */
	public void setAlbums(int albums) {
		this.albums = albums;
	}

	/**
	 * @return the artists
	 */
	public int getArtists() {
		return artists;
	}

	/**
	 * @param artists the artists to set
	 */
	public void setArtists(int artists) {
		this.artists = artists;
	}

	/**
	 * @return the playlists
	 */
	public int getPlaylists() {
		return playlists;
	}

	/**
	 * @param playlists the playlists to set
	 */
	public void setPlaylists(int playlists) {
		this.playlists = playlists;
	}

	/**
	 * @return the videos
	 */
	public int getVideos() {
		return videos;
	}

	/**
	 * @param videos the videos to set
	 */
	public void setVideos(int videos) {
		this.videos = videos;
	}

	/**
	 * @return the catalogs
	 */
	public int getCatalogs() {
		return catalogs;
	}

	/**
	 * @param catalogs the catalogs to set
	 */
	public void setCatalogs(int catalogs) {
		this.catalogs = catalogs;
	}
}
