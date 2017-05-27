package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;
import world.WorldSpatial.RelativeDirection;

public class TrapHandler {

	boolean needLaneChange;
	CalculateScore calculator;

	// for lane change
	boolean changingLane;
	int needChangeNum;
	int changedLaneNum;
	RelativeDirection lastTurnDir;
	MapTile lastTile;
	boolean turningFirst;  // turning first time
	boolean turningSecond;  // turning second time
	Direction carOri;  // car direction before changing lane
	Direction firstTurnTargetDir;



	public TrapHandler() {
		calculator = new CalculateScore();
		needLaneChange = false;
		changingLane = false;
	}

	public void handle(CarController controller, float delta) {
		if(changingLane) {
			DoLaneChange(controller, delta);
			return;
		}

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

	private void DoLaneChange(CarController controller, float delta) {
		if(turningFirst) {
			if(!controller.getOrientation().equals(firstTurnTargetDir)) {
				// continue to apply last turn direction
				if(lastTurnDir.equals(RelativeDirection.LEFT)) {
					controller.turnLeft(delta);
				} else {  // right
					controller.turnRight(delta);
				}
			} else {
				// 
			}
		} else if(turningSecond) {
			if(!controller.getOrientation().equals(carOri)) {

			}
		} else {
			// move forward, adjust orientation
		}
	}


	private boolean canChangeLane() {
		// check if can change lane

		for(int i=-3; i<=3; i++) {
			if(i==0) {  // skip current lane
				continue;
			}
		}

		return false;
	}

	private void changeLane(CarController controller, float delta) {

		// find best lane
		int bestLaneNum = 0;
		int bestLaneScore = 1000;  // the lower the better
		for(int i=-3; i<=3; i++) {
			if(i==0) {  // skip current lane
				continue;
			}
			int score = calculator.calcLaneScore(controller, i);
			if(score < bestLaneScore) {
				bestLaneNum = i;
				bestLaneScore = score;
			}
		}
		// move to best lane
		changeToLane(controller, delta, bestLaneNum);
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
		// find best lane
		int bestLaneNum = 0;
		int bestLaneScore = 1000;  // the lower the better
		for(int i=-3; i<=3; i++) {
			if(i==0) {  // skip current lane
				continue;
			}
			int score = calculator.calcLaneScore(controller, i);
			if(score < bestLaneScore) {
				bestLaneNum = i;
				bestLaneScore = score;
			}
		}

		// move to best lane
		if(bestLaneNum == 0) {  // stay at current lane
			movForward(controller);
		} else {  // best lane
			changeToLane(controller, delta, bestLaneNum);
		}
	}

	private void changeToLane(CarController controller, float delta, int laneNum) {
		// set last tile
		HashMap<Coordinate,MapTile> currentView = controller.getView();
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		lastTile = currentView.get(currentPosition);

		// set other variables for changing lane
		changingLane = true;
		needChangeNum = Math.abs(laneNum);
		changedLaneNum = 0;
		if(laneNum < 0) {  // left
			lastTurnDir = RelativeDirection.LEFT;
		} else {
			lastTurnDir = RelativeDirection.RIGHT;
		}	
		turningFirst = true;
		turningSecond = false;
		carOri = controller.getOrientation();
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
