package Model;

import java.awt.Color;

public interface ArenaAndRobotData {
	static final int EAST = 90;
	static final int SOUTH = 180;
	static final int WEST = 270;
	static final int NORTH = 0;
	static final int ROW = 20;
	static final int COLUMN = 15;
	
	static final Color STARTZONECOLOR = Color.BLUE;
	static final Color GOALZONECOLOR = Color.YELLOW;
	static final Color UNVISITED_COLOR = Color.CYAN;
	static final Color EMPTY_COLOR = Color.WHITE;
	static final Color OBSTACLE_COLOR = Color.BLACK;
	static final Color CELLBORDER_COLOR = Color.GRAY;
	static final Color ROBOT_COLOR = Color.GREEN;
	static final Color ROBOTDIRECTION_COLOR = Color.MAGENTA;
	static final int[] STARTPOSITION = new int[]{18,1};
	static final int[] GOALPOSITION = new int[]{1,13};	//29126
	static final int[] WAYPOINT = new int[]{6,12};
	static final String FILENAME = "PreDesign_Arena.txt";
	static final String FILENAME2 = "Explored_Arena.txt";
	static final int SHROTRANGE_SENSOR_DISTANCE = 2;
	static final int LONGRANGE_SENSOR_MAXIMUM_DISTANCE = 4;

}
