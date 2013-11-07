/**
 * 
 */
package com.racoon.ampache;

/**
 * @author Daniel Schruhl
 * 
 */
public class Artist {

	private int id;
	private String name;
	private int albums;
	private int songs;

	/**
	 * 
	 */
	public Artist() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
