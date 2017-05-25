package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class UTurn implements DeadEndAction {
	
	private WorldSpatial.Direction targetOrientation;
	
	private boolean isDone;
	
	public UTurn() {
		targetOrientation = null;
		isDone = false;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		WorldSpatial.Direction currentOrientation = controller.getOrientation();
		
		if (isDone) {
			controller.applyBrake();
		} else {
			if (controller.getVelocity() < 1) {
				controller.applyForwardAcceleration();
			} else {
				applyUTurn(controller, delta);
//				if (targetOrientation == null) {
//					switch (currentOrientation) {
//					case EAST:
//						targetOrientation = WorldSpatial.Direction.WEST;
//						break;
//					case SOUTH:
//						targetOrientation = WorldSpatial.Direction.NORTH;
//						break;
//					case WEST:
//						targetOrientation = WorldSpatial.Direction.EAST;
//						break;
//					case NORTH:
//						targetOrientation = WorldSpatial.Direction.SOUTH;
//						break;
//					}
//				} else if (currentOrientation != targetOrientation) {
//					applyUTurn(controller, delta);
//				} else {
//					targetOrientation = null;
//					isDone = true;
//				}
			}
		}
	}
	
	
	private void applyUTurn(MyAIController controller, float delta) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		switch(orientation){
		case EAST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.WEST)){
				controller.turnRight(delta);
			}
			break;
		case NORTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				controller.turnRight(delta);
			}
			break;
		case SOUTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				controller.turnRight(delta);
			}
			break;
		case WEST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
				controller.turnRight(delta);
			}
			break;
		default:
			break;
		}
	}

}
