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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.util.Log;

import com.racoon.ampache.CachedData;
import com.racoon.ampache.ServerConnection;

/**
 * @author Daniel Schruhl
 * 
 */
public class ServerConnector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String user;
	private String password;
	private String host;
	private String authKey;
	private ServerConnection ampacheConnection;
	private CachedData cachedData;

	public ServerConnector() {
		super();
	}

	public ServerConnector(String user, String password, String host) {
		super();
		this.user = user;
		this.password = password;
		this.host = host;
	}

	public boolean isConnected(boolean network) {
		if (!network) {
			return false;
		}
		String time = Long.toString(System.currentTimeMillis() / 1000);
		String key = password;
		String passphrase = generateShaHash(time + key);

		String urlString = new String(this.host + "/server/xml.server.php?action=handshake&auth=" + passphrase
				+ "&timestamp=" + time + "&version=370001&user=" + user);
		Log.d("passwort:", key);
		Log.d("passphrase:", passphrase);
		Log.d("url:", urlString);
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

				parseXML(parser);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (this.ampacheConnection != null) {
				Log.d("ampache connection:", this.ampacheConnection.getAuth());
				Calendar today = Calendar.getInstance();
				Calendar expire = this.ampacheConnection.getSessionExpire();
				Log.d("expire:", this.ampacheConnection.getSessionExpire().toString());
				Log.d("token:", this.authKey);
				Log.d("dates:",
						expire.toString() + ", " + today.toString() + " - " + String.valueOf(expire.compareTo(today)));
				if (expire.compareTo(today) <= 0) {
					extendSession();
				}
				
				/** start caching file**/
				if (this.cachedData == null) {
					this.cachedData = new CachedData();
				}
				
				return true;
			}
			return false;
		} catch (Exception e) {
			Log.d("error", "keine Verbindung möglich");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the server
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param server the server to set
	 */
	public void setHost(String server) {
		this.host = server;
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

	public void extendSession() {
		String urlString = new String(host + "/server/xml.server.php?action=ping&auth=" + authKey);
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;

			System.out.println("Response: " + con.getResponseCode());
			System.out.println("Content-type: " + con.getContentType());
			System.out.println("Content-length: " + con.getContentLength());

			while ((line = br.readLine()) != null)
				Log.d("ping:", line);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	/**
	 * @return the authKey
	 */
	public String getAuthKey() {
		return authKey;
	}

	/**
	 * @param authKey the authKey to set
	 */
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		ServerConnection serverConnection = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equals("root")) {
					serverConnection = new ServerConnection();
				} else if (serverConnection != null) {
					if (name.equals("auth")) {
						serverConnection.setAuth(parser.nextText());
						this.authKey = serverConnection.getAuth();
					} else if (name.equals("api")) {
						serverConnection.setApi(parser.nextText());
					} else if (name.equals("session_expire")) {
						/** Date Format: yyyy-MM-ddTHH:mm:ss+01:00 **/
						String rawExpire = parser.nextText();
						Calendar expire = Calendar.getInstance();
						expire.set(Calendar.YEAR, Integer.parseInt(rawExpire.substring(0, 4)));
						expire.set(Calendar.MONTH, Integer.parseInt(rawExpire.substring(5, 7)));
						expire.set(Calendar.DAY_OF_MONTH, Integer.parseInt(rawExpire.substring(8, 10)));
						expire.set(Calendar.HOUR_OF_DAY, Integer.parseInt(rawExpire.substring(11, 13)));
						expire.set(Calendar.MINUTE, Integer.parseInt(rawExpire.substring(14, 16)));
						expire.set(Calendar.SECOND, Integer.parseInt(rawExpire.substring(17, 19)));
						serverConnection.setSessionExpire(expire);
					} else if (name.equals("update")) {
						serverConnection.setUpdate(parser.nextText());
					} else if (name.equals("add")) {
						serverConnection.setAdd(parser.nextText());
					} else if (name.equals("clean")) {
						serverConnection.setClean(parser.nextText());
					} else if (name.equals("songs")) {
						serverConnection.setSongs(Integer.parseInt(parser.nextText()));
					} else if (name.equals("albums")) {
						serverConnection.setAlbums(Integer.parseInt(parser.nextText()));
					} else if (name.equals("artists")) {
						serverConnection.setArtists(Integer.parseInt(parser.nextText()));
					} else if (name.equals("playlists")) {
						serverConnection.setPlaylists(Integer.parseInt(parser.nextText()));
					} else if (name.equals("videos")) {
						serverConnection.setVideos(Integer.parseInt(parser.nextText()));
					} else if (name.equals("catalogs")) {
						serverConnection.setCatalogs(Integer.parseInt(parser.nextText()));
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				Log.d("bug", "ampache connection nicht gesetzt");
				if (name.equalsIgnoreCase("root") && serverConnection != null) {
					setAmpacheConnection(serverConnection);
					Log.d("bug", "ampache connection gesetzt");
				}
			}
			eventType = parser.next();
		}
	}

	/**
	 * @return the ampacheConnection
	 */
	public ServerConnection getAmpacheConnection() {
		return ampacheConnection;
	}

	/**
	 * @param ampacheConnection the ampacheConnection to set
	 */
	public void setAmpacheConnection(ServerConnection ampacheConnection) {
		this.ampacheConnection = ampacheConnection;
	}

	/**
	 * @return the cachedData
	 */
	public CachedData getCachedData() {
		return cachedData;
	}

	/**
	 * @param cachedData the cachedData to set
	 */
	public void setCachedData(CachedData cachedData) {
		this.cachedData = cachedData;
	}
}
