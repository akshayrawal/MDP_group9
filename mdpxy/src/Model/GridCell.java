package Model;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridCell extends JLabel implements Comparable<GridCell>{
	private int row, column, cost;
	public static final int OBSTACLE = 1;
	public static final int EMPTY = 0;
	public static final int VISITED = 1;
	public static final int UNVISITED = 0;
	private int[] position = null;
	private int[] gridCellCondition = new int[2];
	public static final int COST_INFINITY = 65535;	//29126
	
	public GridCell(int row, int column)
	{
		this.row = row;
		this.column = column;
		this.setGridCellCondition(UNVISITED, EMPTY);
	}
	
	public GridCell(int row, int column, int cost)
	{
		this.row = row;
		this.column = column;
		this.cost = cost;
		this.setGridCellCondition(UNVISITED, EMPTY);
	}
	
	public int getRow() {
		return row;
	}
		
	public int getColumn() {
		return column;
	}
	
	public int[] getPosition()
	{
		position = new int[]{row, column};
		return position;
	}

	public int[] getGridCellCondition() {
		return gridCellCondition;
	}

	public void setGridCellCondition(int visitedOrNot, int emptyOrNot) {
		this.gridCellCondition[0] = visitedOrNot;
		this.gridCellCondition[1] = emptyOrNot;
	}
	
	//done by JiaJing
	public int compareTo(GridCell o) {
		if(this.cost == o.cost)
			return 0;
		else if(this.cost > o.cost)
			return 1; 
		else return -1;
	}	

	
	public boolean equals(GridCell o){
		if(this.row == o.getRow() && this.column == o.getColumn())
			return true;
		else return false;
	}

	public int getCost() {
		return cost;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	//TODO: Edit Robot.canmovethrough() to the function written by xuhui
		public static ArrayList<int[]> getWalkableNeighbour(int[] curPos, GridCell[][] gridcell){
			ArrayList<int[]> neighbour = new ArrayList<int[]>();
//				int[][] grid = curMap.getGrid();
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.NORTH))	//up
				neighbour.add(new int[]{curPos[0]-1, curPos[1]});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.SOUTH))	//bottom
				neighbour.add(new int[]{curPos[0]+1, curPos[1]});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.WEST))	//left
				neighbour.add(new int[]{curPos[0], curPos[1]-1});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.EAST))	//right
				neighbour.add(new int[]{curPos[0], curPos[1]+1});
			return neighbour;
		}
		
		//curPos[0] = row, curPos[1] = col
		public static ArrayList<int[]> getUnexploreWalkableNeighbour(int[] curPos, GridCell[][] gridcell){
			ArrayList<int[]> neighbour = new ArrayList<int[]>();
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.NORTH))	//up
				neighbour.add(new int[]{curPos[0]-1, curPos[1]});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.SOUTH))	//bottom
				neighbour.add(new int[]{curPos[0]+1, curPos[1]});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.WEST))	//left
				neighbour.add(new int[]{curPos[0], curPos[1]-1});
			if(Robot.canMoveOrNotFP(curPos[0],curPos[1], gridcell, Robot.EAST))	//right
				neighbour.add(new int[]{curPos[0], curPos[1]+1});
			return neighbour;
		}

		
		public static int[] oneDPosToTwoD(int cur){
			int[] pos = new int[2];
			pos[0] = cur/ArenaAndRobotData.COLUMN;
			pos[1] = cur%ArenaAndRobotData.COLUMN;
			return pos;
		}
		
		public static boolean withinMap(int row, int column){
			if(row >= 0 && row <= ArenaAndRobotData.ROW-1 && column >= 0 && column <= ArenaAndRobotData.COLUMN-1)
				return true;
			return false;
		}
		
		public static boolean isWall(int[] pos){
			return (pos[0] == -1 || pos[0] == ArenaAndRobotData.ROW || pos[1] == -1 || pos[1] ==ArenaAndRobotData.COLUMN);
		}
	
}
