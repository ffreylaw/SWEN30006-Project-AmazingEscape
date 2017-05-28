package mycontroller;

import java.util.HashMap;

import mycontroller.MyAIController.State;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class BasicHandler {
	
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private WorldSpatial.RelativeDirection lastTurnDirection = null;
	private WorldSpatial.Direction previousDirection = null;
	
	public BasicHandler() {
		
	}
	
	public void handleNone(MyAIController controller, float delta) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		
		if (checkFollowingWall(controller)) {
			controller.changeState(State.FOLLOWING_WALL);
		}
		
		checkDirectionChange(controller);
		
		if (controller.getVelocity() < MyAIController.CAR_SPEED) {
			controller.applyForwardAcceleration();
		}
		// Turn towards the north
		if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
			lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
			applyLeftTurn(controller, delta);
		}
		if (controller.checkNorth(currentView)){
			// Turn right until we go back to east!
			if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				applyRightTurn(controller, delta);
			} else {
				controller.changeState(State.FOLLOWING_WALL);	//test
			}
		}
	}
	
	public void handleFollowingWall(MyAIController controller, float delta) {
		checkDirectionChange(controller);
		
//		if (!isTurningRight && !isTurningLeft) {
//			if (deadEndHandler.checkDeadEnd(this)) changeState(State.DEAD_END);
//		}
		
		// Readjust the car if it is misaligned.
		readjust(controller, delta);
		
		if (isTurningRight){
			applyRightTurn(controller, delta);
		} else if (isTurningLeft){
			if (controller.getVelocity() < 2.9) {
				controller.applyForwardAcceleration();
			}
			// Apply the left turn if you are not currently near a wall.
			if (!checkFollowingWall(controller)) {
				applyLeftTurn(controller, delta);
			} else {
				isTurningLeft = false;
			}
		} else if (checkFollowingWall(controller)) {
			// Try to determine whether or not the car is next to a wall.
			
			// Maintain some velocity
			if (controller.getVelocity() < MyAIController.CAR_SPEED) {
				controller.applyForwardAcceleration();
			}
			// If there is wall ahead, turn right!
			if (checkWallAhead(controller)) {
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
	private void readjust(MyAIController controller, float delta) {
		if(lastTurnDirection != null){
			if (!isTurningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)){
				controller.adjustRight(controller.getOrientation(), delta);
			} else if (!isTurningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)){
				controller.adjustLeft(controller.getOrientation(), delta);
			}
		}
	}
	
	/**
	 * Turn the car counter clock wise (think of a compass going counter clock-wise)
	 */
	public void applyLeftTurn(MyAIController controller, float delta) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		switch (orientation) {
		case EAST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				controller.turnLeft(delta);
			}
			break;
		case NORTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
				controller.turnLeft(delta);
			}
			break;
		case SOUTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
				controller.turnLeft(delta);
			}
			break;
		case WEST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				controller.turnLeft(delta);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Turn the car clock wise (think of a compass going clock-wise)
	 */
	public void applyRightTurn(MyAIController controller, float delta) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		switch (orientation) {
		case EAST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				controller.turnRight(delta);
			}
			break;
		case NORTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
				controller.turnRight(delta);
			}
			break;
		case SOUTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
				controller.turnRight(delta);
			}
			break;
		case WEST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				controller.turnRight(delta);
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
	private void checkDirectionChange(MyAIController controller) {
		if (previousDirection == null) {
			previousDirection = controller.getOrientation();
		} else {
			if (previousDirection != controller.getOrientation()) {
				if (isTurningLeft) {
					isTurningLeft = false;
				}
				if (isTurningRight) {
					isTurningRight = false;
				}
				previousDirection = controller.getOrientation();
			}
		}
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkFollowingWall(MyAIController controller) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		switch (orientation) {
		case EAST:
			return controller.checkNorth(currentView);
		case NORTH:
			return controller.checkWest(currentView);
		case SOUTH:
			return controller.checkEast(currentView);
		case WEST:
			return controller.checkSouth(currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(MyAIController controller){
		WorldSpatial.Direction orientation = controller.getOrientation();
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		switch (orientation) {
		case EAST:
			return controller.checkEast(currentView);
		case NORTH:
			return controller.checkNorth(currentView);
		case SOUTH:
			return controller.checkSouth(currentView);
		case WEST:
			return controller.checkWest(currentView);
		default:
			return false;
		}
	}

	public boolean isTurningLeft() {
		return isTurningLeft;
	}

	public boolean isTurningRight() {
		return isTurningRight;
	}

	public WorldSpatial.RelativeDirection getLastTurnDirection() {
		return lastTurnDirection;
	}

	public void setLastTurnDirection(WorldSpatial.RelativeDirection lastTurnDirection) {
		this.lastTurnDirection = lastTurnDirection;
	}
	
}
