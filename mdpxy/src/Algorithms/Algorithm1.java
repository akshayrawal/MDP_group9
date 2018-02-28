package Algorithms;


import java.util.ArrayList;
import Model.Arena;
import Model.ArenaAndRobotData;
import Model.GridCell;
import Model.Robot;
import RPIConnection.RpiToRobotData;

public class Algorithm1 implements ArenaAndRobotData{

	private Robot robot;
	private Arena arena;
	private GridCell[][] gridCell;
	private boolean timeToGoBack = false;
	private boolean reachedGoal = false;
	private int speed;
	private int coveredPercentage;
	private boolean timesUp = false;
	private boolean enableCoverageTerminal = false;
	private boolean enableTimerTerminal = false;
	
	public Algorithm1(Robot robot, Arena arena)
	{
		this.robot = robot;
		this.arena = arena;
		this.gridCell = arena.getGridCell();
	}
	
	public void go(){
		 Thread thread = new Thread(new Runnable() {  
		        public void run() {  
		            startExploration();
		        }
		    }  );
		    thread.setPriority(Thread.NORM_PRIORITY);  
		    thread.start();
	}
	
	public void startExploration()
		{
			int i = 0;
			boolean justTurned = false;

			do{
				i++;
				robot.getSensorsData(gridCell);
				arena.updateRobotPosition();
				int x = robot.getCurrentPosition()[0];
				int y = robot.getCurrentPosition()[1];
//				System.out.println(robot.canLeftCollaborate(x, y, gridCell, robot.getDirection()));

				
//				System.out.println(robotLocationToString(x-1,y-1,robot.getDirection()));
//				System.out.println(arenaInforToString());
				
				
				if(justTurned){
					robot.goStraight();
					justTurned = false;
				} else if(!this.leftIsBlocked()){
					robot.turnLeft();
					justTurned = true;
				} else if(!this.frontIsBlocked()){
					robot.goStraight();
					justTurned = false;
				}  else if(!this.rightIsBlocked()){
					robot.turnRight();
					justTurned = true;
				}
				else {
					robot.turnBack();
					justTurned = false;
				}
				
				if(robot.getCurrentPosition()[0]==1 && robot.getCurrentPosition()[1]==13){
					reachedGoal=true;
				}
				try {
					Thread.sleep(1000/speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				arena.updateCoverageAndTime();
				
					if(enableCoverageTerminal)
					{
						if(arena.calculateExploredPercentage() >= coveredPercentage){
							System.out.println(arena.calculateExploredPercentage());
							System.out.println(coveredPercentage);
							System.out.println("need to go back");
							timeToGoBack = true;
						}
					}
					
			}while((!(robot.getCurrentPosition()[0] ==18 && robot.getCurrentPosition()[1] == 1) || reachedGoal!=true) && timeToGoBack==false && timesUp == false);//stop when robot traverse back to start after reaching goal
			arena.updateRobotPosition();
			if(timeToGoBack || timesUp){
				turnBackAndGoBack();
			}
			if(reachedGoal){
				
				robot.turnToNorth();
				arena.updateRobotPosition();
				System.out.println("Exploration finished, running fastest path is allowed.");
				ArrayList<String> arenaDetails = new ArrayList<String>();
				arenaDetails = arena.exportMap(gridCell);
				arena.saveArenaToFile(FILENAME2,arenaDetails);
				if(enableTimerTerminal)
				{
					arena.stopTimer();
				}
				
			}	
		}
		
	public void turnBackAndGoBack()
	{
		robot.turnBack();
		int i = 0;
		boolean justTurned = true;
		do{
			i++;
			robot.getSensorsData(gridCell);
		
			if(justTurned){
				if(!this.frontIsBlocked())
				{
				robot.goStraight();
				}
				justTurned = false;
			} 
			else if(!this.rightIsBlocked()){
				robot.turnRight();
				justTurned = true;
			}
			 else if(!this.frontIsBlocked()){
				robot.goStraight();
				justTurned = false;
			}  else if(!this.leftIsBlocked()){
				robot.turnLeft();
				justTurned = true;
			}
			else {
				robot.turnBack();
				justTurned = false;
			}
			
			if(robot.getCurrentPosition()[0]==1 && robot.getCurrentPosition()[1]==13){
				reachedGoal=true;
//				System.out.println("reached goal point!");
			}
			try {
				Thread.sleep(1000/speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			arena.updateRobotPosition();
			arena.updateCoverageAndTime();

		}while(!(robot.getCurrentPosition()[0] ==18 && robot.getCurrentPosition()[1] == 1));
		arena.updateRobotPosition();
		robot.turnToNorth();
		arena.updateRobotPosition();
	}
	
	public boolean frontIsBlocked(){
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		int direction = robot.getDirection();
		if(direction == NORTH)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, NORTH))
			{
				return true;
			}
		}
		else if(direction == SOUTH)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, SOUTH))
			{
				return true;
			}
		}
		else if(direction == EAST)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, EAST))
			{
				return true;
			}
		}
		else if(direction == WEST)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, WEST))
			{
				return true;
			}
		}
		return false;	
	}
	
	public boolean leftIsBlocked(){
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		int direction = robot.getDirection();
		if(direction == NORTH)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, WEST))
			{
				return true;
			}
		}
		else if(direction == SOUTH)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, EAST))
			{
				return true;
			}
		}
		else if(direction == EAST)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, NORTH))
			{
				return true;
			}
		}
		else if(direction == WEST)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, SOUTH))
			{
				return true;
			}
		}
		return false;	
	}
	
	public boolean rightIsBlocked(){
		int x = robot.getCurrentPosition()[0];
		int y = robot.getCurrentPosition()[1];
		int direction = robot.getDirection();
		if(direction == NORTH)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, EAST))
			{
				return true;
			}
		}
		else if(direction == SOUTH)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, WEST))
			{
				return true;
			}
		}
		else if(direction == EAST)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, SOUTH))
			{
				return true;
			}
		}
		else if(direction == WEST)
		{
			if(!robot.canMoveOrNot(x, y, gridCell, NORTH))
			{
				return true;
			}
		}
		return false;	
	}
	
	public void setCoveredPercentage(int coveredPercentage)
	{
		this.coveredPercentage = coveredPercentage;
	}
	
	public boolean getCoveredTerminal()
	{
		return enableCoverageTerminal;
	}
	
	public void switchCoveredTerminal()
	{
		if(enableCoverageTerminal)
		{
			enableCoverageTerminal = false;
			timeToGoBack = false;
		}
		else if(!enableCoverageTerminal)
		{
			enableCoverageTerminal = true;
		}
	}

	public void switchTimerTerminal()
	{
		if(enableTimerTerminal)
		{
			enableTimerTerminal = false;
		}
		else if(!enableTimerTerminal)
		{
			enableTimerTerminal = true;
		}
	}
	
	public boolean getTimerTerminal()
	{
		return enableTimerTerminal;
	}
	
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public void setTimesUp(boolean timesUp)
	{
		this.timesUp = timesUp;
	}
	
	//followings are function for android
	private String robotLocationToString(int row, int column, int direction)
	{
		String stringToSend = "";
		stringToSend = "{\"robotPosition\" : [" + row + ", " + column + ", " + direction + "]}";
		return stringToSend;
	}
	
	private String arenaInforToString()
	{
		String stringToSend = "";
        String hexString = "";
        String binaryString = "";
        for (int i = 0; i < ROW; i++)
        {
            for (int j = 0; j < COLUMN; j++)
            {
                binaryString += gridCell[i][j].getGridCellCondition()[1];   
            }
        }
        hexString = arena.binToHex(binaryString);
        stringToSend = "{\"grid\" : \""  + hexString + "\"}";
		return stringToSend;
	}
	
	
	//followings are fastest path methods done by JiaJing
	public String getFastestPath(GridCell[][] gridCell)
	{
		System.out.printf("run fastest path\n");
		System.out.printf("compute fastest path\n");
		FastestPath fp = new FastestPath(gridCell, ArenaAndRobotData.STARTPOSITION, ArenaAndRobotData.GOALPOSITION);
		int[] path = fp.execute();
			
		boolean fpCalibrate = false;
		
		System.out.println("get path string");
		String robotPath = "";
		int curO = (GridCell.oneDPosToTwoD(path[0]))[1] < (GridCell.oneDPosToTwoD(path[1]))[1] ? Robot.EAST : Robot.NORTH;
		robotPath += robot.turnToReqDirection(robot.getDirection(), curO);
		//LATER robot.setSensors(curO);
		
		int preCalibrateStep = 0; int countCalibrate = 0;
//			System.out.printf("%s\n", robotPath);
		for(int j = 0; j<path.length-1;j++){
			int[] curPos = GridCell.oneDPosToTwoD(path[j]);
			int[] nxtPos = GridCell.oneDPosToTwoD(path[j+1]);
//				System.out.printf("%d %d %s\n", curPos[0], curPos[1], robot.getOrientationString(curO));
			int fpFreq = 5; int fpInterval = 7;
			/* LATER
			if(fpCalibrate){
				//calibration periodically
				if(Robot.nextToWall(curPos) && !Robot.nextToWall(nxtPos)){	//leave wall
					if(canRightCalibrate(gridCell2, robot, curPos)){
						robotPath += RpiToRobotData.comCalibrateRight;
						preCalibrateStep = j;
						countCalibrate = 0;				
					}
					//need to ask for calibrate left
					else if(canLeftCalibrate(gridCell, robot, curPos, curO)){
						robotPath = robotPath + RpiToRobotData.comCalibrateLeft;
						preCalibrateStep = j;
						countCalibrate = 0;	
					}
					if(canFrontCalibrate(gridCell2, robot, curPos)){
						robotPath += RpiToRobotData.comCalibrateFront;
						preCalibrateStep = j;
						countCalibrate = 0;
					}
				}
				else{
					if(canRightCalibrate(gridCell2, robot, curPos)){
						countCalibrate++;
						if(!leaveWallLater(fpFreq, path, j, curPos) && (countCalibrate >= fpFreq || j - preCalibrateStep >= fpInterval)){
							robotPath += RpiToRobotData.comCalibrateRight;
							preCalibrateStep = j;
							countCalibrate = 0;
						}				
					}
					//need to ask for calibrate left
					else if(canLeftCalibrate(gridCell2, robot, curPos, curO)){
						countCalibrate++;
						if(!leaveWallLater(fpFreq, path, j, curPos) && (countCalibrate >= fpFreq || j - preCalibrateStep >= fpInterval)){
							robotPath = robotPath + RpiToRobotData.comCalibrateLeft;
							preCalibrateStep = j;
							countCalibrate = 0;
						}	
					}
					if(canFrontCalibrate(gridCell2, robot, curPos)){
						countCalibrate++;
						if(!leaveWallLater(fpFreq, path, j, curPos) && (countCalibrate >= fpFreq || j - preCalibrateStep >= fpInterval)){
							robotPath += RpiToRobotData.comCalibrateFront;
							preCalibrateStep = j;
							countCalibrate = 0;
						}	
					}
				}
			}
			*/
			//turn 
			String curTurn = robot.turnString(curPos, nxtPos, curO);
			if(curTurn.equals(RpiToRobotData.TURNRIGHT)){
				curO = robot.getDirAfterRightTurn(curO);
				//LATER robot.setSensors(curO);
			}
			else if(curTurn.equals(RpiToRobotData.TURNLEFT)){
				curO = robot.getDirAfterLeftTurn(curO);
				//LATER robot.setSensors(curO);
			}
			robotPath += curTurn;

			if(j == path.length-1)	//change to length-2 to calibrate before reaching the goal
				robotPath += RpiToRobotData.comFpGoalCalibrate;
			else{
				robotPath += RpiToRobotData.STRAIGHTMOVE;
				//robot.goStraight();
					//simulate go straight
//				robotPath += PcInterface.comGoStraight;
			}
			
		}
		System.out.printf("robot movement: %s\n", robotPath);
		
		for(int j = 0; j < robotPath.length(); j++){
			switch (robotPath.charAt(j)){
			case 'R':
				robot.turnRight();
				break;
			case 'L':
				robot.turnLeft();
				break;
			case 'M':
				robot.goStraight();
				break;
			case 'B':
				robot.turnBack();
				break;
				default:
					System.out.printf("calibrate %s\n", robotPath.charAt(j));
			}
			System.out.println(robot.getCurrentPosition()[0] +", " + robot.getCurrentPosition()[1]);
			try {
				Thread.sleep(1000/speed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			arena.updateRobotPosition();
		}
		return robotPath;
		//29126 end
	}

	
	public void fpgo(final GridCell[][] gridCell){
		 Thread thread = new Thread(new Runnable() {  
		        public void run() {  
		        	getFastestPath(gridCell);
		        }
		    }  );
		    thread.setPriority(Thread.NORM_PRIORITY);  
		    thread.start();
	}
	
}
