package game;

import graphicLibrary.ImagesLoader;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import map.MapEwin;
import audio.PlayMidiSequence;
import characters.Hero;

public class Game extends AbstractGame {

	private final JPanel panel;
	private final ImagesLoader imsLd;
	private final int programPeriod, periodBetween2Images;
	private Hero ewin;
	private final MapEwin map;
	private final String MAP_DIR = "Maps/";
	private final String mapToLoad = "carte02.txt";
	final PlayMidiSequence midiPlayer;

	public Game(final JPanel p, final ImagesLoader iml,
			final int programPeriod, final int periodBetween2Images) {
		panel = p;
		imsLd = iml;
		map = new MapEwin(this);
		map.loadMap(MAP_DIR + mapToLoad);
		this.programPeriod = programPeriod;
		this.periodBetween2Images = periodBetween2Images;
		midiPlayer = new PlayMidiSequence("ambiance01.mid", true, 0);
		midiPlayer.play();
	}

	@Override
	public void loadGame(final String file) {

	}

	@Override
	public void update() {
		// System.out.println("new update !");
		map.update();

		// update speed
		ewin.updateSpeed();

		// Check collision
		ewin.handleCollision();

		// update position
		ewin.updatePosition();
	}

	@Override
	public void render(final Graphics g) {
		map.draw(g, (int) -ewin.getLocx() + (panel.getWidth() / 2),
				(int) -ewin.getLocy() + (panel.getHeight() / 2));
		ewin.render(g);
	}

	@Override
	public void keyPressed(final int keyCode) {
		// System.out.println("KeyPressed");
		switch (keyCode) {
		case KeyEvent.VK_A:
			ewin.goLeft();
			break;
		case KeyEvent.VK_D:
			ewin.goRight();
			break;
		case KeyEvent.VK_W:
			ewin.goUp();
			break;
		case KeyEvent.VK_S:
			ewin.goDown();
			break;
		case KeyEvent.VK_SPACE:
			ewin.breakCell();
			break;
		}
	}

	@Override
	public void keyReleased(final int keyCode) {
		// System.out.println("KeyReleased");
		switch (keyCode) {
		case KeyEvent.VK_A:
			ewin.horizontalStay();
			break;
		case KeyEvent.VK_D:
			ewin.horizontalStay();
			break;
		case KeyEvent.VK_W:
			ewin.verticalStay();
			break;
		case KeyEvent.VK_S:
			ewin.verticalStay();
			break;
		}
	}

	@Override
	public void mousePressed(final int x, final int y) {
		System.out.println("MousePressed");

	}

	@Override
	public void mouseReleased(final int x, final int y) {
		System.out.println("MouseReleased");

	}

	@Override
	public void mouseMoved(final int x, final int y) {
		// TODO Auto-generated method stub
		System.out.println("Moved");

	}

	public void loadObject(final int type, final int x, final int y) {
		switch (type) {
		case 50:
			ewin = new Hero(this, "Ewin", x, y);
			// System.out.println("x:" + x + ", y:" + y);
			break;
		}
	}

	public int getProgramPeriod() {
		return programPeriod;
	}

	public int getPeriodBetween2Images() {
		return periodBetween2Images;
	}

	public JPanel getPanel() {
		return panel;
	}

	public MapEwin getMap() {
		return map;
	}

}
