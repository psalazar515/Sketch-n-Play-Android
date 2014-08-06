package com.example.sketchnplay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import java.util.ArrayList;


public class Stroke extends Object {
	public ArrayList<PointF> points;
	public ArrayList<double[]> timeStamp;
	public int color;
	public double createtime;
	public double deletetime;
	public double xoffset, yoffset;

	public Stroke(){	
		this.points = new ArrayList<PointF>();
		this.timeStamp = new ArrayList<double[]>(); 
		this.createtime = -1;
		this.deletetime = -1;
	}
	
	public void drawStroke(Canvas canvas, double time){
		if (deletetime >= 0 && time >= deletetime || time < createtime) return;
		if (this.points.size() > 1){	
			setOffset(time);
			Paint p = new Paint();
			p.setColor(this.color);
			p.setStyle(Paint.Style.STROKE);
		    p.setStrokeWidth(5);
		    p.setColor(this.color);
			//Connect and Draw the points
			for (int j = 0; j < this.points.size() - 2; j++){
				PointF p1 =this.points.get(j);
				PointF p2 = this.points.get(j + 1);
				canvas.drawLine(p1.x + (int)this.xoffset , p1.y + (int)this.yoffset, p2.x + (int)this.xoffset, p2.y + (int)this.yoffset, p);
				//System.out.println(p1.x + (int)this.xoffset  + " " +  p1.y + (int)this.yoffset + " " +  
									//p2.x + (int)this.xoffset + " " + p2.y + (int)this.yoffset);
			}
		}
	}
	//set the stroke's offset
	private void setOffset(double time){
		for(double[] el : this.timeStamp){
			if (el[0] - time == 0 || Math.abs(el[0] - time) == 1){
				this.xoffset = el[1];
				this.yoffset = el[2];
				break;
			}
		}
	}
	
	public void setColor(String c){
		if(c.equals("red"))	this.color = Color.RED;
		else if(c.equals("blue")) this.color = Color.BLUE;
		else if(c.equals("green")) this.color = Color.GREEN;
		else if(c.equals("orange")) this.color = Color.rgb(255, 165, 0);
		else if(c.equals("yellow")) this.color = Color.YELLOW;
		else this.color = Color.BLACK;
	}
}

