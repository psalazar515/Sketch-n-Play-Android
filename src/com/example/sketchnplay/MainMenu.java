package com.example.sketchnplay;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainMenu extends Activity{
	
	private String bgcolour = "White";
	private int fps = 30;
	private Bundle extras;
	
	private ToggleButton volume_button;
	boolean _gamevolume = true; //decides whether volume should be turned off onPause
	boolean _volumeon = false;
	MediaPlayer bgm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Menu: onCreate");
		setContentView(R.layout.main);
		
		bgm = MediaPlayer.create(this, R.raw.music);
		bgm.setLooping(true);
		if (!_volumeon){
			bgm.start();
			_volumeon = true;
		}
		volume_button = (ToggleButton) findViewById(R.id.volume_button);
		volume_button.setText(null);
		volume_button.setTextOff(null);
		volume_button.setTextOn(null);
		volume_button.setSelected(true);
		volume_button.setOnClickListener(new OnClickListener(){
			 public void onClick(View v){
				 switchvolume(this);
		     }
		 });
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		System.out.println("Menu: onNewIntent");
		extras = intent.getExtras();
		if (extras != null) {
		    fps = extras.getInt("EXTRA_FPS");
		    bgcolour = extras.getString("EXTRA_BGC");
		    //Toast.makeText(getApplicationContext(), "Received " + fps + " " + bgcolour, Toast.LENGTH_SHORT).show();
		}
		super.onNewIntent(intent);
	}

	//Starts game
	public void start_animation (View view) {
		Intent intent = new Intent(MainMenu.this, Main.class);
		intent.putExtra("EXTRA_FPS", fps);
		intent.putExtra("EXTRA_BGC", bgcolour);
        startActivity(intent);
	}
	
	//Settings Page
	public void settings_page (View view){
		Intent intent = new Intent(MainMenu.this, Settings.class);
		startActivity(intent);
	}
	
	

	// Turns volume on and off
	public void switchvolume (OnClickListener onClickListener) {
		if (_volumeon){
			bgm.pause();
			_volumeon = false;
		}
		else {
			bgm.start();
			_volumeon = true;
		}
	}

	// Quit application
	public void quit (View view) {
		bgm.stop();
		bgm.release();
		finish();
		System.exit(0);
	}
}
