package backGround;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class BackGround {

	private BufferedImage im;
	private int width; // the image's width
	private int pWidth, pHeight; // panel's dimension
	
	private int moveSize; // speed of the background
	private boolean isMovingRight;
	private boolean isMovingLeft;
	
	private int xImHead; // panel position of image's left side.
	
	public BackGround(int w, int h, BufferedImage im, int moveSz) {
		pWidth = w; pHeight = h;
		this.im = im;
		width = im.getWidth();
		if(width < pWidth)
			System.out.println("Background with Panel < pWidth");
		this.moveSize = moveSz;
		isMovingRight = false; // movement
		isMovingLeft = false;
		xImHead = 0; // location
	}
	
	public void moveRight() {
		isMovingRight = true;
		isMovingLeft = false;
	}
	
	public void moveLeft() {
		isMovingRight = false;
		isMovingLeft = true;
	}
	
	public void stayStill() {
		isMovingRight = false;
		isMovingLeft = false;
	}
	
	/**
	 * Adjust the xImHead value depending on the movement flags.
	 * xImHead can be range between -width and width
	 */
	public void update() {
		if(isMovingRight) {
			xImHead = (xImHead + moveSize)%width;
		} else if (isMovingLeft) {
			xImHead = (xImHead + moveSize)%width;
		}
	}
	
	public void display(Graphics g) {
		if(xImHead == 0) // draw im start at (0,0)
			draw(g, im, 0, pWidth, 0, pWidth);
		else if (xImHead > 0 && xImHead < pWidth) {
			draw(g, im, 0, xImHead, width-xImHead, width);
			draw(g, im, xImHead, pWidth, 0, pWidth-xImHead);
		} else if (xImHead >= pWidth) {
			draw(g, im, 0, pWidth, width-xImHead, width-xImHead+pWidth);
		} else if(xImHead < 0 && xImHead >= pWidth-width) {
			draw(g, im, 0, pWidth, -xImHead, pWidth-xImHead);
		} else if(xImHead < pWidth-width) {
			draw(g, im, 0, width+xImHead, -xImHead, width);
			draw(g, im, width+xImHead, pWidth, 0, pWidth-width-xImHead);
		}
	}
	
	private void draw(Graphics g, BufferedImage im, int srcX1, int scrX2, int imX1, int imX2){
		g.drawImage(im, srcX1, 0, scrX2, pHeight,
				imX1, 0, imX2, pHeight, null); // Why are there pHeight everywhere ? In the image too !
		// it's because in this specific game, the image is higher than the Pannel and there are no vertical moves.
	}
	
}
