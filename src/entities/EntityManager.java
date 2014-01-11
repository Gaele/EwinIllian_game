package entities;

import java.util.HashMap;

public class EntityManager {

	private volatile static EntityManager uniqueInstance;

	private final HashMap<Integer, Entity> entityMap;
	private final HashMap<String, Integer> entityNameMap;
	private int nextValidId;

	// private HashMap<String, Entity> entityMap;

	private EntityManager() {
		nextValidId = 1;
		entityMap = new HashMap<Integer, Entity>();
		entityNameMap = new HashMap<String, Integer>();
	}

	public static EntityManager getInstance() {
		if (uniqueInstance == null) {
			synchronized (EntityManager.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new EntityManager();
				}
			}
		}
		return uniqueInstance;
	}

	public int registerEntity(final Entity e) {
		if (e == null) {
			System.out.println("e == null");
			return 0;
		}
		entityMap.put(nextValidId, e);

		if (!e.getName().equals("")) {
			entityNameMap.put(e.getName(), nextValidId);
		}
		nextValidId++;
		return nextValidId - 1;
	}

	public Entity getEntityFromId(final int id) {
		return entityMap.get(id);
	}

	/**
	 * Return the id from the name of the Entity. May not work conveniently...
	 * 
	 * @return
	 */
	public int getIdFromName(final String name) {
		return entityNameMap.get(name);
	}

	public void removeEntity(final Entity e) {
		entityMap.remove(e.getId());
	}

}
