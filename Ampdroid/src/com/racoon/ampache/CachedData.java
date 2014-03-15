/**
 * 
 */
package com.racoon.ampache;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Daniel Schruhl
 * 
 */
public class CachedData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Playlist> playlists;
	private ArrayList<Song> songs;
	private ArrayList<Artist> artists;
	private ArrayList<Album> albums;

	public CachedData() {
		super();
		this.playlists = new ArrayList<Playlist>();
		this.songs = new ArrayList<Song>();
		this.artists = new ArrayList<Artist>();
		this.albums = new ArrayList<Album>();
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

}
