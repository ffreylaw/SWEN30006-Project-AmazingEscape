package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class TrapHandler {
	
	public TrapHandler() {
	}
	
	public boolean checkTrap(Coordinate currentPosition, HashMap<Coordinate, MapTile> currentView) {
		
		return false;
	}
	
	public void handle(MyAIController controller, float delta) {
		
		controller.changeState(MyAIController.State.NONE);
	}
	
	private void applyChangeLeftLane(int n, WorldSpatial.Direction orientation, float delta) {
		
	}
	
	private void applyChangeRightLane(int n, WorldSpatial.Direction orientation, float delta) {
		
	}
	
}
