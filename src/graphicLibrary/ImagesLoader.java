package graphicLibrary;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

/**
 * Load images files only once and return them when needed. The Loaded Images
 * must neither contain any "." in their name, nor any space " "
 * 
 * Initialise with : ImagesLoader imsLd = ImagesLoader.getInstance();
 * imsLd.loadImagesFile(imageFile);
 * 
 * @author Vincent
 * @version 1.0
 */
public class ImagesLoader {

	private static ImagesLoader imagesLoader = null;

	private final HashMap<String, Integer> numberImages;
	// for g files, a list of files associated with a name
	private final HashMap<String, ArrayList<String>> gNamesMap;
	// list of images indexed by bane
	private final HashMap<String, ArrayList<BufferedImage>> imagesMap;
	private final GraphicsConfiguration gc;
	private String IMAGE_DIR = "Images/"; // The directory where the
											// images
											// are
	private final LinkedList<String> dir_crowler;

	// private String LOADER_NAME = "loader.txt"; // the default name of the
	// file text loader

	/**
	 * Initialise a new empty loader
	 */
	private ImagesLoader() {
		// switch translucency acceleration in Windows
		System.setProperty("sun.java2d.tanslaccel", "true");
		System.setProperty("sun.java2d.ddforcevram", "true");

		numberImages = new HashMap<String, Integer>();
		imagesMap = new HashMap<String, ArrayList<BufferedImage>>();
		gNamesMap = new HashMap<String, ArrayList<String>>();
		final GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		dir_crowler = new LinkedList<String>();
	}

	public static ImagesLoader getInstance() {
		if (imagesLoader == null) {
			imagesLoader = new ImagesLoader();
		}
		return imagesLoader;
	}

	/**
	 * Get the BufferedImage for the given name. If there are several
	 * BufferedImage, the first one is returned.
	 * 
	 * @param name
	 *            name of the image to load
	 * @return
	 */
	public BufferedImage getImage(final String name) {
		final ArrayList<BufferedImage> images = imagesMap.get(name);
		return (images == null) ? null : images.get(0);
	}

	/**
	 * This method returns the BufferedImage for the given name at the selected
	 * index in the ArrayList. If the number is negative, it returns the first
	 * BufferedImage. If the number is too large, it will be reduced modulo the
	 * ArrayList's size.
	 * 
	 * @param name
	 *            The name of the Image
	 * @param posn
	 *            The position of the Image in the ArrayList
	 * @return The BufferedImage we want
	 */
	public BufferedImage getImage(final String name, final int posn) {
		final ArrayList<BufferedImage> images = imagesMap.get(name);
		final int position = (posn < 0) ? 0 : (posn % images.size());
		return (images == null) ? null : images.get(position);
	}

	/**
	 * For g images (ie: a list of named image: visage.png visageSouris.png
	 * visagePleure.png...) It returns the BufferedImage at index fnmPrefix for
	 * the given name.
	 * 
	 * @param name
	 *            list of image to load
	 * @param fnmPrefix
	 *            name of the file
	 * @return
	 */
	public BufferedImage getImage(final String name, final String fnmPrefix) {
		final ArrayList<BufferedImage> images = imagesMap.get(name);
		final ArrayList<String> block = gNamesMap.get(name);
		if ((images == null) || (block == null)) {
			return null;
		}
		final int index = block.indexOf(fnmPrefix);
		if (index == -1) {
			return null;
		}
		return images.get(index);
	}

	/**
	 * Return the entire ArrayList for the given name in the HashMap
	 * 
	 * @param name
	 *            list of images to load
	 * @return
	 */
	public ArrayList<BufferedImage> getImages(final String name) {
		return imagesMap.get(name);
	}

	/**
	 * Load a single image from a text file thanks to a text line
	 * 
	 * @param line
	 *            line the line of the file name
	 */
	private void getFileNameImage(final String line) {
		// format is o <fnm>
		final StringTokenizer tokens = new StringTokenizer(line);
		if (tokens.countTokens() != 2) {
			System.out.println("Wrong number of arguments for " + line);
		} else {
			tokens.nextToken(); // skip the command label
			loadSingleImage(tokens.nextToken());
		}
	}

	/**
	 * Load the numbered images from a text file thanks to a text line
	 * 
	 * @param line
	 *            line the line of the file name
	 */
	private void getNumberedImage(final String line) {
		// format is n <fnm*.ext> <number>
		try {
			final StringTokenizer tokens = new StringTokenizer(line);
			if (tokens.countTokens() != 3) {
				System.out.println("Wrong number of arguments for " + line);
				return;
			}
			tokens.nextToken(); // skip the command label
			final String starName = tokens.nextToken();
			final int number = Integer.parseInt(tokens.nextToken());
			loadNumberedImages(starName, number);

		} catch (final NumberFormatException e) {
			System.out
					.println("Expected a integer to load a NumberedFile line : "
							+ line);
		}

	}

	/**
	 * Load the strip images from a text file thanks to a text line
	 * 
	 * @param line
	 *            line the line of the file name
	 */
	private void getStripImages(final String line) {
		// format is s <fnm> <numberCol> <numberLine>
		try {
			final StringTokenizer tokens = new StringTokenizer(line);
			if (tokens.countTokens() != 4) {
				System.out.println("Wrong number of arguments for " + line);
				return;
			}
			tokens.nextToken(); // skip the command label
			final String fnm = tokens.nextToken();
			final int numberCol = Integer.parseInt(tokens.nextToken());
			final int numberLine = Integer.parseInt(tokens.nextToken());
			loadStripImages(fnm, numberCol, numberLine);
		} catch (final NumberFormatException e) {
			System.out
					.println("Expected a integer to load a NumberedFile line : "
							+ line);
		}
	}

	/**
	 * Load the grouped images from a text file thanks to a text line
	 * 
	 * @param line
	 *            the line of the file name
	 */
	private void getGroupImages(final String line) {
		// format is g <name> <fnm> [ <fnm> ]*
		final StringTokenizer tokens = new StringTokenizer(line);
		tokens.nextToken(); // skip the command label
		final String name = tokens.nextToken();
		final LinkedList<String> listFile = new LinkedList<String>();
		while (tokens.hasMoreTokens()) {
			listFile.addLast(tokens.nextToken());
		}
		loadGroupImages(name, listFile);
	}

	/**
	 * Load a text fileLoader in a lower directory thanks to a file line
	 * 
	 * @param line
	 */
	private void getRecursiveFile(final String line) {
		// format is r <path/fnm>
		// fnm is a text file
		final StringTokenizer tokens = new StringTokenizer(line);
		if (tokens.countTokens() != 2) {
			System.out.println("Wrong number of arguments for " + line);
		}
		tokens.nextToken(); // skip the command label
		final String relativePath = tokens.nextToken();
		loadRecursiveFile(relativePath);
	}

	/**
	 * Analyse a coord line
	 * 
	 * @param line
	 *            A string formated like : f <fnm>
	 * @param br
	 *            The BufferReader of the file loader
	 */
	private void getCoordonnees(final String line, final BufferedReader br) {
		// format is c <fnm>. fnm is an image file.
		final StringTokenizer tokens = new StringTokenizer(line);
		if (tokens.countTokens() != 2) {
			System.out.println("Wrong number of arguments for " + line);
		}
		tokens.nextToken(); // skip the command label
		final String coordFnm = tokens.nextToken();
		loadCoordonnee2(coordFnm, br);
	}

	/**
	 * Read and analyse the file loader for the coord images
	 * 
	 * @param fnm
	 * @param br
	 * @return
	 */
	private boolean loadCoordonnee2(final String fnm, final BufferedReader br) {
		/*
		 * synthax : f <fnm> c <depx> <depy> <arrx> <arry> (this is the inner
		 * image to load) ...(There can be several inner images to load) /f
		 */

		try {
			String imageFileName = null;
			final LinkedList<Integer[]> coords = new LinkedList<Integer[]>();

			StringTokenizer token;
			imageFileName = fnm; // init image file

			// Now we have the name of the file to be loaded
			String line = br.readLine();
			while ((line != null) && (!line.startsWith("/f"))) {
				if (line.length() == 0) {// blank line
					continue;
				} else if (line.startsWith("//")) {// comment
					continue;
				} else {
					if (Character.toLowerCase(line.charAt(0)) == 'c') {
						token = new StringTokenizer(line);
						if (token.countTokens() != 5) {
							System.out
									.println("InvalidLine number of aruments, skipped file: "
											+ fnm + " line: " + line);
							continue;
						}
						token.nextToken(); // skip the command
						final Integer[] coord = new Integer[4];
						for (int i = 0; i < 4; i++) {
							coord[i] = Integer.parseInt(token.nextToken());
						}
						coords.add(coord);
					} else {
						System.out.println("Do not recognize line: " + line);
					}
				}
				line = br.readLine();
			}

			if (coords.isEmpty()) {
				System.out.println("strip file : " + fnm + " no coords");
				return false;
			}
			return getCoordonneeImage(imageFileName, coords);

		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		} catch (final NumberFormatException e) {
			System.out.println("Erreur : les coordonnees de l'image " + fnm
					+ " ne sont pas numeriques");
			e.printStackTrace();
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Load a file containing the images to be loaded
	 * 
	 * @param fnm
	 *            the file name
	 */
	public void loadImagesFile(final String fnm) {
		final String imsFNm = getRecursivePath(fnm);
		System.out.println("Reading file: " + imsFNm);
		try {
			// final InputStream in =
			// this.getClass().getResourceAsStream(imsFNm);
			// if (in == null) {
			// System.out.println("FileNotFound : " + fnm);
			// return;
			// }
			final BufferedReader br = new BufferedReader(new FileReader(imsFNm));

			String line;
			char ch;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) {// blank line
					continue;
				} else if (line.startsWith("//")) {// comment
					continue;
				} else {
					ch = Character.toLowerCase(line.charAt(0));
					if (ch == 'o') {
						getFileNameImage(line);
					} else if (ch == 'n') {
						getNumberedImage(line);
					} else if (ch == 's') {
						getStripImages(line);
					} else if (ch == 'g') {
						getGroupImages(line);
					} else if (ch == 'r') {
						getRecursiveFile(line);
					} else if (ch == 'f') {
						getCoordonnees(line, br);
					} else {
						System.out.println("Do not recognize line: " + line);
					}
				}
			}
			br.close();
		} catch (final IOException e) {
			System.exit(1);
		}
	}

	/**
	 * Load a single image with the name fnm in a HashMap
	 * 
	 * @param fnm
	 *            The name of the image
	 * @return true is the Image is loaded, false otherwise
	 */
	public boolean loadSingleImage(final String fnm) {
		final String name = getPrefix(fnm);

		if (imagesMap.containsKey(name)) {
			System.out.println("Error: " + name + " already used");
			return false;
		}

		final BufferedImage bi = loadImage(getRecursivePath(fnm));
		if (bi != null) {
			final ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
			imsList.add(bi);
			imagesMap.put(name, imsList);
			numberImages.put(name, 1);
			System.out.println(" Stored " + name + "/" + fnm);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Load a single image with the name fnm in a HashMap
	 * 
	 * @param starName
	 *            The name of the image
	 * @param number
	 *            The number of images to load
	 * @return true is the Image is loaded, false otherwise
	 */
	public boolean loadNumberedImages(final String starName, final int number) {
		if (number <= 0) {
			System.out
					.println("Inconsistent number of images to load at images :\n"
							+ starName);
			return false;
		}

		final String name = getPrefix(starName).replace("*", "");
		if (imagesMap.containsKey(name)) {
			System.out.println("Error: " + name + " already used");
			return false;
		}

		final ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
		int cpt = 0;
		while (cpt < number) {
			final String fnm = starName.replace("*", String.valueOf(cpt));
			final BufferedImage bi = loadImage(getRecursivePath(fnm));
			if (bi != null) {
				imsList.add(bi);
			} else {
				return false;
			}
			cpt++;
		}
		imagesMap.put(name, imsList);
		numberImages.put(name, imsList.size());
		System.out.println(" Stored " + name);
		return true;
	}

	/**
	 * Load a strip image
	 * 
	 * @param fnm
	 *            The file name of the strip image
	 * @param numberCol
	 *            The number of vertical images contained in the strip image
	 * @param numberLine
	 *            The number of horizontal images contained in the strip image
	 * @return true is the Image is loaded, false otherwise
	 */
	public boolean loadStripImages(final String fnm, final int numberCol,
			final int numberLine) {
		final String name = getPrefix(fnm);
		if (imagesMap.containsKey(name)) {
			System.out.println("Error: " + name + " already used");
			return false;
		}
		final BufferedImage[] images = loadStripImagesArray(fnm, numberCol,
				numberLine);
		final ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>();

		for (int i = 0; i < images.length; i++) {
			imageList.add(images[i]);
		}

		imagesMap.put(name, imageList);
		numberImages.put(name, imageList.size());
		System.out.println(" Stored " + name);
		return true;
	}

	/**
	 * Get a table of InnerBufferedImage from a strip Image.
	 * 
	 * @param fnm
	 *            The source image
	 * @param numberCol
	 *            The number of col of images inside
	 * @param numberLine
	 *            the number of raw of images inside
	 * @return A table of bufferedImages
	 */
	private BufferedImage[] loadStripImagesArray(final String fnm,
			final int numberCol, final int numberLine) {
		if ((numberCol <= 0) || (numberLine <= 0)) {
			System.out.println("number <= 0; returning null");
			return null;
		}

		BufferedImage stripIm;
		if ((stripIm = loadImage(getRecursivePath(fnm))) == null) {
			System.out.println("returning null");
			return null;
		}

		final int imWidth = stripIm.getWidth() / numberCol;
		final int imHeight = stripIm.getHeight() / numberLine;
		final int transparency = stripIm.getTransparency();

		final BufferedImage[] strip = new BufferedImage[numberCol * numberLine];
		Graphics2D stripGC;
		for (int i = 0; i < numberLine; i++) {
			for (int j = 0; j < numberCol; j++) {
				strip[j + i * numberCol] = gc.createCompatibleImage(imWidth,
						imHeight, transparency);
				stripGC = strip[j + i * numberCol].createGraphics();
				stripGC.drawImage(stripIm, 0, 0, imWidth, imHeight,
						j * imWidth, i * imHeight, (j + 1) * imWidth, (i + 1)
								* imHeight, null);
				stripGC.dispose();
			}
		}
		return strip;
	}

	/**
	 * Load a group of Images
	 * 
	 * @param name
	 *            The name of the group
	 * @param fileList
	 *            The list of the file names to load in the group
	 * @return true is the Image is loaded, false otherwise
	 */
	public boolean loadGroupImages(final String name,
			final LinkedList<String> fileList) {
		if (imagesMap.containsKey(name)) {
			System.out.println("Error: " + name + " already used");
			return false;
		}

		final ArrayList<BufferedImage> imgList = new ArrayList<BufferedImage>();
		final ArrayList<String> strList = new ArrayList<String>();

		for (final String s : fileList) {
			final BufferedImage bi = loadImage(getRecursivePath(s));
			if (bi == null) {
				return false;
			}
			imgList.add(bi);
			strList.add(getPrefix(s));

		}
		imagesMap.put(name, imgList);
		gNamesMap.put(name, strList);
		numberImages.put(name, imgList.size());
		System.out.println(" Stored " + name);
		return true;
	}

	/**
	 * Load the inner images from a strip file thanks to coordinates
	 * 
	 * @param fnm
	 *            The strip Image witch is the source
	 * @param list
	 *            The LinkedList<Integer> of coordinates representing the image.
	 *            Synthax:(xDep, yDep, xArr, yArr)
	 * @return true if all the images have been loaded, false otherwise
	 */
	public boolean getCoordonneeImage(final String fnm,
			final LinkedList<Integer[]> list) {
		final String name = getPrefix(fnm);
		if (imagesMap.containsKey(name)) {
			System.out.println("Error: " + name + " already used");
			return false;
		}
		final BufferedImage stripImage = loadImage(getRecursivePath(fnm)); // the
																			// strip
																			// Image
		final ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>();

		for (final Integer[] i : list) {
			if (i.length != 4) {
				System.out
						.println("Illegal number of coordonnates for the file "
								+ fnm);
				return false;
			}
			final int xDep = i[0], yDep = i[1], xArr = i[2], yArr = i[3];
			final BufferedImage innerImage = getInnerImage(stripImage, xDep,
					yDep, xArr, yArr);
			if (innerImage != null) {
				imageList.add(innerImage);
			}
		}
		imagesMap.put(name, imageList);
		numberImages.put(name, imageList.size());
		System.out.println(" Stored " + name);
		return true;

	}

	/**
	 * Load a single inner image from a strip image, thanks to coordinates
	 * 
	 * @param stripImage
	 *            The source (the strip image)
	 * @param xDep
	 *            The first x coordinate of the inner image
	 * @param yDep
	 *            The first y coordinate of the inner image
	 * @param xArr
	 *            The last x coordinate of the inner image
	 * @param yArr
	 *            The last y coordinate of the inner image
	 * @return The inner image
	 */
	private BufferedImage getInnerImage(final BufferedImage stripImage,
			final int xDep, final int yDep, final int xArr, final int yArr) {
		if ((xArr <= xDep) || (yArr <= yDep)) {
			System.out.println("Image mal formee, line: c " + xDep + " " + yDep
					+ " " + xArr + " " + yArr);
			return null;
		} else if ((xDep < 0) || (yDep < 0) || (xArr > stripImage.getWidth())
				|| (yArr > stripImage.getHeight())) {
			System.out.println("Image mal cadree, line: c " + xDep + " " + yDep
					+ " " + xArr + " " + yArr + " imageWidth: "
					+ stripImage.getWidth() + " imageHeight: "
					+ stripImage.getHeight());
			return null;
		}
		// Now the image we wanna load is relevant.
		final int SWIDTH = xArr - xDep, SHEIGHT = yArr - yDep;
		final int transparency = stripImage.getTransparency();
		final BufferedImage innerImage = gc.createCompatibleImage(SWIDTH,
				SHEIGHT, transparency);
		final Graphics2D innerG = innerImage.createGraphics();
		innerG.drawImage(stripImage, 0, 0, SWIDTH, SHEIGHT, xDep, yDep, xArr,
				yArr, null);
		innerG.dispose();
		return innerImage;
	}

	/**
	 * Manage the recursive location of files to load textFiles recursively
	 * 
	 * @param relativePath
	 *            the relative path from the parent file in the recursion for
	 *            the aimed text file
	 */
	private void loadRecursiveFile(final String relativePath) {

		final int indexSeparator = relativePath.lastIndexOf('/');
		final String dir = relativePath.substring(0, indexSeparator + 1);// The
																			// path
																			// with
																			// the
																			// last
																			// "/"
		final String file = relativePath.substring(indexSeparator + 1);// The
																		// name
																		// of
																		// the
																		// file

		// Ajout du dossier a la pile du chemin actuel
		dir_crowler.addLast(dir);

		// Load the file (Just give the name of the file. The LinkedList will
		// find it)
		loadImagesFile(file);

		// Suppression du dossier de la pile du chemin actuel
		dir_crowler.removeLast();
	}

	/**
	 * Return the image named fnm.
	 * 
	 * @param fnm
	 *            The name of the image to load
	 * @return The image or null
	 */
	private BufferedImage loadImage(final String fnm) {
		try {
			// final BufferedImage im =
			// ImageIO.read(getClass().getResource(fnm));
			final BufferedImage im = ImageIO.read(new File(fnm));
			final BufferedImage copy = gc.createCompatibleImage(im.getWidth(),
					im.getHeight(), im.getTransparency());

			// create a graphics context
			final Graphics2D g2d = copy.createGraphics();
			g2d.drawImage(im, 0, 0, null);
			g2d.dispose();
			return copy;
		} catch (final IOException e) {
			e.printStackTrace();
			System.out.println("Load Image error for " + fnm + "\n" + e);
			return null;
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println("Load Image Illegal argument for " + fnm + "\n"
					+ e);
			return null;
		} catch (final Exception e) {
			System.out.println("Load Image error " + fnm + "\n" + e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the prefix of a file with contains no points (the file name minus the
	 * extension) This file can be a relative path, but it must be in the image
	 * directory
	 * 
	 * @param fnm
	 *            The file name to load
	 * @return The file name minus the extension
	 */
	private String getPrefix(final String fnm) {
		final int indexSeparator = fnm.lastIndexOf('/');
		final String prefix = fnm.substring(indexSeparator + 1);// The name of
																// the file
		final StringTokenizer token = new StringTokenizer(prefix, ".");
		return token.nextToken();
	}

	/**
	 * Check if the specified name has been loaded
	 * 
	 * @param name
	 *            The name of the image / the serie of images
	 * @return True if the image has been loaded, false otherwise
	 */
	public boolean isLoaded(final String name) {
		return imagesMap.containsKey(name);
	}

	/**
	 * Remove a single, numbered, strip or grouped image.
	 * 
	 * @param name
	 *            The name to be removed
	 * @return true if the image has been removed, false otherwise
	 */
	public boolean remove(final String name) {
		if (imagesMap.remove(name) == null) {
			return false;
		} else {
			gNamesMap.remove(name);
			numberImages.remove(name);
			return true;
		}
	}

	/**
	 * Remove a list of single, numbered, strip or grouped images.
	 * 
	 * @param names
	 *            The LinkedListe of the names to be removed
	 * @return true if all the images has been removed, false otherwise
	 */
	public boolean remove(final LinkedList<String> names) {
		for (final String name : names) {
			if (imagesMap.remove(name) == null) {
				System.out
						.println("Can't remove "
								+ name
								+ " in loader. Maybe the image has already been removed");
				return false;
			} else {
				gNamesMap.remove(name);
				numberImages.remove(name);
			}
		}
		return true;
	}

	/**
	 * Clear all the data in the loader
	 */
	public void clear() {
		imagesMap.clear();
		gNamesMap.clear();
		numberImages.clear();
	}

	/**
	 * Getters for the directory containing the images
	 * 
	 * @return the name of the directory
	 */
	public String getIMAGE_DIR() {
		return IMAGE_DIR;
	}

	/**
	 * Setters for the directory containing the images
	 */
	public void setIMAGE_DIR(final String iMAGE_DIR) {
		IMAGE_DIR = iMAGE_DIR;
	}

	/**
	 * Return the number of image for the given name
	 * 
	 * @param name
	 * @return
	 */
	public int getNumberImage(final String name) {
		return numberImages.get(name);
	}

	/**
	 * Create the path from the user directory, managing the recursion to load
	 * things recursively.
	 * 
	 * @param fnm
	 *            The file name to be loaded
	 * @return
	 */
	private String getRecursivePath(final String fnm) {
		String imsFNm = IMAGE_DIR;
		final Iterator<String> iterator = dir_crowler.iterator();
		while (iterator.hasNext()) {
			imsFNm += iterator.next();
		}
		imsFNm += fnm; // now we have the complete path
		return imsFNm;
	}

}
