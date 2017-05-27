package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

public class CalculateScore {
	public static final int MUD_SCORE = 1;
	public static final int GRASS_SCORE = 2;
	public static final int LAVA_SCORE = 3;
	public static final int WALL_SCORE = 10000;  // high score for wall

	/* Get the lane score (score on that lane + score of getting to that lane)*/
	public static int calcLaneScore(CarController controller, int laneNum, TrapHandler handler) {
		return getLaneScore(controller, handler, laneNum) + getRouteScore(controller, laneNum, handler);
	}

	/* Get the sum of score of each tile on that lane */
	private static int getLaneScore(CarController controller, TrapHandler handler, int laneNum) {
		int score = 0;
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		
		// score on that lane
		for(int i=1; i<=3; i++) {
			MapTile tile = currentView.get(handler.getTileAt(i, laneNum,controller, controller.getPosition()));
			score += getTileScore(tile, handler);
		}
		return score;
	}
	
	/* Get the score of getting to that lane, return 0 if laneNum == 0 */
	private static int getRouteScore(CarController controller, int laneNum, TrapHandler handler) {
		int score = 0;
		if(laneNum < 0) {  // left
			for(int i=0; i>laneNum; i--) {
				MapTile tile = handler.getTileAt(1, i, controller, controller.getPosition());
				score += getTileScore(tile, handler);
			}
		} else {  // > 0, right
			for(int i=0; i<laneNum; i++) {
				MapTile tile = handler.getTileAt(1, i, controller, controller.getPosition());
				score += getTileScore(tile, handler);
			}
		}
		return score;
	}
	
	/* Get the score of the tile */
	private static int getTileScore(MapTile tile, TrapHandler handler) {
		switch(handler.getTileName(tile)) {
		case "Grass":
			return GRASS_SCORE;
		case "Lava":
			return LAVA_SCORE;
		case "Mud":
			return MUD_SCORE;
		case "Wall":
			return WALL_SCORE;
		}
		return 0;
	}
}
	
