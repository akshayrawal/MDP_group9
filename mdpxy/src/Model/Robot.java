package Model;

import java.util.ArrayList;

import RPIConnection.RpiToRobotData;

public class Robot implements ArenaAndRobotData{
	
	private int[] currentPosition = new int[2];
	private int[] previousPosition = new int[2];
	private int direction;
	public static int[][] sensorDataRecord = new int[ROW][COLUMN];
	
	public Robot(int[]StartPosition, int direction)
	{
		this.currentPosition = StartPosition;
		this.previousPosition[0] = currentPosition[0];
		this.previousPosition[1] = currentPosition[1];
		this.direction = direction;
		//initialize sensor data record. Value 10 means no obstacle or empty record. (1 is obstacle. 0 is empty. 5 means dont update anymore)
		for(int i = 0; i < ROW; i++)
			for(int j = 0; j< COLUMN; j++)
			{
				sensorDataRecord[i][j] = 10;
			}
	}
	
	public int[] getCurrentPosition()
	{
		return this.currentPosition;
	}
	
	public void setCurrentPosition(int[] currentPosition)
	{
		this.currentPosition = currentPosition;
	}
	
	public int[] getPreviousPosition()
	{
		return this.previousPosition;
	}
	
	public void setPreviousPosition(int[] previousPosition)
	{
		this.previousPosition = previousPosition;
	}
	
	public int getDirection()
	{
		return direction;
	}
	
	public void setDirection(int direction)
	{
		this.direction = direction;
	}
	
	public void goStraight()
	{
		previousPosition[0] = currentPosition[0];
		previousPosition[1] = currentPosition[1];
		switch (direction) {
		case EAST:
			currentPosition[1] = currentPosition[1] + 1;
			break;
		case SOUTH:
			currentPosition[0] = currentPosition[0] + 1;
			break;
		case WEST:
			currentPosition[1] = currentPosition[1] - 1;
			break;
		case NORTH:
			currentPosition[0] = currentPosition[0] - 1;
			break;
		}
	}
	
	public void turnLeft()
	{
		switch (direction) {
		case EAST:
			direction = NORTH;
			break;
		case SOUTH:
			direction = EAST;
			break;
		case WEST:
			direction = SOUTH;
			break;
		case NORTH:
			direction = WEST;
			break;
		}
	}
	
	public void turnRight()
	{
		switch (direction) {
		case EAST:
			direction = SOUTH;
			break;
		case SOUTH:
			direction = WEST;
			break;
		case WEST:
			direction = NORTH;
			break;
		case NORTH:
			direction = EAST;
			break;
		}
	}
	
	public void turnBack()
	{
		switch (direction) {
		case EAST:
			direction = WEST;
			break;
		case SOUTH:
			direction = NORTH;
			break;
		case WEST:
			direction = EAST;
			break;
		case NORTH:
			direction = SOUTH;
			break;
		}
//		turnLeft();
//		turnLeft();
	}
	
	public static boolean canMoveOrNot(int positionRow, int positionColumn, GridCell[][] gridCell, int direction)
	{	
		switch (direction) {
		case EAST:
			positionColumn++;
			break;
		case SOUTH:
			positionRow++;
			break;
		case WEST:
			positionColumn--;
			break;
		case NORTH:
			positionRow--;
			break;}
		if(positionRow<=0 || positionRow>=ROW-1 || positionColumn<=0 || positionColumn>=COLUMN-1)
		{
			return false;
		}
		else
		{
			for(int i=-1; i<2; i++){
				for(int j=-1; j<2; j++){
					if(gridCell[positionRow+i][positionColumn+j].getGridCellCondition()[1] == 1){	
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	public static boolean canMoveOrNotFP(int positionRow, int positionColumn, GridCell[][] gridCell, int direction)
	{	
		switch (direction) {
		case EAST:
			positionColumn++;
			break;
		case SOUTH:
			positionRow++;
			break;
		case WEST:
			positionColumn--;
			break;
		case NORTH:
			positionRow--;
			break;}
		if(positionRow<=0 || positionRow>=ROW-1 || positionColumn<=0 || positionColumn>=COLUMN-1)
		{
			return false;
		}
		else
		{
			for(int i=-1; i<2; i++){
				for(int j=-1; j<2; j++){
					if(gridCell[positionRow+i][positionColumn+j].getGridCellCondition()[1] == 1 || gridCell[positionRow+i][positionColumn+j].getGridCellCondition()[0] == 0){	
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean canRightCalibrate(int positionRow, int positionColumn, GridCell[][] gridCell, int direction) 
	{
		switch (direction) {
		case EAST:
			positionRow++;
			break;
		case SOUTH:
			positionColumn--;
			break;
		case WEST:
			positionRow--;
			break;
		case NORTH:
			positionColumn++;
			break;}
		if(positionRow<=0 || positionRow>=ROW-1 || positionColumn<=0 || positionColumn>=COLUMN-1)
		{
			return true;
		}
		else
		{
			switch (direction) {
			case EAST:
					if(gridCell[positionRow+1][positionColumn+1].getGridCellCondition()[1] == 1 && gridCell[positionRow+1][positionColumn-1].getGridCellCondition()[1] == 1){
						return true; 
					}
				break;
			case SOUTH:
				if(gridCell[positionRow+1][positionColumn-1].getGridCellCondition()[1] == 1 && gridCell[positionRow-1][positionColumn-1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;
			case WEST:
				if(gridCell[positionRow-1][positionColumn-1].getGridCellCondition()[1] == 1 && gridCell[positionRow-1][positionColumn+1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;
			case NORTH:
				if(gridCell[positionRow+1][positionColumn+1].getGridCellCondition()[1] == 1 && gridCell[positionRow-1][positionColumn+1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;}
		}
		return false;
	}
	
	public boolean canLeftCalibrate(int positionRow, int positionColumn, GridCell[][] gridCell, int direction)
	{
		switch (direction) {
		case EAST:
			positionRow--;
			break;
		case SOUTH:
			positionColumn++;
			break;
		case WEST:
			positionRow++;
			break;
		case NORTH:
			positionColumn--;
			break;}
		if(positionRow<=0 || positionRow>=ROW-1 || positionColumn<=0 || positionColumn>=COLUMN-1)
		{
			return true;
		}
		else
		{
			switch (direction) {
			case EAST:
					if(gridCell[positionRow-1][positionColumn+1].getGridCellCondition()[1] == 1 && gridCell[positionRow-1][positionColumn-1].getGridCellCondition()[1] == 1){
						return true; 
					}
				break;
			case SOUTH:
				if(gridCell[positionRow+1][positionColumn+1].getGridCellCondition()[1] == 1 && gridCell[positionRow-1][positionColumn+1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;
			case WEST:
				if(gridCell[positionRow+1][positionColumn-1].getGridCellCondition()[1] == 1 && gridCell[positionRow+1][positionColumn+1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;
			case NORTH:
				if(gridCell[positionRow][positionColumn-1].getGridCellCondition()[1] == 1&&gridCell[positionRow+1][positionColumn-1].getGridCellCondition()[1] == 1 && gridCell[positionRow-1][positionColumn-1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;}
		}
		return false;
	}
	
	public boolean canFrontCalibrate(int positionRow, int positionColumn, GridCell[][] gridCell, int direction)
	{
		switch (direction) {
		case EAST:
			positionColumn++;
			break;
		case SOUTH:
			positionRow++;
			break;
		case WEST:
			positionColumn--;
			break;
		case NORTH:
			positionRow--;
			break;}
		if(positionRow<=0 || positionRow>=ROW-1 || positionColumn<=0 || positionColumn>=COLUMN-1)
		{
			return true;
		}
		else
		{
			switch (direction) {
			case EAST:
					if(gridCell[positionRow-1][positionColumn+1].getGridCellCondition()[1] == 1 && gridCell[positionRow+1][positionColumn+1].getGridCellCondition()[1] == 1){
						return true; 
					}
				break;
			case SOUTH:
				if(gridCell[positionRow+1][positionColumn-1].getGridCellCondition()[1] == 1 && gridCell[positionRow+1][positionColumn+1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;
			case WEST:
				if(gridCell[positionRow-1][positionColumn-1].getGridCellCondition()[1] == 1 && gridCell[positionRow+1][positionColumn-1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;
			case NORTH:
				if(gridCell[positionRow-1][positionColumn-1].getGridCellCondition()[1] == 1 && gridCell[positionRow-1][positionColumn+1].getGridCellCondition()[1] == 1){
					return true; 
				}
				break;}
		}
		return false;
	}
	
	public boolean positionInsideArena(int row, int column)
	{
		if(row >= 0 && row <= ROW-1 && column >= 0 && column <= COLUMN-1)
			return true;
		else
			return false;
	}
	
	//for simulator
	public void getSensorsData(GridCell[][] gridCell)
	{
		this.getFrontSensor1Data(gridCell);
		this.getFrontSensor2Data(gridCell);
		this.getFrontSensor3Data(gridCell);
		this.getLeftSensorData(gridCell);
		this.getRightSensorData(gridCell);
	}
	
	private void getFrontSensor1Data(GridCell[][] gridCell) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor2Data(GridCell[][] gridCell) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1])){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1])){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0],currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0],currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor3Data(GridCell[][] gridCell) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			case WEST:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getLeftSensorData(GridCell[][] gridCell) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
		
	private void getRightSensorData(GridCell[][] gridCell) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	public void turnToNorth()
	{
		switch (this.direction) {
		case NORTH:
			break;
		case SOUTH:
			turnBack();
			break;
		case EAST:
			turnLeft();
			break;
		case WEST:
			turnRight();
			break;
		}
	}
	
	public String getDirectionString() {
		switch (this.direction) {
		case NORTH:
			return "north";
		case SOUTH:
			return "south";
		case EAST:
			return "east";
		case WEST:
			return "west";
		default:
			return null;
		}
	}
	
	
	//For real run without update grid cell 
	public void getSensorsData(GridCell[][] gridCell, int[] distance)
	{
		this.getFrontSensor1Data(gridCell, distance[2]);
		this.getFrontSensor2Data(gridCell, distance[1]);
		this.getFrontSensor3Data(gridCell, distance[0]);
		this.getLeftSensorData(gridCell, distance[3]);
		this.getRightSensorData(gridCell, distance[4]);
		
	}
	
	private void getFrontSensor1Data(GridCell[][] gridCell, int obstacleDistance) {
		boolean obstacleDetected = false;
		
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;

						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor2Data(GridCell[][] gridCell, int obstacleDistance) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1])){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1])){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0],currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0],currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor3Data(GridCell[][] gridCell, int obstacleDistance) {

		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			case WEST:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getLeftSensorData(GridCell[][] gridCell, int obstacleDistance) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
		
	private void getRightSensorData(GridCell[][] gridCell, int obstacleDistance) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]+2+i)){
					if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-1][currentPosition[1]+2+i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]-2-i)){
					if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+1][currentPosition[1]-2-i].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]+1)){
					if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]+2+i][currentPosition[1]+1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]-1)){
					if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[0] == GridCell.UNVISITED)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
						}
					}
					else
					{
						if(gridCell[currentPosition[0]-2-i][currentPosition[1]-1].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							obstacleDetected = true;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	//For real run with update grid cell 
	public void getSensorsDataWithUpdate(GridCell[][] gridCell, int[] distance)
	{
		this.getFrontSensor1DataWithUpdate(gridCell, distance[2]);
		this.getFrontSensor2DataWithUpdate(gridCell, distance[1]);
		this.getFrontSensor3DataWithUpdate(gridCell, distance[0]);		
		this.getLeftSensorData(gridCell, distance[3]);
		this.getRightSensorData(gridCell, distance[4]);
	}
	
	private void getFrontSensor1DataWithUpdate(GridCell[][] gridCell, int obstacleDistance) {
		boolean obstacleDetected = false;
		
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]-1)){
					if(sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]-1] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]-1] = 1;//update sensor data record;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]-1] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]-1] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]-1] = 0;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]+1)){
					if(sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]+1] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]+1] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]+1] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]+1] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]+1] = 0;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]+2+i)){
					if(sensorDataRecord[currentPosition[0]-1][currentPosition[1]+2+i] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]-1][currentPosition[1]+2+i] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-1][currentPosition[1]+2+i] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]-1][currentPosition[1]+2+i] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-1][currentPosition[1]+2+i] = 0;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]-2-i)){
					if(sensorDataRecord[currentPosition[0]+1][currentPosition[1]-2-i] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]+1][currentPosition[1]-2-i] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+1][currentPosition[1]-2-i] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]+1][currentPosition[1]-2-i] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+1][currentPosition[1]-2-i] = 0;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor2DataWithUpdate(GridCell[][] gridCell, int obstacleDistance) {
		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1])){
					if(sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]] = 1;//update sensor data record;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]] = 0;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1])){
					if(sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]] = 0;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0],currentPosition[1]+2+i)){
					if(sensorDataRecord[currentPosition[0]][currentPosition[1]+2+i] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]][currentPosition[1]+2+i] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]][currentPosition[1]+2+i] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]][currentPosition[1]+2+i] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]][currentPosition[1]+2+i] = 0;
						}
					}
				}
				break;
				
			case WEST:
				if(positionInsideArena(currentPosition[0],currentPosition[1]-2-i)){
					if(sensorDataRecord[currentPosition[0]][currentPosition[1]-2-i] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]][currentPosition[1]-2-i] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]][currentPosition[1]-2-i] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]][currentPosition[1]-2-i] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]][currentPosition[1]-2-i] = 0;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	
	private void getFrontSensor3DataWithUpdate(GridCell[][] gridCell, int obstacleDistance) {

		boolean obstacleDetected = false;
		for (int i = 0; i < SHROTRANGE_SENSOR_DISTANCE; i++) {
			switch (this.direction) {
			case NORTH:
				if(positionInsideArena(currentPosition[0]-2-i,currentPosition[1]+1)){
					if(sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]+1] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]+1] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]+1] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]+1] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]-2-i][currentPosition[1]+1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-2-i][currentPosition[1]+1] = 0;
						}
					}
				}
				break;
				
			case SOUTH:
				if(positionInsideArena(currentPosition[0]+2+i,currentPosition[1]-1)){
					if(sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]-1] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]-1] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]-1] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]-1] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]+2+i][currentPosition[1]-1].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+2+i][currentPosition[1]-1] = 0;
						}
					}
				}
				break;
				
			case EAST:
				if(positionInsideArena(currentPosition[0]+1,currentPosition[1]+2+i)){
					if(sensorDataRecord[currentPosition[0]+1][currentPosition[1]+2+i] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]+1][currentPosition[1]+2+i] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+1][currentPosition[1]+2+i] = 0;
						}
					}
					else if (sensorDataRecord[currentPosition[0]+1][currentPosition[1]+2+i] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]+1][currentPosition[1]+2+i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]+1][currentPosition[1]+2+i] = 0;
						}
					}
				}
				break;
			case WEST:
				if(positionInsideArena(currentPosition[0]-1,currentPosition[1]-2-i)){
					if(sensorDataRecord[currentPosition[0]-1][currentPosition[1]-2-i] == 10)
					{
						if(i == obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							sensorDataRecord[currentPosition[0]-1][currentPosition[1]-2-i] = 1;
							obstacleDetected = true;
						}
						else
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-1][currentPosition[1]-2-i] = 0;
						}
					}
					else if(sensorDataRecord[currentPosition[0]-1][currentPosition[1]-2-i] == 1)
					{
						if(i != obstacleDistance)
						{
							gridCell[currentPosition[0]-1][currentPosition[1]-2-i].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							sensorDataRecord[currentPosition[0]-1][currentPosition[1]-2-i] = 0;
						}
					}
				}
				break;
			}
			if(obstacleDetected)
				break;

		}
	}
	

	
	//The following done by Jiajing
	public String turnToReqDirection(int curo, int reqo){
		switch(curo){
		case Robot.EAST:
			switch(reqo){
			case Robot.EAST:
				return "";
			case Robot.WEST:
				return RpiToRobotData.TURNBACK;
			case Robot.SOUTH:
				return RpiToRobotData.TURNRIGHT;
			case Robot.NORTH:
				return RpiToRobotData.TURNLEFT;
				default:
					System.out.println("error: turntoreqDirection");
			}
			break;
		case Robot.WEST:
			switch(reqo){
			case Robot.EAST:
				return RpiToRobotData.TURNBACK;
			case Robot.WEST:
				return "";
			case Robot.SOUTH:
				return RpiToRobotData.TURNLEFT;
			case Robot.NORTH:
				return RpiToRobotData.TURNRIGHT;
				default:
					System.out.println("error: turntoreqDirection");
			}
			break;
		case Robot.SOUTH:
			switch(reqo){
			case Robot.EAST:
				return RpiToRobotData.TURNLEFT;
			case Robot.WEST:
				return RpiToRobotData.TURNRIGHT;
			case Robot.SOUTH:
				return "";
			case Robot.NORTH:
				return RpiToRobotData.TURNBACK;
				default:
					System.out.println("error: turntoreqDirection");
			}
			break;
		case Robot.NORTH:
			switch(reqo){
			case Robot.EAST:
				return RpiToRobotData.TURNRIGHT;
			case Robot.WEST:
				return RpiToRobotData.TURNLEFT;
			case Robot.SOUTH:
				return RpiToRobotData.TURNBACK;
			case Robot.NORTH:
				return "";
				default:
					System.out.println("error: turntoreqDirection");
			}
			break;
			default:
				System.out.println("error: turntoreqDirection");
		}
		return null;
	}
	
	public String turnString(int[] prevPos, int[] curPos, int curO){
		switch(curO){
		case Robot.EAST:
			if(prevPos[0] != curPos[0] && prevPos[1] == curPos[1]){
				if(prevPos[0] < curPos[0])	return RpiToRobotData.TURNRIGHT;
				else return RpiToRobotData.TURNLEFT;
			}
			return "";
		case Robot.WEST:
			if(prevPos[0] != curPos[0] && prevPos[1] == curPos[1]){
				if(prevPos[0] < curPos[0])	return RpiToRobotData.TURNLEFT;
				else return RpiToRobotData.TURNRIGHT;
			}
			return "";
		case Robot.SOUTH:
			if(prevPos[1] != curPos[1] && prevPos[0] == curPos[0]){
				if(prevPos[1] < curPos[1])	return RpiToRobotData.TURNLEFT;
				else return RpiToRobotData.TURNRIGHT;
			}
			return "";
		case Robot.NORTH:
			if(prevPos[1] != curPos[1] && prevPos[0] == curPos[0]){
				if(prevPos[1] < curPos[1])	return RpiToRobotData.TURNRIGHT;
				else return RpiToRobotData.TURNLEFT;
			}
			return "";
			default:
				System.out.println("error: turnString");
		}
		return "";
	}
	
	public int getDirAfterRightTurn(int cur0)
	{
		switch (cur0) {
		case EAST:
			return SOUTH;
		case SOUTH:
			return WEST;
		case WEST:
			return NORTH;
		case NORTH:
			return EAST;
		}
		return 0;
	}
	
	public int getDirAfterLeftTurn(int cur0)
	{
		switch (cur0) {
		case EAST:
			return NORTH;
		case SOUTH:
			return EAST;
		case WEST:
			return SOUTH;
		case NORTH:
			return WEST;
		}
		return 0;
	}
	
}
