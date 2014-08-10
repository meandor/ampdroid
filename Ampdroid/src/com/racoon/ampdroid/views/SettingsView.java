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
