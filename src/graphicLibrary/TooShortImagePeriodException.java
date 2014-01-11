package graphicLibrary;

public class TooShortImagePeriodException extends Exception {

	/**
	 * default serialVersionUID (Pour que le PC arrete avec son fichu Warning...)
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "The period between 2 images in the ImagesPlayer can't be shorter than the program's period, otherwise, all the images won't be displayed";
	}
	
	
}
