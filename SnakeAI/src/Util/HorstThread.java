package Util;

import java.util.HashMap;
import java.util.Stack;

import Brains.HorstAI;
import Logic.Field;
import Logic.Point;
import Logic.Snake;
import Logic.Field.CellType;
import Logic.GameInfo;
import Logic.Snake.Direction;

public class HorstThread extends Thread{
	
	private Snake mySnake;
	private Snake enemySnake;
	private Direction last = null;
	private Field tempField;
	private boolean firstRound=true;
	private boolean passedPortal = false;
	
	//Eatable Stuff
	//0 = apple , 1 = wallItem , 2 = changeSnake , 3 = changeHeadTail , 4 = Portal
	private Point[] eatable = new Point[5];
	private Point[] altTargets;
	private int currentAltTarget;
	
	//A* 
	private PathFinder finder;
	
	//HamiltonPath (PreFix h = hat mit dem HamiltonPath zutun
	private HamiltonPath hFinder;
	private HashMap<Point, Direction> hPath;
	private HashMap<Point, Integer> hPointToIndex;
	private Stack<Direction> hDirectionPath = new Stack<>();
	
	//AlphaBeta
	private AlphaBeta alphaBeta;
	
	public HorstThread(GameInfo info, Snake snake)
	{
		tempField = new Field(info.field().width(),info.field().height());
		for(int x=0;x<tempField.width();x++)
		{
			for(int y=0;y<tempField.height();y++)
			{
				Point p = new Point(x,y);
				tempField.setCell(info.field().cell(p), p);
			}
		}
		//A* berechnen
		//snake.move(direction)
		for(Point p : snake.segments())
			tempField.setCell(CellType.SNAKE, p);
		
		//initialisieren
		if(finder == null)
			finder = new PathFinder();
		if(hFinder == null)
			hFinder = new HamiltonPath();
		if(alphaBeta == null)
			alphaBeta = new AlphaBeta();
		if(altTargets == null)
		{
			altTargets = new Point[4];
			altTargets[0] = new Point(info.field().width()/2,1);
			altTargets[1] = new Point(info.field().width()/2,info.field().height()-2);
			altTargets[2] = new Point(1,info.field().height()/2);
			altTargets[3] = new Point(info.field().width()-2,info.field().height()/2);
			currentAltTarget = 0;
		}
					
		if(firstRound)
			return;
		//Initialize mySnake and EnemySnake
		if(mySnake == null || enemySnake == null)
		{
			mySnake = snake;
			for(Snake s : info.snakes())
			{
				if(!s.equals(snake))
				{
					enemySnake = s;
					break;
				}
			}
		}
		
		//Berechne HamiltonPath ueber das gesamte Feld
		if(hPointToIndex == null || hPath == null)
		{
			Node hamiltonPath = hFinder.getCompleteMaxPath(Field.defaultField(tempField.width(), tempField.height()));
			if(hamiltonPath != null)
			{
				hPointToIndex = new HashMap<>();
				hPath = new HashMap<>();
				int index = 0;
				Point first = hamiltonPath.getActual();
				Point last = null;
				while(hamiltonPath != null && hamiltonPath.getFrom() != null)
				{
					hPointToIndex.put(hamiltonPath.getActual(), index);
					hPath.put(hamiltonPath.getActual(), UtilFunctions.getDirection(hamiltonPath.getFrom().getActual(), hamiltonPath.getActual()));
					hamiltonPath = hamiltonPath.getFrom();
					if(hamiltonPath.getFrom() == null)
						last = hamiltonPath.getActual();
					index++;
				}
				if(last != null)
				{
					hPointToIndex.put(last, index);
					hPath.put(last, UtilFunctions.getDirection(first, last));
				}
			}
		}
	
		//Find all eatable Stuff
		getItems(info.field(),snake.headPosition());
	}
	
	@Override
	public void run()
	{
		
	}
	
	private void getItems(Field f, Point snakeHead) {
		eatable = new Point[5];
		for(int x=0;x<f.width();x++)
			for(int y=0;y<f.height();y++)
			{
				Point p = new Point(x,y);
				if(f.cell(p).equals(CellType.APPLE))
					eatable[0] = p;
				if(f.cell(p).equals(CellType.FEATUREWALL))
					eatable[1] = p;
				if(f.cell(p).equals(CellType.CHANGESNAKE))
					eatable[2] = p;
				if(f.cell(p).equals(CellType.CHANGEHEADTAIL))
					eatable[3] = p;
				if(f.cell(p).equals(CellType.PORTAL))
					eatable[4] = p;
			}

	}
}
