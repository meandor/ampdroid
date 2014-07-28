package com.racoon.ampdroid;

import java.io.IOException;
import java.util.ArrayList;

import com.racoon.ampache.Song;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class Mp3PlayerService extends Service {
	private MediaPlayer mediaPlayer;
	private ArrayList<Song> playList;
	private int cursor;
	private Song currentSong;
	private NotificationManager notifManager;
	public static final int NOTIFICATION_ID = 1556;

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(new SongComplitionListener());

	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getStringExtra("ACTION");
		playList = (ArrayList<Song>) intent.getSerializableExtra("com.racoon.ampdroid.NowPlaying");
		if (action != null && playList != null) {
			if (action.equals("play") && !mediaPlayer.isPlaying()) {
				cursor = intent.getIntExtra("CURSOR", 0);
				play(cursor);
			} else if (action.equals("pause")) {
				pause();
			} else if (action.equals("next")) {
				next();
			} else if (action.equals("previous")) {
				previous();
			}

		} else {
			// TODO error handling
		}

		return 0;
	}

	private void pause() {
		mediaPlayer.stop();
		setNotifiction();
	}

	private void stop() {
		pause();
		mediaPlayer.reset();
	}

	private void next() {
		
	}

	private void previous() {
		
	}

	private void play(int id) {
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(playList.get(id).getUrl());
			currentSong = playList.get(id);
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
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mediaPlayer.start();
		setNotifiction();
	}

	private class SongComplitionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			next();
		}
	}

	public String getCurrentTitle() {
		String result = "";
		if (currentSong != null) {
			result = currentSong.getTitle();
		}
		return result;
	}

	public String getArtist() {
		String result = "";
		if (currentSong != null) {
			result = currentSong.getArtist();
		}
		return result;
	}

	public boolean isPlaying() {
		boolean result = false;
		if (mediaPlayer != null) {
			result = mediaPlayer.isPlaying();
		}
		return result;
	}

	/**
	 * Binding
	 */
	private final IBinder binder = new Mp3Binder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class Mp3Binder extends Binder {
		Mp3PlayerService getService() {
			return Mp3PlayerService.this;
		}
	}

	private void setNotifiction() {
		/* 1. Setup Notification Builder */
		Notification.Builder builder = new Notification.Builder(this);

		/* 2. Configure Notification Alarm */
		builder.setSmallIcon(R.drawable.play).setWhen(System.currentTimeMillis()).setTicker(getCurrentTitle());

		/* 3. Configure Drop-down Action */
		builder.setContentTitle(getCurrentTitle()).setContentText(getArtist())
				.setContentInfo(isPlaying() ? "Playing" : "Stopped");
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent notifIntent = PendingIntent.getActivity(this, 0, intent, 0);
		builder.setContentIntent(notifIntent);

		/* 4. Create Notification and use Manager to launch it */
		Notification notification = builder.build();
		String ns = Context.NOTIFICATION_SERVICE;
		notifManager = (NotificationManager) getSystemService(ns);
		notifManager.notify(NOTIFICATION_ID, notification);
	}

}
