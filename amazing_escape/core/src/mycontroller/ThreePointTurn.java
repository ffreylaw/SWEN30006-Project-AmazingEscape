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
			if (checkEndPoint(controller)) {
				applyFirstPoint(controller, delta);
			} else {
				point++;
			}
			break;
		case 2:
			if (checkEndPoint(controller)) {
				applySecondPoint(controller, delta);
			} else {
				point++;
			}
			break;
		case 3:
			if (checkEndPoint(controller)) {
				applyThirdPoint(controller, delta);
			} else {
				point = 1;
				controller.setState(MyAIController.State.FOLLOWING_WALL);
			}
			break;
		}
	}
	
	private boolean checkEndPoint(MyAIController controller) {
		return false;
	}
	
	private void applyFirstPoint(MyAIController controller, float delta) {
		
	}
	
	private void applySecondPoint(MyAIController controller, float delta) {
		
	}
	
	private void applyThirdPoint(MyAIController controller, float delta) {
		
	}

}
