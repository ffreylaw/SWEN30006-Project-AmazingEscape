/* 
 * SWEN30006 Software Modelling and Design
 * Project C - Amazing Escape
 * 
 * Group 66
 * Author: Pei-Yun Sun <667816>
 * Author: Geoffrey Law <759218>
 * Author: HangChen Xiong <753057>
 * 
 */

package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import world.WorldSpatial;

public class TrapHandler {

	private boolean needLaneChange;
	private LaneChanger changer;
	
	/**
	 * Constructor
	 */
	public TrapHandler() {
		needLaneChange = false;
		changer = new LaneChanger();
	}
	
	/**
	 * Handle the traps
	 * @param controller
	 * @param delta
	 */
	public void handle(MyAIController controller, float delta) {
		
		readjust(controller, delta);
		
		if(changer.isChangingLane()) {
			changer.doLaneChange(controller, delta);  // delegate handling to LaneChanger
			return;
		}
		
		String pos = controller.getPosition();

		MapTile tile1 = TileChecker.getTileAt(1, 0, controller, pos);
		MapTile tile2 = TileChecker.getTileAt(2, 0, controller, pos);
		MapTile tile3  = TileChecker.getTileAt(3, 0, controller, pos);

		if(TileChecker.getTileName(tile1).equals("Wall")) {  // wall in front
			movReverse(controller);
			needLaneChange = true;
		} else if(TileChecker.getTileName(tile2).equals("Wall")) {  // wall at 2 tile away
			if(TileChecker.getTileName(tile1).equals("Grass")) {  // grass in front
				movReverse(controller);
				needLaneChange = true;
			} else {  // no grass in front
				if(changer.canChangeLane(controller)) {
					changer.changeLane(controller, delta);
					needLaneChange = false;
				} else {
					movReverse(controller);
					needLaneChange = true;
				}
			}
		} else if(TileChecker.getTileName(tile3).equals("Wall")) {  // wall at 3 tile away
			if(TileChecker.getTileName(tile2).equals("Grass")) {  // grass at 2 tile away
				if(TileChecker.getTileName(tile1).equals("Grass")) {  // grass in front
					movReverse(controller);
					needLaneChange = true;
				} else {   // no grass in front
					if(changer.canChangeLane(controller)) {
						changer.changeLane(controller, delta);
						needLaneChange = false;
					} else {
						movReverse(controller);
						needLaneChange = true;
					}
				}
			} else {  // no grass at two 2 tile away
				if(TileChecker.getTileName(tile1).equals("Grass")) {  // grass in front
					if(needLaneChange == true) {
						movReverse(controller);
					} else {
						movForward(controller);
					}
				} else {  // no grass in front
					if(needLaneChange == true) {
						if(changer.canChangeLane(controller)) {
							changer.changeLane(controller, delta);
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
				// if no grass in front and can change lane
				if(changer.canChangeLane(controller) && 
						!TileChecker.getTileName(tile1).equals("Grass")) {
					changer.changeLane(controller, delta);
					needLaneChange = false;
				} else {
					movReverse(controller);
				}
			} else {
				calcScoreMov(controller, delta);
			}
		}
	}

	/**
	 * Calculate the scores to choose a best movement (best lane),
	 * could choose to stay at current lane
	 * @param controller
	 * @param delta
	 */
	private void calcScoreMov(MyAIController controller, float delta) {
		// find best lane
		int bestLaneNum = 0;
		int bestLaneScore = CalculateScore.calcLaneScore(controller, 0);  // the lower the better
		
		for(int i=-3; i<=3; i++) {
			int score = CalculateScore.calcLaneScore(controller, i);
			if(score < bestLaneScore) {
				bestLaneNum = i;
				bestLaneScore = score;
			}
		}

		// move to best lane
		if(bestLaneNum == 0) {  // stay at current lane
			movForward(controller);
		} else {  // best lane
			changer.changeLane(controller, delta);
		}
	}

	/**
	 * Let the car move forward
	 * @param controller
	 */
	private void movForward(MyAIController controller) {
		if(controller.isReversing()) {
			controller.applyBrake();
		} else {
			if(controller.getVelocity() < 1) {
				controller.applyForwardAcceleration();
			}
		}
	}

	/** 
	 * Let the car move backward
	 * @param controller
	 */
	private void movReverse(MyAIController controller) {
		if(controller.isReversing()) {
			if(controller.getVelocity() < 1) {
				controller.applyReverseAcceleration();
			}
		} else {
			controller.applyBrake();
		}
	}
	
	/** 
	 * Check if any traps ahead
	 * @param controller
	 * @return
	 */
	public boolean checkTrap(MyAIController controller) {
		for(int i=1; i<=3; i++) {
			MapTile tile = TileChecker.getTileAt(i, 0, controller, controller.getPosition());
			if(tile.getName().equals("Wall")) {
				break;
			}
			if(tile instanceof TrapTile && !TileChecker.getTileName(tile).equals("Mud")) {  // trap detected
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return a boolean value of whether the car is changing lane
	 * @return
	 */
	public boolean isChangingLane() {
		return changer.isChangingLane();
	}
	
	/**
	 * Readjust the car to the orientation we are in.
	 * @param controller
	 * @param delta
	 */
	public void readjust(MyAIController controller, float delta) {
		if(controller.getLastTurnDirection() != null) {
			if((!changer.isTurning())) { // not turning
				if(controller.getLastTurnDirection().equals(WorldSpatial.RelativeDirection.RIGHT)) {
					controller.adjustRight(controller.getOrientation(), delta);
				} else {
					controller.adjustLeft(controller.getOrientation(), delta);
				}
			}
		}
	}
}
