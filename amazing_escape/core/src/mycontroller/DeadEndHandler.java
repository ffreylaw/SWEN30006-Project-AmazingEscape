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

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class DeadEndHandler {
	
	private DeadEndAction currentAction;
	
	public DeadEndHandler() {
		currentAction = null;
	}
	
	/**
	 * Handle dead-end
	 * @param controller
	 * @param delta
	 */
	public void handle(MyAIController controller, float delta) {
		if (currentAction == null) {
			// action not chosen
			currentAction = chooseAction(new Coordinate(controller.getPosition()), controller.getView());
		} else {
			// do action
			currentAction.action(controller, delta);
			if (controller.getState() != MyAIController.State.DEAD_END) {
				// if action is done
				currentAction = null;
			}
		}
	}
	
	/**
	 * Choose an action to handle dead-end
	 * @param currentPosition
	 * @param currentView
	 * @return chosen action
	 */
	private DeadEndAction chooseAction(Coordinate currentPosition, HashMap<Coordinate, MapTile> currentView) {
		/* These three actions can handle all cases, any one of them should be OK, even if very little spaces dead-end :) */
		return new UTurn();			 // the fastest way to get out of dead-end
//		return new ThreePointTurn(); // slower than u-turn
//		return new ReverseOut();	 // longer version of three-point-turn, better not to use
	}
	
	/**
	 * Check whether there is a dead-end
	 * @return true if dead-end detected
	 */
	public boolean checkDeadEnd(MyAIController controller) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		boolean flag = false;
		switch (controller.getOrientation()) {
		case EAST:
			for (int i = 1; i <= 3; i++) {	// east walls (front of vehicle)
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if (tile.getName().equals("Wall")) {
					// if there a wall ahead
					isWallAhead = true;
					for (int j = -1; j >= -i; j--) {	// south walls (right of vehicle)
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i-1, currentPosition.y+j));
							if (tile.getName().equals("Wall") && controller.checkNorth(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		case NORTH:
			for (int j = 1; j <= 3; j++) {	// north walls (front of vehicle)
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
				if (tile.getName().equals("Wall")) {
					isWallAhead = true;
					for (int i = 1; i <= j; i++) {	// east walls (right of vehicle)
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j-1));
							if (tile.getName().equals("Wall") && controller.checkWest(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		case SOUTH:
			for (int j = -1; j >= -3; j--) {	// south walls (front of vehicle)
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
				if (tile.getName().equals("Wall")) {
					isWallAhead = true;
					for (int i = -1; i >= j; i--) {	// west walls (right of vehicle)
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j+1));
							if (tile.getName().equals("Wall") && controller.checkEast(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		case WEST:
			for (int i = -1; i >= -3; i--) {	// west walls (front of vehicle)
				if (flag) {
					break;
				}
				boolean isWallAhead = false;
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if (tile.getName().equals("Wall")) {
					isWallAhead = true;
					for (int j = 1; j <= Math.abs(i); j++) {	// north walls (right of vehicle)
						tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+j));
						if (!tile.getName().equals("Wall")) {
							break;
						}
						if (tile.getName().equals("Wall")) {
							tile = currentView.get(new Coordinate(currentPosition.x+i+1, currentPosition.y+j));
							if (tile.getName().equals("Wall") && controller.checkSouth(currentView)) {
								flag = true;
							}
						}
					}
				}
				if (isWallAhead && !flag) {
					break;
				}
			}
			break;
		}
		return flag;
	}

}
