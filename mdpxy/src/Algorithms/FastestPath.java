package Algorithms;

import java.util.ArrayList;

import Model.ArenaAndRobotData;
import Model.GridCell;

public class FastestPath {
	private GridCell[][] gridCell;
	private int turnCost;
	private int[] startPos;
	private int[] goalPos;
	
	public FastestPath(GridCell[][] gridCell, int[] startPos, int[] goalPos){
		this.gridCell = gridCell;
		this.startPos = startPos;
		this.goalPos = goalPos;
	}

	public int[] execute(){
		turnCost = 2;
		int[] path1 = dijkstra(startPos);
//		return path1;
		turnCost = 4;
		int[] path2 = dijkstra(startPos);
		return comparePath(path1, path2);
	}
	
	private int abs(int k){
		if(k>=0 ) return k;
		else	return -k;
	}
	
	private int[] dijkstra(int[] curStart){
		int[][] pi = new int[ArenaAndRobotData.ROW][ArenaAndRobotData.COLUMN];
		BinaryHeap<GridCell> open = new BinaryHeap<GridCell>();
		ArrayList<GridCell> expMap = new ArrayList<GridCell>();
		int[][] visited = new int[ArenaAndRobotData.ROW][ArenaAndRobotData.COLUMN];
		int[] nbPos = new int[2];
		int movementCost = 0;
		//initialize map cells
		for(int i=0; i<ArenaAndRobotData.ROW; i++){
			for(int j=0; j<ArenaAndRobotData.COLUMN; j++){
				visited[i][j] = 0; 
				expMap.add(new GridCell(i, j, GridCell.COST_INFINITY));
			}
		}
				
		GridCell current;
		GridCell start  = expMap.get(curStart[0]*ArenaAndRobotData.COLUMN + curStart[1]);
		GridCell goal  = expMap.get(goalPos[0]*ArenaAndRobotData.COLUMN + goalPos[1]);
		start.setCost(0);
		open.add(start);
		pi[start.getRow()][start.getColumn()] = 0;
		while(open.peek() != goal){	
			current = open.remove();
			visited[current.getRow()][current.getColumn()] = 1;
			ArrayList<int[]> neighbours = new ArrayList<int[]>();
			neighbours = GridCell.getUnexploreWalkableNeighbour(new int[]{current.getRow(), current.getColumn()}, gridCell);
			int[] curPos = new int[]{current.getRow(), current.getColumn()};
			for(int i=0; i<neighbours.size(); i++){
				nbPos = neighbours.get(i);	//not obstacle
				GridCell nb = expMap.get(nbPos[0]*ArenaAndRobotData.COLUMN + nbPos[1]);
				movementCost = current.getCost() + calculateMovementCost(curPos, nbPos, pi);
				//not visited, cost
//				if(nb.getCost() > movementCost){
				if(visited[nb.getRow()][nb.getColumn()] != 1 && nb.getCost() > movementCost){
					if(open.contains(nb))	open.remove(nb);
					nb.setCost(movementCost);
					pi[nb.getRow()][nb.getColumn()] = current.getRow() * ArenaAndRobotData.COLUMN + current.getColumn();
					open.add(nb);
				}  
			}
			if(open.isEmpty()==true){
				System.out.println("no path");
				return null;
			}
		}
		
		int[] ipath = new int[ArenaAndRobotData.ROW*ArenaAndRobotData.COLUMN];
		current = goal;
		int j=0;
		while(!current.equals(start)){
//			System.out.printf("j:%d, (%d, %d)\n", j, current.getRow(), current.getColumn());
			ipath[j++] = current.getRow()*ArenaAndRobotData.COLUMN + current.getColumn();
			current = expMap.get(pi[current.getRow()][current.getColumn()]);
		}
		ipath[j] = start.getRow()*ArenaAndRobotData.COLUMN + start.getColumn();
		//reverse path
		int ps = j+1;
		int[] rpath = new int[ps];
		for(int i=0; i<ps; i++){
			//System.out.printf("i:%d, (%d, %d)\n", i, ipath[ps-i-1]/ArenaAndRobotData.COLUMN, ipath[ps-i-1]%ArenaAndRobotData.COLUMN);
			rpath[i] = ipath[ps-i-1];
		}
		return rpath;
	}
	
	private boolean isDiagonal(int[] p1, int[] p2){
		return (abs(p1[0]-p2[0]) == 1 && abs(p1[1]-p2[1])==1);
	}
	
	private int calculateMovementCost(int[] curPos, int[] nbPos, int[][] pi){
		int cost = 1;
		int pre;
		int[] prePos = new int[2];
		
		if((pre = pi[curPos[0]][curPos[1]]) == 0) return cost;
		prePos[0] = pre/ArenaAndRobotData.COLUMN; prePos[1] = pre%ArenaAndRobotData.COLUMN;
		
		if(isDiagonal(prePos, nbPos))	//check if it is a turn
			cost = turnCost;
		return cost;
	}
	
	private int[] comparePath(int[] path1, int[] path2){
		int turn1 = 0;
		int turn2 = 0;
		for(int i=2; i<path1.length; i++){
			if(isDiagonal(GridCell.oneDPosToTwoD(path1[i-2]), GridCell.oneDPosToTwoD(path1[i])))
				turn1++;
		}
		
		for(int i=2; i<path2.length; i++){
			if(isDiagonal(GridCell.oneDPosToTwoD(path2[i-2]), GridCell.oneDPosToTwoD(path2[i])))
				turn2++;
		}
		
//		System.out.println("cost 2 path");
//		for(int i=0;i<path1.length;i++){
//			System.out.printf("(%d, %d)\n", (Map.oneDPosToTwoD(path1[i]))[0], (Map.oneDPosToTwoD(path1[i]))[1]);
//		}
//		
//		System.out.println("cost 4 path");
//		for(int i=0;i<path2.length;i++){
//			System.out.printf("(%d, %d)\n", (Map.oneDPosToTwoD(path2[i]))[0], (Map.oneDPosToTwoD(path2[i]))[1]);
//		}
//		
//		System.out.printf("path1: %d, path2: %d\n", turn1, turn2);
		
		if(turn1 >= turn2) return path2;
		else return path1;
	}
}
