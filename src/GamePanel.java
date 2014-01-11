import game.AbstractGame;
import game.Game;
import graphicLibrary.ImagesLoader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

	private static final int PWIDTH = 800;// Taille du panel
	private static final int PHEIGHT = 600;
	private Thread animator; // Thead du jeu
	private volatile boolean running = false;// for the animation
	private volatile boolean gameOver = false;// for game termination
	private volatile boolean isPaused = false;// for game pause

	private static boolean printStats = false;

	// Variables globales pour corriger l'impressision des attentes (sleep)
	private static final int NO_DELAY_PER_YIELD = 16;// Nombre de frames de 0ms
														// avant qu'on laisse la
														// place à un autre
														// tread.
	private static final int period = 20;// Le temps d'une boucle

	private static final int MAX_FRAME_SKIPS = 5;

	// Global variables for update rendering
	private Graphics dbg;
	private Image dbImage = null;

	// Game variables
	private final ImagesLoader imsLd;
	private final String imageFile = "EwinImages.txt";
	private final int periodBetween2Images = 50;
	private final AbstractGame abstractGame;

	// stats
	GameStats gameStats;

	public GamePanel() {
		// set the game
		imsLd = ImagesLoader.getInstance();
		imsLd.loadImagesFile(imageFile);
		// imsLd = new ImagesLoader(imageFile);
		abstractGame = new Game(this, imsLd, period, periodBetween2Images);

		// Window's parameters
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		// Add user interaction
		setFocusable(true);
		requestFocus();// JPanel recoit maintenant les evenements clavier
		gameKeyPressed();// Keyboard Listener
		gameMousePressed();// Mouse Listener

		gameStats = new GameStats(period);

	}

	/**
	 * Attend que le JPanel soit ajouté au JFrame/JApplet avant de commencer
	 */
	@Override
	public void addNotify() {
		super.addNotify(); // notifie la paire
		startGame();
	}

	/**
	 * Initialise et démarre les Threads
	 */
	private void startGame() {
		if ((animator == null) || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	/**
	 * Met le jeu en pause
	 */
	public void pauseGame() {
		isPaused = true;
	}

	/**
	 * Reprend le jeu après une pause
	 */
	public void resumeGame() {
		isPaused = false;
	}

	/**
	 * Called by the user to stop execution
	 */
	public void stopGame() {
		running = false;
	}

	@Override
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0;
		int noDelay = 0;
		long excess = 0L;

		beforeTime = System.nanoTime();
		running = true;
		while (running) {
			gameUpdate(); // Game state is updated
			gameRender(); // render to a buffer
			paintScreen(); // draw buffer to screen

			// System.out.println(period);

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period * 1000000 - timeDiff) - overSleepTime;// period
																		// ms ->
																		// ns
			// System.out.println(period/1000 + " " + timeDiff + " " +
			// (sleepTime/1000000));
			if (sleepTime > 0) {// Il reste du temps pour dormir
				try {
					Thread.sleep(sleepTime / 1000000L);// nano -> ms
				} catch (final InterruptedException e) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else {// sleepTime <=0, on a calculé plus qu'une période
				excess -= sleepTime;// enregistre le temps en trop
				overSleepTime = 0L;
				if (++noDelay >= NO_DELAY_PER_YIELD) {
					Thread.yield();// permet à un autre Thread de s'exécuter
					noDelay = 0;
				}
			}
			// Si le jeu est trop lent, on actualise les UPS indépendament des
			// FPS pour garder le rythme.
			int skips = 0;
			while ((excess > period) && (skips > MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate();
				if (printStats) {
					gameStats.addFrameSkipped();
				}
				skips++;
			}
			if (printStats) {
				gameStats.reportStats();
			}

			beforeTime = System.nanoTime();
		}

		System.exit(0);
	}

	public void gameUpdate() {
		// update the game
		abstractGame.update();
	}

	// use double buffer rendering
	public void gameRender() {
		// render the game
		if (dbImage == null) {
			dbImage = createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			dbg = dbImage.getGraphics();
		}

		// clear the background
		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);

		// draw game elements
		abstractGame.render(dbg);

		// Draw the sceen
		// dbg.setColor(Color.RED);
		// dbg.fillRect(PWIDTH/4, PHEIGHT/5, PWIDTH/4, PHEIGHT/5);

	}

	public void defaultGameOverMessage(final Graphics g) {
		final String msg = "Game Over";
		g.setColor(Color.BLACK);
		final Font font = new Font("Arial", Font.BOLD, 40);
		g.setFont(font);
		g.drawString(msg,
				(getWidth() - g.getFontMetrics().stringWidth(msg)) / 2,
				getHeight() / 2);
	}

	// public void paintComponent(Graphics g) {
	// super.paintComponent(g);
	// if(dbImage != null) {
	// g.drawImage(dbImage, 0, 0, null);
	// }
	// }

	public void paintScreen() {
		Graphics g;
		try {
			g = getGraphics();
			if ((g != null) && (dbImage != null)) {
				g.drawImage(dbImage, 0, 0, null);
			}
			Toolkit.getDefaultToolkit().sync(); // sync the display ont some
			// systems
			g.dispose();
		} catch (final Exception e) {
			System.out.println("Graphics context error:" + e);
		}
	}

	public void gameKeyPressed() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				final int keyCode = e.getKeyCode();
				abstractGame.keyPressed(keyCode);
				if ((keyCode == KeyEvent.VK_ESCAPE)
						|| (keyCode == KeyEvent.VK_Q)
						|| (keyCode == KeyEvent.VK_END)
						|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
					running = false;
				}

				if (keyCode == KeyEvent.VK_O) {
					gameOver = true;
				}
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				final int keyCode = e.getKeyCode();
				abstractGame.keyReleased(keyCode);
			}
		});
	}

	public void gameMousePressed() {
		addMouseListener(new MouseAdapter() {// Souris
			@Override
			public void mousePressed(final MouseEvent e) {
				// testPress(e.getX(), e.getY());
				abstractGame.mousePressed(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				abstractGame.mouseReleased(e.getX(), e.getY());
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
				abstractGame.mouseMoved(e.getX(), e.getY());
			}
		});
	}

	private void testPress(final int x, final int y) {
		// est-ce important pour le jeu ?
		if (!isPaused && !gameOver) {
			// faire quelque chose
		}
	}

	public static int getPeriod() {
		return period;
	}

	public static int getPwidth() {
		return PWIDTH;
	}

	public static int getPheight() {
		return PHEIGHT;
	}

}
