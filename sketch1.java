package motionMonsterToxi;
import java.util.ArrayList;

import processing.core.*;
import processing.opengl.*;
import toxi.sim.grayscott.*;
import toxi.math.*;
import toxi.color.*;
import controlP5.*;


public class sketch1 extends PApplet{

	GrayScott gs;
	ToneMap toneMap;
	PImage img;

	ControlP5 cp5;
	Slider fSlider;
	Slider kSlider;
	Slider2D s;
	Slider2D s1;


	float maskF;
	float maskK;
	float bgF;
	float bgK;
	int index;
	ArrayList<PImage> imgs;
	boolean showImage;
	public void setup(){

		size(512,384);
		showImage = false;
		imgs = new ArrayList<PImage>();
		index =0;
		for(int i=0; i< 13; i++){
			imgs.add(loadImage(i+".png"));	
			
		}
		
		for(int i=0; i< 13; i++){
			imgs.get(i).resize(512,384);	
			
		}
		
		cp5 = new ControlP5(this);
		s = cp5.addSlider2D("mask")
				.setPosition(00,0)
				.setSize(50,50)
				.setArrayValue(new float[] {50, 50})
				;
		s1 = cp5.addSlider2D("bg")
				.setPosition(00,60)
				.setSize(50,50)
				.setArrayValue(new float[] {50, 50})
				;
		

		smooth();
		
		gs=new PatternedGrayScott(512,384,false);
		gs.setCoefficients(0.020f, 0.077f, 0.16f, 0.08f);
		// create a color gradient for 256 values
		ColorGradient grad=new ColorGradient();
		// NamedColors are preset colors, but any TColor can be added
		// see javadocs for list of names:
		// http://toxiclibs.org/docs/colorutils/toxi/color/NamedColor.html
		grad.addColorAt(0,NamedColor.WHITE);
		grad.addColorAt(16,NamedColor.CORNSILK);
		grad.addColorAt(128,NamedColor.PINK);
		grad.addColorAt(192,NamedColor.PURPLE);
		grad.addColorAt(255,NamedColor.BLACK);
		// this gradient is used to map simulation values to colors
		// the first 2 parameters define the min/max values of the
		// input range (Gray-Scott produces values in the interval of 0.0 - 0.5)
		// setting the max = 0.33 increases the contrast
		toneMap=new ToneMap(0,0.33f,grad);
		img=loadImage("reactDiffMask3.png");
		// create a duo-tone gradient map with 256 steps
		img.resize(width, height);
		System.out.println(dataPath(""));

		// this gradient is used to map simulation values to colors
		// the first 2 parameters define the min/max values of the
		// input range (Gray-Scott produces values in the interval of 0.0 - 0.5)
		// setting the max = 0.33 increases the contrast
		//		  toneMap=new ToneMap(0f,0.33f,grad);
	}

	public void draw() {

		//		  gs.seedImage(img.pixels,img.width,img.height);
		if (mousePressed) {
			gs.setRect(mouseX, mouseY,20,20);
		
		}
		loadPixels();
		for(int i=0; i<10; i++) gs.update(1f);
		// read out the V result array
		// and use tone map to render colours
		for(int i=0; i<gs.v.length; i++) {
			pixels[i]=toneMap.getARGBToneFor(gs.v[i]);
		}
		updatePixels();
		if(showImage){
			image(imgs.get(index),0,0);	
		}
		

	}

	public void keyPressed() {
		if (key>='1' && key<='9') {
			gs.setF((float) (0.02+(key-'1')*0.001));
		} 
		else if(key == 'r') {
			gs.reset();
		}
		else if(key == ' '){	
			maskF = norm(s.getArrayValue()[0],0,50);
			maskK = norm(s.getArrayValue()[1],0,50);
			maskF /= 100;
			maskK /= 100;
			 
			
			 bgF = norm(s1.getArrayValue()[0],0,50);
			bgK = norm(s1.getArrayValue()[1],0,50);
			bgF /= 100;
			bgK /= 100;
			
//			float newF = norm(fSlider.getValue(),0,1000);
//			float newK =norm( kSlider.getValue(),0,1000);
//			newF /= 100;
//			newK /= 100;
			System.out.println(bgK + " " + bgF );
//			gs.setF(newF);
//			gs.setK(newK);
			
			
			
		}
		else if(key =='g'){
			if(cp5.isVisible()){
				cp5.hide();
			}
			else{
				cp5.show();
			}
		}
		else if(key == 's'){
			saveFrame();
		}
		else if(key =='='){
			
			++index;
			index = (index < imgs.size())?index:0;
		}
		else if(key == '-'){
			--index;
			index = (index > 0)?index:0;
			
		}
		else if(key == 't')
		{
			showImage = !showImage;
		}
	}

	class PatternedGrayScott extends GrayScott {
		public PatternedGrayScott(int w, int h, boolean tiling) {
			super(w,h,tiling);
		}

		public float getFCoeffAt(int x, int y) {
			//x/=32;

			imgs.get(index).loadPixels();
			int loc = x + y*imgs.get(index).width;
			float r = red(imgs.get(index).pixels[loc]);
			float b = blue(imgs.get(index).pixels[loc]);
			float g = green(imgs.get(index).pixels[loc]);
			
			if(r<10 &&b<10 &&g<10){
				return f +bgF ;
			}
			else{
				return f + maskF;
			}

		}

		public float getKCoeffAt(int x, int y) {
			imgs.get(index).loadPixels();
			int loc = x + y*imgs.get(index).width;
			float r = red   (imgs.get(index).pixels[loc]);
			float b = blue(imgs.get(index).pixels[loc]);
			float g = green(imgs.get(index).pixels[loc]);
			
			if(r<10 && b<10 &&g<10){
				return k + bgK;
			}
			else{
				return k + maskK ;
				
				
				//		    	return k = 0.000594f;
			}

		} 
	}
}
