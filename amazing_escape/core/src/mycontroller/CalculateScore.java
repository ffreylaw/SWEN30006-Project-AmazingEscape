package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

public class CalculateScore {
	public static final int MUD_SCORE = 1;
	public static final int GRASS_SCORE = 2;
	public static final int LAVA_SCORE = 3;
	
	int calcLaneScore(CarController controller, int laneNum) {
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		// calculate score on that lane or to change to that lane
		return 0;
	}
}
