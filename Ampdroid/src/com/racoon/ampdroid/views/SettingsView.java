/**
 * 
 */
package com.racoon.ampdroid.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.racoon.ampdroid.Controller;
import com.racoon.ampdroid.R;

/**
 * @author Daniel Schruhl
 * 
 */
public class SettingsView extends Fragment {
	private Controller controller;

	/**
	 * 
	 */
	public SettingsView() {
		// TODO Auto-generated constructor stub
	}

	public static Fragment newInstance(Context context) {
		SettingsView s = new SettingsView();
		return s;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.settings, null);
		final EditText editServer = (EditText) root.findViewById(R.id.settingsServer);
		final EditText editUser = (EditText) root.findViewById(R.id.settingsUser);
		final EditText editPassword = (EditText) root.findViewById(R.id.settingsPassword);
		final TextView connectionInfo = (TextView) root.findViewById(R.id.settingsConnectionInfo);
		final TextView connectionInfoText = (TextView) root.findViewById(R.id.settingsConnectionInfoText);
		if (controller.getServer() != null) {
			if (!controller.getServer().getHost().equals("")) {
				editServer.setText(controller.getServer().getHost());
			}
			if (!controller.getServer().getUser().equals("")) {
				editUser.setText(controller.getServer().getUser());
			}
			if (!controller.getServer().getPassword().equals("")) {
				editPassword.setText(controller.getServer().getPassword());
			}
			if (controller.getServer().isConnected(controller.isOnline(root.getContext()))) {
				connectionInfoText.setText(R.string.settingsExpireText);
				connectionInfo.setText(controller.getServer().getAmpacheConnection().getSessionExpireString());
			}
		}
		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu != null) {
			menu.removeItem(R.id.search);
		}
	}
}
