package statesObjects;

import messages.Message;

public class StateMachine<T> {

	T owner;
	State<T> currentState;
	State<T> previousState;
	State<T> globalState;

	public StateMachine(final T o) {
		owner = o;
		currentState = null;
		previousState = null;
		globalState = null;
	}

	public void update() {
		if (globalState != null) {
			globalState.execute(owner);
		}
		if (currentState != null) {
			currentState.execute(owner);
		}
	}

	public void changeState(final State<T> newState) {
		if (newState == null) {
			System.out.println("Error : Trying to change a null state");
			return;
		}

		// keep previous state
		previousState = currentState;

		// call exit method of existing state
		currentState.exit(owner);

		// change state
		currentState = newState;

		currentState.enter(owner);
	}

	public boolean handleMessage(final Message m) {
		// if the current State is valid and can handle the message
		if ((currentState != null) && currentState.onMessage(owner, m)) {
			return true;
		}
		// else, if a global state has been implemented, send the message to the
		// global state
		if ((globalState != null) && globalState.onMessage(owner, m)) {
			return true;
		}
		// the message is not relevant for this state
		return false;
	}

	public Boolean isInState(final State<T> s) {
		return currentState.equals(s);
	}

	public void revertToPreviousState() {
		changeState(previousState);
	}

	public State<T> getCurrentState() {
		return currentState;
	}

	public State<T> getPreviousState() {
		return currentState;
	}

	public State<T> getGlobalState() {
		return currentState;
	}

	public void setCurrentState(final State<T> s) {
		currentState = s;
	}

	public void setPreviousState(final State<T> s) {
		previousState = s;
	}

	public void setGlobalState(final State<T> s) {
		globalState = s;
	}

}
