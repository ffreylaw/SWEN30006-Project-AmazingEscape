package mycontroller;

import mycontroller.MyAIController.State;
import world.WorldSpatial;

public class UTurn implements DeadEndAction {
	
	private WorldSpatial.Direction targetOrientation;
	
	private boolean isDone;
	private boolean isDecelerated;
	
	/**
	 * Construct an object of UTurn action
	 */
	public UTurn() {
		targetOrientation = null;
		isDone = false;
		isDecelerated = false;
	}

	@Override
	public void action(MyAIController controller, float delta) {
		WorldSpatial.Direction currentOrientation = controller.getOrientation();
		
		// stop the vehicle
		if (!isDecelerated) {
			controller.applyBrake();
		}
		
		// check if is decelerated
		if (controller.getVelocity() <= 0.1) {
			isDecelerated = true;
		}
		
		// finalize the action
		if (isDone) {
			controller.changeState(State.NONE);
			isDone = false;
		} 
		
		// do u-turn
		if (isDecelerated && !isDone) {
			if (controller.getVelocity() < 0.2) {
				controller.applyForwardAcceleration();
			} else {
				applyUTurn(controller, delta);
				if (targetOrientation == null) {
					switch (currentOrientation) {
					case EAST:
						targetOrientation = WorldSpatial.Direction.WEST;
						break;
					case SOUTH:
						targetOrientation = WorldSpatial.Direction.NORTH;
						break;
					case WEST:
						targetOrientation = WorldSpatial.Direction.EAST;
						break;
					case NORTH:
						targetOrientation = WorldSpatial.Direction.SOUTH;
						break;
					}
				} else if (currentOrientation != targetOrientation) {
					applyUTurn(controller, delta);
				} else {
					targetOrientation = null;
					isDone = true;
					isDecelerated = false;
				}
			}
		}
	}
	
	/**
	 * Apply U-turn
	 * @param controller
	 * @param delta
	 */
	private void applyUTurn(MyAIController controller, float delta) {
		WorldSpatial.Direction orientation = controller.getOrientation();
		switch(orientation){
		case EAST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.WEST)){
				controller.turnRight(delta);
			}
			break;
		case NORTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				controller.turnRight(delta);
			}
			break;
		case SOUTH:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.NORTH)){
				controller.turnRight(delta);
			}
			break;
		case WEST:
			if(!controller.getOrientation().equals(WorldSpatial.Direction.EAST)){
				controller.turnRight(delta);
			}
			break;
		default:
			break;
		}
	}

}
