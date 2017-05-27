package mycontroller;

public class 放着 {
//	public void handle(CarController controller, float delta) {
//	MapTile tile1 = getTileAhead(1, controller);
//	MapTile tile2 = getTileAhead(2, controller);
//	MapTile tile3  = getTileAhead(3, controller);
//	
//	if(tile1.getName().equals("Wall")) {  // wall in front
//		movReverse(controller);
//		needLaneChange = true;
//	} else if(tile2.getName().equals("Wall")) {  // wall at 2 tile away			
//		if(canChangeLane(controller)) {
//			changeLane(controller, delta);
//			needLaneChange = false;
//		} else {
//			movReverse(controller);
//			needLaneChange = true;
//		}
//	} else if(tile3.getName().equals("Wall")) {  // wall at 3 tile away
//		if(tile2.getName().equals("Grass")) {  // grass at 2 tile away				
//			if(canChangeLane(controller)) {
//				changeLane(controller, delta);
//				needLaneChange = false;
//			} else {
//				movReverse(controller);
//				needLaneChange = true;
//			}
//		} else {  // no grass at two 2 tile away			
//			if(needLaneChange == true) {  // reached the tile at 2 tiles away already, did not find route
//				if(canChangeLane(controller)) {
//					changeLane(controller, delta);
//					needLaneChange = false;
//				} else {
//					movReverse(controller);
//				}
//			} else {  // go to that tile first
//				movForward(controller);
//			}
//		}
//	} else {  // no wall ahead			
//		if(needLaneChange == true) {  // reached tiles ahead already, did not find route
//			changeLane(controller, delta);
//			needLaneChange = false;
//		} else {
//			movForward(controller);
//		}
//	}
//}

//private boolean canChangeLane(CarController controller) {
//	// check if can change lane
//	MapTile tile1 = getTileAhead(1, controller);
//	if(tile1.getName().equals("Grass")) {  // grass ahead cannot turn
//		return false;
//	}
//	
//	// check turn turning left
//	
//	// check turning right
//	
//	return false;
//}
}
