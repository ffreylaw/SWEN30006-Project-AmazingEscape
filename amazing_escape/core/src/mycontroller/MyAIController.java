/* 
 * SWEN30006 Software Modelling and Design
 * Project C - Amazing Escape
 * 
 * Group 66
 * Author: Pei-Yun Sun <667816>
 * Author: Geoffrey Law <759218>
 * Author: HangChen Xiong <753057>
 * 
 */

package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController {
	
	public enum State {
		NONE, FOLLOWING_WALL, DEAD_END, TRAP
	};
	
	private State state;
	private BasicHandler basicHandler;
	private DeadEndHandler deadEndHandler;
	private TrapHandler trapHandler;
	
	public static final float CAR_SPEED = 3;
	public static final int WALL_SENSITIVITY = 2;
	public static final int EAST_THRESHOLD = 3;
	

	public MyAIController(Car car) {
		super(car);
		this.basicHandler = new BasicHandler();
		this.deadEndHandler = new DeadEndHandler();
		this.trapHandler = new TrapHandler();
		this.state = State.NONE;
	}
	

	@Override
	public void update(float delta) {
		checkDirectionChange();
		
		if(!basicHandler.isTurningLeft() && 
		   !basicHandler.isTurningRight() && 
		   !trapHandler.isChangingLane()) {
			updateState();
		}
		
		switch (this.state) {
		case NONE: 		 	 basicHandler.handleNone(this, delta);			break;
		case FOLLOWING_WALL: basicHandler.handleFollowingWall(this, delta);	break;
		case DEAD_END: 		 deadEndHandler.handle(this, delta);			break;
		case TRAP:			 trapHandler.handle(this, delta);				break;
		}
	}
	
	private void updateState() {
		if(state == State.NONE || state == State.FOLLOWING_WALL) {
			if(deadEndHandler.checkDeadEnd(this)) {  // dead end detected
				state = State.DEAD_END;
			} else if(trapHandler.checkTrap(this)) {  // traps detected
				state = State.TRAP;
			}
		} else if(state == State.TRAP) {
			if(!trapHandler.checkTrap(this)) {  // no more traps
				state = State.NONE;
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
	
	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	private void checkDirectionChange() {
		if (basicHandler.getPreviousDirection() == null) {
			basicHandler.setPreviousDirection(getOrientation());
		} else {
			if (basicHandler.getPreviousDirection() != getOrientation()) {
				if (basicHandler.isTurningLeft()) {
					basicHandler.setTurningLeft(false);
				}
				if (basicHandler.isTurningRight()) {
					basicHandler.setTurningRight(false);
				}
				basicHandler.setPreviousDirection(getOrientation());
			}
		}
	}
	
	public WorldSpatial.RelativeDirection getLastTurnDirection() {
		return basicHandler.getLastTurnDirection();
	}

	public void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
		basicHandler.setLastTurnDirection(lastTurnDirection);
	}
	
	public State getState() {
		return state;
	}
	
	public void changeState(State state) {
		this.state = state;
	}
	
}
