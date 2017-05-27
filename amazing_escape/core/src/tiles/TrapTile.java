package tiles;

import world.Car;

public abstract class TrapTile extends MapTile{

	public TrapTile(String layerName) {
		super(layerName);
	}
	public abstract void applyTo(Car car, float delta);
}
