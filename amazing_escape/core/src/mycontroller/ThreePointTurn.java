package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class ThreePointTurn implements DeadEndAction {
	
	private int point;
	
	public ThreePointTurn() {
		this.point = 1;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		switch (point) {
		case 1:
			applyFirstPoint(controller, controller.getOrientation(), delta);
			break;
		case 2:
			applySecondPoint(controller, controller.getOrientation(), delta);
			break;
		case 3:
			applyThirdPoint(controller, controller.getOrientation(), delta);
			break;
		}
	}
	
	private boolean checkEndPoint(Coordinate currentPosition, HashMap<Coordinate, MapTile> currentView) {
		return false;
	}
	
	private void applyFirstPoint(MyAIController controller, WorldSpatial.Direction orientation, float delta) {
		
	}
	
	private void applySecondPoint(MyAIController controller, WorldSpatial.Direction orientation, float delta) {
		
	}
	
	private void applyThirdPoint(MyAIController controller, WorldSpatial.Direction orientation, float delta) {
		
	}

}
