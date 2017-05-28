package mycontroller;


import tiles.MapTile;

public class CalculateScore {
	public static final int MUD_SCORE = 1;
	public static final int GRASS_SCORE = 2;
	public static final int LAVA_SCORE = 3;
	public static final int WALL_SCORE = 10000;  // high score for wall

	/**
	 * Get the lane score (score on that lane + score of getting to that lane)
	 * @param controller
	 * @param laneNum
	 * @return
	 */
	public static int calcLaneScore(MyAIController controller, int laneNum) {
		int score = getLaneScore(controller, laneNum) + getRouteScore(controller, laneNum);
		return score;
	}

	/**
	 * Get the sum of score of each tile on that lane
	 * @param controller
	 * @param laneNum
	 * @return
	 */
	private static int getLaneScore(MyAIController controller, int laneNum) {
		int score = 0;
		
		// score on that lane
		for(int i=1; i<=3; i++) {
			MapTile tile = TileChecker.getTileAt(i, laneNum,controller, controller.getPosition());
			score += getTileScore(tile, controller);
		}
		return score;
	}
	
	/**
	 * Get the score of getting to that lane, return 0 if laneNum == 0
	 * @param controller
	 * @param laneNum
	 * @return
	 */
	private static int getRouteScore(MyAIController controller, int laneNum) {
		int score = 0;
		if(laneNum < 0) {  // left
			for(int i=0; i>laneNum; i--) {
				MapTile tile = TileChecker.getTileAt(1, i, controller, controller.getPosition());
				score += getTileScore(tile, controller);
			}
		} else {  // > 0, right
			for(int i=0; i<laneNum; i++) {
				MapTile tile = TileChecker.getTileAt(1, i, controller, controller.getPosition());
				score += getTileScore(tile, controller);
			}
		}
		return score;
	}
	
	/**
	 * Get the score of that tile
	 * @param tile
	 * @param controller
	 * @return
	 */
	private static int getTileScore(MapTile tile, MyAIController controller) {
		switch(TileChecker.getTileName(tile)) {
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
	
