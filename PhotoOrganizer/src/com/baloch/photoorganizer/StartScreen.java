package com.baloch.photoorganizer;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;

public class StartScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new Handler().postDelayed(new Thread() {
			@Override
			public void run() {
				Intent login_menu = new Intent(StartScreen.this, Activity_Login.class);
				StartScreen.this.startActivity(login_menu);
				StartScreen.this.finish();
				overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
			}
		}, 4000);
	}

}
