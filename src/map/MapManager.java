package map;

import statesObjects.State;
import statesObjects.cellStates.StateCellEarth;
import statesObjects.cellStates.StateCellGaz;
import statesObjects.cellStates.StateCellGrava;
import statesObjects.cellStates.StateCellMetal;
import statesObjects.cellStates.StateCellVoid;
import statesObjects.cellStates.StateCellWater;

public class MapManager {

	public static final int VOID = 0;
	public static final int EARTH = 1;
	public static final int WATER = 2;
	public static final int GAZ = 3;
	public static final int METAL = 4;
	public static final int GRAVA = 5;
	public static final int HERO = 50;

	public static boolean stopPlayer(final int type) {
		// 0 / 50 => decors
		// 50 / 100 => personnages
		// 100 / 150 => objets
		switch (type) {
		case VOID:
			return false;
		case EARTH:
			return true;
		case WATER:
			return false;
		case GAZ:
			return false;
		case METAL:
			return true;
		case GRAVA:
			return true;
		case HERO:
			return false;
		default:
			return true;
		}
	}

	public static String returnImage(final int type) {
		// 0 / 50 => decors
		// 50 / 100 => personnages
		// 100 / 150 => objets
		switch (type) {
		case VOID:
			return "";
		case EARTH:
			return "terre";
		case WATER:
			return "eau";
		case GAZ:
			return "gaz";
		case METAL:
			return "metal";
		case GRAVA:
			return "grava";
		case HERO:
			return "mario_bas";
		default:
			return "objectif";
		}
	}

	/**
	 * True if the type is a map type (earth, water, ...) false if this is an
	 * other type (character, monster...)
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isInMap(final int type) {
		// 0 / 50 => decors
		// 50 / 100 => personnages
		// 100 / 150 => objets
		if ((type < 50) || ((type >= 100) && (type < 150))) {
			return true;
		} else {
			return false;
		}
	}

	public static State<Cell> typeToCellState(final int type) {
		switch (type) {
		case VOID:
			return StateCellVoid.getInstance();
		case EARTH:
			return StateCellEarth.getInstance();
		case WATER:
			return StateCellWater.getInstance();
		case GAZ:
			return StateCellGaz.getInstance();
		case METAL:
			return StateCellMetal.getInstance();
		case GRAVA:
			return StateCellGrava.getInstance();
		default:
			return null;
		}
	}
}
