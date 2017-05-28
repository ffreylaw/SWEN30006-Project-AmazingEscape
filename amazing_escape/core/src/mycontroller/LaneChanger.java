package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;
import world.WorldSpatial.RelativeDirection;

public class LaneChanger {
		private RelativeDirection firstTurnDir;  // record whether the first turn is turning left or right
		private MapTile turningTile;  // turning point of the second turn during change of lane
		private int turnNum;  // either 1 or 2
		private boolean turning;  // turning first time during change of lane
		private Direction carOri;  // car direction before changing lane, target direction of the second turn
		private Direction firstTurnTargetDir;  // target direction of the first turn when changing lane
		private boolean changingLane;
		
		/**
		 * Constructor
		 */
		LaneChanger() {
			changingLane = false;
		}
		
		/**
		 * Readjust the car to the orientation we are in.
		 * @param controller
		 * @param delta
		 */
		public void readjust(MyAIController controller, float delta) {
			if(controller.getLastTurnDirection() != null){
				if((!turning))  // not turning
					if(controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.RIGHT)) {
						controller.adjustRight(controller.getOrientation(), delta);
					} else {
						controller.adjustLeft(controller.getOrientation(), delta);
					}
				}
		}
		
		/**
		 * Return a boolean value of whether the car is changing lane
		 * @return
		 */
		public boolean isChangingLane() {
			return changingLane;
		}
		
		/**
		 * Get the orientation on the car's left or right
		 * @param currentDir
		 * @param left
		 * @return
		 */
		private Direction getOri(Direction currentDir, boolean left) {
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
		
		/**
		 * Set the variables for lane changing
		 * @param controller
		 * @param delta
		 * @param laneNum negative lane num for lanes at the car's left, positive for right, 0 for current lane
		 * @param handler
		 */
		public void setChangeLane(MyAIController controller, float delta, int laneNum) {
			System.out.println("set target lane num = " + laneNum);
			System.out.println("orientation: " + controller.getOrientation());
			readjust(controller, delta);
			
			// set last tile
			turningTile = TileChecker.getTileAt(1, laneNum, controller, controller.getPosition());
			
			HashMap<Coordinate,MapTile> currentView = controller.getView();
			Coordinate currentPosition = new Coordinate(controller.getPosition());
			
			System.out.println("turning tile at " + (currentPosition.x+1) + ", " + (currentPosition.y - laneNum));
			
			// set other variables for changing lane
			changingLane = true;
			turning = true;
			turnNum = 1;
			carOri = controller.getOrientation();
			
			if(laneNum < 0) {  // turn left
				firstTurnDir = RelativeDirection.LEFT;
				firstTurnTargetDir = getOri(carOri, true);
			} else {  // turn right
				firstTurnDir = RelativeDirection.RIGHT;
				firstTurnTargetDir = getOri(carOri, false);
			}	
		}
		
		/**
		 * Check if the car can change lane
		 * @param controller
		 * @return
		 */
		public boolean canChangeLane(MyAIController controller) {
			for(int i=-3; i<=3; i++) {
				if(i==0) {  // skip current lane
					continue;
				}
				int score = CalculateScore.calcLaneScore(controller, i);
				if(score < CalculateScore.WALL_SCORE) {  // no wall on that lane
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Find a best lane and setChangeLane
		 * @param controller
		 * @param delta
		 * @param handler
		 */
		public void changeLane(MyAIController controller, float delta) {
			controller.applyReverseAcceleration();
			
			// find best lane
			int bestLaneNum = -1;
			int bestLaneScore = CalculateScore.calcLaneScore(controller, -1);  // the lower the better
			for(int i=-3; i<=3; i++) {
				if(i==0) {  // skip current lane
					continue;
				}
				System.out.println("currentPos = " + controller.getPosition());
				int score = CalculateScore.calcLaneScore(controller, i);
				System.out.println("lane " + i + " Score = " + score);
				if(score < bestLaneScore) {
					bestLaneNum = i;
					bestLaneScore = score;
				}
			}
			// move to best lane
			setChangeLane(controller, delta, bestLaneNum);
		}
		
		/**
		 * keep doing lane changing
		 * @param controller
		 * @param delta
		 * @param handler
		 */
		public void doLaneChange(MyAIController controller, float delta) {
			System.out.println("Changing lane");
			System.out.println(controller.getOrientation());
			if(controller.getVelocity() < 1) {
				controller.applyForwardAcceleration();
				return;
			}
			if(turning && turnNum == 1) {
				System.out.println("first turning");
				if(!controller.getOrientation().equals(firstTurnTargetDir)) {
					// apply first turn direction
					if(firstTurnDir.equals(RelativeDirection.LEFT)) {
						controller.turnLeft(delta);
					} else {  // right
						controller.turnRight(delta);
					}
				} else {
					turning = false;  // finish first turning
					if(firstTurnDir.equals(RelativeDirection.LEFT)) {
						controller.adjustLeft(firstTurnTargetDir, delta);
					} else {  // right
						controller.adjustRight(firstTurnTargetDir, delta);
					}
				}
			} else if(turning && turnNum == 2) {
				System.out.println("second turning");
				if(!controller.getOrientation().equals(carOri)) {
					// turn opposite direction to first turn direction
					if(firstTurnDir.equals(RelativeDirection.LEFT)) {
						controller.turnRight(delta);
					} else {
						controller.turnLeft(delta);
					}
				} else {
					turning = false;
					changingLane = false;  // finish changing lane
				}
			} else {  // not turning
				// get this tile
				HashMap<Coordinate,MapTile> currentView = controller.getView();
				Coordinate currentPosition = new Coordinate(controller.getPosition());
				MapTile thisTile = currentView.get(currentPosition);

				// increment changedLane number if tile changed
				if(!thisTile.equals(turningTile)) {  // cross a tile
					if(controller.getVelocity() < 1) {
						controller.applyForwardAcceleration();
					}
				} else {
					turning = true;
					turnNum = 2;
				}
			}
		}
}
