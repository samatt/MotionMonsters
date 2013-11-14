package motionMonsterToxi;

import java.util.ArrayList;

import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.volume.*;
import toxi.math.noise.*;
import toxi.processing.*;
import processing.core.*;
import processing.opengl.*;

public class Sketch2 extends PApplet {

	int DIMX=192;
	int DIMY=32;
	int DIMZ=64;

	float ISO_THRESHOLD = 0.1f;
	float NS=0.03f;
	Vec3D SCALE=new Vec3D(1f,1f,1f).scaleSelf(400);

	IsoSurface surface;
	TriangleMesh mesh;

	boolean isWireframe=false;
	float currScale=1;

	ToxiclibsSupport gfx;
	
	ArrayList<PImage> imgs;

	public void setup(){
		size(1024,768,OPENGL);
		//  hint(ENABLE_OPENGL_4X_SMOOTH);
		imgs = new ArrayList<PImage>();
		
		for(int i=0; i< 3; i++){
			imgs.add(loadImage(i+".tif"));	
		}
		
		//img=loadImage("screen-6607.tif");
		//		img.resize(500, 500);
		gfx=new ToxiclibsSupport(this);
		strokeWeight((float) 0.5);
		/*
	  VolumetricSpace volume=new VolumetricSpaceArray(SCALE,DIMX,DIMY,DIMZ);
	  // fill volume with noise
	  for(int z=0; z<DIMZ; z++) {
	    for(int y=0; y<DIMY; y++) {
	      for(int x=0; x<DIMX; x++) {

	        volume.setVoxelAt(x,y,z,(float) ((float)SimplexNoise.noise(x*NS,y*NS,z*NS)*0.5));
	      } 
	    } 
	  }
	  volume.closeSides();
		 */
		//	  long t0=System.nanoTime();
		//	  // store in IsoSurface and compute surface mesh for the given threshold value
		//	  mesh=new TriangleMesh("iso");
		//	  surface=new HashIsoSurface(volume,0.333333f);
		//	  surface.computeSurfaceMesh(mesh,ISO_THRESHOLD);
		//	  float timeTaken=(float) ((System.nanoTime()-t0)*1e-6);
		//	  println(timeTaken+"ms to compute "+mesh.getNumFaces()+" faces");

		loadImageData();
	}

	public void loadImageData(){
		VolumetricSpace volume=new VolumetricSpaceArray(SCALE,1000,1000,10);
		

//		System.out.println(img.width+ " , "+ img.height);
		for(int k=0;k< imgs.size(); k++){
			imgs.get(k).loadPixels();
			for(int j=0; j<imgs.get(k).height; j++){
			for(int i =0; i<imgs.get(k).width; i++){

				int loc = i+ j*imgs.get(k).width;

				float r = red(imgs.get(k).pixels[loc]);
				float g = green(imgs.get(k).pixels[loc]);
				float b = blue(imgs.get(k).pixels[loc]);

				if(r <250 && g<250 && b< 250){
					volume.setVoxelAt(	i,j,k,random(0.2f,0.5f));
				}	
			}
		}
	}

		volume.closeSides();
		long t0=System.nanoTime();
		// store in IsoSurface and compute surface mesh for the given threshold value
		mesh=new TriangleMesh("iso");
		surface=new HashIsoSurface(volume,0.333333f);
		surface.computeSurfaceMesh(mesh,ISO_THRESHOLD);
		float timeTaken=(float) ((System.nanoTime()-t0)*1e-6);
		println(timeTaken+"ms to compute "+mesh.getNumFaces()+" faces");
	}

	public void draw(){
		background(128);
		translate(width/3,height/2,0);
		rotateX(mouseY*0.01f);
		rotateY(mouseX*0.01f);
		scale(currScale);
		ambientLight(48,48,48);
		lightSpecular(230,230,230);
		directionalLight(255,255,255,0,-0.5f,-1f);
		specular(255,255,255);
		shininess(12.0f);
		beginShape(TRIANGLES);
		if (isWireframe) {
			stroke(255);
			noFill();
		} 
		else {
			noStroke();
			fill(255);
		}
		gfx.mesh(mesh);		
	}
	public void mousePressed() {
		  isWireframe=!isWireframe;
		}
	public void keyPressed() {
	  if(key=='-') currScale=max(currScale-0.1f,0.5f);
	  if(key=='=') currScale=min(currScale+0.1f,10f);
	  if (key=='s') {
	    // save mesh as STL or OBJ file
	    mesh.saveAsSTL(sketchPath("noise.stl"));
	  }
	}
}


