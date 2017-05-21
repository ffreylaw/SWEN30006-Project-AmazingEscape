package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class DeadEndHandler {
	
	private DeadEndAction currentAction;
	
	public DeadEndHandler() {
		currentAction = null;
	}
	
	public boolean checkDeadEnd(Coordinate currentPosition, HashMap<Coordinate, MapTile> currentView) {
		
		return false;
	}
	
	public void handle(MyAIController controller, float delta) {
		if (currentAction == null) {
			currentAction = chooseAction(new Coordinate(controller.getPosition()), controller.getView());
		} else {
			currentAction.action(controller, delta);
			if (controller.getState() != MyAIController.State.DEAD_END) {
				currentAction = null;
			}
		}
	}
	
	private DeadEndAction chooseAction(Coordinate currentPosition, HashMap<Coordinate, MapTile> currentView) {
		return null;
	}

}
