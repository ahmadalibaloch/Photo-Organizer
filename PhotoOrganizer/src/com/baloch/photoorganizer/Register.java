package com.baloch.photoorganizer;

import java.text.ParseException;
import java.util.ArrayList;

import providers.PhotosProvider;
import providers.UsersProvider;
import utils.UIUtilities;

import com.baloch.photoorganizer.PhotoSearch.ImageAdapter;

import models.Photo;
import models.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Register extends Activity {
	Button btnRegister, btnCancel;
	EditText name, username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		btnRegister = (Button) findViewById(R.id.btn_register_register);
		btnCancel = (Button) findViewById(R.id.btn_register_cancel);

		name = (EditText) findViewById(R.id.txt_register_name);
		password = (EditText) findViewById(R.id.txt_register_password);
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (name.getText().toString().length() > 3)
					if (password.getText().toString().length() > 3) {
						User user = new User(0, "", name.getText().toString(),
								password.getText().toString());
						new SaveTask().execute(user);
					} else
						UIUtilities.message(getApplicationContext(),
								"enter password, more than 3 characters");
				else
					UIUtilities.message(getApplicationContext(),
							"enter username, more than 3 characters");

			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class SaveTask extends AsyncTask<User, Void, Boolean> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(User... params) {
			if (UsersProvider.save(getApplicationContext(), params[0]) != -1)
				return true;
			else
				return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent resultIntent = new Intent(Register.this,
						Activity_MainOption.class);
				startActivity(resultIntent);
				UIUtilities.message(getApplicationContext(), "Welcome !");
				finish();
			} else
				UIUtilities.message(getApplicationContext(),
						"error occured, try later or go back and sign in");
		}

	}
}
