package mycontroller;

import java.util.HashMap;

import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import tiles.TrapTile;
import utilities.Coordinate;

public class TrapHandler {

	private boolean needLaneChange;
	
	private LaneChanger changer;

	private boolean changingLane;  // for lane change
	
	public boolean getChangingLane() {
		return changingLane;
	}

	public TrapHandler() {
		needLaneChange = false;
		changingLane = false;
		changer = new LaneChanger();
	}
	
	public void setChangingLane(boolean changing) {
		changingLane = changing;
	}

	public void handle(MyAIController controller, float delta) {
		if(changingLane) {
			changer.doLaneChange(controller, delta, this);
			return;
		}
		
		changer.readjust(controller, delta);
		
		String pos = controller.getPosition();

		MapTile tile1 = getTileAt(1, 0, controller, pos);
		MapTile tile2 = getTileAt(2, 0, controller, pos);
		MapTile tile3  = getTileAt(3, 0, controller, pos);

		if(getTileName(tile1).equals("Wall")) {  // wall in front
			movReverse(controller);
			needLaneChange = true;
		} else if(getTileName(tile2).equals("Wall")) {  // wall at 2 tile away
			if(getTileName(tile1).equals("Grass")) {  // grass in front
				movReverse(controller);
				needLaneChange = true;
			} else {  // no grass in front
				if(changer.canChangeLane(controller, this)) {
					changer.changeLane(controller, delta, this);
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
					if(changer.canChangeLane(controller, this)) {
						changer.changeLane(controller, delta, this);
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
						if(changer.canChangeLane(controller, this)) {
							changer.changeLane(controller, delta, this);
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
				if(changer.canChangeLane(controller, this) && !getTileName(tile1).equals("Grass")) {  // no grass in front and can change lane
					changer.changeLane(controller, delta, this);
					needLaneChange = false;
				} else {
					movReverse(controller);
				}
			} else {
				System.out.println("calcScoreMov");
				calcScoreMov(controller, delta);
			}
		}
	}

	public MapTile getTileAt(int numAhead, int numRight, MyAIController controller, String pos) {
		HashMap<Coordinate,MapTile> currentView = controller.getView();
		Coordinate currentPosition = new Coordinate(pos);
		switch(controller.getOrientation()) {
		case EAST:
			return currentView.get(new Coordinate(currentPosition.x+numAhead, currentPosition.y-numRight));
		case NORTH:
			return currentView.get(new Coordinate(currentPosition.x+numRight, currentPosition.y+numAhead));
		case SOUTH:
			return currentView.get(new Coordinate(currentPosition.x-numRight, currentPosition.y-numAhead));
		case WEST:
			return currentView.get(new Coordinate(currentPosition.x-numAhead, currentPosition.y+numRight));
		}
		return null;  // will never return null
	}

	private void calcScoreMov(MyAIController controller, float delta) {
		// find best lane
		int bestLaneNum = 0;
		int bestLaneScore = CalculateScore.calcLaneScore(controller, 0, this);  // the lower the better
		
		System.out.println("line 0 score: " + bestLaneScore);
		
		for(int i=-3; i<=3; i++) {
			int score = CalculateScore.calcLaneScore(controller, i, this);
			if(score < bestLaneScore) {
				bestLaneNum = i;
				bestLaneScore = score;
			}
		}
		
		System.out.println("bestLaneNum = " + bestLaneNum);
		System.out.println("bestLaneScore = " + bestLaneScore);

		// move to best lane
		if(bestLaneNum == 0) {  // stay at current lane
			movForward(controller);
		} else {  // best lane
			changer.changeLane(controller, delta, this);
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

	public void movForward(MyAIController controller) {
		if(controller.isReversing()) {
			controller.applyBrake();
		} else {
			if(controller.getVelocity() < 2) {
				controller.applyForwardAcceleration();
			}
		}
	}

	private void movReverse(MyAIController controller) {
		if(controller.isReversing()) {
			if(controller.getVelocity() < 1) {
				controller.applyReverseAcceleration();
			}
		} else {
			controller.applyBrake();
		}
	}
	
	/** Check if any traps ahead */
	public boolean checkTrap(MyAIController controller) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		Coordinate currentPosition = new Coordinate(controller.getPosition());
		switch(controller.getOrientation()) {
		case EAST:
			for(int i=1; i<=3; i++) {  // right
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if(tile instanceof TrapTile) {  // trap detected
					return true;
				}
			}
			break;
		case NORTH:
			for(int j=1; j<=3; j++) {  // top
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
				if(tile instanceof TrapTile) {  // trap detected
					return true;
				}
			}
			break;
		case SOUTH:
			for(int j=-3; j<=-1; j++) {  // bot
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+j));
				if(tile instanceof TrapTile) {  // trap detected
					return true;
				}
			}
			break;
		case WEST:
			for(int i=-3; i<=-1; i++) {  // left
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if(tile instanceof TrapTile) {  // trap detected
					return true;
				}
			}
			break;
		}
		return false;
	}
	
}
