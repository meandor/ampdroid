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

/**
 * @author Daniel Schruhl
 * 
 */
public class Song implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String artist;
	private int artistId;
	private String album;
	private int albumId;
	private int trackNumber;
	private int time;
	private int year;
	private int bitrate;
	private String mode;
	private String mime;
	private String url;
	private int size;
	private String art;

	/**
	 * 
	 */
	public Song() {
		// TODO Auto-generated constructor stub
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

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the artistId
	 */
	public int getArtistId() {
		return artistId;
	}

	/**
	 * @param artistId the artistId to set
	 */
	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * @return the albumId
	 */
	public int getAlbumId() {
		return albumId;
	}

	/**
	 * @param albumId the albumId to set
	 */
	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	/**
	 * @return the trackNumber
	 */
	public int getTrackNumber() {
		return trackNumber;
	}

	/**
	 * @param trackNumber the trackNumber to set
	 */
	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the bitrate
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * @param bitrate the bitrate to set
	 */
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the mime
	 */
	public String getMime() {
		return mime;
	}

	/**
	 * @param mime the mime to set
	 */
	public void setMime(String mime) {
		this.mime = mime;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the art
	 */
	public String getArt() {
		return art;
	}

	/**
	 * @param art the art to set
	 */
	public void setArt(String art) {
		this.art = art;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return title;
	}

}
