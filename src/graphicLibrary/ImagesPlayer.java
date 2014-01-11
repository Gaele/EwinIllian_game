package graphicLibrary;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Contains one serie of images making an animation. It's possible to draw this
 * serie, to stop it, pause, resume...
 * 
 * @author Vincent
 * @version 1.0
 */
public class ImagesPlayer {

	private final boolean isRepeating; // sequence looping
	private final ImagesLoader imLoader; // The image loader
	private final ArrayList<BufferedImage> images; // The list of images to play
	private int numImages; // The number of images in the sequence
	private String playerName; // The name of the entity. (The group of images
								// to load)
	private final int animPeriod; // The animation theorical TPS of the game
	private final int periodBetween2Images; // theorical time between 2 images
	private ImagesPlayerWatcher watcher; // watcher to notify when null
	private int actualImageIndex = 0; // L'image courante, a afficher
	private int currentTimeInPeriod = 0; // count the time before the image
											// changes
	private boolean loaded = false, pause = false, atEnd = false;
	private int restartIndex = 0;

	/**
	 * Initialise an ImagePlayer with a null watcher
	 * 
	 * @param playerName
	 *            The name of the image to be loaded
	 * @param programPeriod
	 *            The period of the program (ups)
	 * @param periodBetween2Images
	 *            The period between 2 images in the animation
	 * @param isRepeating
	 *            True if the animation has to be repeated
	 * @param il
	 *            The Image loader
	 * @throws TooShortImagePeriodException
	 */
	public ImagesPlayer(final String playerName, final int programPeriod,
			final int periodBetween2Images, final boolean isRepeating,
			final ImagesLoader il) throws TooShortImagePeriodException {
		this(playerName, programPeriod, periodBetween2Images, isRepeating, il,
				null);
	}

	/**
	 * Initialise an ImagePlayer
	 * 
	 * @param playerName
	 *            The name of the image player (can be change later)
	 * @param programPeriod
	 *            The period of the program (ups)
	 * @param periodBetween2Images
	 *            The period between 2 images in the animation
	 * @param isRepeating
	 *            True if the animation has to be repeated
	 * @param il
	 *            The Image loader
	 * @param watcher
	 *            The object to notify when the animation is reached its end
	 * @throws TooShortImagePeriodException
	 */
	public ImagesPlayer(final String playerName, final int animPeriod,
			final int periodBetween2Images, final boolean isRepeating,
			final ImagesLoader il, final ImagesPlayerWatcher watcher)
			throws TooShortImagePeriodException {
		if (animPeriod > periodBetween2Images) {
			throw new TooShortImagePeriodException();
		}
		imLoader = il;
		this.isRepeating = isRepeating;
		images = new ArrayList<BufferedImage>();
		numImages = 0;
		this.playerName = playerName;
		this.animPeriod = animPeriod;
		this.periodBetween2Images = periodBetween2Images;
		this.watcher = watcher;
	}

	/**
	 * Notify this object that an other period in the program has passed
	 */
	public void updateTick() {
		if (atEnd || pause || !loaded) {
			return;
		}
		currentTimeInPeriod += animPeriod; // Update period
		if (currentTimeInPeriod >= periodBetween2Images) { // image changes
			actualImageIndex++;
			if (actualImageIndex == numImages) {
				atSequenceEnd();
			}
			currentTimeInPeriod -= periodBetween2Images;
		}
	}

	/**
	 * Get the current image to display
	 * 
	 * @return the image to display
	 */
	public BufferedImage getCurrentImage() {
		if (loaded) {
			return images.get(actualImageIndex);
		} else {
			return null;
		}
	}

	/**
	 * Notify the listener that the sequence has ended
	 */
	public void atSequenceEnd() {
		if (!isRepeating) {
			atEnd = true;
			pause = true;
			actualImageIndex = numImages - 1;
		} else {
			actualImageIndex = restartIndex;
		}
		watcher.imageSequenceEnded(playerName);
	}

	/**
	 * restart the sequence at the specified index
	 * 
	 * @param pos
	 */
	public void restartAt(final int pos) {
		if ((pos < 0) || (pos >= numImages)) {
			System.out.println("Restart point for " + playerName
					+ " beyond limits");
			return;
		}
		actualImageIndex = pos;
		currentTimeInPeriod = 0;
	}

	/**
	 * inform the loader to restart always at the index pos (skip the first
	 * images)
	 * 
	 * @param pos
	 *            The index of the image to restart
	 */
	public void alwaysRestartAt(final int pos) {
		if ((pos < 0) || (pos >= numImages)) {
			System.out.println("Restart point for " + playerName
					+ " beyond limits");
			return;
		}
		restartIndex = pos;
	}

	/**
	 * Set the object that will be notified when the anim sequence will reaches
	 * its end
	 * 
	 * @param watcher
	 */
	public void setWatcher(final ImagesPlayerWatcher watcher) {
		this.watcher = watcher;
	}

	/**
	 * Pauses the animation and go back to the beginning
	 */
	public void stop() {
		pause = true;
		actualImageIndex = 0;
	}

	/**
	 * Start the Image Player
	 */
	public void start() {
		pause = false;
		actualImageIndex = 0;
	}

	/**
	 * Pauses the animation
	 */
	public void pause() {
		pause = true;
	}

	/**
	 * Resume the animation
	 */
	public void resume() {
		pause = false;
	}

	/**
	 * Set the current name of the player
	 * 
	 * @param name
	 */
	public void setName(final String playerName) {
		this.playerName = playerName;
	}

	/**
	 * Load all the images for the current name
	 * 
	 * @return
	 */
	public boolean loadAll(final String name) {
		try {
			final ArrayList<BufferedImage> newImages = imLoader.getImages(name);

			if (images.addAll(newImages)) {
				numImages += newImages.size();
				loaded = true;
				return true;
			} else {
				System.out.println("FAIL TO LOAD IMAGE : " + name);
				return false;
			}
		} catch (final Exception e) {
			System.out.println("Error: can't load all the images for the name:"
					+ name + " in player " + playerName);
			return false;
		}
	}

	/**
	 * Load in the player all the images with the specified name, from the index
	 * [depIndex, arrIndex[ (arrIndex not in)
	 * 
	 * @param depIndex
	 * @param arrIndex
	 * @return
	 */
	public boolean load(final String name, final int depIndex,
			final int arrIndex) {
		if (depIndex > arrIndex) { // decreasing order
			if (arrIndex < 0) {
				System.out.println("Can't load an index < 0");
				return false;
			} else if (depIndex > imLoader.getNumberImage(name)) {
				System.out
						.println("Can't load an index greater than the number of images");
				return false;
			} else {
				for (int i = depIndex; i > arrIndex; i--) {
					images.add(imLoader.getImage(name, i));
				}
				numImages += (depIndex - arrIndex);
				loaded = true;
				return true;
			}
		} else { // increasing or constant
			if (depIndex < 0) {
				System.out.println("Can't load an index < 0");
				return false;
			} else if (arrIndex > imLoader.getNumberImage(name)) {
				System.out
						.println("Can't load an index greater than the number of images");
				return false;
			} else {
				for (int i = depIndex; i < arrIndex; i++) {
					images.add(imLoader.getImage(name, i));
				}
				numImages += (arrIndex - depIndex);
				loaded = true;
				return true;
			}
		}
	}

	/**
	 * Load the image with the given name and the given innerName
	 * 
	 * @param name
	 *            The name to load
	 * @param movementName
	 *            The innerName to load
	 * @return true if the image has been loaded, false otherwise
	 */
	public boolean load(final String name, final String movementName) {
		images.add(imLoader.getImage(name, movementName));
		loaded = true;
		return true;
	}

	public void setActualImageIndex(final int actualImageIndex) {
		this.actualImageIndex = actualImageIndex;
	}

}
