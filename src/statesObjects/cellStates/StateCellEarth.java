package statesObjects.cellStates;

import map.Cell;
import messages.Message;
import statesObjects.State;
import entities.EntityManager;

public class StateCellEarth extends State<Cell> {

	private volatile static StateCellEarth uniqueInstance;

	@Override
	public void enter(final Cell t) {

	}

	@Override
	public void execute(final Cell t) {

	}

	@Override
	public void exit(final Cell t) {

	}

	public static StateCellEarth getInstance() {
		if (uniqueInstance == null) {
			synchronized (EntityManager.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new StateCellEarth();
				}
			}
		}
		return uniqueInstance;
	}

	@Override
	public boolean onMessage(final Cell e, final Message m) {
		return false;
	}
}
