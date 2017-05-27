package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

public class CalculateScore {
	public static final int MUD_SCORE = 1;
	public static final int GRASS_SCORE = 2;
	public static final int LAVA_SCORE = 3;
	public static final int WALL_SCORE = 1000;

	public int calcLaneScore(CarController controller, int laneNum, TrapHandler handler) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		int score = 0;

		if(laneNum == 0) {  // current lane
			score += getLaneScore(controller, handler, controller.getPosition());
		} else {
			Coordinate currentPos = new Coordinate(controller.getPosition());
			String pos = null;
			switch(controller.getOrientation()) {
			case EAST:
				pos = new Coordinate(currentPos.x, currentPos.y+laneNum).toString();
			case NORTH:
				pos = new Coordinate(currentPos.x+laneNum, currentPos.y).toString();
			case SOUTH:
				pos = new Coordinate(currentPos.x-laneNum, currentPos.y).toString();
			case WEST:
				pos = new Coordinate(currentPos.x, currentPos.y-laneNum).toString();
			}

			// get score on that lane
			score += getLaneScore(controller, handler, pos);

			// score on the way to that lane

		}

		return score;
	}

	// score one that lane
	private int getLaneScore(CarController controller, TrapHandler handler, String pos) {
		int score = 0;
		
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		
		// score on that lane
		for(int i=1; i<=3; i++) {
			MapTile tile = currentView.get(handler.getTileAhead(i, controller, pos));
			switch(handler.getTileName(tile)) {
			case "Grass":
				score += GRASS_SCORE;
				break;
			case "Lava":
				score += LAVA_SCORE;
				break;
			case "Mud":
				score += MUD_SCORE;
				break;
			case "Wall":
				score += WALL_SCORE;
				break;
			}
		}
		
		return score;
		
	}
	
	// score get to that lane
	private int getRouteScore(CarController controller, int laneNum, TrapHandler handler) {
		int score = 0;
		
		return score;
	}
}
	
