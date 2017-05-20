package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class UTurn implements DeadEndAction {
	
	private boolean isTurningLeftU;
	private boolean isTurningRightU;
	
	public UTurn() {
		isTurningLeftU = false;
		isTurningRightU = false;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		
	}
	
	private void applyLeftUTurn(MyAIController controller, WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.WEST)){
				controller.turnLeft(delta);
			}
			break;
		case NORTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				controller.turnLeft(delta);
			}
			break;
		case SOUTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				controller.turnLeft(delta);
			}
			break;
		case WEST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
				controller.turnLeft(delta);
			}
			break;
		default:
			break;
		}
	}
	
	private void applyRightUTurn(MyAIController controller, WorldSpatial.Direction orientation, float delta) {
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
