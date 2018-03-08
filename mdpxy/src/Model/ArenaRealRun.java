package Model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;


import RPIConnection.RpiConnect;
import RPIConnection.RpiToRobotData;

import Algorithms.Algorithm1RealRun;

public class ArenaRealRun extends JFrame implements ArenaAndRobotData, RpiToRobotData{

    GridCell[][] gridCell = new GridCell[ROW][COLUMN];
	GridCell[][] gridCell2 = new GridCell[ROW][COLUMN];
	JButton jbtnExplore, jbtnFastestPath, jbtnClear, jbtnImportMap, jbtnExportMap,jbtnEnableCoverageTerminal, jbtnEnableTimeLimitTerminal;
	JLabel jlbTimeLimit, jlArenaDesign, jlTimeCost, jlSpeed, jlbExplorationCoverage, jlbCoveragedPrecentage, jlbTerminal, jlbTerminal2;
	JPanel jpManual,jpManualPart1, jpManualPart2, jpArenaContainer, jpLeft, jpRight, jpLeftMap, jpRightMap, jpToplabel;
	JTextField jtfTimeLimit, jtfExplorationCoverage;
	JComboBox jcbSpeed;
	private String[] strSpeed = {"1", "2", "5", "10"};
	GridCellclick clickGrid = new GridCellclick();
	private Robot robot;
	private ArenaRealRun arenaRealRun;
	private Algorithm1RealRun algorithm1RealRun;
	RpiConnect rc;
	Timer timer;
	
	public ArenaRealRun(Robot robot)
	{
		
		this.robot = robot;
		this.setUI();	
		arenaRealRun = this;
		rc = new RpiConnect();
		algorithm1RealRun = new Algorithm1RealRun(robot, arenaRealRun, rc);
		System.out.println("RPI ok");
		
		
		//wait command from android, set start position
		int[] startPosition = new int[2];
		String[]startInfo = rc.getStartPositionFromAndroid();
		for(int i = 0; i < startPosition.length; i++)
			startPosition[i] = Integer.parseInt(startInfo[i]);
		robot.setCurrentPosition(startPosition);
		robot.setDirection(Integer.parseInt(startInfo[2]));
		
		
		this.updateRobotPosition();
		System.out.println("Robot ready");
		
		int speed = Integer.parseInt(jcbSpeed.getSelectedItem().toString());
		int coveredPercentage = Integer.parseInt(jtfExplorationCoverage.getText().toString());
		algorithm1RealRun.setSpeed(speed);
		algorithm1RealRun.setCoveredPercentage(coveredPercentage);
		algorithm1RealRun.prepareExploration();
//		rc.sendCommand("r"+"Exploration finished, running fastest path is allowed.");
		
		rc.getFastestPathCommandFromAndroid();
		String path1=algorithm1RealRun.getFastestPath(gridCell2);
		rc.sendCommand(path1);
		
		System.out.println("Fastest path finished");
	}
		
	public void setUpGridCell()
	{

		for(int i=0; i < ROW; i++){
			for(int j = 0; j < COLUMN; j++){
				gridCell[i][j] = new GridCell(i, j);
				gridCell[i][j].addMouseListener(clickGrid);
				gridCell[i][j].setOpaque(true);
				//arena for design, always set to visited.
				gridCell[i][j].setGridCellCondition(1, 0);
				gridCell[i][j].setBorder(BorderFactory.createLineBorder(CELLBORDER_COLOR));
				jpLeftMap.add(gridCell[i][j]);
				gridCell2[i][j] = new GridCell(i, j);
				gridCell2[i][j].setOpaque(true);
				gridCell2[i][j].setBorder(BorderFactory.createLineBorder(CELLBORDER_COLOR));
				jpRightMap.add(gridCell2[i][j]);
			}
		}
	}
	
	private void setUI()
	{
		this.setTitle("Simulator");
	   	this.setSize(1200,600);
	   	this.setLocation(100,80);
	   	this.setLayout(new BorderLayout());
	   	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   	
	   	//combobox
	   	jcbSpeed = new JComboBox(strSpeed);
	   	jcbSpeed.setSelectedIndex(3);
	   	//button
	   	jbtnExplore = new JButton("Explore the arena");
	   	jbtnExplore.addActionListener(new ExploreArenaHandler());
	   	
	   	jbtnFastestPath = new JButton("Run fastest path");
	   	jbtnFastestPath.addActionListener(new RunFastestPathHandler());
	   	
	   	jbtnClear = new JButton("Reset the arena");
	   	jbtnClear.addActionListener(new ResetArenaHandler());
	   	
	   	jbtnImportMap = new JButton("Import the arena");
	   	jbtnImportMap.addActionListener(new ImportMapHandler());
	   	jbtnExportMap = new JButton("Export the arena");
	   	jbtnExportMap.addActionListener(new ExportMapHandler());
	   	
	   	jbtnEnableCoverageTerminal = new JButton("Disabled");
	   	jbtnEnableCoverageTerminal.addActionListener(new CoverageTerminalHandler());
	   	jbtnEnableTimeLimitTerminal = new JButton("Disabled");
	   	jbtnEnableTimeLimitTerminal.addActionListener(new TimeLimitTerminalHandler());
	   	
	   	//label
	   	jlbTimeLimit = new JLabel("Time limit (sec):");

	   	jlArenaDesign = new JLabel("Design the arena:");
//	   	jlTimeCost = new JLabel("Time used: 0S");
	   	jlSpeed = new JLabel("Speed (step/sec):");
	   	jlbExplorationCoverage = new JLabel("Coverage (%):");
	   	jlbCoveragedPrecentage = new JLabel("Coverage: 0%");
	   	
	   	jlbTerminal = new JLabel("Coverage switch:");
	   	jlbTerminal2 = new JLabel("Timer switch:");
	   	
	   	//test field
	   	jtfTimeLimit = new JTextField(5);
	   	jtfTimeLimit.setText("360");
	   	jtfExplorationCoverage = new JTextField(5);
	   	jtfExplorationCoverage.setText("100");
	    //panel
	   	jpManual = new JPanel(new BorderLayout());
	   	jpManual.setPreferredSize(new Dimension(250, 500));
	   	jpManualPart1 = new JPanel(new GridLayout(5,5));
	   	jpManualPart2 = new JPanel(new GridLayout(0,1,0,20));

	   	jpManualPart1.add(jlbTimeLimit);
	   	jpManualPart1.add(jtfTimeLimit);
	   	jpManualPart1.add(jlbTerminal2);
	   	jpManualPart1.add(jbtnEnableTimeLimitTerminal);
	   	jpManualPart1.add(jlbExplorationCoverage);
	   	jpManualPart1.add(jtfExplorationCoverage);
	   	jpManualPart1.add(jlbTerminal);
	   	jpManualPart1.add(jbtnEnableCoverageTerminal);
	   	jpManualPart1.add(jlSpeed);
	   	jpManualPart1.add(jcbSpeed);
	   	
	   	jpManualPart2.add(jbtnExplore);
	   	jpManualPart2.add(jbtnFastestPath);
	   	jpManualPart2.add(jbtnClear);
	   	jpManualPart2.add(jbtnImportMap);
	   	jpManualPart2.add(jbtnExportMap);
	   	
	   	jpManual.add(jpManualPart1,BorderLayout.NORTH);
	   	jpManual.add(jpManualPart2,BorderLayout.SOUTH);
	   	
	   	jpArenaContainer = new JPanel(new FlowLayout());
	   	
	   	jpLeft = new JPanel(new BorderLayout());
	   	jpRight = new JPanel(new BorderLayout());
	   	
	   	jpLeftMap = new JPanel(new GridLayout(ROW,COLUMN));
	   	jpRightMap = new JPanel(new GridLayout(ROW,COLUMN));
	   	jpLeftMap.setPreferredSize(new Dimension(400, 500));
	   	jpRightMap.setPreferredSize(new Dimension(400, 500));
	   	
	   	
	   	this.setUpGridCell();
	   	this.updateArena(gridCell2);
	   	this.updateArena(gridCell);
	    this.setUpStartAndGoalGridCell(); 		 
	   	
	    jpToplabel = new JPanel(new GridLayout(1,1));
//	    jpToplabel.add(jlTimeCost);
	    jpToplabel.add(jlbCoveragedPrecentage);
	
	   	jpLeft.add(jpLeftMap,BorderLayout.SOUTH);
	   	jpLeft.add(jlArenaDesign,BorderLayout.NORTH);

	   	jpRight.add(jpRightMap,BorderLayout.SOUTH);
	   	jpRight.add(jpToplabel,BorderLayout.NORTH);
	   	
	   	jpArenaContainer.add(jpLeft);
	   	jpArenaContainer.add(jpRight);
	   	
	   	this.add(jpManual,BorderLayout.WEST);
	   	this.add(jpArenaContainer,BorderLayout.CENTER);
	   	jtfExplorationCoverage.setEditable(false);
	   	jtfTimeLimit.setEditable(false);
	   	this.setVisible(true);
	}
	
	
	//Update arena and robot(after movement) functions
	public void updateRobotPosition()
	{	
		
		//update explored arena before update robot position
		updateArenaAfterMovement();
		//change color from green to white
		updateArena(gridCell2);
		
		int[] currentPosition = new int[2];
		currentPosition[0] = robot.getCurrentPosition()[0];
		currentPosition[1] = robot.getCurrentPosition()[1];
		for(int i=-1; i < 2; i++){
			for(int j=-1; j < 2; j++){
				gridCell2[currentPosition[0]+i][currentPosition[1]+j].setBackground(ROBOT_COLOR);
			}
		}
		switch(robot.getDirection()){
		case EAST:
			currentPosition[1] = currentPosition[1] + 1;
			break;
		case WEST:
			currentPosition[1] = currentPosition[1] - 1;
			break;
		case SOUTH:
			currentPosition[0] = currentPosition[0] + 1;
			break;
		case NORTH:
			currentPosition[0] = currentPosition[0] - 1;
			break;	
		}
		gridCell2[currentPosition[0]][currentPosition[1]].setBackground(ROBOTDIRECTION_COLOR);
		
		
		
	}
		
	public void updateArenaAfterMovement()
	{	
		//set explored arena to visited
		int[] position = robot.getPreviousPosition();
		for(int i=-1; i<2; i++){
			for(int j=-1; j<2; j++)
				if(gridCell2[position[0]+i][position[1]+j].getGridCellCondition()[0] != 1)
				{
					gridCell2[position[0]+i][position[1]+j].setGridCellCondition(1, 0);
					//set to 5, will not update anymore
					robot.sensorDataRecord[position[0]+i][position[1]+j] = 5;
				}
		}
			
				int [] sensorData = null;
				String sensorDataString = rc.sendCommand("h"+SCANARENA);
//				sensorDataString = rc.sendCommand("h"+SCANARENA);
				sensorData = this.rc.getSensorReading(sensorDataString);
				robot.getSensorsData(gridCell2, sensorData);
	}
	
	public void setUpStartAndGoalGridCell()
	{
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				gridCell[ROW - i - 1][j].setBackground(STARTZONECOLOR);
				gridCell[ROW - i - 1][j].removeMouseListener (clickGrid);
				gridCell[i][COLUMN - j - 1].setBackground(GOALZONECOLOR);
				gridCell[i][COLUMN - j - 1].removeMouseListener (clickGrid);
			}
		}	
	}
	
	public void updateArena(GridCell[][] gridCell)
	{
		for(int i=0; i < ROW; i++){
			for(int j = 0; j < COLUMN; j++){
				if(gridCell[i][j].getGridCellCondition()[0] == 0)
				{
					gridCell[i][j].setBackground(UNVISITED_COLOR);
				}
				else
				{
					//grid cell is empty
					if(gridCell[i][j].getGridCellCondition()[1] == 0)
					{
						gridCell[i][j].setBackground(EMPTY_COLOR);
					}
					//grid cell has obstacle
					else if(gridCell[i][j].getGridCellCondition()[1] == 1)
					{
						gridCell[i][j].setBackground(OBSTACLE_COLOR);
					}
				}
				
			}
		}
		
	}
	
	public double calculateExploredPercentage(){
		int count = 0;
		double arenaSize = ROW*COLUMN;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if(gridCell2[i][j].getGridCellCondition()[0] == 1){
					count++;
				}
			}
		}
		return ((count/arenaSize)*100);
	}
	
//	public int getTimeLimied()
//	{
//		return Integer.parseInt(jtfTimeLimit.getText());
//	}
	
	//Export map and import map functions 
	public String binToHex(String binary)
	{
		int count = 1;
	    int sum = 0;
	    String hex = "";
	    for(int i = 0; i < binary.length(); i++){
	        if(count == 1)
	            sum+=Integer.parseInt(binary.charAt(i) + "")*8;
	        else if(count == 2)
	            sum+=Integer.parseInt(binary.charAt(i) + "")*4;
	        else if(count == 3)
	            sum+=Integer.parseInt(binary.charAt(i) + "")*2;
	        else if(count == 4 || i < binary.length()+1){
	            sum+=Integer.parseInt(binary.charAt(i) + "")*1;
	            count = 0;
	            if(sum < 10)
	            	hex = Concat(hex,String.valueOf(sum));
	            else if(sum == 10)
	            	hex = Concat(hex,"A");
	            else if(sum == 11)
	            	hex = Concat(hex,"B");
	            else if(sum == 12)
	            	hex = Concat(hex,"C");
	            else if(sum == 13)
	            	hex = Concat(hex,"D");
	            else if(sum == 14)
	            	hex = Concat(hex,"E");
	            else if(sum == 15)
	            	hex = Concat(hex,"F");
	            sum=0;
	        }
	        count++;  
	    }
		return hex;
	}
	
	private String hexToBinary(String hex) {
		int len = hex.length() * 4;
	    String bin = new BigInteger(hex, 16).toString(2);

	    if(bin.length() < len){
	        int diff = len - bin.length();
	        String pad = "";
	        for(int i = 0; i < diff; ++i){
	            pad = pad.concat("0");
	        }
	        bin = pad.concat(bin);
	    }
	    return bin;
	}
	
	private String Concat(String a, String b) {
	        a += b;
	        return a;
	}
	
	private String padBitstreamToFullByte(String bitstream)
	{
		if (bitstream.length() % 8 == 0)
			return bitstream;
		else
		{
			String fullByteBitStream = bitstream;
			int remainder = bitstream.length()%8;
			for(int i = 0; i<remainder; i++)
			{
				fullByteBitStream = Concat(fullByteBitStream, "0");
			}
			return fullByteBitStream;
		}
		
	}
	
	public void saveArenaToFile(String fileName, ArrayList<String> arenaDetails){
		File file = new File(fileName);
		try {
			if(file.exists() == true)
			{
				file.delete();
				file.createNewFile();
			}
			PrintStream fileStream = new PrintStream(file);
			for(int i = 0; i < arenaDetails.size(); i++)
			{
				fileStream.println(arenaDetails.get(i));
			}
	      fileStream.close();
//	      JOptionPane.showMessageDialog(null, "Arena saved successfully");
	    } catch (IOException e) {
//			JOptionPane.showMessageDialog(null, "Failed to save arena");
		}
	}
	
	private ArrayList<String> getArenaFromFile(String fileName)
	{
		ArrayList<String> arenaDetails = new ArrayList<String>(); 
		File file = new File(fileName); 	
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNext()){
				arenaDetails.add(sc.next());
			}
			sc.close();
			JOptionPane.showMessageDialog(null, "Import arena successfully");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to import arena");
		}
		return arenaDetails;
	}
		
	public ArrayList<String> exportMap(GridCell[][] gridCell)
	{
		String exploreBinary = "";
		String obstacleBinary = "";
		for (int i = ROW-1; i >= 0; i--) {
			for (int j = 0; j < COLUMN; j++) {
						if(gridCell[i][j].getGridCellCondition()[0] == 0)
							exploreBinary = Concat(exploreBinary,"0");
						else
						{
							exploreBinary = Concat(exploreBinary,"1");
							if(gridCell[i][j].getGridCellCondition()[1]==0)
							{
								obstacleBinary = Concat(obstacleBinary,"0");
							}
							else
								obstacleBinary = Concat(obstacleBinary,"1");
						}
		        }
			}
			exploreBinary = "11" + exploreBinary +"11";
			String exploreHex = binToHex(exploreBinary);
			
			obstacleBinary = padBitstreamToFullByte(obstacleBinary);
			String obstacleHex= binToHex(obstacleBinary);
			
			ArrayList<String> arenaDetails = new ArrayList<String>();
			arenaDetails.add(exploreHex);
			arenaDetails.add(obstacleHex);
			return arenaDetails;
	}
	
	private void ImportMap(String fileName, GridCell[][] gridCell)
	{
		ArrayList<String> arenaDetails = new ArrayList<String>();
		arenaDetails = getArenaFromFile(FILENAME);
		String exploreBinary = "";
		String obstacleBinary = "";

		
		exploreBinary = hexToBinary(arenaDetails.get(0));
		obstacleBinary = hexToBinary(arenaDetails.get(1));
		exploreBinary = exploreBinary.substring(2, exploreBinary.length()-2);
		
		int exploreIndex = 0, obstacleIndex = 0;
		for(int i = ROW-1; i>=0;i--)
		{
			for(int j = 0; j<COLUMN; j++)
			{
				if(exploreBinary.charAt(exploreIndex)=='1')
				{
				gridCell[i][j].setGridCellCondition(Character.getNumericValue(exploreBinary.charAt(exploreIndex)), Character.getNumericValue(obstacleBinary.codePointAt(obstacleIndex)));
				obstacleIndex++;
				}
				else
					gridCell[i][j].setGridCellCondition(Character.getNumericValue(exploreBinary.charAt(exploreIndex)), 0);	
					exploreIndex++;
			}
		}
		updateArena(gridCell);
	}
	
	private void startTimer()
	{
		int time = Integer.parseInt(jtfTimeLimit.getText())*1000;
		ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
            	System.out.println("Times up");
            	algorithm1RealRun.setTimesUp(true);
            }
        };
        timer = new Timer( time , actionListener );
		timer.setRepeats(false);
        timer.start();	
	}
	
	public void stopTimer()
	{
		System.out.println("try to stop timer");
		timer.stop();
	}
	
	public GridCell[][] getGridCell()
	{
		return gridCell2;
	}

	public void updateCoverageAndTime(){
		double result = calculateExploredPercentage();
		jlbCoveragedPrecentage.setText("Coverage: "+result+"%");

	}
	
 	class GridCellclick extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt){
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COLUMN; j++) {
					if(evt.getSource()==gridCell[i][j]){
						if(gridCell[i][j].getGridCellCondition()[1]== GridCell.EMPTY)
						{
							gridCell[i][j].setGridCellCondition(GridCell.VISITED, GridCell.OBSTACLE);
							gridCell[i][j].setBackground(Color.BLACK);
							gridCell2[i][j].setGridCellCondition(GridCell.UNVISITED, GridCell.OBSTACLE);
						}
						else if(gridCell[i][j].getGridCellCondition()[1]== GridCell.OBSTACLE)
						{
							gridCell[i][j].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							gridCell[i][j].setBackground(Color.WHITE);
							gridCell2[i][j].setGridCellCondition(GridCell.UNVISITED, GridCell.EMPTY);
						}
			        }
				}
			}

	    }

	}
	
	class ResetArenaHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COLUMN; j++) {
							gridCell[i][j].setGridCellCondition(GridCell.VISITED, GridCell.EMPTY);
							gridCell2[i][j].setGridCellCondition(GridCell.UNVISITED, GridCell.EMPTY);
							updateArena(gridCell);
			        }
				}
			setUpStartAndGoalGridCell();
			JOptionPane.showMessageDialog(null, "Reset arena successfully");

		}
	}
	
	class ExportMapHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			ArrayList<String> arenaDetails = new ArrayList<String>();
			arenaDetails = exportMap(gridCell);
			saveArenaToFile(FILENAME,arenaDetails);
		}
	}
	
	class ImportMapHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			ImportMap(FILENAME, gridCell);
			setUpStartAndGoalGridCell();
			for(int i=0; i < ROW; i++){
				for(int j = 0; j < COLUMN; j++){
					if(gridCell[i][j].getGridCellCondition()[1]== 1)
						{
							gridCell2[i][j].setGridCellCondition(0, 1);
						}
					
					}
				}
		}
	}
	
	class ExploreArenaHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int speed = Integer.parseInt(jcbSpeed.getSelectedItem().toString());
			int coveredPercentage = Integer.parseInt(jtfExplorationCoverage.getText().toString());
				if(algorithm1RealRun.getTimerTerminal())	// No declaration
			{
				startTimer();
			}
			algorithm1RealRun.setSpeed(speed);
			algorithm1RealRun.setCoveredPercentage(coveredPercentage);
			algorithm1RealRun.prepareExploration();	
		}
	}
	
	
	class RunFastestPathHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			System.out.println("fastest path will be run only after the exploration");
			//29126
			//29126 start
			//exp.getFastestPath(gridCell2);
			algorithm1RealRun.fpgo(gridCell2);
		}
	}
	
	class CoverageTerminalHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(algorithm1RealRun.getCoveredTerminal())
			{
				jbtnEnableCoverageTerminal.setText("Disabled");
				jtfExplorationCoverage.setEditable(false);
			}
			else
			{
				jbtnEnableCoverageTerminal.setText("Enabled");
				jtfExplorationCoverage.setEditable(true);
			}
			algorithm1RealRun.switchCoveredTerminal();
		}
	}
	
	class TimeLimitTerminalHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(algorithm1RealRun.getTimerTerminal())
			{
				jbtnEnableTimeLimitTerminal.setText("Disabled");
				jtfTimeLimit.setEditable(false);
			}
			else
			{
				jbtnEnableTimeLimitTerminal.setText("Enabled");
				jtfTimeLimit.setEditable(true);
			}
			algorithm1RealRun.switchTimerTerminal();
		}
	}
}
