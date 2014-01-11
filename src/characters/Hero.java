package characters;

import java.awt.Graphics;
import java.awt.Rectangle;

import map.Cell;
import map.MapEwin;
import map.MapManager;
import messages.Message;
import sprite.Sprite;
import sprite.SpriteEwin;
import statesObjects.StateMachine;
import statesObjects.heroStates.StayHero;
import entities.Entity;
import game.Game;

public class Hero extends Entity {

	private final int cellX, cellY;
	final int lambda = 10;

	// private final int MAX_MUGGET = 5;
	private final int FOOD_LOW = 100;

	private final StateMachine<Hero> stateMachine;
	private final MapEwin map;
	private final Game game;
	private final Sprite sprite;
	// private final int moneyInBank;
	private int foodInPocket;
	private int fatigue;
	private final float masse = 0.1f;

	private final int STAY = 0;
	private final int LEFT = 1;
	private final int UP = 2;
	private final int RIGHT = 3;
	private final int DOWN = 4;
	// 0:stay, 1:left, 2:up, 3:right, 4:down
	// private int direction;
	// 0:stay, 1:left, 2:up
	private int horizontalDirection;
	// 0:stay, 3:right, 4:down
	private int verticalDirection;
	// if the character were previously going right
	private boolean oldRight;

	private final Long lastUpdate;

	private Long previousTime;

	// private final Long timeBetween2updates = 1000000000L;

	public Hero(final Game g, final String name, final int x, final int y) {
		super(name);
		cellX = x;
		cellY = y;

		game = g;
		map = game.getMap();
		sprite = new SpriteEwin(game, this, x * map.getSizeX()
		/* + map.getWorldOffsetX() */, y * map.getSizeY()
		/* + map.getWorldOffsetY() */, map.getSizeX(), map.getSizeY(), name,
				"Zero_court_gauche");
		horizontalDirection = STAY;
		verticalDirection = STAY;
		oldRight = false;
		// locationType = Location.house;
		// moneyInBank = 10;
		foodInPocket = 1000;
		fatigue = 0;

		stateMachine = new StateMachine<Hero>(this);
		stateMachine.setCurrentState(StayHero.getInstance());
		// stateMachine.setGlobalState(MinerGlobalState.instance());
		lastUpdate = System.nanoTime();
	}

	@Override
	public void updateSpeed() {
		sprite.updateSpriteSpeed();
	}

	@Override
	public void handleCollision() {
		final Rectangle r = sprite.getMyRectangle();

		// y check
		final int newYLoc = (int) (r.getY() + sprite.getDy());
		final int newXLoc = (int) (r.getX() + sprite.getDx());

		final int topRowWeTouch = newYLoc / map.getSizeY();
		final int downRowWeTouch = (int) ((newYLoc + r.getHeight()) / map
				.getSizeY());
		final int leftColumnWeTouch = (newXLoc / map.getSizeX());
		final int rightColumnWeTouch = (int) ((newXLoc + r.getWidth()) / map
				.getSizeX());

		if (sprite.getDy() < 0) {
			// check we don't pass over the floor
			if (newYLoc < 0) {
				// go under the bottom of the map
				sprite.setDy(0);
			} else if (MapManager.stopPlayer(map.get(topRowWeTouch,
					leftColumnWeTouch).getType())
					|| MapManager.stopPlayer(map.get(topRowWeTouch,
							rightColumnWeTouch).getType())) {
				// go through the ground
				sprite.setDy(0);
			}
		} else if (sprite.getDy() > 0) {
			// check we don't pass through the ground
			if (downRowWeTouch >= map.getHeight()) {
				// go under the bottom of the map
				sprite.setDy(0);
			} else if ((newXLoc >= 0)
					&& (rightColumnWeTouch < map.getWidth())
					&& (MapManager.stopPlayer(map.get(downRowWeTouch,
							leftColumnWeTouch).getType()) || MapManager
							.stopPlayer(map.get(downRowWeTouch,
									rightColumnWeTouch).getType()))) {
				// go through the ground
				sprite.setDy(0);
			}
		} // else, no movement

		if (sprite.getDx() < 0) {
			// don't go through the walls on the left
			if (newXLoc < 0) {
				// go under the bottom of the map
				sprite.setDx(0);
			} else if (MapManager.stopPlayer(map.get(topRowWeTouch,
					leftColumnWeTouch).getType())
			// || MapManager.stopPlayer(map.get(downRowWeTouch,
			// leftColumnWeTouch).getType())
			) {
				// go through the ground
				sprite.setDx(0);
			}
		} else if (sprite.getDx() > 0) {
			// don't go through the walls on the right
			// System.out.println("col" + rightColumnWeTouch + "map: "
			// + map.getWidth());
			if (rightColumnWeTouch >= map.getWidth()) {
				// go under the bottom of the map
				sprite.setDx(0);
			} else if (MapManager.stopPlayer(map.get(topRowWeTouch,
					rightColumnWeTouch).getType())
			// || MapManager.stopPlayer(map.get(downRowWeTouch,
			// rightColumnWeTouch).getType())
			) {
				// go through the ground
				sprite.setDx(0);
			}
		} // else, no movement
	}

	@Override
	public void render(final Graphics g) {
		if ((horizontalDirection != STAY) && !sprite.isLooping()) {
			sprite.loopImage(game.getProgramPeriod(),
					game.getPeriodBetween2Images(), 2);
		}
		sprite.drawSprite(g);
	}

	public void increaseFatigue() {
		fatigue++;
	}

	public void decreaseFatigue() {
		fatigue--;
	}

	public boolean thirsty() {
		return foodInPocket > FOOD_LOW;
	}

	public void decreaseFood() {
		foodInPocket--;
	}

	// GETTERS

	@Override
	public String getName() {
		return name;
	}

	// SETTERS

	@Override
	public boolean handleMessage(final Message m) {
		return stateMachine.handleMessage(m);
	}

	public void goUp() {
		sprite.setDy(-17);
		verticalDirection = UP;
		// sprite.stopLooping();
	}

	public void goLeft() {
		horizontalDirection = LEFT;
		sprite.setDx(-5);
		oldRight = false;
	}

	public void goRight() {
		horizontalDirection = RIGHT;
		sprite.setDx(5);
		oldRight = true;
	}

	public void goDown() {
		verticalDirection = DOWN;
	}

	public void breakCell() {
		final Rectangle r = sprite.getMyRectangle();
		// System.out.println("r.getX: " + r.getX() + ", r.getY(): " + r.getY()
		// + ", r.getWidth(): " + r.getWidth() + ", r.getHeight(): "
		// + r.getHeight());
		final Rectangle middle = new Rectangle(
				(int) (r.getX() + (r.getWidth() / 2)),
				(int) (r.getY() + (r.getHeight() / 2)));
		final Rectangle cellToConsider = new Rectangle(0, 0);
		if (verticalDirection == UP) {
			cellToConsider.height = -map.getSizeY();
		} else if (verticalDirection == DOWN) {
			cellToConsider.height = map.getSizeY();
		} else if (oldRight) {
			// right
			cellToConsider.width = map.getSizeX();
		} else {
			// left
			cellToConsider.width = -map.getSizeX();
		}

		// System.out.println("x:" + (middle.width + cellToConsider.width)
		// + ", y:" + (middle.height + cellToConsider.height));
		// System.out.println("middle_x:" + middle.width + ", y:" +
		// middle.height);
		// System.out.println("delta_x:" + cellToConsider.height + ", delta_y:"
		// + cellToConsider.height);

		final int cellRow = (middle.height + cellToConsider.height)
				/ map.getSizeY();
		final int cellColumn = (middle.width + cellToConsider.width)
				/ map.getSizeX();
		// System.out.println("row:" + cellRow + ", column:" + cellColumn);
		final Cell c = map.get(cellRow, cellColumn);
		if (c == null) {
			return;
		}
		// System.out.println("row:" + c.getRow() + ", column:" + c.getCol());
		c.breakCell();
	}

	public void horizontalStay() {
		sprite.stopLooping();
		horizontalDirection = STAY;
		sprite.setDx(0);
	}

	public void verticalStay() {
		verticalDirection = STAY;
	}

	public int getHorizontalDirection() {
		return horizontalDirection;
	}

	public int getVerticalDirection() {
		return verticalDirection;
	}

	public boolean isUp() {
		sprite.setDy(0);
		return verticalDirection == UP;
	}

	public boolean isDown() {
		return verticalDirection == DOWN;
	}

	public boolean isStay() {
		return (horizontalDirection == STAY) && (verticalDirection == STAY);
	}

	public boolean isLeft() {
		return horizontalDirection == LEFT;
	}

	public boolean isRight() {
		return horizontalDirection == RIGHT;
	}

	public boolean getOldRight() {
		return oldRight;
	}

	public boolean getOldLeft() {
		return !oldRight;
	}

	public float getDy() {
		return sprite.getDy();
	}

	@Override
	public void updatePosition() {
		sprite.updateSpritePosition();

	}

	public float getLocx() {
		return sprite.getLocx();
	}

	public float getLocy() {
		return sprite.getLocy();
	}
}
