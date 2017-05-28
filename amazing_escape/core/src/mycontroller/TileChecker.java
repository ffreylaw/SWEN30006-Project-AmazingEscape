/* 
 * SWEN30006 Software Modelling and Design
 * Project C - Amazing Escape
 * 
 * Author: Pei-Yun Sun <667816>
 * Author: Geoffrey Law <759218>
 * Author: HangChen Xiong <753057>
 * 
 */

package mycontroller;

import java.util.HashMap;

import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;

public final class TileChecker {
	
	/**
	 * Avoiding instantiation, since the class is a functional class
	 */
	private TileChecker() {
	}
	
	/**
	 * Get the tile at a specific position provided
	 * @param numAhead
	 * @param numRight
	 * @param controller
	 * @param pos
	 * @return
	 */
	public static MapTile getTileAt(int numAhead, int numRight, MyAIController controller, String pos) {
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
		return null;
	}
	
	/**
	 * Get the name of the tile
	 * @param tile
	 * @return
	 */
	public static String getTileName(MapTile tile) {
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
}
