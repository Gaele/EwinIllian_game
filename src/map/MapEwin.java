package map;

import game.Game;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MapEwin {

	// private static MapEwin map = null;

	private int worldOffsetX = 0, worldOffsetY = 0;
	private ArrayList<ArrayList<Cell>> tab;
	private final LinkedList<Cell> toUpdate;
	private final LinkedList<Cell> toAdd;
	private final LinkedList<Cell> toRemove;
	private int nbX, nbY;

	private final int sizeX, sizeY;
	private final String DIR_NAME = "Maps";
	private final Game game;

	// public static MapEwin getInstance(final Game g) {
	// if (map == null) {
	// map = new MapEwin(g);
	// }
	// return map;
	// }

	public MapEwin(final Game g) {
		this(g, 10, 10, 50, 50);
	}

	public MapEwin(final Game g, final int nbx, final int nby, final int sizex,
			final int sizey) {
		game = g;
		nbX = nbx;
		nbY = nby;
		sizeX = sizex;
		sizeY = sizey;
		tab = new ArrayList<ArrayList<Cell>>(nbx);
		for (int i = 0; i < nbx; i++) {
			final ArrayList<Cell> nextList = new ArrayList<Cell>(nby);
			for (int j = 0; j < nby; j++) {
				nextList.add(new Cell(game, 1, j, i));
			}
			tab.add(nextList);
		}
		toUpdate = new LinkedList<Cell>();
		toAdd = new LinkedList<Cell>();
		toRemove = new LinkedList<Cell>();
	}

	public synchronized void addToUpdate(final Cell c) {
		toAdd.add(c);
	}

	public synchronized void remoteToUpdate(final Cell c) {
		toRemove.remove(c);
	}

	public synchronized void update() {
		// new cells to update
		final Iterator<Cell> itAdd = toAdd.iterator();
		while (itAdd.hasNext()) {
			toUpdate.add(itAdd.next());
		}
		toAdd.clear();

		// cells not to update anymore
		final Iterator<Cell> itRemove = toRemove.iterator();
		while (itRemove.hasNext()) {
			toUpdate.remove(itRemove.next());
		}
		toRemove.clear();

		// update cells
		final Iterator it = toUpdate.iterator();
		Cell c;
		while (it.hasNext()) {
			c = (Cell) it.next();
			c.updateSpeed();
		}
	}

	public Cell get(final int w, final int h) {
		if ((w >= 0) && (w < nbY) && (h >= 0) && (h < nbX)) {
			return tab.get(w).get(h);
		} else {
			return null;
		}

	}

	public void set(final Cell c, final int w, final int h) {
		tab.get(w).set(h, c);
	}

	public void draw(final Graphics g, final int x, final int y) {
		worldOffsetX = x;
		worldOffsetY = y;
		// for (int i = 0; i < nbX; i++) {
		// for (int j = 0; j < nbY; j++) {
		// tab.get(j)
		// .get(i)
		// .draw(g, worldOffsetX + i * sizeX,
		// worldOffsetY + j * sizeY, sizeX, sizeY);
		// }
		// }
		int i = 0, j = 0;
		for (final ArrayList<Cell> column : tab) {
			i = 0;
			for (final Cell c : column) {
				c.draw(g, worldOffsetX + i * sizeX, worldOffsetY + j * sizeY,
						sizeX, sizeY);
				i++;
			}
			j++;
		}
	}

	public void loadMap(final String fnm) {
		// initialise the size of the map
		sizeOfMap(fnm);
		final ArrayList<ArrayList<Cell>> mapList = new ArrayList<ArrayList<Cell>>(
				nbY);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fnm));
			String line;
			String[] cellsFileCode;
			ArrayList<Cell> cellsRealCode;
			int j = 0;
			while ((line = br.readLine()) != null) {
				cellsFileCode = line.split(";");
				cellsRealCode = new ArrayList<Cell>(nbX);

				int i;
				for (i = 0; i < cellsFileCode.length; i++) {
					final int type = Integer.parseInt(cellsFileCode[i]);
					if (MapManager.isInMap(type)) {
						final Cell c = new Cell(game, type, j, i);
						cellsRealCode.add(c);
						loadDynamicObjects(c);
					} else {
						game.loadObject(type, i, j);
						cellsRealCode.add(new Cell(game, i, j));
					}
				}
				while (i < nbX) {
					cellsRealCode.add(new Cell(game, MapManager.METAL, i, j));
					i++;
				}
				j++;
				mapList.add(cellsRealCode);
			}
			tab = mapList;
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("map loaded");
	}

	public void loadDynamicObjects(final Cell c) {
		if ((c.getType() == MapManager.GAZ)
				|| (c.getType() == MapManager.WATER)
				|| (c.getType() == MapManager.GRAVA)) {
			addToUpdate(c);
		}
	}

	public void sizeOfMap(final String fileMapName) {
		Integer x = 0, y = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileMapName));
			String line;
			String[] cellsFileCode;
			while ((line = br.readLine()) != null) {
				cellsFileCode = line.split(";");
				final int length = cellsFileCode.length;
				if (x < length) {
					x = length;
				}
				y++;
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		// return new Vector<Integer>(x, y);
		nbX = x;
		nbY = y;
		System.out.println("map size: " + x + ", " + y);
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getWidth() {
		return nbX;
	}

	public int getHeight() {
		return nbY;
	}

	public int getWorldOffsetX() {
		return worldOffsetX;
	}

	public int getWorldOffsetY() {
		return worldOffsetY;
	}

}
