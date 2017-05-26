package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class ReverseOut implements DeadEndAction {
	
	private boolean isReverseTurningLeft;
	private boolean isReverseTurningRight;
	private boolean isFollowingWall;
	
	private WorldSpatial.Direction previousDirection = null;
	
	private boolean flag = false;
	private boolean flag2 = false;
	
	public ReverseOut() {
		isReverseTurningLeft = false;
		isReverseTurningRight = false;
		isFollowingWall = false;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		
		checkDirectionChange(controller);
		
		if ((controller.getVelocity() < 2) && !flag) {
			System.out.println("-2");
			controller.applyForwardAcceleration();
			return;
		} else {
			flag = true;
		}
		if (flag && !flag2) {
			System.out.println("-1");
			if ((controller.getVelocity() < 2) && !flag2) {
				controller.applyReverseAcceleration();
			} else {
				flag2 = true;
			}
		}
//		flag2 = true;
		if (flag2) {
			System.out.println(controller.getOrientation());
		if(!isFollowingWall){
			System.out.println("1");
			if(controller.getVelocity() < 1){
				System.out.println("2");
				controller.applyReverseAcceleration();
			}
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				System.out.println("3");
				controller.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
				applyReverseLeft(controller, delta);
			}
			if(controller.checkSouth(currentView)){
				System.out.println("4");
				if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
					System.out.println("5");
					controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
					applyReverseRight(controller, delta);
				}
				else{
					System.out.println("6");
					isFollowingWall = true;
				}
			}
		} else {
			
			readjust(controller, delta);
			
			if (isReverseTurningRight){
				System.out.println("7");
				applyReverseRight(controller, delta);
			} else if (isReverseTurningLeft){
				System.out.println("8");
				if (!checkFollowingWall(controller)) {
					System.out.println("9");
					applyReverseLeft(controller, delta);
				} else {
					System.out.println("10");
					isReverseTurningLeft = false;
				}
			} else if (checkFollowingWall(controller)) {
				System.out.println("11");
				if (controller.getVelocity() < 3) {
					System.out.println("12");
					controller.applyReverseAcceleration();
				}
				if (checkWallBehind(controller)) {
					System.out.println("13");
					controller.setLastTurnDirection(WorldSpatial.RelativeDirection.RIGHT);
					isReverseTurningRight = true;	
				}
			} else {
				System.out.println("14");
				controller.setLastTurnDirection(WorldSpatial.RelativeDirection.LEFT);
				isReverseTurningLeft = true;
			}
		}
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
