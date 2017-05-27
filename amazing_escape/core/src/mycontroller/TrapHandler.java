package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
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
	RelativeDirection firstTurnDir;
	RelativeDirection secondTurnDir;

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
		
		String pos = controller.getPosition();

		MapTile tile1 = getTileAhead(1, controller, pos);
		MapTile tile2 = getTileAhead(2, controller, pos);
		MapTile tile3  = getTileAhead(3, controller, pos);

		if(getTileName(tile1).equals("Wall")) {  // wall in front
			movReverse(controller);
			needLaneChange = true;
		} else if(getTileName(tile2).equals("Wall")) {  // wall at 2 tile away
			if(getTileName(tile1).equals("Grass")) {  // grass in front
				movReverse(controller);
				needLaneChange = true;
			} else {  // no grass in front
				if(canChangeLane()) {
					changeLane(controller, delta);
					needLaneChange = false;
				} else {
					movReverse(controller);
					needLaneChange = true;
				}
			}
		} else if(getTileName(tile3).equals("Wall")) {  // wall at 3 tile away
			if(getTileName(tile2).equals("Grass")) {  // grass at 2 tile away
				if(getTileName(tile1).equals("Grass")) {  // grass in front
					movReverse(controller);
					needLaneChange = true;
				} else {   // no grass in front
					if(canChangeLane()) {
						changeLane(controller, delta);
						needLaneChange = false;
					} else {
						movReverse(controller);
						needLaneChange = true;
					}
				}
			} else {  // no grass at two 2 tile away
				if(getTileName(tile1).equals("Grass")) {  // grass in front
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
				if(canChangeLane() && !getTileName(tile1).equals("Grass")) {  // no grass in front and can change lane
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
				// apply first turn direction
				if(firstTurnDir.equals(RelativeDirection.LEFT)) {
					controller.turnLeft(delta);
				} else {  // right
					controller.turnRight(delta);
				}
			} else {
				turningFirst = false;  // finish first turning
				// adjust dir
				if(firstTurnDir.equals(RelativeDirection.LEFT)) {
					((MyAIController)controller).adjustLeft(firstTurnTargetDir, delta);
				} else {  // right
					((MyAIController)controller).adjustRight(firstTurnTargetDir, delta);
				}
			}
		} else if(turningSecond) {
			if(!controller.getOrientation().equals(carOri)) {
				// apply opposite direction to last turn direction
				if(secondTurnDir.equals(RelativeDirection.LEFT)) {
					controller.turnLeft(delta);
				} else {  // right
					controller.turnRight(delta);
				}
			} else {
				turningSecond = false;
				changingLane = false;  // finish changing lane
				// adjust dir
				if(secondTurnDir.equals(RelativeDirection.LEFT)) {
					((MyAIController)controller).adjustLeft(firstTurnTargetDir, delta);
				} else {  // right
					((MyAIController)controller).adjustRight(firstTurnTargetDir, delta);
				}
			}
		} else {

			// get this tile
			HashMap<Coordinate,MapTile> currentView = controller.getView();
			Coordinate currentPosition = new Coordinate(controller.getPosition());
			MapTile thisTile = currentView.get(currentPosition);

			// increment changedLane number if tile changed
			if(!thisTile.equals(lastTile)) {  // cross a tile
				changedLaneNum++;
			}

			if(changedLaneNum >= needChangeNum) {
				turningSecond = true;
			} else {
				movForward(controller);
			}
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
			int score = calculator.calcLaneScore(controller, i, this);
			if(score < bestLaneScore) {
				bestLaneNum = i;
				bestLaneScore = score;
			}
		}
		// move to best lane
		changeToLane(controller, delta, bestLaneNum);
	}

	public MapTile getTileAhead(int numAhead, CarController controller, String pos) {
		HashMap<Coordinate,MapTile> currentView = controller.getView();
		Coordinate currentPosition = new Coordinate(pos);
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
			int score = calculator.calcLaneScore(controller, i, this);
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
	
	public String getTileName(MapTile tile) {
		if(tile.getName().equals("Trap")) {
			if(tile instanceof GrassTrap) {
				return "Grass";
			} else if(tile instanceof MudTrap) {
				return "Mud";
			} else if(tile instanceof LavaTrap) {
				return "Lava";
			}
		}
		return tile.getName();
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
		turningFirst = true;
		turningSecond = false;
		carOri = controller.getOrientation();
		if(laneNum < 0) {  // left
			firstTurnDir = RelativeDirection.LEFT;
			secondTurnDir = RelativeDirection.RIGHT;
			firstTurnTargetDir = getOri(carOri, true);
		} else {
			firstTurnDir = RelativeDirection.RIGHT;
			secondTurnDir = RelativeDirection.LEFT;
			firstTurnTargetDir = getOri(carOri, false);
		}	
	}

	private Direction getOri(Direction currentDir, boolean left) {  // left or right of current direction
		switch(currentDir) {
		case EAST:
			if(left) {
				return Direction.NORTH;
			} else {
				return Direction.SOUTH;
			}
		case NORTH:
			if(left) {
				return Direction.WEST;
			} else {
				return Direction.EAST;
			}
		case SOUTH:
			if(left) {
				return Direction.EAST;
			} else {
				return Direction.WEST;
			}
		case WEST:
			if(left) {
				return Direction.SOUTH;
			} else {
				return Direction.NORTH;
			}
		}
		return null;  // will never return null
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
