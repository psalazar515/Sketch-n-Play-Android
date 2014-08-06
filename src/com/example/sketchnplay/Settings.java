package com.example.sketchnplay;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Settings extends Activity {

	private Spinner spinner;
	private EditText mEdit;
	private String selection;
	private int fps = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        Spinner spinner = (Spinner) findViewById(R.id.background_colour);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bg_colours, R.layout.spinner_textview);
        adapter.setDropDownViewResource(R.layout.spinner_textview);
        spinner.setAdapter(adapter);
        
        Toast.makeText(getApplicationContext(), 
                "Select the background colour and the frames per second!", Toast.LENGTH_LONG).show();
        
        addSpinnerListener();
        mEdit   = (EditText)findViewById(R.id.fps_text);
    }
    
    
	public void addSpinnerListener() {
		spinner = (Spinner) findViewById(R.id.background_colour);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
	    {
	        public void onItemSelected(AdapterView<?> arg0, View v, int position, long id){
	        	selection = arg0.getItemAtPosition(position).toString();
	        	//Toast.makeText(arg0.getContext(), selection, Toast.LENGTH_SHORT).show();
	        }

	        public void onNothingSelected(AdapterView<?> arg0){}
	    });
	}
	 
	public void submit(View view){
		String temp = mEdit.getText().toString();
		if (temp.equals("")){
			fps = 30;
			Toast.makeText(getApplicationContext(), "Using 30fps as default", Toast.LENGTH_SHORT).show();
		}
		else fps = Integer.parseInt(temp);
		//System.out.println("FPS = " + fps);
		finish();
		Intent intent = new Intent(Settings.this, MainMenu.class);
		intent.putExtra("EXTRA_FPS", fps);
		intent.putExtra("EXTRA_BGC", selection);
		//intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
}
