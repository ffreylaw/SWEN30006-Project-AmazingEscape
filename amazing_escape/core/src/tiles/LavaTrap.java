package tiles;

import world.Car;

public class LavaTrap extends TrapTile {
	
	private static final String layerName = "Lava";
	
	LavaTrap() {
		super(layerName);
	}
	
	public void applyTo(Car car, float delta) {
		car.reduceHealth(20 * delta);
	}

}
