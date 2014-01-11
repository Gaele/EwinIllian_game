package statesObjects.cellStates;

import map.Cell;
import messages.Message;
import statesObjects.State;
import entities.EntityManager;

public class StateCellGrava extends State<Cell> {

	private volatile static StateCellGrava uniqueInstance;

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

	public static StateCellGrava getInstance() {
		if (uniqueInstance == null) {
			synchronized (EntityManager.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new StateCellGrava();
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
