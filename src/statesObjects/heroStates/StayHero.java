package statesObjects.heroStates;

import messages.Message;
import messages.TypeMessage;
import statesObjects.State;
import characters.Hero;
import entities.EntityManager;

public class StayHero extends State<Hero> {

	private volatile static StayHero uniqueInstance;

	@Override
	public void enter(final Hero t) {

	}

	@Override
	public void execute(final Hero t) {
		// sleeping

	}

	@Override
	public void exit(final Hero t) {

	}

	public static StayHero getInstance() {
		if (uniqueInstance == null) {
			synchronized (EntityManager.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new StayHero();
				}
			}
		}
		return uniqueInstance;
	}

	@Override
	public boolean onMessage(final Hero e, final Message m) {
		switch (m.getMessage()) {
		case TypeMessage.STEW_READY:

		}
		return false;
	}
}
