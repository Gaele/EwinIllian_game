package entities;

import java.awt.Graphics;

import messages.Message;

public abstract class Entity {

	private final int id;
	protected String name;

	// private static int nextValidId = 0;

	public Entity(final String name) {
		this.name = name;
		// name need to be defined before
		id = EntityManager.getInstance().registerEntity(this);
	}

	public abstract void updateSpeed();

	public abstract void handleCollision();

	public abstract void updatePosition();

	public abstract void render(Graphics g);

	public abstract boolean handleMessage(final Message m);

	// GETTERS

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	// SETTERS

}
