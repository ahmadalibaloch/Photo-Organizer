package com.baloch.photoorganizer;

import models.User;

import com.baloch.photoorganizer.Register.SaveTask;

import providers.UsersProvider;
import utils.UIUtilities;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class Activity_Login extends Activity {
	Button buttonLogin;
	Button buttonRegister;

	EditText name, password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button buttonLogin = (Button) findViewById(R.id.btnLogin);
		Button buttonRegister = (Button) findViewById(R.id.btnRegister);

		name = (EditText) findViewById(R.id.txt_email);
		password = (EditText) findViewById(R.id.txt_password);

		buttonLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (name.getText().toString() != null
						&& name.getText().toString().length() > 3)
					if (password.getText().toString() != null
							&& password.getText().toString().length() > 3) {
						User user = new User(0, "", name.getText().toString(),
								password.getText().toString());
						new LoadUserTask().execute(user);
					} else
						UIUtilities.message(getApplicationContext(),
								"enter password, more than 3 characters");
				else
					UIUtilities.message(getApplicationContext(),
							"enter username, more than 3 characters");

			}
		});
		buttonRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent register = new Intent(Activity_Login.this,
						Register.class);
				startActivity(register);
			}
		});
	}


	class LoadUserTask extends AsyncTask<User, Void, Boolean> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(User... params) {
			String query = "username = '" + params[0].username
					+ "' AND password = '" + params[0].passwrod + "'";
			if (UsersProvider.getOne(getApplicationContext(), query) != null)
				return true;
			else
				return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// Intent resultIntent = new Intent(Activity_Login.this,
			// Activity_MainOption.class);
			// startActivity(resultIntent);
			// UIUtilities.message(getApplicationContext(), "Welcome !");
			// finish();
			if (result) {
				Intent resultIntent = new Intent(Activity_Login.this,
						Activity_MainOption.class);
				startActivity(resultIntent);
				UIUtilities.message(getApplicationContext(), "Welcome !");
				finish();
			} else
				UIUtilities
						.message(getApplicationContext(),
								"Wrong username and or password, try again or register");
		}

	}
}
