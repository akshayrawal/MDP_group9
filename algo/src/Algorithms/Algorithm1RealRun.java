package Algorithms;

import java.util.ArrayList;
import Model.ArenaAndRobotData;
import Model.ArenaRealRun;
import Model.GridCell;
import Model.Robot;
import RPIConnection.RpiConnect;
import RPIConnection.RpiToRobotData;

public class Algorithm1RealRun implements ArenaAndRobotData, RpiToRobotData{

	private Robot robot;
	private ArenaRealRun arenaRealRun;
	private GridCell[][] gridCell;
	private boolean timeToGoBack = false;
	private boolean reachedGoal = false;
	private int speed;
	private int coveredPercentage;
	private boolean timesUp = false;
	private boolean enableCoverageTerminal = false;
	private boolean enableTimerTerminal = false;
	private RpiConnect rc = null;
	
	public Algorithm1RealRun(Robot robot, ArenaRealRun arenaRealRun, RpiConnect rc)
	{
		this.robot = robot;
		this.arenaRealRun = arenaRealRun;
		this.gridCell = arenaRealRun.getGridCell();
		this.rc = rc;	
	}
	
	public void prepareExploration(){
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
			boolean goStraight = false;
			int [] sensorData = null;		
			sensorData = rc.getSensorReading(rc.sendCommand("h"+SCANARENA));
			int moveCount = 0;
			//if want to do calibrate at start
//			rc.sendCommand(TURNLEFT);
//			rc.sendCommand(LEFTCALIBRATE);
//			sensorData = rc.getSensorReading(rc.sendCommand(TURNRIGHT));
			
			do{
				//send arena info to android
				rc.sendCommand("a"+arenaInforToString2());
				rc.sendCommand("a"+arenaInforToString());
				
				int row = robot.getCurrentPosition()[0];
				int column = robot.getCurrentPosition()[1];	
					
				/*if(moveCount>=7)
				{ 
					if(robot.canLeftCalibrate(row, column, gridCell, robot.getDirection()))
					{
						rc.sendCommand("h"+TURNLEFT);
						rc.sendCommand("h"+CALIBRATE);
						rc.sendCommand("h"+TURNRIGHT);
						moveCount=0;
					}
					*/
				if(robot.canFrontCalibrate(row, column, gridCell, robot.getDirection()))
					{	
						rc.sendCommand("h"+CALIBRATE);
						moveCount=0;
					}
				
				
						
				
				if(goStraight)//go straight
				{
					moveCount++;
					rc.sendCommand("h"+STRAIGHTMOVE);
					robot.goStraight();
					goStraight = false;
					rc.sendCommand("a"+robotLocationToString(robot.getCurrentPosition()[1]-1, robot.getCurrentPosition()[0]-1, robot.getDirection()));
				} 
				else if(!this.leftIsBlocked())//turn left
				{
					//before turn left. Try to do front and/or right calibration
					if(robot.canRightCalibrate(row, column, gridCell, robot.getDirection()))
					{	
						rc.sendCommand("h"+TURNRIGHT);
						rc.sendCommand("h"+CALIBRATE);
						rc.sendCommand("h"+TURNLEFT);
						moveCount=0;
					}
					if(robot.canFrontCalibrate(row, column, gridCell, robot.getDirection()))
					{	
						rc.sendCommand("h"+CALIBRATE);
						moveCount=0;
					}
					
					rc.sendCommand("h"+TURNLEFT);
					robot.turnLeft();
					goStraight = true;
					rc.sendCommand("a"+robotLocationToString(robot.getCurrentPosition()[1]-1, robot.getCurrentPosition()[0]-1, robot.getDirection()));
				} 
				else if(!this.frontIsBlocked())//go straight
				{
					moveCount++;
					rc.sendCommand("h"+STRAIGHTMOVE);
					robot.goStraight();
					goStraight = false;
					rc.sendCommand("a"+robotLocationToString(robot.getCurrentPosition()[1]-1, robot.getCurrentPosition()[0]-1, robot.getDirection()));
				}  
				else if(!this.rightIsBlocked())//turn right
				{
					//before turn right. Try to do front and/or right calibration
					if(robot.canLeftCalibrate(row, column, gridCell, robot.getDirection()))
						{
							rc.sendCommand("h"+TURNLEFT);
							rc.sendCommand("h"+CALIBRATE);
							rc.sendCommand("h"+TURNRIGHT);
							moveCount=0;
						}
					if(robot.canFrontCalibrate(row, column, gridCell, robot.getDirection()))
						{	
							rc.sendCommand("h"+CALIBRATE);
							moveCount=0;
						}
					rc.sendCommand("h"+TURNRIGHT);
					robot.turnRight();
					goStraight = true;
					rc.sendCommand("a"+robotLocationToString(robot.getCurrentPosition()[1]-1, robot.getCurrentPosition()[0]-1, robot.getDirection()));
				}
				else {//go back
					//before turn back. Try to do front and/or left calibration
					if(robot.canFrontCalibrate(row, column, gridCell, robot.getDirection()))
					{	
						rc.sendCommand("h"+CALIBRATE);
						moveCount=0;
					}
					if(robot.canLeftCalibrate(row, column, gridCell, robot.getDirection()))
					{
						rc.sendCommand("h"+TURNLEFT);
						rc.sendCommand("h"+CALIBRATE);
						rc.sendCommand("h"+TURNRIGHT);
						moveCount=0;
					}
					
					rc.sendCommand("h"+TURNBACK);
					robot.turnBack();
					goStraight = false;	
					rc.sendCommand("a"+robotLocationToString(robot.getCurrentPosition()[1]-1, robot.getCurrentPosition()[0]-1, robot.getDirection()));
				}
				
				if(robot.getCurrentPosition()[0]==1 && robot.getCurrentPosition()[1]==13){
					reachedGoal=true;
				}		
				//not required for real run
//				try {
//					Thread.sleep(1000/speed);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				arenaRealRun.updateRobotPosition();
				arenaRealRun.updateCoverageAndTime();	
					//not required for real run
//					if(enableCoverageTerminal)
//					{
//						if(arenaRealRun.calculateExploredPercentage() >= coveredPercentage){
//							System.out.println(arenaRealRun.calculateExploredPercentage());
//							System.out.println(coveredPercentage);
//							System.out.println("need to go back");
//							timeToGoBack = true;
//						}
//					}
					
			}while(!(robot.getCurrentPosition()[0] ==18 && robot.getCurrentPosition()[1] == 1) || reachedGoal!=true);
			arenaRealRun.updateRobotPosition();
			
			
			//after go back to start zone, try to do front calibration and (left or right) calibration
			int row = robot.getCurrentPosition()[0];
			int column = robot.getCurrentPosition()[1];	
			if(robot.canFrontCalibrate(row, column, gridCell, robot.getDirection()))
			{	
				rc.sendCommand("h"+CALIBRATE);
			}
			if(robot.canLeftCalibrate(row, column, gridCell, robot.getDirection()))
			{
				rc.sendCommand("h"+TURNLEFT);
				rc.sendCommand("h"+CALIBRATE);
				rc.sendCommand("h"+TURNRIGHT);
			}
			else if(robot.canRightCalibrate(row, column, gridCell, robot.getDirection()))
			{	
				rc.sendCommand("h"+TURNRIGHT);
				rc.sendCommand("h"+CALIBRATE);
				rc.sendCommand("h"+TURNLEFT);
			}
			
			//not required for real run
//			if(timeToGoBack || timesUp){
//				turnBackAndGoBack();
//			}
			if(reachedGoal){
				switch(robot.getDirection())
				{
				case NORTH:
					break;
				case SOUTH:
					rc.sendCommand("h"+TURNBACK);
					break;
				case EAST:
					rc.sendCommand("h"+TURNLEFT);
					break;
				case WEST:
					rc.sendCommand("h"+TURNRIGHT);
					break;
				}
				robot.turnToNorth();
				arenaRealRun.updateRobotPosition();
				
				rc.sendCommand("a"+robotLocationToString(robot.getCurrentPosition()[1]-1, robot.getCurrentPosition()[0]-1, robot.getDirection()));
				rc.sendCommand("a"+arenaInforToString2());
				rc.sendCommand("a"+arenaInforToString());
				
				//not required for real run
				ArrayList<String> arenaDetails = new ArrayList<String>();
				arenaDetails = arenaRealRun.exportMap(gridCell);
				arenaRealRun.saveArenaToFile(FILENAME2,arenaDetails);
				
				rc.sendCommand("r"+"Exploration finished, running fastest path is allowed.");
				fpgo(gridCell);
				//not required for real run
//				if(enableTimerTerminal)
//				{
//					arenaRealRun.stopTimer();
//				}
				
			}	
		}
		
	//not required for real run
	public void turnBackAndGoBack()
	{
		robot.turnBack();
		int i = 0;
		boolean justTurned = false;
		do{
			i++;
			robot.getSensorsData(gridCell);
		
			if(justTurned){
				robot.goStraight();
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
				System.out.println("reached goal point!");
			}
			try {
				Thread.sleep(1000/speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			arenaRealRun.updateRobotPosition();
			arenaRealRun.updateCoverageAndTime();

		}while(!(robot.getCurrentPosition()[0] ==18 && robot.getCurrentPosition()[1] == 1));
		robot.turnToNorth();
		arenaRealRun.updateRobotPosition();
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
	
	//not required for real run
	public void setCoveredPercentage(int coveredPercentage)
	{
		this.coveredPercentage = coveredPercentage;
	}
	//not required for real run
	public boolean getCoveredTerminal()
	{
		return enableCoverageTerminal;
	}
	
	//not required for real run
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

	//not required for real run
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
	
	//not required for real run
	public boolean getTimerTerminal()
	{
		return enableTimerTerminal;
	}
	
	//not required for real run
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	//not required for real run
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
        hexString = arenaRealRun.binToHex(binaryString);
        stringToSend = "{\"grid\" : \""  + hexString + "\"}";
		return stringToSend;
	}
	
	private String arenaInforToString2()
	{
		String stringToSend = "";
        String hexString = "";
        String binaryString = "";
        for (int i = 0; i < ROW; i++)
        {
            for (int j = 0; j < COLUMN; j++)
            {
                binaryString += gridCell[i][j].getGridCellCondition()[0];   
            }
        }
        hexString = arenaRealRun.binToHex(binaryString);
        stringToSend = "{\"Explore\" : \""  + hexString + "\"}";
		return stringToSend;
	}
	
	
	
	//following done by JiaJing
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
		
		int preCalibrateStep = 0; int countCalibrate = 0;
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
//				robotPath += RpiToRobotData.comFpGoalCalibrate;
			robotPath += RpiToRobotData.STRAIGHTMOVE;
			else{
				robotPath += RpiToRobotData.STRAIGHTMOVE;
				//robot.goStraight();
					//simulate go straight
//				robotPath += PcInterface.comGoStraight;
			}
			
		}
		System.out.printf("robot movement: %s\n", robotPath);
		String newPath = "";
		for(int j = 0; j < robotPath.length(); j++){
			
			switch (robotPath.charAt(j)){
			/*
			case 'R':
				robot.turnRight();
				rc.sendCommand("h"+TURNRIGHT);
				break;
			case 'L':
				robot.turnLeft();
				rc.sendCommand("h"+TURNLEFT);
				break;
			case 'M':
				int fCount = 1;
				for(int k=j+1; robotPath.charAt(k) == 'M' && k<robotPath.length();k++)
				{
						robot.goStraight();
						fCount++;
						j++;
				}
				robot.goStraight();
				rc.sendCommand("h"+STRAIGHTMOVEF + fCount);
				break;
			case 'B':
				robot.turnBack();
				rc.sendCommand("h"+TURNBACK);
				break;
				default:
					System.out.printf("calibrate %s\n", robotPath.charAt(j));
					*/
			case 'R':
				robot.turnRight();
				newPath += "R";
				break;
			case 'L':
				robot.turnLeft();
				newPath += "L";
				break;
			case 'M':
				int fCount = 1;
				for(int k=j+1; k<robotPath.length();k++)
				{
					if(robotPath.charAt(k) == 'M')
					{
						robot.goStraight();
						fCount++;
						j++;
						if(fCount>=9)
							break;
					}
					else
						break;
				}
				robot.goStraight();
				newPath += STRAIGHTMOVEF+fCount;
				break;
			}

			
			
			System.out.println(robot.getCurrentPosition()[0] +", " + robot.getCurrentPosition()[1]);
//			try {
//				Thread.sleep(1000/speed);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			arenaRealRun.updateRobotPosition();
		}
		rc.sendCommand("h"+newPath);
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
