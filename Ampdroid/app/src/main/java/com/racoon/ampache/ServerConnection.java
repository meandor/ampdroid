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
	 * @return the session_expire String in German locale
	 */
	public String getSessionExpireString() {
		return String.format("%02d", sessionExpire.get(Calendar.DAY_OF_MONTH)) + "."
				+ String.format("%02d", sessionExpire.get(Calendar.MONTH)) + "."
				+ String.valueOf(sessionExpire.get(Calendar.YEAR)) + " - "
				+ String.format("%02d", sessionExpire.get(Calendar.HOUR_OF_DAY)) + ":"
				+ String.format("%02d", sessionExpire.get(Calendar.MINUTE)) + ":"
				+ String.format("%02d", sessionExpire.get(Calendar.SECOND));
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
