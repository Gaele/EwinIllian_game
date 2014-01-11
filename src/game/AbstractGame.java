package game;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import entities.Entity;

public abstract class AbstractGame {

	// All the sprites
	protected Map<String, Entity> elements = new HashMap<String, Entity>();
	// Sprite which are sensible to the keyboard and the mouse interaction
	protected LinkedList<String> touchableSprites = new LinkedList<String>();

	// protected BackGroundManager bg = null;

	public void update() {
		final Set cles = elements.keySet();
		final Iterator it = cles.iterator();
		while (it.hasNext()) {
			final Object cle = it.next();
			final Entity valeur = elements.get(cle);
			valeur.updateSpeed();
		}
		final Iterator it2 = cles.iterator();
		while (it2.hasNext()) {
			final Object cle = it2.next();
			final Entity valeur = elements.get(cle);
			valeur.handleCollision();
		}
		final Iterator it3 = cles.iterator();
		while (it3.hasNext()) {
			final Object cle = it3.next();
			final Entity valeur = elements.get(cle);
			valeur.updatePosition();
		}
		// if(bg != null)
		// bg.update();
	}

	public void render(final Graphics g) {
		// if(bg != null)
		// bg.display(g);
		final Set cles = elements.keySet();
		final Iterator it = cles.iterator();
		while (it.hasNext()) {
			final Object cle = it.next();
			final Entity valeur = elements.get(cle);
			// valeur.drawSprite(g);
			valeur.render(g);
		}
	}

	public abstract void loadGame(String file);

	public abstract void keyPressed(int keyCode);

	public abstract void keyReleased(int keyCode);

	public abstract void mousePressed(int x, int y);

	public abstract void mouseReleased(int x, int y);

	public abstract void mouseMoved(int x, int y);

}
