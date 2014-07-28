package com.racoon.ampdroid;

import java.io.IOException;

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
	private int titleColumn;
	private int artistColumn;
	private int idColumn;
	private Cursor cursor;
	private long idCurrentSong;
	private NotificationManager notifManager;
	public static final int NOTIFICATION_ID = 1556;

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(new SongComplitionListener());
		ContentResolver contentResolver = getContentResolver();
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

		cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor == null) {

		} else if (!cursor.moveToFirst()) {

		} else {
			titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			setNotifiction();
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getStringExtra("ACTION");
		if (action != null) {
			if (action.equals("play") && !mediaPlayer.isPlaying()) {
				idCurrentSong = cursor.getLong(idColumn);
				play(idCurrentSong);

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
		if (!cursor.moveToNext()) {
			cursor.moveToFirst();
		}

		idCurrentSong = cursor.getLong(idColumn);
		;
		stop();
		play(idCurrentSong);
	}

	private void previous() {
		if (!cursor.moveToPrevious()) {
			cursor.moveToLast();
		}
		idCurrentSong = cursor.getLong(idColumn);
		stop();
		play(idCurrentSong);
	}

	private void play(long id) {

		idCurrentSong = id;
		Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
		try {
			mediaPlayer.setDataSource(getApplicationContext(), contentUri);
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
		if (cursor != null && cursor.getColumnCount() > 0) {
			result = cursor.getString(titleColumn);
		}
		return result;
	}

	public String getArtist() {
		String result = "";
		if (cursor != null && cursor.getColumnCount() > 0) {
			result = cursor.getString(artistColumn);
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
