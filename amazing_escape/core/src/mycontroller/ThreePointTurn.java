package mycontroller;

import mycontroller.MyAIController.State;
import world.WorldSpatial;

public class ThreePointTurn implements DeadEndAction {
	
	private int point;
	
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private boolean isDone = false;
	private boolean speedFlag = false;
	
	private WorldSpatial.Direction targetOrientation = null;
	
	public ThreePointTurn() {
		this.point = 1;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		switch (point) {
		case 1:
			applyFirstPoint(controller, delta);
			break;
		case 2:
			applySecondPoint(controller, delta);
			break;
		case 3:
			controller.changeState(State.NONE);
			this.point = 1;
			break;
		}
	}
	
	
	private void applyFirstPoint(MyAIController controller, float delta) {
		WorldSpatial.Direction currentOrientation = controller.getOrientation();
		
		readjust(controller, delta);
		
		if (isDone) {
			controller.applyReverseAcceleration();
			if (controller.getVelocity() < 0.1) {
				isDone = false;
				speedFlag = false;
				targetOrientation = null;
				point++;
				return;
			}
		}
		
		if (controller.getVelocity() >= 0.5 && !speedFlag) {
			controller.applyReverseAcceleration();
		} else {
			speedFlag = true;
		}
		
		if (speedFlag && !isDone) {
			if (controller.getVelocity() < 0.5) {
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
	
	private void applySecondPoint(MyAIController controller, float delta) {
		WorldSpatial.Direction currentOrientation = controller.getOrientation();
		
		readjust(controller, delta);
		
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
		
		if (controller.getVelocity() >= 0.5 && !speedFlag) {
			controller.applyReverseAcceleration();
		} else {
			speedFlag = true;
		}
		
		if (speedFlag && !isDone) {
			if (controller.getVelocity() < 0.5) {
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
	
	private void readjust(MyAIController controller, float delta) {
		if(controller.getLastTurnDirection() != null){
			if (!isTurningRight && controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.RIGHT)){
				controller.adjustRight(controller.getOrientation(), delta);
			} else if (!isTurningLeft && controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.LEFT)){
				controller.adjustLeft(controller.getOrientation(), delta);
			}
		}
	}

}
