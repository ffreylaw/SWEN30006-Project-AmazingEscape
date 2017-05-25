package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class ReverseOut implements DeadEndAction {
	
	private boolean isReverseTurningLeft;
	private boolean isReverseTurningRight;
	
	private boolean flag = false;
	
	public ReverseOut() {
		isReverseTurningLeft = false;
		isReverseTurningRight = false;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		
		readjust(controller, delta);
		
		if ((controller.getVelocity() < 3) && !flag) {
			controller.applyForwardAcceleration();
			return;
		} else {
			flag = true;
		}
		
		if (isReverseTurningRight){
			applyReverseRight(controller, delta);
		} else if (isReverseTurningLeft){
			if (!controller.checkFollowingWall(controller.getOrientation(), currentView)) {
				applyReverseLeft(controller, delta);
			} else {
				isReverseTurningLeft = false;
			}
		} else if (controller.checkFollowingWall(controller.getOrientation(), currentView)) {
			if (controller.getVelocity() < 3) {
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
	
	private void readjust(MyAIController controller, float delta) {
		if(controller.getLastTurnDirection() != null){
			if (!isReverseTurningRight && controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.RIGHT)){
				controller.adjustRight(controller.getOrientation(), delta);
			} else if (!isReverseTurningLeft && controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.LEFT)){
				controller.adjustLeft(controller.getOrientation(), delta);
			}
		}
	}
	
	private void applyReverseLeft(MyAIController controller, float delta) {
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
	
	private void applyReverseRight(MyAIController controller, float delta) {
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

}
