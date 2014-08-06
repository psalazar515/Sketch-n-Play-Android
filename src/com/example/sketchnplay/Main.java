package com.example.sketchnplay;

//import java.util.ArrayList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import com.example.sketchnplay.Stroke;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;



public class Main extends Activity{
	
	mySurface sView;
	private static ArrayList<Stroke> strokes = new ArrayList<Stroke>();
	private double endTime;
	private int time = 0;
	private double fps = 30;
	private Bundle extras;
	private String bgcolour;
	private int BGC;//background colour
	private boolean _playing = true;
	//private static ArrayList<Stroke> strokes = new ArrayList<Stroke>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FrameLayout AnimationView = new FrameLayout(this);
		LinearLayout ButtonsView = new LinearLayout(this);
		
		Button PlayButton = new Button(this);
		Button StopButton = new Button(this);
		Button RewindButton = new Button(this);
		Button QuitButton = new Button (this);
		
		ButtonsView.addView(PlayButton);
		ButtonsView.addView(StopButton);
		ButtonsView.addView(RewindButton);
		ButtonsView.addView(QuitButton);
		
		sView = new mySurface(this);
		
		AnimationView.addView(sView);
		AnimationView.addView(ButtonsView);
		setContentView(AnimationView);
		
		extras = getIntent().getExtras();
		if (extras != null) {
		    fps = extras.getInt("EXTRA_FPS");
		    bgcolour = extras.getString("EXTRA_BGC");
		    Toast.makeText(getApplicationContext(), "Received " + fps + " " + bgcolour, Toast.LENGTH_SHORT).show();
		}
		getColour(bgcolour);
	}
	 @Override
	 protected void onResume() {
	  super.onResume();
	  sView.resume();
	 }
	 
	 @Override
	 protected void onPause() {
	  super.onPause();
	  sView.pause();
	 }
	 
	 
	 public void readXML(View view){
		try {
			strokes = new ArrayList<Stroke>();//reset the strokes
			
			AssetManager am = this.getAssets();
			InputStream is = am.open("file.xml");
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			doc.getDocumentElement().normalize();
			
			endTime = Integer.parseInt(doc.getDocumentElement().getAttribute("endTime")) * 1000;
			//System.out.println("Root element " + doc.getDocumentElement().getNodeName());	
			
			/*****INITIATE THE STROKES*****/
			NodeList strokeLst = doc.getElementsByTagName("Stroke");
			//System.out.println(strokeLst.getLength() + " Strokes");
			String temp;
			for (int s = 0; s < strokeLst.getLength(); s++) {
				
				Node strokeNode = strokeLst.item(s);
				
				if (strokeNode.getNodeType() == Node.ELEMENT_NODE) {
					/****INITIALIZE MAIN ATTRIBUTES ****/
					strokes.add(new Stroke());
					Element eElement = (Element) strokeNode;				
					temp = eElement.getAttribute("Colour");
					//System.out.println("Getting " + temp);
					strokes.get(s).setColor(temp);
					strokes.get(s).createtime = (int)(1000 * Double.parseDouble(eElement.getAttribute("CreateTime")));
					strokes.get(s).deletetime = (int)(1000 * Double.parseDouble(eElement.getAttribute("DeleteTime")));
					//System.out.println(temp + " " + strokes.get(s).createtime + " " + strokes.get(s).deletetime);
					
					/***INITIALIZE Points****/				
					NodeList points = eElement.getElementsByTagName("Point");
					//System.out.println("Number of Points: " + points.getLength());

					for (int i = 0; i < points.getLength(); i++){
						
						Node point = points.item(i);
						
						if (point.getNodeType() == Node.ELEMENT_NODE){
							
							/*INITIALIZE POINTS*/
							Element pointElement = (Element) point;
							PointF p = new PointF();
							p.x = (float) Double.parseDouble(pointElement.getAttribute("x"));
							p.y = (float) Double.parseDouble(pointElement.getAttribute("y"));
							strokes.get(s).points.add(p);
							//System.out.println("Point: " + p.x + " " + p.y);
							
						}
					}
					/***INITIALIZE Stamps****/
					NodeList stamps = eElement.getElementsByTagName("Stamp");
					//System.out.println("Number of Stamps: " + stamps.getLength());
					double last_time_stamp = -1;
					
					for (int i = 0; i < stamps.getLength(); i++){
						
						Node stamp = stamps.item(i);
						
						if (stamp.getNodeType() == Node.ELEMENT_NODE){
							
							/*INITIALIZE STAMPS*/
							Element stampElement = (Element) stamp;
							int s_time = (int)(1000 * Double.parseDouble(stampElement.getAttribute("time")));
							//Check for repeated time stamps:
							if (last_time_stamp == s_time) continue;
							else last_time_stamp = s_time;
							
							//make all time stamps multiples of 5
							if (s_time % 10 == 4 || s_time % 10 == 9) s_time++;
							
							double s_x = Double.parseDouble(stampElement.getAttribute("x"));
							double s_y = Double.parseDouble(stampElement.getAttribute("y"));
							double [] s_stamp = {s_time, s_x, s_y};
							strokes.get(s).timeStamp.add(s_stamp);
							System.out.println("Stamp: " + s_time + " " + s_x + " " + s_y);
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void paintStrokes(Canvas c){
		if (strokes.size() > 0){
			//draw every stroke in the list
			//System.out.println(strokes.size());
			for (int i = 0; i < strokes.size(); i++){
				strokes.get(i).drawStroke(c, time);
			}
		}
	}
	
	private void getColour(String c){
		if(c.equals("White"))BGC = Color.WHITE;
		else if(c.equals("Black")) BGC = Color.BLACK;
		else if(c.equals("Red")) BGC = Color.RED;
		else if(c.equals("Orange")) BGC = Color.rgb(255, 165, 0);
		else if(c.equals("Yellow")) BGC = Color.YELLOW;
		else if(c.equals("Blue")) BGC = Color.BLUE;
		else if(c.equals("Green")) BGC = Color.GREEN;
		else if(c.equals("Magenta")) BGC = Color.MAGENTA;
		else if(c.equals("Cyan")) BGC = Color.CYAN;
		else BGC = Color.GRAY;
	}
	 
	
	public class mySurface extends SurfaceView implements Runnable{
		
		double lastdraw = 0;
		
		Thread thread = null;
	    SurfaceHolder surfaceHolder;
	    volatile boolean running = true;
	    Random random;
	    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		public mySurface(Context context) {
			super(context);
			surfaceHolder = getHolder();
			random = new Random();
			readXML(this);
		}
		
		public void pause(){
			running = false;
			while(true){
				try {
					thread.join();	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			thread = null;
		}
		public void resume(){
			running = true;
			thread = new Thread(this);
			thread.start();
		}

		public void run() {
			while(time <= endTime && running){
				if(!surfaceHolder.getSurface().isValid()) continue;	    
				    if (System.currentTimeMillis() - lastdraw >= 1000/fps && _playing) {
				    	Canvas canvas = surfaceHolder.lockCanvas();
				    	paint.setColor(BGC);
				    	canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
				    	paintStrokes(canvas);
				    	time += 25;
				    	lastdraw = System.currentTimeMillis();
				    	surfaceHolder.unlockCanvasAndPost(canvas);
				    }
			}
			finish();
		}
	}
		
}
	
