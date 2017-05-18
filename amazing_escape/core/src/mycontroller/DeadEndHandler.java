package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;

public class DeadEndHandler {
	
	private DeadEndAction uTurn;
	private DeadEndAction threePointTurn;
	private DeadEndAction reverseOut;
	
	public DeadEndHandler() {
		this.uTurn = new UTurn();
		this.threePointTurn = new ThreePointTurn();
		this.reverseOut = new ReverseOut();
	}
	
	public boolean checkDeadEnd(HashMap<Coordinate, MapTile> currentView) {
		return false;
	}
	
	public void handle(CarController controller, float delta) {
		
	}

}
