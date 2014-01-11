package map;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import messages.Message;
import entities.Entity;
import game.Game;
import graphicLibrary.ImagesLoader;

public class Cell extends Entity {

	private int type;
	private final int row;
	private final int col;
	private final Game game;
	int pingPongLastDir = 0;

	// private final StateMachine<Cell> stateMachine;

	public static final int MESSAGE_BREAK = 0;
	private boolean broken;

	/**
	 * Initialise a Cell
	 * 
	 * @param g
	 *            the Game instance
	 * @param row
	 *            the number of the row of the cell
	 * @param col
	 *            the number of the column of the cell
	 */
	public Cell(final Game g, final int row, final int col) {
		this(g, 0, row, col);
	}

	/**
	 * Initialise a Cell
	 * 
	 * @param g
	 *            the Game instance
	 * @param t
	 *            the type of the cell
	 * @param row
	 *            the number of the row of the cell
	 * @param col
	 *            the number of the column of the cell
	 */
	public Cell(final Game g, final int t, final int row, final int col) {
		super(row + ";" + col);
		type = t;
		game = g;
		this.row = row;
		this.col = col;
		broken = false;
		// stateMachine = new StateMachine<Cell>(this);
		// stateMachine.setCurrentState(MapManager.typeToCellState(type));
	}

	public int getType() {
		return type;
	}

	public void setType(final int t) {
		type = t;
	}

	public void draw(final Graphics g, final int x, final int y, final int w,
			final int h) {
		final ImagesLoader imsLd = ImagesLoader.getInstance();
		final BufferedImage im = imsLd.getImage(MapManager.returnImage(type));
		if (im != null) {
			g.drawImage(im, x, y, w, h, null);
		}
	}

	public void breakCell() {
		broken = true;
		final MapEwin map = game.getMap();
		map.addToUpdate(this);
	}

	@Override
	public void updateSpeed() {
		final MapEwin map = game.getMap();
		switch (type) {
		case MapManager.EARTH:
			if (broken) {
				type = MapManager.GRAVA;
			}
			break;
		case MapManager.GAZ:
			break;
		case MapManager.GRAVA:
			final Cell underCell = map.get(getRow() + 1, getCol());
			if (underCell == null) {
				return;
			}
			if (!MapManager.stopPlayer(underCell.getType())) {
				type = underCell.type;
				underCell.setType(MapManager.GRAVA);
				map.addToUpdate(underCell);
				map.remoteToUpdate(this);
				System.out.println("fall from " + getRow() + "," + getCol()
						+ " to " + underCell.getRow());
			} else {
				// we try to fall randomly on a side
				// We load data here
				Cell overCell = map.get(getRow() - 1, getCol());
				if (overCell == null) {
					overCell = new Cell(game, MapManager.METAL, getRow(),
							getCol() - 1);
				}
				final Cell downLeftCell = map.get(getRow(), getCol() - 1);
				final Cell downRightCell = map.get(getRow(), getCol() + 1);
				final Random r = new Random();
				final Cell[] c = new Cell[2];
				if (r.nextFloat() < 0.5) {
					c[0] = downLeftCell;
					c[1] = downRightCell;
				} else {
					c[1] = downLeftCell;
					c[0] = downRightCell;
				}

				// and we try to fall
				if (overCell.getType() == MapManager.GRAVA) {
					for (final Cell cell : c) {
						if (cell == null) {
							continue;
						}
						if (!MapManager.stopPlayer(cell.getType())) {
							type = cell.getType();
							cell.setType(MapManager.GRAVA);
							map.addToUpdate(cell);
							map.remoteToUpdate(this);
							System.out.println("SIDE from " + getRow() + ","
									+ getCol() + " to " + cell.getRow() + ","
									+ cell.getCol());
							break;
						}
					}
				}
			}
			// we still update those cells
			break;
		case MapManager.HERO:
			break;
		case MapManager.METAL:
			// nothing to do
			break;
		case MapManager.VOID:
			break;
		case MapManager.WATER:
			final Cell underCell2 = map.get(getRow() + 1, getCol());
			if (underCell2 == null) {
				return;
			}
			// fall if you can
			if (!MapManager.stopPlayer(underCell2.getType())
					&& (underCell2.getType() != MapManager.WATER)) {
				type = underCell2.type;
				underCell2.setType(MapManager.WATER);
				map.addToUpdate(underCell2);
				map.remoteToUpdate(this);
				pingPongLastDir = 0; // we don't play ping pong right/left
			} else {
				// if the is water on one side, try to go the other side !
				// We load data here
				Cell overCell = map.get(getRow() - 1, getCol());
				if (overCell == null) {
					overCell = new Cell(game, MapManager.METAL, getRow(),
							getCol() - 1);
				}
				Cell leftCell = map.get(getRow(), getCol() - 1);
				if (leftCell == null) {
					leftCell = new Cell(game, MapManager.METAL, getRow(),
							getCol() - 1);
				}
				Cell rightCell = map.get(getRow(), getCol() + 1);
				if (rightCell == null) {
					rightCell = new Cell(game, MapManager.METAL, getRow(),
							getCol() + 1);
				}
				final Random r = new Random();
				final Cell[] c = new Cell[2];
				c[0] = leftCell;
				c[1] = rightCell;
				int i;
				if (r.nextFloat() < 0.5) {
					i = 0;
				} else {
					i = 1;
				}

				// and we try to fall
				for (int j = 0; j <= 1; j++) {
					final int k = (i + 1) % 2;
					// if the cell on one side is water and that we can go to
					// the other side (which is not water), go !
					// if the cell on one side is not water but that there is
					// water above us, we try to go !
					if (((c[i].getType() == MapManager.WATER) || (!MapManager
							.stopPlayer(overCell.getType()) && (overCell
							.getType() == MapManager.WATER)))
							&& !MapManager.stopPlayer(c[k].getType())
							&& (c[k].getType() != MapManager.WATER)) {
						// if we don't play ping pong
						// if (getCol() - c[k].getCol() != pingPongLastDir % 2)
						// {
						pingPongLastDir = getCol() - c[k].getCol();
						type = c[k].getType();
						c[k].setType(MapManager.WATER);
						map.addToUpdate(c[k]);
						map.remoteToUpdate(this);
						break;
						// }

					}
					i = (i + 1) % 2;
				}
			}
			break;
		}

	}

	@Override
	public void handleCollision() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(final Graphics g) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleMessage(final Message m) {
		// return stateMachine.handleMessage(m);
		return false;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
