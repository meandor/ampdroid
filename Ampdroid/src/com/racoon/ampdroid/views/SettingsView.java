/**
 * 
 */
package com.racoon.ampdroid.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		controller = Controller.getInstance();
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.settings, null);
		final EditText editServer = (EditText) root.findViewById(R.id.input_server);
		final EditText editUser = (EditText) root.findViewById(R.id.input_user);
		final EditText editPassword = (EditText) root.findViewById(R.id.input_password);
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
		}
		return root;
	}
}
