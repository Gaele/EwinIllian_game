package graphicLibrary;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;


public class ImageSFXs {

	public static final int HORIZONTAL_FLIP = 0;
	public static final int VERTICAL_FLIP = 1;
	public static final int DOUBLE_FLIP = 2;
	private GraphicsConfiguration gc;
	private ConvolveOp blurOp;
	
	public ImageSFXs() {
		gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		this.initEffects();
	}
	
	/**
	 * Create a pre-defined op for the image negation and blurring
	 */
	private void initEffects() {
		// image negation, explained later
		
		// blur by convolving the image with a matrix
		float ninth = 1.0f / 9.0f;
		float[] blurKernel = {
				ninth, ninth, ninth,
				ninth, ninth, ninth,
				ninth, ninth, ninth,
		};
		blurOp = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, null);
	}
	
	/**
	 * Change the size of an image
	 * @param g2d draw in this graphic
	 * @param im the image to draw
	 * @param x the old x coordinates of the image
	 * @param y the old y coordinates
	 * @param widthChange A coefficient to rescale on the X axis
	 * @param heightChange coefficient to rescale on the Y axis
	 */
	public void drawResizedImage(Graphics2D g2d, BufferedImage im, int x, int y, double widthChange, double heightChange) {
		int destWidth = (int)(im.getWidth() * widthChange);
		int destHeight = (int)(im.getHeight() * heightChange);
		
		// adjust top-left (x,y) coord of resized image so remains centered
		int destX = x + im.getWidth()/2 - destWidth/2;
		int destY = y + im.getHeight()/2 - destHeight/2;
		
		g2d.drawImage(im, destX, destY, destWidth, destHeight, null);
		
	}
	
	public BufferedImage getFlippedImage(BufferedImage im, int flipKind) {
		if(im == null) {
			System.out.println("getFlipedImage: input image is null");
			return null;
		}
		int imWidth = im.getWidth();
		int imHeight = im.getHeight();
		int transparency = im.getColorModel().getTransparency();
		
		BufferedImage copy = gc.createCompatibleImage(imWidth, imHeight, transparency);
		Graphics2D g2d = copy.createGraphics();
		
		renderFlip(g2d, im, imWidth, imHeight, flipKind);
		g2d.dispose();
		
		return copy;
	}
	
	private void renderFlip(Graphics2D g2d, BufferedImage im, int imWidth, int imHeight, int flipKind) {
		if(flipKind == VERTICAL_FLIP)
			g2d.drawImage(im, imWidth, 0, 0, imHeight,
					0, 0, imWidth, imHeight, null);
		else if(flipKind == HORIZONTAL_FLIP)
			g2d.drawImage(im, 0, imHeight, imWidth, 0,
					0, 0, imWidth, imHeight, null);
		else if (flipKind == DOUBLE_FLIP)
			g2d.drawImage(im, imWidth, imHeight, 0, 0,
					0, 0, imWidth, imHeight, null);
		else {
			System.out.println("In ImageSFXs.java, method renderFlip, argument flipKind unknown");
		}
			
	}
	
	public void drawFadedImage(Graphics2D g2d, BufferedImage im, int x, int y, float alpha) {
		Composite c = g2d.getComposite(); // backup the old composite
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		
		g2d.drawImage(im, x, y, null);
		
		g2d.setComposite(c);
	}
	
	public BufferedImage getRotatedImage(BufferedImage im, int angle) {
		if(im == null) {
			System.out.println("getRotatedImage is null");
			return null;
		}
		BufferedImage dest = gc.createCompatibleImage(im.getWidth(), im.getHeight(), im.getTransparency());
		Graphics2D g2d = dest.createGraphics();
		
		AffineTransform origAT = g2d.getTransform(); // save original
		
		// rotate the coord. system of the dest. image around its center
		AffineTransform rot = new AffineTransform();
		rot.rotate(Math.toRadians(angle), im.getWidth()/2, im.getHeight()/2);
		g2d.transform(rot);
		
		g2d.drawImage(im, 0, 0, null);
		
		g2d.setTransform(origAT);
		g2d.dispose();
		
		return dest;
	}
	
	public void drawBlurredImage(Graphics2D g2d, BufferedImage im, int x, int y) {
		if(im == null) {
			System.out.println("getBlurredImage: input image is null");
			return;
		}
		g2d.drawImage(im, blurOp, x, y);
	}
	
	/**
	 * 
	 * @param g2d
	 * @param im
	 * @param x
	 * @param y
	 * @param size used to specify a size*size blur kernel, filled with 1/(size*size) values.
	 */
	public void drawBlurredImage(Graphics2D g2d, BufferedImage im, int x, int y, int size) {
		if(im == null) {
			System.out.println("getBlurredImage: input image is null");
			return;
		}
		int imWidth = im.getWidth();
		int imHeight = im.getHeight();
		int maxSize = (imWidth > imHeight)?imWidth:imHeight;
		
		if((maxSize%2) == 0) // if event
			maxSize--;
		
		if(size%2 == 0) { // if event
			size++; // make it odd
			System.out.println("Blur size must be odd; adding 1 to make size = " + size);
		}
		
		if(size < 3) {
			System.out.println("Minimum blur size is 3");
			size = 3;
		} else if(size > maxSize){
			System.out.println("Maximum blur size is " + maxSize);
			size = maxSize;
		}
		
		//create a blur kernel
		int numCoords = size * size;
		float blurFactor = 1.0f / (float) numCoords;
		
		float[] blurKernel = new float[numCoords];
		for(int i=0; i<numCoords; i++)
			blurKernel[i] = blurFactor;
		
		ConvolveOp blurringOp = new ConvolveOp(
				new Kernel(size, size, blurKernel),
				ConvolveOp.EDGE_NO_OP, null);
		
		g2d.drawImage(im, blurringOp, x, y);
	} 
	
	
	
	
	
	
	public boolean hasAlpha(BufferedImage im) {
		if(im == null)
			return false;
		int transparency = im.getTransparency();
		if((transparency == Transparency.BITMASK) ||(transparency == Transparency.TRANSLUCENT))
			return true;
		else
			return false;
	}
	
	// Deformation trouvee sur internet
	public static BufferedImage getCircleDeformation(BufferedImage source,
			double amplitude, double longueurOnde) {
		//copie image
		BufferedImage newImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getTransparency());
		// ensuite prenons le centre de l'image
		int centX = source.getWidth() / 2;
		int centY = source.getHeight() / 2;
		//On parcourt l'image en colonnes
		for (int i = 0; i < newImage.getWidth(); i++) {
			for (int j = 0; j < newImage.getHeight(); j++) {
				// x est entre -cenX et cenX
				int x = i - centX, y = j - centY;
				// We want the polar coordinates
				double theta = Math.atan2(y, x); // The angle of the point (x,y) in the "repere" of the center of the image
				double r = Math.sqrt(x * x + y * y);// Thales gives us the distance in polar coordinates
				double R = r + amplitude * Math.sin(r * 2 * Math.PI / longueurOnde);//On modifie cette distance R
				int I = (int) (R * Math.cos(theta) + centX);// Puis on calcul les coord du point pointe
				int J = (int) (R * Math.sin(theta) + centY);
				try {
					//System.out.println(source.getRGB(I, J));
					newImage.setRGB(i, j, source.getRGB(I, J));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return newImage;
	}

	
	
}
