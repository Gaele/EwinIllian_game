package statesObjects;

import messages.Message;

public abstract class State<T> {

	private T t;

	/**
	 * Executed when the state is entered
	 * 
	 * @param t
	 *            the entity
	 */
	public abstract void enter(T t);

	/**
	 * Each update step
	 * 
	 * @param t
	 *            the entity
	 */
	public abstract void execute(T t);

	/**
	 * Executed when the state is exited
	 * 
	 * @param t
	 *            the entity
	 */
	public abstract void exit(T t);

	public abstract boolean onMessage(T e, final Message m);

}
