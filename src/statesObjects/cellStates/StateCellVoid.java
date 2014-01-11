package statesObjects.cellStates;

import map.Cell;
import messages.Message;
import statesObjects.State;
import entities.EntityManager;

public class StateCellVoid extends State<Cell> {

	private volatile static StateCellVoid uniqueInstance;

	@Override
	public void enter(final Cell t) {

	}

	@Override
	public void execute(final Cell t) {
		// sleeping

	}

	@Override
	public void exit(final Cell t) {

	}

	public static StateCellVoid getInstance() {
		if (uniqueInstance == null) {
			synchronized (EntityManager.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new StateCellVoid();
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
