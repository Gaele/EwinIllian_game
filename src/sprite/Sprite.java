package sprite;

import graphicLibrary.ImagesLoader;
import graphicLibrary.ImagesPlayer;
import graphicLibrary.ImagesPlayerWatcher;
import graphicLibrary.TooShortImagePeriodException;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Represent a Sprite
 * 
 * Collision detection and collision response is left to subclasses.
 * 
 * @author Vincent
 * 
 */
public abstract class Sprite implements ImagesPlayerWatcher {

	// Default speed is dx = 0, dy = 0
	// Default dimension when there is no image
	private static final int SIZE = 12;
	protected String name = "default";

	// image-related globals
	protected ImagesLoader imsLoader;
	private String imageName;
	protected BufferedImage image;
	protected int width, height;
	protected boolean lockX = false, lockY = false;

	protected ImagesPlayer player; // for playing a loop of images
	protected boolean isLooping;
	protected boolean isActive = true;

	private final int pWidth, pHeight; // panel dimensions

	// protected vars
	protected float locx, locy; // location of sprite
	protected float dx, dy; // amount to move for each update

	/**
	 * 
	 * @param x
	 *            location
	 * @param y
	 *            location
	 * @param w
	 *            panel dimension
	 * @param h
	 *            panel dimension
	 * @param imsLd
	 *            Image loader
	 * @param name
	 *            The name of the sprite
	 * @param image
	 *            The name of the image to load
	 */
	public Sprite(final int x, final int y, final int w, final int h,
			final ImagesLoader imsLd, final String name, final String image) {
		locx = x;
		locy = y;
		pWidth = w;
		pHeight = h;
		dx = 0f;
		dy = 0f;
		imsLoader = imsLd;
		this.name = name;
		setImage(image); // the sprite's default name is 'name'
	}

	/**
	 * Permit a Sprite to be altered at runtime
	 * 
	 * @param name
	 */
	public void setImage(final String name) {
		imageName = name;
		image = imsLoader.getImage(name);
		if (image == null) {// no image of that name found
			System.out.println("No sprite image for " + imageName);
			width = SIZE;
			height = SIZE;
		} else {
			width = image.getWidth();
			height = image.getHeight();
		}
		// no image loop playing
		player = null;
		isLooping = false;
	}

	/**
	 * Loop the image
	 * 
	 * @param programPeriod
	 * @param periodBetween2Images
	 */
	public void loopImage(final int programPeriod,
			final int periodBetween2Images, final int alwaysRestartAt) {
		if (imsLoader.getNumberImage(imageName) > 1) {
			player = null; // for garbage collection of previous player
			try {
				player = new ImagesPlayer("Loader1", programPeriod,
						periodBetween2Images, true, imsLoader, this);
				player.loadAll(imageName);
				player.alwaysRestartAt(alwaysRestartAt);
				player.start();
			} catch (final TooShortImagePeriodException e) {
				e.printStackTrace();
			}
			isLooping = true;
		} else {
			System.out.println(imageName + " is not a sequence of images");
		}
	}

	/**
	 * Stop looping the player
	 */
	public void stopLooping() {
		if (isLooping) {
			player.stop();
			image = player.getCurrentImage();
			isLooping = false;
		}
	}

	/**
	 * Sprite's bounding box
	 * 
	 * @return
	 */
	public Rectangle getMyRectangle() {
		return new Rectangle((int) locx, (int) locy, width, height);
	}

	/**
	 * update the Sprite
	 */
	public void updateSpriteSpeed() {
		if (isActive) {
			if (isLooping) {
				player.updateTick();
			}
		}
	}

	public void updateSpritePosition() {
		locx += dx;
		locy += dy;
	}

	/**
	 * Draw the sprite
	 */
	public void prepareDrawSprite(final Graphics g) {
		if (isActive) {
			if (image == null) { // the sprite has no image
				g.setColor(Color.yellow);
				g.fillOval((int) locx, (int) locy, SIZE, SIZE);
			} else {
				if (isLooping) {
					image = player.getCurrentImage();
					// g.drawImage(image, locx, locy, null);
				}
			}
		}
	}

	public abstract void drawSprite(Graphics g);

	protected BufferedImage getImage() {
		return image;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isLooping() {
		return isLooping;
	}

	public void activate() {
		isActive = true;
	}

	public void deactivate() {
		isActive = false;
	}

	public String getName() {
		return name;
	}

	public float getLocx() {
		return locx;
	}

	public float getLocy() {
		return locy;
	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public void setLocx(final float locx) {
		this.locx = locx;
	}

	public void setLocy(final float locy) {
		this.locy = locy;
	}

	public void setDx(final float dx) {
		this.dx = dx;
	}

	public void setDy(final float dy) {
		this.dy = dy;
	}

}
