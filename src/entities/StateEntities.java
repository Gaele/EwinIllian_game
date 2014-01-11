package entities;

import statesObjects.StateMachine;

public abstract class StateEntities extends Entity {

	protected StateMachine sm;

	public StateEntities(final String name) {
		super(name);
		// sm = new StateMachine(this); // parameter state machine according to
		// the needs

		// sm.setCurrentState(STATE01.Instance()); // if using singleton pattern
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateSpeed() {
		sm.update();
	}

	public StateMachine getStateMachine() {
		return sm;
	}

	// public StateMachine<Personnage> getStateMachine() {}

}
