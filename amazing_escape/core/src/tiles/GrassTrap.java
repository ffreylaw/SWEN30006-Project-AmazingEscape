package tiles;

import world.Car;

public class GrassTrap extends TrapTile {
	
	private static final String layerName = "Grass";
	
	GrassTrap() {
		super(layerName);
	}
	
	public void applyTo(Car car, float delta) {
		// car.setVelocity(1f, 1f);
	}

}
