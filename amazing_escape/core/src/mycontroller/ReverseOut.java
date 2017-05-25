package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class ReverseOut implements DeadEndAction {
	
	private boolean isReverseTurningLeft;
	private boolean isReverseTurningRight;
	
	public ReverseOut() {
		isReverseTurningLeft = false;
		isReverseTurningRight = false;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		
	}
	
	private void applyReverseLeft(MyAIController controller, float delta) {
		
	}
	
	private void applyReverseRight(MyAIController controller, float delta) {
		
	}
	
	private boolean checkWallBehind(MyAIController controller, WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
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
