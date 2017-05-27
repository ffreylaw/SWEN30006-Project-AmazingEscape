package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

public class CalculateScore {
	public static final int MUD_SCORE = 1;
	public static final int GRASS_SCORE = 2;
	public static final int LAVA_SCORE = 3;
	
	int calcLaneScore(CarController controller, int laneNum, TrapHandler handler) {  // return 1000 if cannot reach there
		HashMap<Coordinate, MapTile> currentView = controller.getView();
		int score = 0;
		
		if(laneNum == 0) {  // current lane
			for(int i=1; i<=3; i++) {
				MapTile tile = currentView.get(handler.getTileAhead(i, controller, controller.getPosition()));
				switch(tile.getName()) {
					case "Grass":
						score += GRASS_SCORE;
						break;
					case "Lava":
						score += LAVA_SCORE;
						break;
					case "MUD":
						score += MUD_SCORE;
						break;
				}
			}
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
			
			// score on that lane
			for(int i=1; i<=3; i++) {
				MapTile tile = currentView.get(handler.getTileAhead(i, controller, pos));
				switch(tile.getName()) {
					case "Grass":
						score += GRASS_SCORE;
						break;
					case "Lava":
						score += LAVA_SCORE;
						break;
					case "MUD":
						score += MUD_SCORE;
						break;
				}
			}
			
			// score on the way to that lane
			
		}
		
		return score;
	}
}
