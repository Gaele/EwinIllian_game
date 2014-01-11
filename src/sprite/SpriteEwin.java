package sprite;

import game.Game;
import graphicLibrary.ImagesLoader;

import java.awt.Graphics;
import java.awt.Rectangle;

import map.MapEwin;
import characters.Hero;

public class SpriteEwin extends Sprite {

	Game game;
	Hero ewin;

	public SpriteEwin(final Game g, final Hero hero, final int x, final int y,
			final int w, final int h, final String name, final String image) {
		super(x, y, g.getPanel().getWidth(), g.getPanel().getHeight(),
				ImagesLoader.getInstance(), name, image);
		width = w;
		height = h;
		game = g;
		ewin = hero;
		// try {
		// player = new ImagesPlayer("run", g.getProgramPeriod(),
		// g.getPeriodBetween2Images(), true,
		// ImagesLoader.getInstance());
		// } catch (final TooShortImagePeriodException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@Override
	public void updateSpriteSpeed() {
		if (isActive) {
			dy += 10 * ((float) game.getProgramPeriod() / 200);
			if (isLooping) {
				player.updateTick();
			}
		}
	}

	@Override
	public void drawSprite(final Graphics g) {
		if (isActive) {
			super.prepareDrawSprite(g);
			if (image == null) { // the sprite has no image
				return;
			}
			final MapEwin map = game.getMap();
			if (!ewin.getOldLeft()) {
				// System.out.println("x: " + (locx + map.getWorldOffsetX())
				// + ", y: " + (map.getWorldOffsetY() + locy));
				// System.out.println("xoff: " + (map.getWorldOffsetX())
				// + ", yoff: " + (map.getWorldOffsetY()));
				// System.out.println("x-off: " + (locx) + ", y-off: " +
				// (locy));
				g.drawImage(image, (int) (locx + map.getWorldOffsetX()),
						(int) (map.getWorldOffsetY() + locy), width, height,
						null);
			} else {
				g.drawImage(image,
						(int) (locx + width + map.getWorldOffsetX()),
						(int) (locy + map.getWorldOffsetY()),
						(int) (locx + map.getWorldOffsetX()), (int) (locy
								+ height + map.getWorldOffsetY()), 0, 0,
						image.getWidth(), image.getHeight(), null);
			}

		}
	}

	/**
	 * Sprite's bounding box
	 * 
	 * @return
	 */
	@Override
	public Rectangle getMyRectangle() {
		final int lambda = 10;
		return new Rectangle((int) locx + lambda, (int) locy + 2 * lambda,
				width - 2 * lambda, height - 2 * lambda);
	}

	@Override
	public void imageSequenceEnded(final String imageName) {

	}
}
