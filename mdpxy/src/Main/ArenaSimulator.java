package Main;

import Model.Arena;
import Model.ArenaAndRobotData;
import Model.ArenaRealRun;
import Model.Robot;
import RPIConnection.RpiConnect;

public class ArenaSimulator implements ArenaAndRobotData{
	private static int[] startPosition;
	private static Robot robot;
	private static Arena arena;
	private static ArenaRealRun arenaRealRun;
	private static RpiConnect rc;
	public ArenaSimulator()
	{
		super();
	}
	
	public static void main(String[] args) {
		
		startPosition = STARTPOSITION;
		robot = new Robot(startPosition, NORTH);
//		arena = new Arena(robot);
		arenaRealRun = new ArenaRealRun(robot);
//		rc = new RpiConnect();
		
	}
	

	

}
