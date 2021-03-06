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

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class ReverseOut implements DeadEndAction {
	
	private boolean isReverseTurningLeft;
	private boolean isReverseTurningRight;
	private boolean isFollowingWall;
	private boolean isDecelerated;
	private boolean isReversing;
	
	private WorldSpatial.Direction previousDirection = null;
	
	/**
	 * Construct an object of ReverseOut action
	 */
	public ReverseOut() {
		isReverseTurningLeft = false;
		isReverseTurningRight = false;
		isFollowingWall = false;
		isDecelerated = false;
		isReversing = false;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		checkDirectionChange(controller);
		
		// if not decelerated
		if (controller.getVelocity() >= 0.1 && !isDecelerated) {
			controller.applyReverseAcceleration();
		} else {
			isDecelerated = true;
		}
		
		if (isDecelerated) {
			// if not reversing
			if (controller.getVelocity() < 1.2 && !isReversing) {
				controller.applyReverseAcceleration();
			} else {
				isReversing = true;
			}
			
			if (isReversing) {
				if (!isFollowingWall) {
					if (controller.getVelocity() < 1) {
						controller.applyReverseAcceleration();
					}
					if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
						controller.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
						applyReverseLeft(controller, delta);
					}
					if (controller.checkSouth(currentView)) {
						if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
							controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
							applyReverseRight(controller, delta);
						} else {
							isFollowingWall = true;
						}
					}
				} else {
					readjust(controller, delta);
		
					if (isReverseTurningRight) {
						applyReverseRight(controller, delta);
					} else if (isReverseTurningLeft) {
						if (!checkFollowingWall(controller)) {
							applyReverseLeft(controller, delta);
						} else {
							isReverseTurningLeft = false;
						}
					} else if (checkFollowingWall(controller)) {
						if (controller.getVelocity() < 2.25) {
							controller.applyReverseAcceleration();
						}
						if (checkWallBehind(controller)) {
							controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
							isReverseTurningRight = true;	
						}
					} else {
						controller.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
						isReverseTurningLeft = true;
					}
				}
			}
		}
	}
	
	/**
	 * Re-adjust orientation perpendicularly
	 * @param controller
	 * @param delta
	 */
	private void readjust(MyAIController controller, float delta) {
		if(controller.getLastTurnDirection() != null){
			if (!isReverseTurningRight && controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.RIGHT)){
				controller.adjustRight(controller.getOrientation(), delta);
			} else if (!isReverseTurningLeft && controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.LEFT)){
				controller.adjustLeft(controller.getOrientation(), delta);
			}
		}
	}
	
	/**
	 * Apply left turn when reversing
	 * @param controller
	 * @param delta
	 */
	private void applyReverseLeft(MyAIController controller, float delta) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		switch (orientation) {
		case EAST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				controller.turnLeft(delta);
			}
			break;
		case NORTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
				controller.turnLeft(delta);
			}
			break;
		case SOUTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
				controller.turnLeft(delta);
			}
			break;
		case WEST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				controller.turnLeft(delta);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Apply right turn when reversing
	 * @param controller
	 * @param delta
	 */
	private void applyReverseRight(MyAIController controller, float delta) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		switch (orientation) {
		case EAST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)) {
				controller.turnRight(delta);
			}
			break;
		case NORTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.WEST)) {
				controller.turnRight(delta);
			}
			break;
		case SOUTH:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.EAST)) {
				controller.turnRight(delta);
			}
			break;
		case WEST:
			if (!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
				controller.turnRight(delta);
			}
			break;
		default:
			break;
		}	
	}
	
	/**
	 * Check if there is a wall behind
	 * @param controller
	 * @return boolean
	 */
	private boolean checkWallBehind(MyAIController controller){
		WorldSpatial.Direction orientation = controller.getOrientation();
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		switch (orientation) {
		case EAST:
			return controller.checkWest(currentView);
		case NORTH:
			return controller.checkSouth(currentView);
		case SOUTH:
			return controller.checkNorth(currentView);
		case WEST:
			return controller.checkEast(currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Check whether the vehicle is following wall
	 * @param controller
	 * @return boolean
	 */
	private boolean checkFollowingWall(MyAIController controller) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		switch (orientation) {
		case EAST:
			return controller.checkSouth(currentView);
		case NORTH:
			return controller.checkEast(currentView);
		case SOUTH:
			return controller.checkWest(currentView);
		case WEST:
			return controller.checkNorth(currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Check direction change
	 * @param controller
	 */
	private void checkDirectionChange(MyAIController controller) {
		if (previousDirection == null) {
			previousDirection = controller.getOrientation();
		} else {
			if (previousDirection != controller.getOrientation()) {
				if (isReverseTurningLeft) {
					isReverseTurningLeft = false;
				}
				if (isReverseTurningRight) {
					isReverseTurningRight = false;
				}
				previousDirection = controller.getOrientation();
			}
		}
	}

}
