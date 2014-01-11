package backGround;

import graphicLibrary.ImagesLoader;

import java.awt.Graphics;



public class BackGroundManager {

	private String bgImages[] = {"default"};//{"Avignon", "infection", "euh"};
	private double moveFactors[] = {1.0};//{0.1, 0.5, 1.0};
	
	private BackGround[] backgrounds;
	private int numBackGrounds;
	private int moveSize;
	//private ImagesLoader imsLd; // provisoire
	
	public BackGroundManager(int w, int h, int brickMoveSize, ImagesLoader imsLd) {
		moveSize = brickMoveSize;
		numBackGrounds = bgImages.length;
		backgrounds = new BackGround[numBackGrounds];
		for(int i=0; i<numBackGrounds; i++) {
			backgrounds[i] = new BackGround(w, h, imsLd.getImage(bgImages[i]), (int)(moveFactors[i]*moveSize));
		}
		//this.imsLd = imsLd; // provisoire
	}
	
	public void moveLeft() {
		for(BackGround b : backgrounds) {
			b.moveLeft();
		}
	}
	
	public void moveRight() {
		for(BackGround b : backgrounds) {
			b.moveRight();
		}
	}
	
	public void stayStill() {
		for(BackGround b : backgrounds) {
			b.stayStill();
		}
	}
	
	public void update() {
		for(BackGround b : backgrounds) {
			b.update();
		}
	}
	
	public void display(Graphics g) {
		//g.drawImage(imsLd.getImage("Avignon"), 0, 0, imsLd.getImage("Avignon").getWidth(), imsLd.getImage("Avignon").getHeight(), null); // provisoire
		for(BackGround b : backgrounds) {
			b.display(g);
		}
	}
	
}
