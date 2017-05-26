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
			if(tile1.getName().equals("Grass")) {  // grass in front
//				movReverse(controller);
//				needLaneChange = true;
			} else {  // no grass in front
				if(canChangeLane()) {
					changeLane(controller, delta);
					needLaneChange = false;
				} else {
//					movReverse(controller);
//					needLaneChange = true;
				}
			}
		} else if(tile3.getName().equals("Wall")) {  // wall at 3 tile away
			if(tile2.getName().equals("Grass")) {  // grass at 2 tile away
				if(tile1.getName().equals("Grass")) {  // grass in front
//					movReverse(controller);
//					needLaneChange = true;
				} else {   // no grass in front
					if(canChangeLane()) {
						changeLane(controller, delta);
						needLaneChange = false;
					} else {
//						movReverse(controller);
//						needLaneChange = true;
					}
				}
			} else {  // no grass at two 2 tile away
				if(tile1.getName().equals("Grass")) {  // grass in front
					if(needLaneChange == true) {
						movReverse(controller);
					} else {
						movForward(controller);
					}
				} else {  // no grass in front
					if(needLaneChange == true) {
						if(canChangeLane()) {
							changeLane(controller, delta);
							needLaneChange = false;
						} else {
							movReverse(controller);
						}
					} else {
						calcScoreMov(controller, delta);
					}
				}
			}
		} else {  // no wall ahead
			if(needLaneChange == true) {
				if(canChangeLane() && !tile1.getName().equals("Grass")) {  // no grass in front and can change lane
					changeLane(controller, delta);
					needLaneChange = false;
				} else {
					movReverse(controller);
				}
			} else {
				calcScoreMov(controller, delta);
			}
		}
	}
	
//	public void handle(CarController controller, float delta) {
//		MapTile tile1 = getTileAhead(1, controller);
//		MapTile tile2 = getTileAhead(2, controller);
//		MapTile tile3  = getTileAhead(3, controller);
//		
//		if(tile1.getName().equals("Wall")) {  // wall in front
//			movReverse(controller);
//			needLaneChange = true;
//		} else if(tile2.getName().equals("Wall")) {  // wall at 2 tile away			
//			if(canChangeLane(controller)) {
//				changeLane(controller, delta);
//				needLaneChange = false;
//			} else {
//				movReverse(controller);
//				needLaneChange = true;
//			}
//		} else if(tile3.getName().equals("Wall")) {  // wall at 3 tile away
//			if(tile2.getName().equals("Grass")) {  // grass at 2 tile away				
//				if(canChangeLane(controller)) {
//					changeLane(controller, delta);
//					needLaneChange = false;
//				} else {
//					movReverse(controller);
//					needLaneChange = true;
//				}
//			} else {  // no grass at two 2 tile away			
//				if(needLaneChange == true) {  // reached the tile at 2 tiles away already, did not find route
//					if(canChangeLane(controller)) {
//						changeLane(controller, delta);
//						needLaneChange = false;
//					} else {
//						movReverse(controller);
//					}
//				} else {  // go to that tile first
//					movForward(controller);
//				}
//			}
//		} else {  // no wall ahead			
//			if(needLaneChange == true) {  // reached tiles ahead already, did not find route
//				changeLane(controller, delta);
//				needLaneChange = false;
//			} else {
//				movForward(controller);
//			}
//		}
//	}
	
	private boolean canChangeLane() {
		// check if can change lane
		
		// check turn turning left
		
		// check turning right
		return false;
	}
	
	private void changeLane(CarController controller, float delta) {
		// 
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
		return null;  // will never return null
	}
	
	private void calcScoreMov(CarController controller, float delta) {
		// calculate score to take a movement
		
		// check change lane score
		
		// check staying on current lane score
		
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
