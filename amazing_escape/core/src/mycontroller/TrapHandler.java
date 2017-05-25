package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

public class TrapHandler {
	
	boolean needLaneChange;
	
	
	public TrapHandler() {
		needLaneChange = false;
	}
	
	public void handle(CarController controller, float delta) {
		MapTile tile1 = getTileAhead(1, controller);
		MapTile tile2 = getTileAhead(2, controller);
		MapTile tile3  = getTileAhead(3, controller);
		
		if(tile1.getName().equals("Wall")) {  // wall in front
			movReverse(controller);
			needLaneChange = true;
		} else if(tile2.getName().equals("Wall")) {  // wall at 2 tile away
			if(tile1.getName().equals("Grass")) {
				movReverse(controller);
				needLaneChange = true;
			} else {
				changeLane(controller, delta);
				needLaneChange = false;
			}
		} else if(tile3.getName().equals("Wall")) {  // wall at 3 tile away
			if(tile2.getName().equals("Grass")) {
				if(tile1.getName().equals("Grass")) {
					movReverse(controller);
					needLaneChange = true;
				} else {
					changeLane(controller, delta);
					needLaneChange = false;
				}
			} else {
				if(tile1.getName().equals("Grass")) {
					if(needLaneChange == true) {
						movReverse(controller);
						needLaneChange = true;
					} else {
						movForward(controller);
					}
				} else {
					if(needLaneChange == true) {
						changeLane(controller, delta);
						needLaneChange = false;
					} else {
						calcScoreMov(controller, delta);
					}
				}
			}
		} else {  // no wall ahead
			if(needLaneChange == true) {
				changeLane(controller, delta);
				needLaneChange = false;
			} else {
				calcScoreMov(controller, delta);
			}
		}
	}
	
	private void changeLane(CarController controller, float delta) {
	}
	
	
	private MapTile getTileAhead(int numAhead, CarController controller) {
		HashMap<Coordinate,MapTile> currentView = controller.getView();
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		switch(controller.getOrientation()) {
		case EAST:
			return currentView.get(new Coordinate(currentPosition.x+numAhead, currentPosition.y));
		case NORTH:
			return currentView.get(new Coordinate(currentPosition.x, currentPosition.y-numAhead));
		case SOUTH:
			return currentView.get(new Coordinate(currentPosition.x, currentPosition.y+numAhead));
		case WEST:
			return currentView.get(new Coordinate(currentPosition.x-numAhead, currentPosition.y));
		}
		return null;  // will never return true
	}
	
	private void calcScoreMov(CarController controller, float delta) {
		
	}
	
	private void movForward(CarController controller) {
		if(controller.isReversing()) {
			controller.applyBrake();
		} else {
			controller.applyForwardAcceleration();
		}
	}
	
	private void movReverse(CarController controller) {
		if(controller.isReversing()) {
			controller.applyReverseAcceleration();
		} else {
			controller.applyBrake();
		}
	}
}
