package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;
import world.WorldSpatial.RelativeDirection;

public class LaneChanger {
		RelativeDirection firstTurnDir;
		RelativeDirection secondTurnDir;
		
		MapTile turningTile;  // turning point of the second turn during change of lane
		boolean turningFirst;  // turning first time during change of lane
		boolean turningSecond;  // turning second time during change of lane
		Direction carOri;  // car direction before changing lane, target direction of the second turn
		Direction firstTurnTargetDir;  // target direction of the first turn when changing lane
		
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
		
		private void setChangeLane(CarController controller, float delta, int laneNum, TrapHandler handler) {
			// set last tile
			turningTile = handler.getTileAt(1, laneNum, controller, controller.getPosition());
			
			// set other variables for changing lane
			handler.setChangingLane(true);
			turningFirst = true;
			turningSecond = false;
			carOri = controller.getOrientation();
			
			if(laneNum < 0) {  // turn left
				firstTurnDir = RelativeDirection.LEFT;
				secondTurnDir = RelativeDirection.RIGHT;
				firstTurnTargetDir = getOri(carOri, true);
			} else {  // turn right
				firstTurnDir = RelativeDirection.RIGHT;
				secondTurnDir = RelativeDirection.LEFT;
				firstTurnTargetDir = getOri(carOri, false);
			}	
		}
		
		public boolean canChangeLane(CarController controller, TrapHandler handler) {
			// check if can change lane
			for(int i=-3; i<=3; i++) {
				if(i==0) {  // skip current lane
					continue;
				}
				int score = CalculateScore.calcLaneScore(controller, i, handler);
				if(score < CalculateScore.WALL_SCORE) {  // no wall on that lane
					return true;
				}
			}
			return false;
		}
		
		public void changeLane(CarController controller, float delta, TrapHandler handler) {
			// find best lane
			int bestLaneNum = 0;
			int bestLaneScore = 1000;  // the lower the better
			for(int i=-3; i<=3; i++) {
				if(i==0) {  // skip current lane
					continue;
				}
				int score = CalculateScore.calcLaneScore(controller, i, handler);
				if(score < bestLaneScore) {
					bestLaneNum = i;
					bestLaneScore = score;
				}
			}
			// move to best lane
			setChangeLane(controller, delta, bestLaneNum, handler);
		}
		
		public void doLaneChange(CarController controller, float delta, TrapHandler handler) {
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
					
					handler.setChangingLane(false);  // finish changing lane
					// adjust dir
					if(secondTurnDir.equals(RelativeDirection.LEFT)) {
						((MyAIController)controller).adjustLeft(firstTurnTargetDir, delta);
					} else {  // right
						((MyAIController)controller).adjustRight(firstTurnTargetDir, delta);
					}
				}
			} else {  // not turning
				// get this tile
				HashMap<Coordinate,MapTile> currentView = controller.getView();
				Coordinate currentPosition = new Coordinate(controller.getPosition());
				MapTile thisTile = currentView.get(currentPosition);

				// increment changedLane number if tile changed
				if(!thisTile.equals(turningTile)) {  // cross a tile
					handler.movForward(controller);
				} else {
					turningSecond = true;
				}
			}
		}
}
