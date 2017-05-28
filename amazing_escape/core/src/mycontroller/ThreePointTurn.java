package mycontroller;

import mycontroller.MyAIController.State;
import utilities.Coordinate;
import world.WorldSpatial;

public class ThreePointTurn implements DeadEndAction {
	
	private int point;
	
	private boolean isDone;
	private boolean speedFlag;
	private boolean isBackOne;
	
	private Coordinate previousPosition;
	private WorldSpatial.Direction targetOrientation;
	
	/**
	 * Construct an object of ThreePointTurn action
	 */
	public ThreePointTurn() {
		this.point = 1;
		isDone = false;
		speedFlag = false;
		isBackOne = false;
		previousPosition = null;
		targetOrientation = null;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		switch (point) {
		case 1:
			// forward turn right 90 degree
			applyFirstPoint(controller, delta);
			break;
		case 2:
			// reverse turn left 90 degree
			applySecondPoint(controller, delta);
			break;
		case 3:
			// done
			controller.changeState(State.NONE);
			this.point = 1;
			isDone = false;
			speedFlag = false;
			isBackOne = false;
			previousPosition = null;
			targetOrientation = null;
			break;
		}
	}
	
	/**
	 * Apply forward acceleration, to turn right
	 * @param controller
	 * @param delta
	 */
	private void applyFirstPoint(MyAIController controller, float delta) {
		WorldSpatial.Direction currentOrientation = controller.getOrientation();
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		
		// finalize first point state
		if (isDone) {
			controller.applyReverseAcceleration();
			if (controller.getVelocity() < 0.1) {
				isDone = false;
				speedFlag = false;
				isBackOne = false;
				previousPosition = null;
				targetOrientation = null;
				point++;
				return;
			}
		}
		
		// stop the vehicle
		if (controller.getVelocity() >= 0.1 && !speedFlag) {
			controller.applyBrake();
		} else if (!speedFlag) {
			speedFlag = true;
			previousPosition = new Coordinate(controller.getPosition());
		}
		
		// reverse back one tile
		if (speedFlag && !isBackOne) {
			if (controller.getVelocity() < 0.25) {
				controller.applyReverseAcceleration();
			}
			switch (currentOrientation) {
			case EAST:
				if ((currentPosition.x+1) == (previousPosition.x)) {
					isBackOne = true;
				}
				break;
			case NORTH:
				if ((currentPosition.y+1) == (previousPosition.y)) {
					isBackOne = true;
				}
				break;
			case SOUTH:
				if ((currentPosition.y-1) == (previousPosition.y)) {
					isBackOne = true;
				}
				break;
			case WEST:
				if ((currentPosition.x-1) == (previousPosition.x)) {
					isBackOne = true;
				}
				break;
			default:
				break;
			}
		}
		
		// to forward turn right
		if (speedFlag && isBackOne && !isDone) {
			if (controller.getVelocity() < 0.25) {
				controller.applyForwardAcceleration();
			} else {
				if (targetOrientation == null) {
					switch (currentOrientation) {
					case EAST:
						targetOrientation = WorldSpatial.Direction.SOUTH;
						break;
					case SOUTH:
						targetOrientation = WorldSpatial.Direction.WEST;
						break;
					case WEST:
						targetOrientation = WorldSpatial.Direction.NORTH;
						break;
					case NORTH:
						targetOrientation = WorldSpatial.Direction.EAST;
						break;
					}
				} else if (currentOrientation != targetOrientation) {
					controller.turnRight(delta);
				} else if (currentOrientation == targetOrientation) {
					isDone = true;
				}
			}
		}
	}
	
	/**
	 * Apply reverse acceleration, to turn left
	 * @param controller
	 * @param delta
	 */
	private void applySecondPoint(MyAIController controller, float delta) {
		WorldSpatial.Direction currentOrientation = controller.getOrientation();
		
		// finalize second point state
		if (isDone) {
			controller.applyForwardAcceleration();
			if (controller.getVelocity() < 0.1) {
				isDone = false;
				speedFlag = false;
				targetOrientation = null;
				point++;
				return;
			}
		}
		
		// stop the vehicle
		if (controller.getVelocity() >= 0.1 && !speedFlag) {
			controller.applyBrake();
		} else {
			speedFlag = true;
		}
		
		// to reverse turn left
		if (speedFlag && !isDone) {
			if (controller.getVelocity() < 0.25) {
				controller.applyReverseAcceleration();
			} else {
				if (targetOrientation == null) {
					switch (currentOrientation) {
					case EAST:
						targetOrientation = WorldSpatial.Direction.SOUTH;
						break;
					case SOUTH:
						targetOrientation = WorldSpatial.Direction.WEST;
						break;
					case WEST:
						targetOrientation = WorldSpatial.Direction.NORTH;
						break;
					case NORTH:
						targetOrientation = WorldSpatial.Direction.EAST;
						break;
					}
				} else if (currentOrientation != targetOrientation) {
					controller.turnRight(delta);
				} else if (currentOrientation == targetOrientation) {
					isDone = true;
				}
			}
		}
	}
	
}
