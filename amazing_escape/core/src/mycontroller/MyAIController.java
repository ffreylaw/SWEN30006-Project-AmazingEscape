package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController {
	
	public enum State {
		NONE, FOLLOWING_WALL, DEAD_END, TRAP
	};
	
	private State state;
	private DeadEndHandler deadEndHandler;
	private TrapHandler trapHandler;
	
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private WorldSpatial.RelativeDirection lastTurnDirection = null;
	private WorldSpatial.Direction previousDirection = null;
	
	public static final float CAR_SPEED = 3;
	public static final int WALL_SENSITIVITY = 2;
	public static final int EAST_THRESHOLD = 3;
	

	public MyAIController(Car car) {
		super(car);
		this.deadEndHandler = new DeadEndHandler();
		this.trapHandler = new TrapHandler();
		this.state = State.NONE;
	}

	@Override
	public void update(float delta) {
		checkDirectionChange();
		if(!isTurningLeft && !isTurningRight) {
			updateState();
		}
		
		Coordinate currentPosition = new Coordinate(getPosition());
		
		System.out.println("x = " + currentPosition.x + ", y = " + currentPosition.y);
		System.out.println("state " + state);
		
		switch (this.state) {
		case NONE: 		 	 handleNone(delta);						break;
		case FOLLOWING_WALL: handleFollowingWall(delta);			break;
		case DEAD_END: 		 deadEndHandler.handle(this, delta);	break;
		case TRAP:			 trapHandler.handle(this, delta);		break;
		}
	}
	
	private void updateState() {
		if(state == State.NONE || state == State.FOLLOWING_WALL) {
			if(checkDeadEnd()) {  // dead end detected
				state = State.DEAD_END;
			} else if(checkTrap()) {  // traps detected
				state = State.TRAP;
			}
		} else if(state == State.TRAP) {
			if(!checkTrap()) {  // no more traps
				state = State.NONE;
			}
		}
	}
	
	/** Check if any traps at the front view of the car */
	private boolean checkTrap() {
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate currentPosition = new Coordinate(getPosition());
		switch (getOrientation()) {
		case EAST:
			for(int i=1; i<=3; i++) {  // right
				for(int j=-3; j<=3; j++) {  // top to bot
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
					if(tile instanceof TrapTile) {  // trap detected
						return true;
					}
				}
			}
			break;
		case NORTH:
			for(int i=-3; i<=3; i++) {  // left to right
				for(int j=-3; j<=1; j++) {  // top
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
					if(tile instanceof TrapTile) {  // trap detected
						return true;
					}
				}
			}
			break;
		case SOUTH:
			for(int i=-3; i<=3; i++) {  // left to right
				for(int j=1; j<=3; j++) {  // bot
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
					if(tile instanceof TrapTile) {  // trap detected
						return true;
					}
				}
			}
			break;
		case WEST:
			for(int i=-3; i<=-1; i++) {  // left
				for(int j=-3; j<=3; j++) {  // top to bot
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
					if(tile instanceof TrapTile) {  // trap detected
						return true;
					}
				}
			}
			break;
		}
		return false;
	}
	
//	/** Check if any traps ahead */
//	private boolean checkTrap() {
//		HashMap<Coordinate, MapTile> currentView = getView();
//		Coordinate currentPosition = new Coordinate(getPosition());
//		switch(getOrientation()) {
//		case EAST:
//			for(int i=1; i<=3; i++) {  // right
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
//				if(tile instanceof TrapTile) {  // trap detected
//					return true;
//				}
//			}
//			break;
//		case NORTH:
//			for(int j=1; j<=3; j++) {  // top
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
//				if(tile instanceof TrapTile) {  // trap detected
//					return true;
//				}
//			}
//			break;
//		case SOUTH:
//			for(int j=-3; j<=1; j++) {  // bot
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
//				if(tile instanceof TrapTile) {  // trap detected
//					return true;
//				}
//			}
//			break;
//		case WEST:
//			for(int i=-3; i<=-1; i++) {  // left
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
//				if(tile instanceof TrapTile) {  // trap detected
//					return true;
//				}
//			}
//			break;
//		}
//		return false;
//	}
	
	private boolean checkDeadEnd() {
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate currentPosition = new Coordinate(getPosition());
		boolean flag = false;
		switch (getOrientation()) {
		case EAST:
			for (int i = 1; i <= 3; i++) {
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if (tile.getName().equals("Wall")) {
					isWallAhead = true;
					for (int j = -1; j >= -i; j--) {
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i-1, currentPosition.y+j));
							if (tile.getName().equals("Wall") && checkNorth(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		case NORTH:
			for (int j = 1; j <= 3; j++) {
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
				if (tile.getName().equals("Wall")) {
					isWallAhead = true;
					for (int i = 1; i <= j; i++) {
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j-1));
							if (tile.getName().equals("Wall") && checkWest(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		case SOUTH:
			for (int j = -1; j >= -3; j--) {
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
				if (tile.getName().equals("Wall")) {
					isWallAhead = true;
					for (int i = -1; i >= j; i--) {
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j+1));
							if (tile.getName().equals("Wall") && checkEast(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		case WEST:
			for (int i = -1; i >= -3; i--) {
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if (tile.getName().equals("Wall")) {
					isWallAhead = true;
					for (int j = 1; j <= Math.abs(i); j++) {
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i+1, currentPosition.y+j));
							if (tile.getName().equals("Wall") && checkSouth(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		}
		return flag;
	}
	
	public void changeState(State state) {
		this.state = state;
	}
	
	public void handleNone(float delta) {
		HashMap<Coordinate, MapTile> currentView = getView();
		
		if (checkFollowingWall(getOrientation(), currentView)) {
			changeState(State.FOLLOWING_WALL);
		}
		
		checkDirectionChange();
		
		if (getVelocity() < CAR_SPEED) {
			applyForwardAcceleration();
		}
		// Turn towards the north
		if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
			System.out.println("1");
			lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
			applyLeftTurn(getOrientation(), delta);
		}
		if (checkNorth(currentView)){
			// Turn right until we go back to east!
			if (!getOrientation().equals(WorldSpatial.Direction.EAST)) {
				System.out.println("2");
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				applyRightTurn(getOrientation(), delta);
			} else {
				changeState(State.FOLLOWING_WALL);	//test
			}
		}
	}
	
	public void handleFollowingWall(float delta) {
		HashMap<Coordinate, MapTile> currentView = getView();
		
		checkDirectionChange();
		
		if (!isTurningRight && !isTurningLeft) {
			if (checkDeadEnd()) changeState(State.DEAD_END);
		}
		
		// Readjust the car if it is misaligned.
		readjust(lastTurnDirection, delta);
		
		if (isTurningRight){
			applyRightTurn(getOrientation(), delta);
		} else if (isTurningLeft){
			if (getVelocity() < 2.9) {
				applyForwardAcceleration();
			}
			// Apply the left turn if you are not currently near a wall.
			if (!checkFollowingWall(getOrientation(), currentView)) {
				applyLeftTurn(getOrientation(), delta);
			} else {
				isTurningLeft = false;
			}
		} else if (checkFollowingWall(getOrientation(), currentView)) {
			// Try to determine whether or not the car is next to a wall.
			
			// Maintain some velocity
			if (getVelocity() < CAR_SPEED) {
				applyForwardAcceleration();
			}
			// If there is wall ahead, turn right!
			if (checkWallAhead(getOrientation(), currentView)) {
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				isTurningRight = true;				
			}
		} else {
			// This indicates that I can do a left turn if I am not turning right
			lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
			isTurningLeft = true;
		}
	}
	
	/**
	 * Readjust the car to the orientation we are in.
	 * @param lastTurnDirection
	 * @param delta
	 */
	private void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta) {
		if(lastTurnDirection != null){
			if (!isTurningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)){
				adjustRight(getOrientation(), delta);
			} else if (!isTurningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)){
				adjustLeft(getOrientation(), delta);
			}
		}
	}
	
	/**
	 * Try to orient myself to a degree that I was supposed to be at if I am
	 * misaligned.
	 */
	public void adjustLeft(WorldSpatial.Direction orientation, float delta) {
		
		switch(orientation){
		case EAST:
			if (getAngle() > WorldSpatial.EAST_DEGREE_MIN + EAST_THRESHOLD) {
				turnRight(delta);
			}
			break;
		case NORTH:
			if (getAngle() > WorldSpatial.NORTH_DEGREE) {
				turnRight(delta);
			}
			break;
		case SOUTH:
			if (getAngle() > WorldSpatial.SOUTH_DEGREE) {
				turnRight(delta);
			}
			break;
		case WEST:
			if (getAngle() > WorldSpatial.WEST_DEGREE) {
				turnRight(delta);
			}
			break;
		default:
			break;
		}
	}

	public void adjustRight(WorldSpatial.Direction orientation, float delta) {
		switch (orientation) {
		case EAST:
			if (getAngle() > WorldSpatial.SOUTH_DEGREE && getAngle() < WorldSpatial.EAST_DEGREE_MAX) {
				turnLeft(delta);
			}
			break;
		case NORTH:
			if (getAngle() < WorldSpatial.NORTH_DEGREE) {
				turnLeft(delta);
			}
			break;
		case SOUTH:
			if (getAngle() < WorldSpatial.SOUTH_DEGREE) {
				turnLeft(delta);
			}
			break;
		case WEST:
			if (getAngle() < WorldSpatial.WEST_DEGREE) {
				turnLeft(delta);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Turn the car counter clock wise (think of a compass going counter clock-wise)
	 */
	public void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
		switch (orientation) {
		case EAST:
			if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				turnLeft(delta);
			}
			break;
		case NORTH:
			if (!getOrientation().equals(WorldSpatial.Direction.WEST)) {
				turnLeft(delta);
			}
			break;
		case SOUTH:
			if (!getOrientation().equals(WorldSpatial.Direction.EAST)) {
				turnLeft(delta);
			}
			break;
		case WEST:
			if (!getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				turnLeft(delta);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Turn the car clock wise (think of a compass going clock-wise)
	 */
	public void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
		switch (orientation) {
		case EAST:
			if (!getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				turnRight(delta);
			}
			break;
		case NORTH:
			if (!getOrientation().equals(WorldSpatial.Direction.EAST)) {
				turnRight(delta);
			}
			break;
		case SOUTH:
			if (!getOrientation().equals(WorldSpatial.Direction.WEST)) {
				turnRight(delta);
			}
			break;
		case WEST:
			if (!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				turnRight(delta);
			}
			break;
		default:
			break;
		}	
	}
	
	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	private void checkDirectionChange() {
		if (previousDirection == null) {
			previousDirection = getOrientation();
		} else {
			if (previousDirection != getOrientation()) {
				if (isTurningLeft) {
					isTurningLeft = false;
				}
				if (isTurningRight) {
					isTurningRight = false;
				}
				previousDirection = getOrientation();
			}
		}
	}
	
	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	public boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch (orientation) {
		case EAST:
			return checkEast(currentView);
		case NORTH:
			return checkNorth(currentView);
		case SOUTH:
			return checkSouth(currentView);
		case WEST:
			return checkWest(currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		switch (orientation) {
		case EAST:
			return checkNorth(currentView);
		case NORTH:
			return checkWest(currentView);
		case SOUTH:
			return checkEast(currentView);
		case WEST:
			return checkSouth(currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= WALL_SENSITIVITY; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.getName().equals("Wall")){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= WALL_SENSITIVITY; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.getName().equals("Wall")){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= WALL_SENSITIVITY; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.getName().equals("Wall")){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= WALL_SENSITIVITY; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.getName().equals("Wall")){
				return true;
			}
		}
		return false;
	}
	
	public boolean isTurningLeft() {
		return this.isTurningLeft;
	}
	
	public boolean isTurningRight() {
		return this.isTurningRight;
	}

	public void setTurningLeft(boolean isTurningLeft) {
		this.isTurningLeft = isTurningLeft;
	}

	public void setTurningRight(boolean isTurningRight) {
		this.isTurningRight = isTurningRight;
	}
	
	public WorldSpatial.RelativeDirection getLastTurnDirection() {
		return lastTurnDirection;
	}

	public void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
		this.lastTurnDirection = lastTurnDirection;
	}

	public WorldSpatial.Direction getPreviousDirection() {
		return previousDirection;
	}

	public void setPreviousDirection(WorldSpatial.Direction previousDirection) {
		this.previousDirection = previousDirection;
	}

	public State getState() {
		return state;
	}
	
}
